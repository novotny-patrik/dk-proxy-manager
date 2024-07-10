package cz.tw.proxymanager.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProxyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Proxy getProxySample1() {
        return new Proxy().id(1L).ipAddress("ipAddress1").port(1).username("username1").password("password1");
    }

    public static Proxy getProxySample2() {
        return new Proxy().id(2L).ipAddress("ipAddress2").port(2).username("username2").password("password2");
    }

    public static Proxy getProxyRandomSampleGenerator() {
        return new Proxy()
            .id(longCount.incrementAndGet())
            .ipAddress(UUID.randomUUID().toString())
            .port(intCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString());
    }
}
