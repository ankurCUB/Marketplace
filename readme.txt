The given client server architecture is run as separate java processes.
The MarketPlace class creates the client for sending requests
The four backend components to be implemented are ServerSideSellersInterface, ServerSideBuyersInterface, CustomerDBServer
and ProductDBServer.

The databases used in this setup are sqlite databases.

The project uses gRPC server for product and user dbs. The front end interfaces
are run on spring boot instances and the user instances are run as threads in Marketplace file.
