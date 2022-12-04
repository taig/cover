# Cover

> A Java library that mimics CSS `object-fit` image scaling behavior

[![Maven Central](https://img.shields.io/maven-central/v/io.taig/object-fit)](https://search.maven.org/artifact/io.taig/object-fit)

This library exposes a single method `Cover.fit(InputStream input, int width, int height)` where `input` must be a valid image source that can be decoded with `ImageIO.read(input)`.

Like CSS' `object-fit: cover`, the returned `BufferedImage` will always obey the aspect ratio of the given `width` and `height` parameters. If the given image is larger, it will be down-scaled and cropped (to center) to fit into the given dimensions.

Unlike `object-fit: cover`, this library won't upscale images that are smaller than the given dimensions. It does, however, make sure to crop smaller images into the correct aspect ratio.

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
    <td><img src="/modules/samples/src/main/resources/otter.2.1.result.webp" /></td>
  </tr>
</table>