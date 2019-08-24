/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.guacamole.auth.consulio.user;

import com.google.inject.Inject;
import java.util.Collections;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.AbstractUserContext;
import org.apache.guacamole.net.auth.AuthenticationProvider;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.User;
import org.apache.guacamole.net.auth.permission.ObjectPermissionSet;
import org.apache.guacamole.net.auth.simple.SimpleConnectionGroup;
import org.apache.guacamole.net.auth.simple.SimpleObjectPermissionSet;
import org.apache.guacamole.net.auth.simple.SimpleUser;

/**
 * A user context implementation that provides integration into the Consul.IO
 * service for the purpose of retrieving services from Consul.IO as connections.
 */
public class ConsulIOUserContext extends AbstractUserContext {
    
    /**
     * Identifier of the root connection group.
     */
    public static final String ROOT_CONNECTION_GROUP = "ROOT";
    
    /**
     * The authentication provider that instantiated this user context.
     */
    @Inject
    private AuthenticationProvider authProvider;
    
    /**
     * The user to whom the context belongs.
     */
    private User self;
    
    /**
     * The connection directory associated with this context.
     */
    private Directory<Connection> connections;
    
    /**
     * The root connection group for this user context.
     */
    private ConnectionGroup rootGroup;
    
    /**
     * Initialize the user context with the provided username, setting up the
     * reference to the user and the connection directory.
     * 
     * @param username
     *     The username for this context.
     * 
     * @param connections
     *     A connection directory to associate with this user context.
     * 
     * @throws GuacamoleException
     *     If an error occurs creating the connection directory.
     */
    public void init(String username, Directory<Connection> connections)
            throws GuacamoleException {
        
        // Store connections
        this.connections = connections;
        
        // Create a new root connection group.
        this.rootGroup = new SimpleConnectionGroup(
                ROOT_CONNECTION_GROUP,
                ROOT_CONNECTION_GROUP,
                connections.getIdentifiers(),
                Collections.<String>emptyList()
        );
        
        // Create a new representation of this user, with basic permissions.
        this.self = new SimpleUser(username) {
            
            @Override
            public ObjectPermissionSet getConnectionPermissions() throws GuacamoleException {
                return new SimpleObjectPermissionSet(connections.getIdentifiers());
            }

            @Override
            public ObjectPermissionSet getConnectionGroupPermissions() throws GuacamoleException {
                return new SimpleObjectPermissionSet(Collections.singleton(ROOT_CONNECTION_GROUP));
            }
            
        };
        
    }
    
    @Override
    public AuthenticationProvider getAuthenticationProvider() {
        return authProvider;
    }
    
    @Override
    public User self() {
        return self;
    }
    
    @Override
    public Directory<Connection> getConnectionDirectory() throws GuacamoleException {
        return connections;
    }
    
    @Override
    public ConnectionGroup getRootConnectionGroup() throws GuacamoleException {
        return rootGroup;
    }
    
}
