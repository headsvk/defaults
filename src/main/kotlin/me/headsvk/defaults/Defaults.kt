package me.headsvk.defaults

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Entry point for creating default values.
 */
public object Defaults {

    private val registry = mutableMapOf<KClass<*>, Any>()

    init {
        reset()
    }

    /**
     * Resets registered value bindings to initial state with primitives only.
     */
    public fun reset() {
        registry.clear()
        register(0)
        register(0.toByte())
        register(0.toChar())
        register(0.toDouble())
        register(0.toFloat())
        register(0.toShort())
        register(0.toLong())
        register(false)
        register("")
    }

    /**
     * Registers [value] to be used when calling [default] for [T].
     * Overrides previous bindings for the class.
     * Generic classes are not supported.
     */
    public inline fun <reified T : Any> register(value: T) {
        register(T::class, value)
    }

    /**
     * Registers [value] to be used when calling [default] for [T].
     * Overrides previous bindings for the class [kClass].
     * Generic classes are not supported.
     */
    public fun <T : Any> register(kClass: KClass<T>, value: T) {
        if (kClass.typeParameters.isNotEmpty()) {
            throw IllegalArgumentException("Registering generic classes is not supported")
        }
        registry[kClass] = value
    }

    /**
     * Returns a default value registered with [register] for [T]
     * or an empty Kotlin collection
     * or a data class instantiated with default values.
     * Defaults for [Array] are not supported.
     */
    public inline fun <reified T : Any> default(): T {
        return default(T::class)
    }

    /**
     * Returns a default value registered with [register] for [T]
     * or an empty Kotlin collection
     * or a data class instantiated with default values or nulls for nullable fields.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> default(kClass: KClass<T>): T {
        return registry[kClass] as? T ?: run {
            when {
                kClass.isData -> defaultDataClass(kClass)
                kClass == List::class -> emptyList<Any>() as T
                kClass == MutableList::class -> mutableListOf<Any>() as T
                kClass == Map::class -> emptyMap<Any, Any>() as T
                kClass == MutableMap::class -> mutableMapOf<Any, Any>() as T
                kClass == Set::class -> emptySet<Any>() as T
                kClass == MutableSet::class -> mutableSetOf<Any>() as T
                else -> throw IllegalStateException("Class $kClass is not registered")
            }
        }
    }

    private fun <T : Any> defaultDataClass(kClass: KClass<T>): T {
        val constructor = kClass.primaryConstructor
            ?: throw IllegalArgumentException("No primary constructor found for data class $kClass")
        val arguments = constructor.parameters.asSequence()
            .filter { !it.isOptional }
            .associate { param ->
                when {
                    param.type.isMarkedNullable -> {
                        param to null
                    }
                    param.type.classifier is KClass<*> -> {
                        param to default(param.type.classifier as KClass<*>)
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported type ${param.type}")
                    }
                }
            }
        return constructor.callBy(arguments)
    }
}
