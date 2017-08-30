/*
 * Copyright 2017 Nimrod Dayan CodePond.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codepond.imdemo.service.chat

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

import org.codepond.imdemo.model.ChatMessage
import javax.inject.Inject

class FirebaseMessagingService @Inject constructor(): MessagingService, ChildEventListener {
    var mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var mListener: MessagingService.OnMessageReceivedListener? = null
    var mRef: DatabaseReference = mDatabase.getReference("messages/nimrod-joni")

    override fun sendMessage(chatMessage: ChatMessage) {
        chatMessage.createdDate = ServerValue.TIMESTAMP
        mRef.push().setValue(chatMessage)
    }

    override fun setCurrentParticipant(participantJid: String) {

    }

    override fun setOnMessageReceivedListener(listener: MessagingService.OnMessageReceivedListener?) {
        mListener = listener
        if (listener == null) {
            mRef.removeEventListener(this)
        } else {
            mRef.addChildEventListener(this)
        }
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
        val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)
        mListener?.onMessageReceived(chatMessage)
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot) {

    }

    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

    }

    override fun onCancelled(databaseError: DatabaseError) {

    }
}
