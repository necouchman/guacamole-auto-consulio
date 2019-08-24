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

import com.google.inject.AbstractModule;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.consulio.conf.ConfigurationService;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.environment.LocalEnvironment;
import org.apache.guacamole.net.auth.AuthenticationProvider;

/**
 * Guice module for Consul.IO-specific injections.
 */
public class ConsulIOAuthenticationProviderModule extends AbstractModule {
    
    /**
     * The Guacamole server environment.
     */
    private final Environment environment;
    
    /**
     * The authentication provider that instantiated this module.
     */
    private final AuthenticationProvider authProvider;
    
    /**
     * Set up a new instance of this injection module for the Consul.IO
     * authentication provider.
     * 
     * @param authProvider
     *     The AuthenticationProvider for which injection is being configured.
     * 
     * @throws GuacamoleException 
     *     If an error occurs retrieving the server environment.
     */
    public ConsulIOAuthenticationProviderModule(
            AuthenticationProvider authProvider) throws GuacamoleException {
        
        // Get the local server environment.
        this.environment = new LocalEnvironment();
        
        // Set the authentication provder that instantiated this.
        this.authProvider = authProvider;
    }
    
    @Override
    protected void configure() {
        
        // Bind core implementations of guacamole-ext classes
        bind(AuthenticationProvider.class).toInstance(authProvider);
        bind(Environment.class).toInstance(environment);
        
        // Bind Consul.IO-specific services
        bind(ConfigurationService.class);        
    }
    
}
