# DiffDetector-sdk

*Java SDK for connecting to Diff Detector services*


## Introduction

**DiffDetector-sdk** contains Java clients for connecting to [Diff Detector](https://github.com/giancosta86/DiffDetector)'s RESTful web services without having to know the actual URLs exposed by the web service.

The practice of creating a kit for connecting to web services is very common in network-oriented languages such as [Erlang](https://speakerdeck.com/giancosta86/introduction-to-erlang) - and enables developers to actually handle entities in their own domain (objects, functions, ...), without dealing with network connection details and marshalling/unmarshalling.

In particular, the main classes are **DiffServiceClient** and **SimpleDiffServiceClient**. For further information, please, refer to the Javadoc or the source code.


## Requirements

Java 8u151 or later compatible is recommended to employ the library.


## Referencing the library

DiffDetector-sdk is available on [Hephaestus](https://bintray.com/giancosta86/Hephaestus) and can be declared as a Gradle or Maven dependency; please refer to [its dedicated page](https://bintray.com/giancosta86/Hephaestus/DiffDetector-sdk).

Alternatively, you could download the JAR file from Hephaestus and manually add it to your project structure.


## Further references

* [Diff Detector](https://github.com/giancosta86/DiffDetector)
