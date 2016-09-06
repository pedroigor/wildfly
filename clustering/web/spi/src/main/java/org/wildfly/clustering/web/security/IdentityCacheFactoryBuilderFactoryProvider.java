package org.wildfly.clustering.web.security;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public interface IdentityCacheFactoryBuilderFactoryProvider {

    IdentityCacheFactoryBuilderProvider createIdentityCacheFactory(String name);
}
