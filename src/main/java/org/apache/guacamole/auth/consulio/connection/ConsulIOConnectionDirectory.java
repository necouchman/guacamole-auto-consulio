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

package org.apache.guacamole.auth.consulio.connection;

import com.ecwid.consul.transport.TransportException;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.catalog.CatalogServiceRequest;
import com.ecwid.consul.v1.catalog.CatalogServicesRequest;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.auth.consulio.conf.ConfigurationService;
import org.apache.guacamole.auth.consulio.user.ConsulIOUserContext;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.simple.SimpleConnection;
import org.apache.guacamole.net.auth.simple.SimpleDirectory;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a connection directory that retrieves data from the
 * Consul.IO service.
 */
public class ConsulIOConnectionDirectory extends SimpleDirectory<Connection> {
    
    /**
     * Logger for this class.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(ConsulIOConnectionDirectory.class);
    
    /**
     * The configuration service for this module.
     */
    @Inject
    private ConfigurationService confService;
    
    /**
     * The Consul.IO client.
     */
    private ConsulClient consulClient;
    
    /**
     * Map to store the connections
     */
    private final Map<String, Connection> connections = new HashMap<>();
    
    /**
     * Create a new instance of this directory, configuring the Consul.IO
     * client using the configuration parameters provided in the
     * guacamole.properties file through the configuration service.
     * 
     * @throws GuacamoleException 
     *     If an error occurs parsing guacamole.properties or connecting
     *     to the Consul.IO service.
     */
    public void init() throws GuacamoleException {
        try {
            
            // Configure the Consul.IO client
            this.consulClient = new ConsulClient(confService.getConsulHostname(),
                    confService.getConsulPort());
            
            // Get all of the Guacamole-tagged services
            List<CatalogService> services = getServices();
            
            // Add each of the services as a connection
            for (CatalogService svc : services) {
                String identifier = svc.getServiceName();
                Connection svcConnection = new SimpleConnection(identifier, identifier, svcToConfig(svc));
                svcConnection.setParentIdentifier(ConsulIOUserContext.ROOT_CONNECTION_GROUP);
                connections.put(identifier, svcConnection);
            }
            
        }
        catch (TransportException e) {
            logger.error("Unable to connect to Consul.IO service: {}", e.getMessage());
            logger.debug("Exception while connecting to Consul.IO.", e);
            throw new GuacamoleServerException(
                    "Unable to connect to Consul.IO service.", e);
        }
    }
    
    /**
     * Convert a CatalogService item provided by Consul.IO to a
     * GuacamoleConfiguration, provided that the required information is
     * present in the service definition.
     * 
     * @param service
     *     The catalogService item to convert.
     * 
     * @return 
     *     A GuacamoleConfiguration object that can be used in a Guacamole
     *     connection.
     */
    private GuacamoleConfiguration svcToConfig(CatalogService service) {
        
        // Extract require information from service
        String address = service.getServiceAddress();
        int port = service.getServicePort();
        Map<String, String> parameters = service.getServiceMeta();
        String protocol = parameters.remove("protocol");
        
        // Check that minimum connection data is present
        if (address == null || address.isEmpty()) {
            logger.warn("Hostname or address not specified.");
            return null;
        }
        
        if ( port < 1 || port > 65535 ) {
            logger.warn("Port number not specified or invalid.");
            return null;
        }
        
        if (protocol == null || protocol.isEmpty()) {
            logger.warn("Protocol not specified.");
            return null;
        }
        
        // Generate the parameters and configuration
        parameters.put("hostname", address);
        parameters.put("port", Integer.toString(port));
        GuacamoleConfiguration config = new GuacamoleConfiguration();
        config.setProtocol(protocol);
        config.setParameters(parameters);
        
        return config;
        
    }
    
    /**
     * Connects to the Consul.IO service and gets a list of all of the available
     * services, returning the list of services tagged for Guacamole.
     * 
     * @return 
     *     A list of CatalogService items that have been tagged for Guacamole.
     */
    private List<CatalogService> getServices() {
        
        // Retrieve all services
        CatalogServicesRequest svcsRequest =
                CatalogServicesRequest.newBuilder().build();
        Map<String, List<String>> getServices =
                consulClient.getCatalogServices(svcsRequest).getValue();
        
        // Loop through and generate a service list
        ArrayList<CatalogService> svcList = new ArrayList<>();
        for (String service : getServices.keySet()) {
            
            // Get services if they're tagged for guacamole
            CatalogServiceRequest svcRequest = CatalogServiceRequest
                    .newBuilder()
                    .setTag("guacamole")
                    .build();
            svcList.addAll(consulClient
                    .getCatalogService(service, svcRequest)
                    .getValue());
            
        }
        
        return svcList;
        
    }
    
    @Override
    public Set<String> getIdentifiers() {
        return connections.keySet();
    }
    
    @Override
    protected Map<String, Connection> getObjects() {
        return connections;
    }
    
    @Override
    public Connection get(String identifier) throws GuacamoleException {
        return connections.get(identifier);
    }
    
    @Override
    public Collection<Connection> getAll(Collection<String> identifiers)
            throws GuacamoleException {
        
        Collection<Connection> foundConnections = new ArrayList<>();
        
        for (String id : identifiers) {
            Connection connection = connections.get(id);
            if (connection != null)
                foundConnections.add(connection);
        }
        
        return foundConnections;
        
    }
    
}
