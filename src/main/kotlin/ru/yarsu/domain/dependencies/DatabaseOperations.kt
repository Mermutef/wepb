package ru.yarsu.domain.dependencies

interface DatabaseOperations {
    val userOperations: UsersDatabase
    val mediaOperations: MediaDatabase
}
