package org.wildfly.clustering.web.security;

import org.jboss.msc.value.InjectedValue;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public interface SingleSignOnManagerFactoryBuilderFactoryProvider {

    IdentityCacheFactoryBuilderProvider createIdentityCacheFactory(String name, InjectedValue<KeyStore> keyStore, String keyAlias, String keyPassword, InjectedValue<SSLContext> sslContext);
}
