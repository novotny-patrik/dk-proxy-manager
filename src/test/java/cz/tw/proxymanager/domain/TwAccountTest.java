package cz.tw.proxymanager.domain;

import static cz.tw.proxymanager.domain.ProxyTestSamples.*;
import static cz.tw.proxymanager.domain.TwAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import cz.tw.proxymanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TwAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TwAccount.class);
        TwAccount twAccount1 = getTwAccountSample1();
        TwAccount twAccount2 = new TwAccount();
        assertThat(twAccount1).isNotEqualTo(twAccount2);

        twAccount2.setId(twAccount1.getId());
        assertThat(twAccount1).isEqualTo(twAccount2);

        twAccount2 = getTwAccountSample2();
        assertThat(twAccount1).isNotEqualTo(twAccount2);
    }

    @Test
    void proxyTest() {
        TwAccount twAccount = getTwAccountRandomSampleGenerator();
        Proxy proxyBack = getProxyRandomSampleGenerator();

        twAccount.setProxy(proxyBack);
        assertThat(twAccount.getProxy()).isEqualTo(proxyBack);

        twAccount.proxy(null);
        assertThat(twAccount.getProxy()).isNull();
    }
}
