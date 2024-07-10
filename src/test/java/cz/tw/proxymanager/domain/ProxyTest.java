package cz.tw.proxymanager.domain;

import static cz.tw.proxymanager.domain.ProxyTestSamples.*;
import static cz.tw.proxymanager.domain.TwAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import cz.tw.proxymanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProxyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Proxy.class);
        Proxy proxy1 = getProxySample1();
        Proxy proxy2 = new Proxy();
        assertThat(proxy1).isNotEqualTo(proxy2);

        proxy2.setId(proxy1.getId());
        assertThat(proxy1).isEqualTo(proxy2);

        proxy2 = getProxySample2();
        assertThat(proxy1).isNotEqualTo(proxy2);
    }

    @Test
    void twAccountTest() {
        Proxy proxy = getProxyRandomSampleGenerator();
        TwAccount twAccountBack = getTwAccountRandomSampleGenerator();

        proxy.setTwAccount(twAccountBack);
        assertThat(proxy.getTwAccount()).isEqualTo(twAccountBack);
        assertThat(twAccountBack.getProxy()).isEqualTo(proxy);

        proxy.twAccount(null);
        assertThat(proxy.getTwAccount()).isNull();
        assertThat(twAccountBack.getProxy()).isNull();
    }
}
