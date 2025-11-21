package com.miapp.xanostorekotlin

import com.miapp.xanostorekotlin.api.UserService
import org.junit.Assert.assertTrue
import org.junit.Test

class UserServiceTest {
    @Test
    fun `UserService exposes expected API methods`() {
        val methodNames = UserService::class.java.methods.map { it.name }.toSet()
        assertTrue(methodNames.contains("getUsers"))
        assertTrue(methodNames.contains("getUser"))
        assertTrue(methodNames.contains("updateUser"))
    }
}