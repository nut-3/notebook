package com.github.notebook.user.web

import com.github.notebook.user.model.EditUser
import com.github.notebook.user.model.NewUser
import com.github.notebook.user.model.Role
import com.github.notebook.user.model.User

@kotlinx.serialization.Serializable
data class NoValidationUser(
    val id: Int? = null,
    val name: String? = null,
    val password: String? = null,
    val active: Boolean? = null,
    val email: String? = null,
    val fullName: String? = null,
    val roles: Set<Role>? = emptySet()
)

internal fun User.toEditUser(
    name: String = this.name,
    password: String? = null,
    active: Boolean = this.active,
    email: String = this.email,
    fullName: String? = this.fullName,
    roles: Set<Role> = this.roles
) = EditUser(
    this.id,
    name,
    password,
    active,
    email,
    fullName,
    roles
)

internal fun EditUser.toUser() = User(
    this.id,
    this.name,
    this.active,
    this.email,
    this.fullName,
    this.roles
)

internal fun NewUser.toUser(id: Int) = User(
    id,
    this.name,
    this.active,
    this.email,
    this.fullName,
    this.roles
)