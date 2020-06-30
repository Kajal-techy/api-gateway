API Gateway
----------------
This is a spring boot microservice which is built on top of **Zuul (Spring Cloud)** and is the backend for the frontend.
  * This microservice registers and is discoverable via **Discover-Service (Eureka Server)**.
  * It uses **Zuul** for routing and ribbon to route to the call to the designated micro-service.
  * It has a filter which intercepts the request and checks for the authorization of the passed JWT Token.
  * Authorizes calls based on the JWT token passed from the front-end, by ensuring its validity from Auth-Service
  * **AOP** is used for logging across application
