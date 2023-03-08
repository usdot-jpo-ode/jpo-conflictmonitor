# Test requests for Kafka Connect

These requests call the Kafka Connect REST API.  These are for manual testing and development only.

Assumes MongoDB and Kafka Connect are running in Docker.

The `.http` files can be run in VSCode using the [REST Client Extension](https://marketplace.visualstudio.com/items?itemName=humao.rest-client).

The `createSourceConnector.http` request requires copying the .env file containing DOCKER_HOST_IP to this folder.

Reference: [Kafka Connect REST Interface documentation by Confluent](https://docs.confluent.io/platform/current/connect/references/restapi.html)