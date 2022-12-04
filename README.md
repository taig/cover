# Cover

> A Java library that mimics CSS `object-fit` image scaling behavior

[![Maven Central](https://img.shields.io/maven-central/v/io.taig/object-fit)](https://search.maven.org/artifact/io.taig/object-fit)

A Java AWT based image scaling library that is inspired by <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/object-fit?retiredLocale=de">CSS `object-fit`</a> scaling modes. This library allows to scale images in `cover`, `contain` and `fill` modes, where `cover` mode is the noteworthy feature.

Additionally, this library automatically handles image rotation from JPEG Exif data if available.

_object-fit_ is well suited for thumbnail generation but works just as well with larger images.

## Installation

### sbt

```sbt
libraryDependencies += "io.taig" % "object-fit" % "x.y.z"
```

### Maven

```xml
<dependency>
  <groupId>io.taig</groupId>
  <artifactId>object-fit</artifactId>
  <version>x.y.z</version>
</dependency>
```

### Gradle

```groovy
implementation("io.taig:object-fit:x.y.z")
```

## Samples

### Cover

```scala
ObjectFit.of(...)
  .mode(ObjectFit.Mode.COVER)
  .size(250, 150)
  .format("webp")
```

<table>
  <tr>
    <th>Input</th>
    <th>Output</th>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.1.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.1.1.result.webp" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.2.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.1.2.result.webp" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.3.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.1.3.result.webp" /></td>
  </tr>
</table>

<hr />

```scala
ObjectFit.of(...)
  .mode(ObjectFit.Mode.COVER)
  .size(250, 150)
  .format("webp")
  .scaleUp()
  ```

<table>
  <tr>
    <th>Input</th>
    <th>Output</th>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.3.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.2.3.result.webp" /></td>
  </tr>
</table>

### Fill

```scala
ObjectFit.of(...)
  .mode(ObjectFit.Mode.FILL)
  .size(250, 250)
  .format("png")
```

<table>
  <tr>
    <th>Input</th>
    <th>Output</th>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.1.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.3.1.result.png" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.2.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.3.2.result.png" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.3.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.3.3.result.png" /></td>
  </tr>
</table>

<hr />

```scala
ObjectFit.of(...)
  .mode(ObjectFit.Mode.FILL)
  .size(250, 250)
  .format("png")
  .scaleUp()
```

<table>
  <tr>
    <th>Input</th>
    <th>Output</th>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.3.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.4.3.result.png" /></td>
  </tr>
</table>

### Contain

```scala
ObjectFit.of(...)
  .mode(ObjectFit.Mode.CONTAIN)
  .size(250, 250)
  .format("jpg")
```

<table>
  <tr>
    <th>Input</th>
    <th>Output</th>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.1.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.5.1.result.jpg" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.2.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.5.2.result.jpg" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.3.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.5.3.result.jpg" /></td>
  </tr>
</table>

<hr />

```scala
ObjectFit.of(...)
  .mode(ObjectFit.Mode.CONTAIN)
  .size(400, 500)
  .format("jpg")
  .scaleUp()
```

<table>
  <tr>
    <th>Input</th>
    <th>Output</th>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.1.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.6.1.result.jpg" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.2.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.6.2.result.jpg" /></td>
  </tr>
  <tr>
    <td><img src="/modules/samples/src/main/resources/otter.3.jpg" /></td>
    <td><img src="/modules/samples/src/main/resources/otter.6.3.result.jpg" /></td>
  </tr>
</table>