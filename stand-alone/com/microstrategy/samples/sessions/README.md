# MicroStrategy WebIServerSession Workflows

A WebIServerSession needs to be established to interact with an Intelligence Server. Almost all Web API workflows depend on a valid WebIServerSession.

## Intelligence Server Clusters
For clustered environments, simply connect to any node in the cluster - load balancing is handled by the cluster. This behaviour can be bypassed while configuring a WebIServerSession.