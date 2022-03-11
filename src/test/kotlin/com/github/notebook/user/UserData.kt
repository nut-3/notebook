package com.github.notebook.user

import com.github.notebook.user.model.NewUser
import com.github.notebook.user.model.Role
import com.github.notebook.user.model.User

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
val nonExistentUserName = "nonexistent"