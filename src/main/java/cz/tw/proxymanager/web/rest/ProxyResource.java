package cz.tw.proxymanager.web.rest;

import cz.tw.proxymanager.domain.Proxy;
import cz.tw.proxymanager.repository.ProxyRepository;
import cz.tw.proxymanager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link cz.tw.proxymanager.domain.Proxy}.
 */
@RestController
@RequestMapping("/api/proxies")
@Transactional
public class ProxyResource {

    private static final Logger log = LoggerFactory.getLogger(ProxyResource.class);

    private static final String ENTITY_NAME = "proxy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProxyRepository proxyRepository;

    public ProxyResource(ProxyRepository proxyRepository) {
        this.proxyRepository = proxyRepository;
    }

    /**
     * {@code POST  /proxies} : Create a new proxy.
     *
     * @param proxy the proxy to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new proxy, or with status {@code 400 (Bad Request)} if the proxy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Proxy> createProxy(@Valid @RequestBody Proxy proxy) throws URISyntaxException {
        log.debug("REST request to save Proxy : {}", proxy);
        if (proxy.getId() != null) {
            throw new BadRequestAlertException("A new proxy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        proxy = proxyRepository.save(proxy);
        return ResponseEntity.created(new URI("/api/proxies/" + proxy.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, proxy.getId().toString()))
            .body(proxy);
    }

    /**
     * {@code PUT  /proxies/:id} : Updates an existing proxy.
     *
     * @param id the id of the proxy to save.
     * @param proxy the proxy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proxy,
     * or with status {@code 400 (Bad Request)} if the proxy is not valid,
     * or with status {@code 500 (Internal Server Error)} if the proxy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Proxy> updateProxy(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Proxy proxy)
        throws URISyntaxException {
        log.debug("REST request to update Proxy : {}, {}", id, proxy);
        if (proxy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proxy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proxyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        proxy = proxyRepository.save(proxy);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proxy.getId().toString()))
            .body(proxy);
    }

    /**
     * {@code PATCH  /proxies/:id} : Partial updates given fields of an existing proxy, field will ignore if it is null
     *
     * @param id the id of the proxy to save.
     * @param proxy the proxy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proxy,
     * or with status {@code 400 (Bad Request)} if the proxy is not valid,
     * or with status {@code 404 (Not Found)} if the proxy is not found,
     * or with status {@code 500 (Internal Server Error)} if the proxy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Proxy> partialUpdateProxy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Proxy proxy
    ) throws URISyntaxException {
        log.debug("REST request to partial update Proxy partially : {}, {}", id, proxy);
        if (proxy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proxy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proxyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Proxy> result = proxyRepository
            .findById(proxy.getId())
            .map(existingProxy -> {
                if (proxy.getIpAddress() != null) {
                    existingProxy.setIpAddress(proxy.getIpAddress());
                }
                if (proxy.getPort() != null) {
                    existingProxy.setPort(proxy.getPort());
                }
                if (proxy.getUsername() != null) {
                    existingProxy.setUsername(proxy.getUsername());
                }
                if (proxy.getPassword() != null) {
                    existingProxy.setPassword(proxy.getPassword());
                }
                if (proxy.getActive() != null) {
                    existingProxy.setActive(proxy.getActive());
                }

                return existingProxy;
            })
            .map(proxyRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proxy.getId().toString())
        );
    }

    /**
     * {@code GET  /proxies} : get all the proxies.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of proxies in body.
     */
    @GetMapping("")
    public List<Proxy> getAllProxies(@RequestParam(name = "filter", required = false) String filter) {
        if ("twaccount-is-null".equals(filter)) {
            log.debug("REST request to get all Proxys where twAccount is null");
            return StreamSupport.stream(proxyRepository.findAll().spliterator(), false)
                .filter(proxy -> proxy.getTwAccount() == null)
                .toList();
        }
        log.debug("REST request to get all Proxies");
        return proxyRepository.findAll();
    }

    /**
     * {@code GET  /proxies/:id} : get the "id" proxy.
     *
     * @param id the id of the proxy to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the proxy, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Proxy> getProxy(@PathVariable("id") Long id) {
        log.debug("REST request to get Proxy : {}", id);
        Optional<Proxy> proxy = proxyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(proxy);
    }

    /**
     * {@code DELETE  /proxies/:id} : delete the "id" proxy.
     *
     * @param id the id of the proxy to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProxy(@PathVariable("id") Long id) {
        log.debug("REST request to delete Proxy : {}", id);
        proxyRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
