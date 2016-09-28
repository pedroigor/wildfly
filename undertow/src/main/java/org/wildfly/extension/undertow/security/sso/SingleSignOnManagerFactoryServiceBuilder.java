package org.wildfly.extension.undertow.security.sso;

import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.security.http.util.sso.SingleSignOnSessionFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public interface SingleSignOnManagerFactoryServiceBuilder {
    ServiceBuilder<SingleSignOnSessionFactory> build(ServiceTarget target, ServiceName name, String address, InjectedValue<KeyStore> keyStore, String keyAlias, String keyPassword, InjectedValue<SSLContext> sslContext);
}
