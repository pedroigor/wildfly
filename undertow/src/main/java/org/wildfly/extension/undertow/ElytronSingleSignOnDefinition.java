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

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.operations.validation.StringLengthValidator;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a>  2014 Red Hat Inc.
 * @author Paul Ferraro
 */
class ElytronSingleSignOnDefinition extends PersistentResourceDefinition {

    static final SimpleAttributeDefinition DOMAIN = new SimpleAttributeDefinitionBuilder(Constants.DOMAIN, ModelType.STRING, true)
            .setDefaultValue(new ModelNode("localhost"))
            .setAllowNull(true)
            .setAllowExpression(true)
            .build();
    static final SimpleAttributeDefinition PATH = new SimpleAttributeDefinitionBuilder("path", ModelType.STRING, true)
            .setAllowNull(true)
            .setAllowExpression(true)
            .setDefaultValue(new ModelNode("/"))
            .build();
    static final SimpleAttributeDefinition HTTP_ONLY = new SimpleAttributeDefinitionBuilder("http-only", ModelType.BOOLEAN, true)
            .setAllowNull(true)
            .setAllowExpression(true)
            .setDefaultValue(new ModelNode(false))
            .build();

    static final SimpleAttributeDefinition SECURE = new SimpleAttributeDefinitionBuilder("secure", ModelType.BOOLEAN, true)
            .setAllowNull(true)
            .setAllowExpression(true)
            .setDefaultValue(new ModelNode(false))
            .build();

    static final SimpleAttributeDefinition COOKIE_NAME = new SimpleAttributeDefinitionBuilder("cookie-name", ModelType.STRING, true)
            .setAllowNull(true)
            .setAllowExpression(true)
            .setDefaultValue(new ModelNode("JSESSIONIDSSO"))
            .build();

    static final SimpleAttributeDefinition KEY_STORE = new SimpleAttributeDefinitionBuilder("key-store", ModelType.STRING, false)
            .setAllowNull(false)
            .setAllowExpression(true)
            .build();

    static final SimpleAttributeDefinition KEY_ALIAS = new SimpleAttributeDefinitionBuilder("key-alias", ModelType.STRING, false)
            .setAllowNull(false)
            .setAllowExpression(true)
            .build();

    static final SimpleAttributeDefinition KEY_PASSWORD = new SimpleAttributeDefinitionBuilder("key-password", ModelType.STRING, false)
            .setAllowNull(false)
            .setAllowExpression(true)
            .build();

    static final SimpleAttributeDefinition SSL_CONTEXT = new SimpleAttributeDefinitionBuilder("client-ssl-context", ModelType.STRING, true)
            .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
            .setValidator(new StringLengthValidator(1))
            .build();

    static final List<AttributeDefinition> ATTRIBUTES = Arrays.<AttributeDefinition>asList(DOMAIN, PATH, HTTP_ONLY, SECURE, COOKIE_NAME, KEY_STORE, KEY_ALIAS, KEY_PASSWORD, SSL_CONTEXT);

    static final ElytronSingleSignOnDefinition INSTANCE = new ElytronSingleSignOnDefinition();

    private ElytronSingleSignOnDefinition() {
        super(PathElement.pathElement(Constants.SINGLE_SIGN_ON),
                UndertowExtension.getResolver(Constants.SINGLE_SIGN_ON),
                new ReloadRequiredAddStepHandler(ATTRIBUTES),
                ReloadRequiredRemoveStepHandler.INSTANCE);
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return ATTRIBUTES;
    }
}
