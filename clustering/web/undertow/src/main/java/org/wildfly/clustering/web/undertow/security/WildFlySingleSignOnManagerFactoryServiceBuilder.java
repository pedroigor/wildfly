package org.wildfly.clustering.web.undertow.security;

import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.InjectedValue;
import org.jboss.msc.value.Value;
import org.wildfly.clustering.web.security.IdentityCacheFactoryBuilderProvider;
import org.wildfly.clustering.web.security.SingleSignOnManagerFactoryBuilderFactoryProvider;
import org.wildfly.extension.undertow.security.sso.SingleSignOnManagerFactoryServiceBuilder;
import org.wildfly.security.http.util.sso.SingleSignOnSessionFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class WildFlySingleSignOnManagerFactoryServiceBuilder implements SingleSignOnManagerFactoryServiceBuilder, Value<SingleSignOnSessionFactory> {

    private final SingleSignOnManagerFactoryBuilderFactoryProvider provider;
    private InjectedValue<SingleSignOnSessionFactory> factoryInjector = new InjectedValue<>();

    public WildFlySingleSignOnManagerFactoryServiceBuilder() {
        Iterator<SingleSignOnManagerFactoryBuilderFactoryProvider> iterator = ServiceLoader.load(SingleSignOnManagerFactoryBuilderFactoryProvider.class, SingleSignOnManagerFactoryBuilderFactoryProvider.class.getClassLoader()).iterator();
        SingleSignOnManagerFactoryBuilderFactoryProvider provider = null;

        if (iterator.hasNext()) {
            provider = iterator.next();
        }

        this.provider = provider;
    }

    @Override
    public ServiceBuilder<SingleSignOnSessionFactory> build(ServiceTarget serviceTarget, ServiceName name, String mechName, InjectedValue<KeyStore> keyStore, String keyAlias, String keyPassword, InjectedValue<SSLContext> sslContext) {
        IdentityCacheFactoryBuilderProvider builder = this.provider.createIdentityCacheFactory(mechName, keyStore, keyAlias, keyPassword, sslContext);
        builder.build(serviceTarget).install();
        return serviceTarget.addService(name, new ValueService(this)).addDependency(builder.getServiceName(), SingleSignOnSessionFactory.class, factoryInjector)
                .setInitialMode(Mode.ON_DEMAND);
    }

    @Override
    public SingleSignOnSessionFactory getValue() throws IllegalStateException, IllegalArgumentException {
        return this.factoryInjector.getValue();
    }
}
