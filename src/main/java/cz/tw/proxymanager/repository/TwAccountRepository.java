package cz.tw.proxymanager.repository;

import cz.tw.proxymanager.domain.TwAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TwAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TwAccountRepository extends JpaRepository<TwAccount, Long> {}
