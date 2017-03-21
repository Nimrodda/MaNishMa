package org.codepond.imdemo

data class ChatMessage(var from: String, var to: String, var messageText: String, var incomingMessage: Boolean, var timestamp: Long)
