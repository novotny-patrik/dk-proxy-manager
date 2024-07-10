package cz.tw.proxymanager.repository;

import cz.tw.proxymanager.domain.Proxy;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Proxy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProxyRepository extends JpaRepository<Proxy, Long> {}
