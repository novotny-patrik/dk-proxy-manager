package cz.tw.proxymanager.web.rest;

import cz.tw.proxymanager.domain.TwAccount;
import cz.tw.proxymanager.repository.TwAccountRepository;
import cz.tw.proxymanager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link cz.tw.proxymanager.domain.TwAccount}.
 */
@RestController
@RequestMapping("/api/tw-accounts")
@Transactional
public class TwAccountResource {

    private static final Logger log = LoggerFactory.getLogger(TwAccountResource.class);

    private static final String ENTITY_NAME = "twAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TwAccountRepository twAccountRepository;

    public TwAccountResource(TwAccountRepository twAccountRepository) {
        this.twAccountRepository = twAccountRepository;
    }

    /**
     * {@code POST  /tw-accounts} : Create a new twAccount.
     *
     * @param twAccount the twAccount to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new twAccount, or with status {@code 400 (Bad Request)} if the twAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TwAccount> createTwAccount(@Valid @RequestBody TwAccount twAccount) throws URISyntaxException {
        log.debug("REST request to save TwAccount : {}", twAccount);
        if (twAccount.getId() != null) {
            throw new BadRequestAlertException("A new twAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        twAccount = twAccountRepository.save(twAccount);
        return ResponseEntity.created(new URI("/api/tw-accounts/" + twAccount.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, twAccount.getId().toString()))
            .body(twAccount);
    }

    /**
     * {@code PUT  /tw-accounts/:id} : Updates an existing twAccount.
     *
     * @param id the id of the twAccount to save.
     * @param twAccount the twAccount to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated twAccount,
     * or with status {@code 400 (Bad Request)} if the twAccount is not valid,
     * or with status {@code 500 (Internal Server Error)} if the twAccount couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TwAccount> updateTwAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TwAccount twAccount
    ) throws URISyntaxException {
        log.debug("REST request to update TwAccount : {}, {}", id, twAccount);
        if (twAccount.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, twAccount.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!twAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        twAccount = twAccountRepository.save(twAccount);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, twAccount.getId().toString()))
            .body(twAccount);
    }

    /**
     * {@code PATCH  /tw-accounts/:id} : Partial updates given fields of an existing twAccount, field will ignore if it is null
     *
     * @param id the id of the twAccount to save.
     * @param twAccount the twAccount to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated twAccount,
     * or with status {@code 400 (Bad Request)} if the twAccount is not valid,
     * or with status {@code 404 (Not Found)} if the twAccount is not found,
     * or with status {@code 500 (Internal Server Error)} if the twAccount couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TwAccount> partialUpdateTwAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TwAccount twAccount
    ) throws URISyntaxException {
        log.debug("REST request to partial update TwAccount partially : {}, {}", id, twAccount);
        if (twAccount.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, twAccount.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!twAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TwAccount> result = twAccountRepository
            .findById(twAccount.getId())
            .map(existingTwAccount -> {
                if (twAccount.getUsername() != null) {
                    existingTwAccount.setUsername(twAccount.getUsername());
                }
                if (twAccount.getPassword() != null) {
                    existingTwAccount.setPassword(twAccount.getPassword());
                }
                if (twAccount.getActive() != null) {
                    existingTwAccount.setActive(twAccount.getActive());
                }

                return existingTwAccount;
            })
            .map(twAccountRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, twAccount.getId().toString())
        );
    }

    /**
     * {@code GET  /tw-accounts} : get all the twAccounts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of twAccounts in body.
     */
    @GetMapping("")
    public List<TwAccount> getAllTwAccounts() {
        log.debug("REST request to get all TwAccounts");
        return twAccountRepository.findAll();
    }

    /**
     * {@code GET  /tw-accounts/:id} : get the "id" twAccount.
     *
     * @param id the id of the twAccount to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the twAccount, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TwAccount> getTwAccount(@PathVariable("id") Long id) {
        log.debug("REST request to get TwAccount : {}", id);
        Optional<TwAccount> twAccount = twAccountRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(twAccount);
    }

    /**
     * {@code DELETE  /tw-accounts/:id} : delete the "id" twAccount.
     *
     * @param id the id of the twAccount to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTwAccount(@PathVariable("id") Long id) {
        log.debug("REST request to delete TwAccount : {}", id);
        twAccountRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
