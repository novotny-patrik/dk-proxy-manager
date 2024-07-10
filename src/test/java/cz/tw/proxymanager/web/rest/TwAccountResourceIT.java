package cz.tw.proxymanager.web.rest;

import static cz.tw.proxymanager.domain.TwAccountAsserts.*;
import static cz.tw.proxymanager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.tw.proxymanager.IntegrationTest;
import cz.tw.proxymanager.domain.TwAccount;
import cz.tw.proxymanager.repository.TwAccountRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TwAccountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TwAccountResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/tw-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TwAccountRepository twAccountRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTwAccountMockMvc;

    private TwAccount twAccount;

    private TwAccount insertedTwAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TwAccount createEntity(EntityManager em) {
        TwAccount twAccount = new TwAccount().username(DEFAULT_USERNAME).password(DEFAULT_PASSWORD).active(DEFAULT_ACTIVE);
        return twAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TwAccount createUpdatedEntity(EntityManager em) {
        TwAccount twAccount = new TwAccount().username(UPDATED_USERNAME).password(UPDATED_PASSWORD).active(UPDATED_ACTIVE);
        return twAccount;
    }

    @BeforeEach
    public void initTest() {
        twAccount = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTwAccount != null) {
            twAccountRepository.delete(insertedTwAccount);
            insertedTwAccount = null;
        }
    }

    @Test
    @Transactional
    void createTwAccount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TwAccount
        var returnedTwAccount = om.readValue(
            restTwAccountMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(twAccount)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TwAccount.class
        );

        // Validate the TwAccount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTwAccountUpdatableFieldsEquals(returnedTwAccount, getPersistedTwAccount(returnedTwAccount));

        insertedTwAccount = returnedTwAccount;
    }

    @Test
    @Transactional
    void createTwAccountWithExistingId() throws Exception {
        // Create the TwAccount with an existing ID
        twAccount.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTwAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(twAccount)))
            .andExpect(status().isBadRequest());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        twAccount.setUsername(null);

        // Create the TwAccount, which fails.

        restTwAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(twAccount)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        twAccount.setPassword(null);

        // Create the TwAccount, which fails.

        restTwAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(twAccount)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTwAccounts() throws Exception {
        // Initialize the database
        insertedTwAccount = twAccountRepository.saveAndFlush(twAccount);

        // Get all the twAccountList
        restTwAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(twAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getTwAccount() throws Exception {
        // Initialize the database
        insertedTwAccount = twAccountRepository.saveAndFlush(twAccount);

        // Get the twAccount
        restTwAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, twAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(twAccount.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingTwAccount() throws Exception {
        // Get the twAccount
        restTwAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTwAccount() throws Exception {
        // Initialize the database
        insertedTwAccount = twAccountRepository.saveAndFlush(twAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the twAccount
        TwAccount updatedTwAccount = twAccountRepository.findById(twAccount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTwAccount are not directly saved in db
        em.detach(updatedTwAccount);
        updatedTwAccount.username(UPDATED_USERNAME).password(UPDATED_PASSWORD).active(UPDATED_ACTIVE);

        restTwAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTwAccount.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTwAccount))
            )
            .andExpect(status().isOk());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTwAccountToMatchAllProperties(updatedTwAccount);
    }

    @Test
    @Transactional
    void putNonExistingTwAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        twAccount.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTwAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, twAccount.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(twAccount))
            )
            .andExpect(status().isBadRequest());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTwAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        twAccount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTwAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(twAccount))
            )
            .andExpect(status().isBadRequest());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTwAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        twAccount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTwAccountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(twAccount)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTwAccountWithPatch() throws Exception {
        // Initialize the database
        insertedTwAccount = twAccountRepository.saveAndFlush(twAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the twAccount using partial update
        TwAccount partialUpdatedTwAccount = new TwAccount();
        partialUpdatedTwAccount.setId(twAccount.getId());

        restTwAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTwAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTwAccount))
            )
            .andExpect(status().isOk());

        // Validate the TwAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTwAccountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTwAccount, twAccount),
            getPersistedTwAccount(twAccount)
        );
    }

    @Test
    @Transactional
    void fullUpdateTwAccountWithPatch() throws Exception {
        // Initialize the database
        insertedTwAccount = twAccountRepository.saveAndFlush(twAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the twAccount using partial update
        TwAccount partialUpdatedTwAccount = new TwAccount();
        partialUpdatedTwAccount.setId(twAccount.getId());

        partialUpdatedTwAccount.username(UPDATED_USERNAME).password(UPDATED_PASSWORD).active(UPDATED_ACTIVE);

        restTwAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTwAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTwAccount))
            )
            .andExpect(status().isOk());

        // Validate the TwAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTwAccountUpdatableFieldsEquals(partialUpdatedTwAccount, getPersistedTwAccount(partialUpdatedTwAccount));
    }

    @Test
    @Transactional
    void patchNonExistingTwAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        twAccount.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTwAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, twAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(twAccount))
            )
            .andExpect(status().isBadRequest());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTwAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        twAccount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTwAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(twAccount))
            )
            .andExpect(status().isBadRequest());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTwAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        twAccount.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTwAccountMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(twAccount)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TwAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTwAccount() throws Exception {
        // Initialize the database
        insertedTwAccount = twAccountRepository.saveAndFlush(twAccount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the twAccount
        restTwAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, twAccount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return twAccountRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TwAccount getPersistedTwAccount(TwAccount twAccount) {
        return twAccountRepository.findById(twAccount.getId()).orElseThrow();
    }

    protected void assertPersistedTwAccountToMatchAllProperties(TwAccount expectedTwAccount) {
        assertTwAccountAllPropertiesEquals(expectedTwAccount, getPersistedTwAccount(expectedTwAccount));
    }

    protected void assertPersistedTwAccountToMatchUpdatableProperties(TwAccount expectedTwAccount) {
        assertTwAccountAllUpdatablePropertiesEquals(expectedTwAccount, getPersistedTwAccount(expectedTwAccount));
    }
}
