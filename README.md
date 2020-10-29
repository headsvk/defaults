# Kotlin Data Class Defaults
##### Simple instantiation of data classes in your tests.

![Build](https://github.com/headsvk/defaults/workflows/Build/badge.svg)
[![codecov](https://codecov.io/gh/headsvk/defaults/branch/main/graph/badge.svg?token=G29O34T1M8)]()
[![Download](https://api.bintray.com/packages/headsvk/defaults/defaults/images/download.svg) ](https://bintray.com/headsvk/defaults/defaults/_latestVersion)

### Installation
```
repositories {
    jcenter()
}

dependencies {
    testImplementation("me.headsvk.defaults:defaults:${version}")
}
```

### Usage
Let's say we have following nested data classes (coming from an api for example):
```
data class Foo(
    val string: String,
    val optionalInt: Int?,
)
data class Bar(
    val locale: Locale,
    val foo: Foo,
)
data class Baz(
    val int: Int,
    val double: Double,
    val bar: Bar,
)
```

We don't want to provide default values in constructors to enforce full initialization in production code,
but we can easily instantiate `Baz` in tests by:
```
register(Locale.US)                 // Register a default value for a class
register(Double.POSITIVE_INFINITY)  // Register a custom primitive value
val baz = default<Baz>()            // Create Baz with default values or nulls
```

Then `baz` has following values:
```
assertEquals(Baz(
    int = 0,
    double = Double.POSITIVE_INFINITY,
    bar = Bar(
        locale = Locale.US,
        foo = Foo(
            string = "",
            optionalInt = null,
        ),
    ),
), baz) 
```

The created instance of `Baz` is **not a mock** thus data class functions such as `equals`, 
`toString` and `copy` work as expected.

### Limitations
Registering defaults for generic classes is not supported but all kotlin collections default to empty.

The library depends on the full kotlin-reflect JVM library so support for Kotlin Multiplatform is not currently possible.

