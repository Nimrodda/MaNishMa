package org.codepond.imdemo.model

data class ChatMessage(var from: String = "", var text: String = "") {
    var createdDate: Any? = null
}
