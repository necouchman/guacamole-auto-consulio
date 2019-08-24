# Guacamole Consul.IO Authentication Extension
This is an authentication extension for Guacmaole that connects to a
Consul.IO server and retrieves connections based on the service information
provided by Consul.IO.

This module is currently extremely basic and should be used with caution.  It
performs no authentication to the Consul.IO service, and does not use SSL or TLS,
relying on the HTTP API for the Consul.IO service to be publicly available.

## Configuration
There are two options that can be configured for this module in the
guacamole.properties file:
* consul-io-hostname - The hostname or IP of the system running the consul.io service.
* consul-io-port - The port on which to find the Consul.IO HTTP API.

## Service Enumeration
This module queries the Consul.IO service for all available services, filtering
out for services with the "guacamole" tag and adds these services to a single
root connection group which will appear on the Guacamole home page.

The module uses the ServiceAddress node for the hostname or IP of the Guacamole
Connection, and the ServicePort node for the port number.  The protocol must be
specified in the ServiceMeta node as "protocol", and any additional connection
parameters can be specified in the ServiceMeta node and will be added to the
Guacamole connection.
