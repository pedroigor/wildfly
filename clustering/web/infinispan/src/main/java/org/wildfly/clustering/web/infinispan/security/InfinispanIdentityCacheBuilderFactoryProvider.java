package org.wildfly.clustering.web.infinispan.security;

import org.wildfly.clustering.web.security.IdentityCacheFactoryBuilderFactoryProvider;
import org.wildfly.clustering.web.security.IdentityCacheFactoryBuilderProvider;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class InfinispanIdentityCacheBuilderFactoryProvider implements IdentityCacheFactoryBuilderFactoryProvider {
    @Override
    public IdentityCacheFactoryBuilderProvider createIdentityCacheFactory(String name) {
        return new InfinispanIdentityCacheBuilderProvider(name);
    }
}
