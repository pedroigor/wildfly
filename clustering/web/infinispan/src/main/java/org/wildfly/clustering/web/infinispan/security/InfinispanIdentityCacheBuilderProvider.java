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
import org.wildfly.security.http.HttpServerRequest;
import org.wildfly.security.http.util.sso.DefaultSingleSignOnSessionFactory;
import org.wildfly.security.http.util.sso.SingleSignOnSession;
import org.wildfly.security.http.util.sso.SingleSignOnSessionFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class InfinispanIdentityCacheBuilderProvider implements IdentityCacheFactoryBuilderProvider, Value<SingleSignOnSessionFactory>, SingleSignOnSessionFactory {

    private static final String DEFAULT_CONTAINER = "elytron";
    private final String name;
    private final InjectedValue<KeyStore> keyStore;
    private final String keyAlias;
    private final String keyPassword;
    private final InjectedValue<SSLContext> sslContext;
    private SingleSignOnSessionFactory singleSignOnSessionFactory;

    private static ServiceName getCacheServiceName(String cacheName) {
        ServiceName baseServiceName = CacheContainerServiceName.CACHE_CONTAINER.getServiceName("elytron").getParent();
        ServiceName serviceName = ServiceName.parse((cacheName != null) ? cacheName : DEFAULT_CONTAINER);
        if (!baseServiceName.isParentOf(serviceName)) {
            serviceName = baseServiceName.append(serviceName);
        }
        return (serviceName.length() < 4) ? serviceName.append(SubGroupServiceNameFactory.DEFAULT_SUB_GROUP) : serviceName;
    }

    private final InjectedValue<Cache> cache = new InjectedValue<>();

    public InfinispanIdentityCacheBuilderProvider(String name, InjectedValue<KeyStore> keyStore, String keyAlias, String keyPassword, InjectedValue<SSLContext> sslContext) {
        this.name = name;
        this.keyStore = keyStore;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;
        this.sslContext = sslContext;
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
    public SingleSignOnSessionFactory getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public SingleSignOnSession findById(String id, HttpServerRequest request) {
        return getSingleSignSessionFactory().findById(id, request);
    }

    @Override
    public SingleSignOnSession create(HttpServerRequest request, String mechanismName) {
        return getSingleSignSessionFactory().create(request, mechanismName);
    }

    @Override
    public void logout(SingleSignOnSession singleSignOnSession) {
        getSingleSignSessionFactory().logout(singleSignOnSession);
    }

    private SingleSignOnSessionFactory getSingleSignSessionFactory() {
        if (singleSignOnSessionFactory == null) {
            singleSignOnSessionFactory = new DefaultSingleSignOnSessionFactory(cache.getValue(), keyStore.getValue(), keyAlias, keyPassword, sslContext.getValue());
        }
        return singleSignOnSessionFactory;
    }
}
