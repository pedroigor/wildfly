package org.wildfly.extension.undertow.security.sso;

import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.wildfly.security.http.util.SingleSignOnServerMechanismFactory.IdentityCacheFactory;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public interface IdentityCacheFactoryServiceBuilder {
    ServiceBuilder<IdentityCacheFactory> build(ServiceTarget target, ServiceName name, String address);
}
