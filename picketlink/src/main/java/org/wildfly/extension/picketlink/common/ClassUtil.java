/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.wildfly.extension.picketlink.common;

import org.jboss.dmr.ModelNode;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;

import static org.wildfly.extension.picketlink.PicketLinkMessages.MESSAGES;

/**
 * @author Pedro Igor
 */
public class ClassUtil {

    @SuppressWarnings("unchecked")
    public static final <T> Class<T> loadClass(ModelNode moduleNode, String typeName, ClassLoader defaultClassLoader) {
        try {
            Module module = getModule(moduleNode);

            if (module != null) {
                return (Class<T>) module.getClassLoader().loadClass(typeName);
            } else {
                return (Class<T>) defaultClassLoader.loadClass(typeName);
            }
        } catch (ClassNotFoundException cnfe) {
            throw MESSAGES.couldNotLoadClass(typeName, cnfe);
        }
    }

    private static Module getModule(ModelNode moduleNode) {
        Module module;

        if (moduleNode.isDefined()) {
            ModuleLoader moduleLoader = Module.getBootModuleLoader();
            try {
                module = moduleLoader.loadModule(ModuleIdentifier.create(moduleNode.asString()));
            } catch (ModuleLoadException e) {
                throw MESSAGES.moduleCouldNotLoad(moduleNode.asString(), e);
            }
        } else {
            // fallback to caller module.
            module = Module.getCallerModule();
        }

        return module;
    }
}
