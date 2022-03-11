package com.github.notebook

import com.github.notebook.model.NewUser
import com.github.notebook.model.Role
import com.github.notebook.model.User

val user = User(1, "user", true, "user@test.test", "User User", setOf(Role.USER))
val admin = User(2, "admin", true, "admin@test.test", "Admin Admin", setOf(Role.USER, Role.ADMIN))
val newUser = NewUser("new", "new_password", true, "new_user@test.test", "New User", setOf(Role.USER))
val updatedUser = user.toEditUser(email = "user1@test.test", roles = setOf(Role.ADMIN, Role.USER))
val invalidUser = NoValidationUser(
    name = "1",
    password = "123",
    active = true,
    email = "wrong_at_email",
    fullName = "Invalid",
    roles = setOf()
)
const val NON_EXISTENT_USER_NAME = "nonexistent"
const val USER_PASSWORD = "password"