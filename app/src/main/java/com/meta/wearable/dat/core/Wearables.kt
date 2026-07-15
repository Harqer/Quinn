package com.meta.wearable.dat.core

import android.content.Context

class DatError(val description: String)

sealed class DatResult<out T> {
    data class Success<out T>(val value: T) : DatResult<T>()
    data class Failure(val error: DatError) : DatResult<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): DatResult<T> {
        if (this is Success) action(value)
        return this
    }

    inline fun onFailure(action: (error: DatError, extra: Any?) -> Unit): DatResult<T> {
        if (this is Failure) action(error, null)
        return this
    }
    
    fun getOrElse(default: (DatError) -> @UnsafeVariance T): T {
        return when (this) {
            is Success -> value
            is Failure -> default(error)
        }
    }
    
    fun getOrThrow(): T {
        return when (this) {
            is Success -> value
            is Failure -> throw IllegalStateException(error.description)
        }
    }
}

object Wearables {
    fun initialize(context: Context): DatResult<Unit> {
        return DatResult.Success(Unit)
    }
}
