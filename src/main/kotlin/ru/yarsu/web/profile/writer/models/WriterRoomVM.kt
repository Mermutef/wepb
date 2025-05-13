package ru.yarsu.web.profile.writer.models

import org.http4k.template.ViewModel

class WriterRoomVM(
    val isModerator: Boolean = false,
) : ViewModel
