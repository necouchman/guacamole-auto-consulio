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

package org.apache.guacamole.auth.consulio;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.consulio.connection.ConsulIOConnectionDirectory;
import org.apache.guacamole.auth.consulio.user.ConsulIOUserContext;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.UserContext;

/**
 * Authentication provider that reads connections from the Consul.IO API.
 */
public class ConsulIOAuthenticationProvider extends AbstractAuthenticationProvider {
    
    /**
     * Injector instance for handling dependency injection.
     */
    private final Injector injector;
    
    /**
     * Set up a new instance of this provider, creating the injector.
     * 
     * @throws GuacamoleException 
     *     If the Guacamole environment cannot be retrieved for the injection
     *     module.
     */
    public ConsulIOAuthenticationProvider() throws GuacamoleException {

        // Set up Guice injector.
        injector = Guice.createInjector(
            new ConsulIOAuthenticationProviderModule(this)
        );
        
    }
    
    @Override
    public String getIdentifier() {
        return "consulio";
    }
    
    @Override
    public UserContext getUserContext(AuthenticatedUser authenticatedUser)
            throws GuacamoleException {
        
        // Initialize the connection directory
        ConsulIOConnectionDirectory connections = injector.getInstance(ConsulIOConnectionDirectory.class);
        connections.init();
        
        // Initialize the user context with the retrieved connections
        ConsulIOUserContext userContext = injector.getInstance(ConsulIOUserContext.class);
        userContext.init(authenticatedUser.getIdentifier(), connections);
        
        return userContext;
        
    }
    
}
