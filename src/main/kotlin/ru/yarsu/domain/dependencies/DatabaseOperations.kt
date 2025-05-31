package ru.yarsu.domain.dependencies

interface DatabaseOperations {
    val userOperations: UsersDatabase
    val mediaOperations: MediaDatabase
    val postsOperations: PostsDatabase
    val hashtagOperations: HashtagsDatabase
    val commentOperations: CommentsDatabase
}
