package ru.yarsu.web.posts.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.Post
import ru.yarsu.domain.models.User
import ru.yarsu.web.auth.AUTH_SEGMENT
import ru.yarsu.web.auth.SIGN_IN
import ru.yarsu.web.auth.SIGN_UP
import ru.yarsu.web.posts.utils.render

class PostVM(
    val postWithHashtag: Pair<Post, Hashtag>,
    val author: User,
) : ViewModel {
    val signInLink = "$AUTH_SEGMENT$SIGN_IN"
    val signUpLink = "$AUTH_SEGMENT$SIGN_UP"

    fun renderMD(content: String) = content.render()
}
