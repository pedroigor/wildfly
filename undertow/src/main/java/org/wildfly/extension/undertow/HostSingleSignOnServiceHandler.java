/*
 *
 *  JBoss, Home of Professional Open Source.
 *  Copyright 2014, Red Hat, Inc., and individual contributors
 *  as indicated by the @author tags. See the copyright.txt file in the
 *  distribution for a full listing of individual contributors.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * /
 */

package org.wildfly.extension.undertow;

import static org.wildfly.extension.undertow.SingleSignOnDefinition.Attribute.*;

import org.jboss.as.clustering.controller.ResourceServiceHandler;
import org.jboss.as.clustering.dmr.ModelNodes;

import io.undertow.security.impl.InMemorySingleSignOnManager;
import io.undertow.security.impl.SingleSignOnManager;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.ImmediateValue;
import org.wildfly.extension.undertow.security.sso.DistributableHostSingleSignOnManagerBuilder;

/**
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a> (c) 2014 Red Hat Inc.
 * @author Paul Ferraro
 */
class HostSingleSignOnServiceHandler implements ResourceServiceHandler {

    @Override
    public void installServices(OperationContext context, ModelNode model) throws OperationFailedException {
        PathAddress address = context.getCurrentAddress();
        PathAddress hostAddress = address.getParent();
        PathAddress serverAddress = hostAddress.getParent();
        String hostName = hostAddress.getLastElement().getValue();
        String serverName = serverAddress.getLastElement().getValue();

        String domain = ModelNodes.optionalString(DOMAIN.resolveModelAttribute(context, model)).orElse(null);
        String path = PATH.resolveModelAttribute(context, model).asString();
        boolean secure = SECURE.resolveModelAttribute(context, model).asBoolean();
        boolean httpOnly = HTTP_ONLY.resolveModelAttribute(context, model).asBoolean();
        String cookieName = COOKIE_NAME.resolveModelAttribute(context, model).asString();

        ServiceName serviceName = UndertowService.ssoServiceName(serverName, hostName);
        ServiceName virtualHostServiceName = UndertowService.virtualHostName(serverName, hostName);

        ServiceTarget target = context.getServiceTarget();

        ServiceName managerServiceName = serviceName.append("manager");
        if (DistributableHostSingleSignOnManagerBuilder.INSTANCE.isPresent()) {
            DistributableHostSingleSignOnManagerBuilder builder = DistributableHostSingleSignOnManagerBuilder.INSTANCE.get();
            builder.build(target, managerServiceName, context.getCapabilityServiceSupport(), serverName, hostName).setInitialMode(ServiceController.Mode.ON_DEMAND).install();
        } else {
            target.addService(managerServiceName, new ValueService<>(new ImmediateValue<>(new InMemorySingleSignOnManager()))).setInitialMode(ServiceController.Mode.ON_DEMAND).install();
        }

        SingleSignOnService service = new SingleSignOnService(domain, path, httpOnly, secure, cookieName);
        target.addService(serviceName, service)
                .addDependency(virtualHostServiceName, Host.class, service.getHost())
                .addDependency(managerServiceName, SingleSignOnManager.class, service.getSingleSignOnSessionManager())
                .setInitialMode(ServiceController.Mode.ACTIVE)
                .install();
    }

    @Override
    public void removeServices(OperationContext context, ModelNode model) throws OperationFailedException {
        PathAddress address = context.getCurrentAddress();
        PathAddress hostAddress = address.getParent();
        PathAddress serverAddress = hostAddress.getParent();
        String hostName = hostAddress.getLastElement().getValue();
        String serverName = serverAddress.getLastElement().getValue();

        ServiceName serviceName = UndertowService.ssoServiceName(serverName, hostName);

        context.removeService(serviceName);
        context.removeService(serviceName.append("manager"));
    }
}