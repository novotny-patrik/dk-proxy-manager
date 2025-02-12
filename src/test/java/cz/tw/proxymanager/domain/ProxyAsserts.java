package cz.tw.proxymanager.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProxyAllPropertiesEquals(Proxy expected, Proxy actual) {
        assertProxyAutoGeneratedPropertiesEquals(expected, actual);
        assertProxyAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProxyAllUpdatablePropertiesEquals(Proxy expected, Proxy actual) {
        assertProxyUpdatableFieldsEquals(expected, actual);
        assertProxyUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProxyAutoGeneratedPropertiesEquals(Proxy expected, Proxy actual) {
        assertThat(expected)
            .as("Verify Proxy auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProxyUpdatableFieldsEquals(Proxy expected, Proxy actual) {
        assertThat(expected)
            .as("Verify Proxy relevant properties")
            .satisfies(e -> assertThat(e.getIpAddress()).as("check ipAddress").isEqualTo(actual.getIpAddress()))
            .satisfies(e -> assertThat(e.getPort()).as("check port").isEqualTo(actual.getPort()))
            .satisfies(e -> assertThat(e.getUsername()).as("check username").isEqualTo(actual.getUsername()))
            .satisfies(e -> assertThat(e.getPassword()).as("check password").isEqualTo(actual.getPassword()))
            .satisfies(e -> assertThat(e.getActive()).as("check active").isEqualTo(actual.getActive()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProxyUpdatableRelationshipsEquals(Proxy expected, Proxy actual) {}
}
