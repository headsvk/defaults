package me.headsvk.defaults

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

public object Defaults {

    private val registry = mutableMapOf<KClass<*>, Any>()

    init {
        reset()
    }

    public fun reset() {
        registry.clear()
        register(0)
        register(0.toByte())
        register(0.toChar())
        register(0.toDouble())
        register(0.toFloat())
        register(0.toShort())
        register(false)
        register("")
    }

    public inline fun <reified T : Any> register(value: T) {
        register(T::class, value)
    }

    public fun <T : Any> register(kClass: KClass<T>, value: T) {
        if (kClass.typeParameters.isNotEmpty()) {
            throw IllegalArgumentException("Registering generic classes is not supported")
        }
        registry[kClass] = value
    }

    public inline fun <reified T : Any> default(): T {
        return default(T::class)
    }

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
                kClass.java.isArray -> throw IllegalArgumentException("Default arrays are not supported")
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
                    val paramClass = param.type.classifier
                    if (paramClass is KClass<*>) {
                        param to default(paramClass)
                    } else {
                        throw IllegalArgumentException("Unsupported type ${param.type.classifier}")
                    }
                }
        return constructor.callBy(arguments)
    }
}
