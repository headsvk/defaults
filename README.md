# Kotlin Data Class Defaults
##### Simple instantiation of data classes in your tests.

![Build](https://github.com/headsvk/defaults/workflows/Build/badge.svg)
[![codecov](https://codecov.io/gh/headsvk/defaults/branch/main/graph/badge.svg?token=G29O34T1M8)]()

### How to use
Let's say we have following nested data classes (coming from an api for example):
```
data class Foo(
    val string: String,
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
val baz = default<Baz>()            // Create Baz with default values
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
        ),
    ),
), baz) 
```

The created instance of `Baz` is **not a mock** thus data class functions such as `equals`, 
`toString` and `copy` work as expected.
