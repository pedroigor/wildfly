/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.extension.messaging.activemq;

import java.security.AccessController;

import org.apache.activemq.artemis.api.core.BroadcastEndpointFactory;
import org.apache.activemq.artemis.ra.ConnectionFactoryProperties;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceContainer;

/**
 * Custom resource adapter that returns an appropriate BroadcastEndpointFactory if discovery is configured using JGroups.
 * @author Paul Ferraro
 */
public class ActiveMQResourceAdapter extends org.apache.activemq.artemis.ra.ActiveMQResourceAdapter {
    private static final long serialVersionUID = 170278234232275756L;

    @Override
    protected BroadcastEndpointFactory createBroadcastEndpointFactory(ConnectionFactoryProperties overrideProperties) {
        String channelName = overrideProperties.getJgroupsChannelName() != null ? overrideProperties.getJgroupsChannelName() : getJgroupsChannelName();
        if (channelName != null) {
            String channelRefName = this.getProperties().getJgroupsChannelRefName();

            String[] split = channelRefName.split("/");
            String serverName = split[0];
            String channelFactoryName = split[1];

            ActiveMQServerService service = (ActiveMQServerService) currentServiceContainer().getService(MessagingServices.getActiveMQServiceName(serverName)).getService();
            return new JGroupsBroadcastEndpointFactory(service.getChannelFactory(channelFactoryName), channelName);
        }
        return super.createBroadcastEndpointFactory(overrideProperties);
    }

    private static ServiceContainer currentServiceContainer() {
        return (System.getSecurityManager() == null) ? CurrentServiceContainer.getServiceContainer() : AccessController.doPrivileged(CurrentServiceContainer.GET_ACTION);
    }
}
