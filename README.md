# Ruta System

Ruta System is designed for creation, exchange, storing and management of business documents like Order, Invoice, etc.
Actors of the Ruta System called Parties can upload information about themselves and their products and services
on Central Data Repository (CDR). Other Parties can make search requests to the CDR and discover new products, services 
and Parties that are of interest to their businesses. By following some other Party of the Ruta System, follower Party gets 
notifications and data updates of followed Party.

Parties can make business relationships and thus become Business Partners. Business Partners can create business documents and 
exchange them with their respective Business Partners that way realizing their business tasks. Business documents are stored
locally and are used during open business processes and later can be used for different purposes like statistics, optimization, 
auditing etc.

## Getting started

Architecture of the Ruta System is a client-server architecture where the server is implemented as a Web Service. Ruta System is 
consisted of two sub-projects:
* Service side application which part is a Central Data Repository,
* Client side application used for interaction with the service.
 
 ### Prerequisites
 
 * Java ver. 1.8 or higher.
 * Maven ver. 3.5.0 or higher.
 
 ## Deployment
 
 Service side requires installation of Wildfly Server as an application server and eXist database as a data layer.
 Service application's war file is deployed on the Wildfly Server using Maven build.
 
 Client side application does not need installation of any additional software. Its jar file is generated using Maven build.
 
 ### Service side software
 
 * Wildfly Server ver. 10.1.0.Final or higher
 * eXist-db ver. 3.6.0. or higher
  
 ## Built with
 
* [Wildfly Server](http://wildfly.org/) - Application server
* [Maven](https://maven.apache.org/) - Dependency management
* [eXist-db](http://exist-db.org/) - XML database used for persisting data on both service and client side

## Author

* [Miljan Ušćumlić](https://github.com/miljanuscumlic)

## Acknowledgments

Thanks to all programmers which code was used.
