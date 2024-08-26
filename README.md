# cloud-lock

<img align="right" width="192px" src="https://github.com/original-flipster69/cloud-lock/blob/9c50c4b458c3e433f13b88e06436d8d2ab19cb3e/logo.webp">

![GitHub License](https://img.shields.io/github/license/original-flipster69/cloud-lock)
![Maven Central Version](https://img.shields.io/maven-central/v/com.originalflipster/cloud-lock)

**cloud-lock** provides a means of distributed locking via cloud storage in Java. 
This let’s you do leader election to run certain workloads only once when your service is scaled out horizontally. 
Doing it via cloud has the benefit of not needing to provision a database for that purpose if your service is stateless in the first place.

## Which cloud providers are supported?

At the moment, **cloud-lock** works with 3 different cloud storage solutions by the following providers:
- Google Cloud Storage (GCS) powered by **Google Cloud Platform**
- Azure Blob Storage powered by **Microsoft Azure**
- Object Storage Service powered by **Alibaba Cloud**

## Getting started

### Requirements

- Java 11+
- SLF4J
- Dependency of cloud provider's storage solution that you actually use

### Installation

Add the following dependency to your project:

- Maven
    ```xml
  <dependency>
        <groupId>com.originalflipster</groupId>
        <artifactId>cloud-lock</artifactId>
        <version>0.0.7</version>
  </dependency>
  ```
- Gradle
  ```gradle
  implementation 'com.originalflipster:cloud-lock:0.0.7'
  ```

## Documentation

Still questions left? Check the docs in my [crib](https://originalflipster.com/docs/distributed-locking-with-cloud-lock)