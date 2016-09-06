package org.wildfly.clustering.web.undertow.security;

import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.InjectedValue;
import org.jboss.msc.value.Value;
import org.wildfly.clustering.web.security.IdentityCacheFactoryBuilderFactoryProvider;
import org.wildfly.clustering.web.security.IdentityCacheFactoryBuilderProvider;
import org.wildfly.extension.undertow.security.sso.IdentityCacheFactoryServiceBuilder;
import org.wildfly.security.http.util.SingleSignOnServerMechanismFactory.IdentityCacheFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class WildFlyIdentityCacheFactoryServiceBuilder implements IdentityCacheFactoryServiceBuilder, Value<IdentityCacheFactory> {

    private final IdentityCacheFactoryBuilderFactoryProvider provider;
    private InjectedValue<IdentityCacheFactory> factoryInjector = new InjectedValue<>();

    public WildFlyIdentityCacheFactoryServiceBuilder() {
        Iterator<IdentityCacheFactoryBuilderFactoryProvider> iterator = ServiceLoader.load(IdentityCacheFactoryBuilderFactoryProvider.class, IdentityCacheFactoryBuilderFactoryProvider.class.getClassLoader()).iterator();
        IdentityCacheFactoryBuilderFactoryProvider provider = null;

        if (iterator.hasNext()) {
            provider = iterator.next();
        }

        this.provider = provider;
    }

    @Override
    public ServiceBuilder<IdentityCacheFactory> build(ServiceTarget serviceTarget, ServiceName name, String mechName) {
        IdentityCacheFactoryBuilderProvider builder = this.provider.createIdentityCacheFactory(mechName);
        builder.build(serviceTarget).install();
        return serviceTarget.addService(name, new ValueService(this)).addDependency(builder.getServiceName(), IdentityCacheFactory.class, factoryInjector)
                .setInitialMode(Mode.ON_DEMAND);
    }

    @Override
    public IdentityCacheFactory getValue() throws IllegalStateException, IllegalArgumentException {
        return this.factoryInjector.getValue();
    }
}
