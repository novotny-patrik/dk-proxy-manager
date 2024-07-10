package cz.tw.proxymanager.web.rest;

import static cz.tw.proxymanager.domain.ProxyAsserts.*;
import static cz.tw.proxymanager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.tw.proxymanager.IntegrationTest;
import cz.tw.proxymanager.domain.Proxy;
import cz.tw.proxymanager.repository.ProxyRepository;
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
 * Integration tests for the {@link ProxyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProxyResourceIT {

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_PORT = 1;
    private static final Integer UPDATED_PORT = 2;

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/proxies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProxyRepository proxyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProxyMockMvc;

    private Proxy proxy;

    private Proxy insertedProxy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Proxy createEntity(EntityManager em) {
        Proxy proxy = new Proxy()
            .ipAddress(DEFAULT_IP_ADDRESS)
            .port(DEFAULT_PORT)
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD)
            .active(DEFAULT_ACTIVE);
        return proxy;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Proxy createUpdatedEntity(EntityManager em) {
        Proxy proxy = new Proxy()
            .ipAddress(UPDATED_IP_ADDRESS)
            .port(UPDATED_PORT)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .active(UPDATED_ACTIVE);
        return proxy;
    }

    @BeforeEach
    public void initTest() {
        proxy = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProxy != null) {
            proxyRepository.delete(insertedProxy);
            insertedProxy = null;
        }
    }

    @Test
    @Transactional
    void createProxy() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Proxy
        var returnedProxy = om.readValue(
            restProxyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proxy)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Proxy.class
        );

        // Validate the Proxy in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProxyUpdatableFieldsEquals(returnedProxy, getPersistedProxy(returnedProxy));

        insertedProxy = returnedProxy;
    }

    @Test
    @Transactional
    void createProxyWithExistingId() throws Exception {
        // Create the Proxy with an existing ID
        proxy.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProxyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proxy)))
            .andExpect(status().isBadRequest());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIpAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        proxy.setIpAddress(null);

        // Create the Proxy, which fails.

        restProxyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proxy)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPortIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        proxy.setPort(null);

        // Create the Proxy, which fails.

        restProxyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proxy)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProxies() throws Exception {
        // Initialize the database
        insertedProxy = proxyRepository.saveAndFlush(proxy);

        // Get all the proxyList
        restProxyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proxy.getId().intValue())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].port").value(hasItem(DEFAULT_PORT)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getProxy() throws Exception {
        // Initialize the database
        insertedProxy = proxyRepository.saveAndFlush(proxy);

        // Get the proxy
        restProxyMockMvc
            .perform(get(ENTITY_API_URL_ID, proxy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(proxy.getId().intValue()))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.port").value(DEFAULT_PORT))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingProxy() throws Exception {
        // Get the proxy
        restProxyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProxy() throws Exception {
        // Initialize the database
        insertedProxy = proxyRepository.saveAndFlush(proxy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proxy
        Proxy updatedProxy = proxyRepository.findById(proxy.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProxy are not directly saved in db
        em.detach(updatedProxy);
        updatedProxy
            .ipAddress(UPDATED_IP_ADDRESS)
            .port(UPDATED_PORT)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .active(UPDATED_ACTIVE);

        restProxyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProxy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProxy))
            )
            .andExpect(status().isOk());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProxyToMatchAllProperties(updatedProxy);
    }

    @Test
    @Transactional
    void putNonExistingProxy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proxy.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProxyMockMvc
            .perform(put(ENTITY_API_URL_ID, proxy.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proxy)))
            .andExpect(status().isBadRequest());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProxy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proxy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProxyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proxy))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProxy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proxy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProxyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proxy)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProxyWithPatch() throws Exception {
        // Initialize the database
        insertedProxy = proxyRepository.saveAndFlush(proxy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proxy using partial update
        Proxy partialUpdatedProxy = new Proxy();
        partialUpdatedProxy.setId(proxy.getId());

        partialUpdatedProxy.ipAddress(UPDATED_IP_ADDRESS).port(UPDATED_PORT);

        restProxyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProxy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProxy))
            )
            .andExpect(status().isOk());

        // Validate the Proxy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProxyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProxy, proxy), getPersistedProxy(proxy));
    }

    @Test
    @Transactional
    void fullUpdateProxyWithPatch() throws Exception {
        // Initialize the database
        insertedProxy = proxyRepository.saveAndFlush(proxy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proxy using partial update
        Proxy partialUpdatedProxy = new Proxy();
        partialUpdatedProxy.setId(proxy.getId());

        partialUpdatedProxy
            .ipAddress(UPDATED_IP_ADDRESS)
            .port(UPDATED_PORT)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .active(UPDATED_ACTIVE);

        restProxyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProxy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProxy))
            )
            .andExpect(status().isOk());

        // Validate the Proxy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProxyUpdatableFieldsEquals(partialUpdatedProxy, getPersistedProxy(partialUpdatedProxy));
    }

    @Test
    @Transactional
    void patchNonExistingProxy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proxy.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProxyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, proxy.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(proxy))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProxy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proxy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProxyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proxy))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProxy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proxy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProxyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(proxy)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Proxy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProxy() throws Exception {
        // Initialize the database
        insertedProxy = proxyRepository.saveAndFlush(proxy);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the proxy
        restProxyMockMvc
            .perform(delete(ENTITY_API_URL_ID, proxy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return proxyRepository.count();
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

    protected Proxy getPersistedProxy(Proxy proxy) {
        return proxyRepository.findById(proxy.getId()).orElseThrow();
    }

    protected void assertPersistedProxyToMatchAllProperties(Proxy expectedProxy) {
        assertProxyAllPropertiesEquals(expectedProxy, getPersistedProxy(expectedProxy));
    }

    protected void assertPersistedProxyToMatchUpdatableProperties(Proxy expectedProxy) {
        assertProxyAllUpdatablePropertiesEquals(expectedProxy, getPersistedProxy(expectedProxy));
    }
}
