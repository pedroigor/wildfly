package org.wildfly.clustering.web.infinispan.security;

import org.jboss.msc.value.InjectedValue;
import org.wildfly.clustering.web.security.SingleSignOnManagerFactoryBuilderFactoryProvider;
import org.wildfly.clustering.web.security.IdentityCacheFactoryBuilderProvider;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class InfinispanSingleSignOnManagerBuilderFactoryProvider implements SingleSignOnManagerFactoryBuilderFactoryProvider {
    @Override
    public IdentityCacheFactoryBuilderProvider createIdentityCacheFactory(String name, InjectedValue<KeyStore> keyStore, String keyAlias, String keyPassword, InjectedValue<SSLContext> sslContext) {
        return new InfinispanIdentityCacheBuilderProvider(name, keyStore, keyAlias, keyPassword, sslContext);
    }
}
