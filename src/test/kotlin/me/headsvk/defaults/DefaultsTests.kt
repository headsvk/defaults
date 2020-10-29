package me.headsvk.defaults

import me.headsvk.defaults.Defaults.default
import me.headsvk.defaults.Defaults.register
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DefaultsTests {

    @Test
    fun `Default primitives`() {
        assertEquals(0, default())
        assertEquals(0.toByte(), default())
        assertEquals(0.toChar(), default())
        assertEquals(0.toDouble(), default())
        assertEquals(0.toFloat(), default())
        assertEquals(0.toShort(), default())
        assertEquals(0.toLong(), default())
        assertEquals(false, default())
        assertEquals("", default())
    }

    @Test
    fun `Register primitives`() {
        register(1)
        assertEquals(1, default())

        register(2.toByte())
        assertEquals(2.toByte(), default())

        register(3.toChar())
        assertEquals(3.toChar(), default())

        register(4.5.toDouble())
        assertEquals(4.5.toDouble(), default())

        register(6.7.toFloat())
        assertEquals(6.7.toFloat(), default())

        register(8.toShort())
        assertEquals(8.toShort(), default())

        register(9.toLong())
        assertEquals(9.toLong(), default())

        register(true)
        assertEquals(true, default())

        register("string")
        assertEquals("string", default())
    }

    @Test
    fun `Register classes`() {
        register(OffsetDateTime.MIN)
        assertEquals(OffsetDateTime.MIN, default())

        register(Locale.US)
        assertEquals(Locale.US, default())

        class Foo(val int: Int)
        register(Foo(8))
        assertEquals(8, default<Foo>().int)
    }

    @Test
    fun `Fail not registered classes`() {
        assertFailsWith<IllegalStateException> {
            default<OffsetDateTime>()
        }
        assertFailsWith<IllegalStateException> {
            default<Locale>()
        }
        class Foo(val int: Int)
        assertFailsWith<IllegalStateException> {
            default<Foo>()
        }
    }

    @Test
    fun `Default simple data class`() {
        data class Foo(
            val int: Int,
            val string: String?,
            val list: List<String>,
        )

        assertEquals(Foo(int = 0, string = null, list = emptyList()), default())
    }

    @Test
    fun `Default data class with default values`() {
        data class Foo(
            val int: Int,
            val optional: Double = 1.2,
            val string: String,
        )

        assertEquals(Foo(int = 0, string = ""), default())
    }

    @Test
    fun `Default data class with registered values`() {
        data class Foo(
            val int: Int,
            val double: Double?,
            val string: String,
            val locale: Locale,
        )

        register(5)
        register("foo")
        register(Locale.US)
        assertEquals(
            Foo(
                int = 5,
                double = null,
                string = "foo",
                locale = Locale.US,
            ), default()
        )
    }


    @Test
    fun `Default nested data classes with registered values`() {
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

        register("hello")
        register(Locale.US)
        assertEquals(
            Baz(
                int = 0,
                double = 0.0,
                bar = Bar(
                    locale = Locale.US,
                    foo = Foo(
                        string = "hello",
                    ),
                ),
            ), default()
        )
    }

    @Test
    fun `Default empty collections`() {
        assertEquals(emptyList<String>(), default())
        assertEquals(emptyList<Int>(), default())
        assertEquals(mutableListOf<String>(), default())
        assertEquals(emptyMap<String, Int>(), default())
        assertEquals(mutableMapOf<String, Int>(), default())
        assertEquals(emptySet<String>(), default())
        assertEquals(mutableSetOf<String>(), default())
    }

    @BeforeTest
    fun reset() {
        Defaults.reset()
    }
}
