# Cover

> A Java micro-library that mimics CSS `object-fit: cover` behavior

[![Maven Central](https://img.shields.io/maven-central/v/io.taig/cover)](https://search.maven.org/artifact/io.taig/cover)

This library exposes a single method `Cover.fit(InputStream input, int width, int height)` where `input` must be a valid image source that can be decoded with `ImageIO.read(input)`.

Like CSS' `object-fit: cover`, the returned `BufferedImage` will always obey the aspect ratio of the given `width` and `height` parameters. If the given image is larger, it will be down-scaled and cropped (to center) to fit into the given dimensions.

Unlike `object-fit: cover`, this library won't upscale images that are smaller than the given dimensions. It does, however, make sure to crop smaller images into the correct aspect ratio.

Additionally, this library automatically handles image rotation from JPEG Exif data if available.

_Cover_ is well suited for thumbnail generation but works just as well with larger images.

## Installation

### sbt

```sbt
libraryDependencies += "io.taig" % "cover" % "x.y.z"
```

### Maven

```xml
<dependency>
  <groupId>io.taig</groupId>
  <artifactId>cover</artifactId>
  <version>x.y.z</version>
</dependency>
```

### Gradle

```groovy
implementation("io.taig:cover:x.y.z")
```

## Samples

```scala
ObjectFit.of(getClass.getResourceAsStream("/otter-1.jpg"))
  .mode(ObjectFit.Mode.COVER)
  .size(250)
  .format("webp")
  .write(target("otter-1.result.webp"))
```

