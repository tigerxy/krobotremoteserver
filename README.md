# krobotremoteserver

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tigerxy_krobotremoteserver&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tigerxy_krobotremoteserver)

This is a Kotlin implementation of krobotremoteserver, a framework for creating remote libraries for Robot Framework in Kotlin. This implementation utilizes Ktor as the HTTP server framework for providing remote library functionalities.

## Installation

To use this library in your project, you can download the JAR file directly from Maven Central or use Maven/Gradle to add it as a dependency.

Maven:

```xml
<dependency>
    <groupId>de.rolandgreim.krobotremoteserver</groupId>
    <artifactId>krobotremoteserver-ktor</artifactId>
    <version>{VERSION}</version>
</dependency>
```

Gradle:

```groovy
implementation 'de.rolandgreim.krobotremoteserver:krobotremoteserver-ktor:{VERSION}'
```

## Usage

To create a custom library with krobotremoteserver in Kotlin, you need to create a class that provides your library functions. You can use annotations to mark the functions as keywords.

Example:

```kotlin
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

class MyLibrary {
    @RobotKeyword(tags = ["x"])
    fun abc(a: Int) = a * 2
}

fun main() {
    val myLibrary = MyLibrary()

    embeddedServer(Netty, port = 8270) {
        robotFrameworkServer(myLibrary)
    }.start(wait = true)
}
```

For further information on using krobotremoteserver, please refer to the official documentation.

## Contributors

- Roland Greim (@tigerxy) - Lead Developer
- Andreas Scheja (@ascheja)
    - Sources in xmlrpc package are based on his [xml-rpc-kt](https://github.com/ascheja/xml-rpc-kt)

## License

This implementation is released under the Apache License 2.0. For more information, see the `LICENSE` file.
