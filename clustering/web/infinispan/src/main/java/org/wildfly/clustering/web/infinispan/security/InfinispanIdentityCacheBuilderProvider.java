package org.wildfly.clustering.web.infinispan.security;

import org.infinispan.Cache;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.InjectedValue;
import org.jboss.msc.value.Value;
import org.wildfly.clustering.group.NodeFactory;
import org.wildfly.clustering.infinispan.spi.service.CacheBuilder;
import org.wildfly.clustering.infinispan.spi.service.CacheContainerServiceName;
import org.wildfly.clustering.infinispan.spi.service.CacheServiceName;
import org.wildfly.clustering.infinispan.spi.service.TemplateConfigurationBuilder;
import org.wildfly.clustering.registry.Registry;
import org.wildfly.clustering.service.AliasServiceBuilder;
import org.wildfly.clustering.service.SubGroupServiceNameFactory;
import org.wildfly.clustering.spi.CacheGroupServiceName;
import org.wildfly.clustering.web.infinispan.session.InfinispanRouteLocatorBuilder;
import org.wildfly.clustering.web.security.IdentityCacheFactoryBuilderProvider;
import org.wildfly.security.http.util.SingleSignOnServerMechanismFactory.IdentityCacheFactory;

import java.util.Map;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class InfinispanIdentityCacheBuilderProvider implements IdentityCacheFactoryBuilderProvider, Value<IdentityCacheFactory>, IdentityCacheFactory {

    private static final String DEFAULT_CONTAINER = "elytron";
    private final String name;

    private static ServiceName getCacheServiceName(String cacheName) {
        ServiceName baseServiceName = CacheContainerServiceName.CACHE_CONTAINER.getServiceName("elytron").getParent();
        ServiceName serviceName = ServiceName.parse((cacheName != null) ? cacheName : DEFAULT_CONTAINER);
        if (!baseServiceName.isParentOf(serviceName)) {
            serviceName = baseServiceName.append(serviceName);
        }
        return (serviceName.length() < 4) ? serviceName.append(SubGroupServiceNameFactory.DEFAULT_SUB_GROUP) : serviceName;
    }

    private final InjectedValue<Cache> cache = new InjectedValue<>();

    public InfinispanIdentityCacheBuilderProvider(String name) {
        this.name = name;
    }

    @Override
    public ServiceName getServiceName() {
        return ServiceName.JBOSS.append("clustering", "web", name);
    }

    @Override
    public ServiceBuilder build(ServiceTarget target) {
        // TODO: allow users to provide an alternative cache
        ServiceName templateCacheServiceName = getCacheServiceName(null);
        String templateCacheName = templateCacheServiceName.getSimpleName();
        String containerName = templateCacheServiceName.getParent().getSimpleName();
        String cacheName = name;

        new TemplateConfigurationBuilder(containerName, cacheName, templateCacheName).build(target).install();

        new CacheBuilder<>(containerName, cacheName).build(target)
                .addAliases(InfinispanRouteLocatorBuilder.getCacheServiceAlias(cacheName))
                .install();

        new AliasServiceBuilder<>(InfinispanRouteLocatorBuilder.getNodeFactoryServiceAlias(cacheName), CacheGroupServiceName.NODE_FACTORY.getServiceName(containerName, "routing"), NodeFactory.class).build(target).install();
        new AliasServiceBuilder<>(InfinispanRouteLocatorBuilder.getRegistryServiceAlias(cacheName), CacheGroupServiceName.REGISTRY.getServiceName(containerName, "routing"), Registry.class).build(target).install();

        return target.addService(this.getServiceName(), new ValueService(this))
                .addDependency(CacheServiceName.CACHE.getServiceName(containerName, cacheName), Cache.class, this.cache)
                .setInitialMode(ServiceController.Mode.ON_DEMAND);
    }

    @Override
    public IdentityCacheFactory getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public Map<String, Object> create() {
        return cache.getValue();
    }
}
