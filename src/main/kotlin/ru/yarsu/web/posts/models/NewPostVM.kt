package ru.yarsu.web.posts.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.models.Hashtag
import ru.yarsu.domain.models.MediaType
import ru.yarsu.web.form.CustomWebForm

class NewPostVM(
    val form: CustomWebForm? = null,
    val hashTags: List<Hashtag> = emptyList(),
) : ViewModel {
    val mediaTypes = MediaType.entries.toTypedArray()
}
