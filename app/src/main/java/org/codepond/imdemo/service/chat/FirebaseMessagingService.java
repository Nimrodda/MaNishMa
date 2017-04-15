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

package org.codepond.imdemo.service.chat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.codepond.imdemo.ChatMessage;

public class FirebaseMessagingService implements MessagingService, ChildEventListener {
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    OnMessageReceivedListener mListener;
    DatabaseReference mRef = mDatabase.getReference("messages/nimrod-joni");

    @Override
    public void sendMessage(ChatMessage chatMessage) {
        chatMessage.setCreatedDate(ServerValue.TIMESTAMP);
        mRef.push().setValue(chatMessage);
    }

    @Override
    public void setCurrentParticipant(String participantJid) {

    }

    @Override
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        mListener = listener;
        if (listener == null) {
            mRef.removeEventListener(this);
        }
        else {
            mRef.addChildEventListener(this);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
        if (mListener != null) {
            mListener.onMessageReceived(chatMessage);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
