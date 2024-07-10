package cz.tw.proxymanager.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TwAccountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TwAccount getTwAccountSample1() {
        return new TwAccount().id(1L).username("username1").password("password1");
    }

    public static TwAccount getTwAccountSample2() {
        return new TwAccount().id(2L).username("username2").password("password2");
    }

    public static TwAccount getTwAccountRandomSampleGenerator() {
        return new TwAccount()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString());
    }
}
