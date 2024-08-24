# cloud-lock

<img align="right" width="256px" src="https://github.com/original-flipster69/cloud-lock/blob/9c50c4b458c3e433f13b88e06436d8d2ab19cb3e/logo.webp">

![GitHub License](https://img.shields.io/github/license/original-flipster69/cloud-lock)
![Maven Central Version](https://img.shields.io/maven-central/v/com.originalflipster/cloud-lock)

**cloud-lock** enables you to do leader election making use of cloud storage. This is useful for services without a database or serverless functions still scaled out horizontally.
With a lock in the storage you can ensure that certain workloads are only done on a single instance/node of your services. It is written in Java and easy to use.

## Getting started

### Requirements

- Java 11+
- SLF4J
- Dependency of cloud provider' storage solution that you actually use

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

Still questions left? Find the docs on [originalflipster.com](https://originalflipster.com):