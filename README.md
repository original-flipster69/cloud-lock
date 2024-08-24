# cloud-lock

<img align="right" width="128px" src="https://github.com/original-flipster69/cloud-lock/blob/9c50c4b458c3e433f13b88e06436d8d2ab19cb3e/logo.webp">

![GitHub License](https://img.shields.io/github/license/original-flipster69/cloud-lock)

**cloud-lock** enables you to do leader election making use of cloud storage. This is useful for services without a database or serverless functions still scaled out horizontally.
With a lock in the storage you can ensure that certain workloads are only done on a single instance/node of your services. It is written in Java and easy to use.

## Getting started

### Requirements

**cloud-lock** requires Java 11+ to compile and build.
Java version supported by the Hibernate ORM version you are using.
SLF4J
Jackson Databind

### Installation

Add the following dependency to your project:

- Maven
    ```xml
  <dependency>
    <groupId>com.originalflipster</groupId>
    <artifactId>cloud-lock</artifactId>
    <version>0.1.0</version>
  </dependency>
  ```
- Gradle

## Documentation

Still questions left? Find the docs on [gin-gonic.com](https://gin-gonic.com) in several languages: