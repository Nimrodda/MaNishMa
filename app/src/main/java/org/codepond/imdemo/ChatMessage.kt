package org.codepond.imdemo

data class ChatMessage(var from: String = "", var text: String = "") {
    var createdDate: Any? = null
}
