package com.example.myapp

import org.junit.Test
import com.meta.wearable.dat.core.Wearables

class WearablesTest {
    @Test
    fun dumpMethods() {
        println("=== WEARABLES METHODS ===")
        val methods = Wearables::class.java.declaredMethods
        for (m in methods) {
            print("Method: " + m.name + "(")
            val params = m.parameterTypes
            for (i in params.indices) {
                print(params[i].simpleName)
                if (i < params.size - 1) print(", ")
            }
            println(") -> " + m.returnType.simpleName)
        }
        println("=========================")
    }
}
