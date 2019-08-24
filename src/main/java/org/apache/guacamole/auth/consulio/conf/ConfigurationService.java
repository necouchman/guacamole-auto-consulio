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

package org.apache.guacamole.auth.consulio.conf;

import com.google.inject.Inject;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.IntegerGuacamoleProperty;
import org.apache.guacamole.properties.StringGuacamoleProperty;

/**
 *
 * @author nick_couchman
 */
public class ConfigurationService {
    
    /**
     * The Guacamole server environment.
     */
    @Inject
    private Environment environment;
    
    /**
     * The hostname of the Consul.IO server to connect to retrieve services.
     */
    private static final StringGuacamoleProperty CONSUL_IO_HOSTNAME =
            new StringGuacamoleProperty() {
                
        @Override
        public String getName() { return "consul-io-hostname"; }
                
    };
    
    /**
     * The port on which the Consul.IO server is listening for HTTP API
     * requests.
     */
    private static final IntegerGuacamoleProperty CONSUL_IO_PORT =
            new IntegerGuacamoleProperty() {
                
        @Override
        public String getName() { return "consul-io-port"; }
                
    };
    
    /**
     * The base of the services to retrieve from Consul.IO
     */
    private static final StringGuacamoleProperty CONSUL_IO_BASE =
            new StringGuacamoleProperty() {
                
        @Override
        public String getName() { return "consul-io-base"; }
                
    };
    
    /**
     * Retrieve the hostname of the Consul.IO server to connect to, or localhost
     * if none is specified.
     * 
     * @return
     *     The hostname of the Consul.IO server.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public String getConsulHostname() throws GuacamoleException {
        return environment.getProperty(CONSUL_IO_HOSTNAME, "localhost");
    }
    
    /**
     * Retrieve the port number of the Consul.IO HTTP API, or 8500 if it is not
     * specified.
     * 
     * @return
     *     The port of the Consul.IO HTTP API.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public int getConsulPort() throws GuacamoleException {
        return environment.getProperty(CONSUL_IO_PORT, 8500);
    }
    
    /**
     * Retrieve the base of Consul.IO services, or service.consul if it is not
     * configured.
     * 
     * @return
     *     The base of the services in the Consul.IO server.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public String getConsulBase() throws GuacamoleException {
        return environment.getProperty(CONSUL_IO_BASE, "service.consul");
    }
    
}
