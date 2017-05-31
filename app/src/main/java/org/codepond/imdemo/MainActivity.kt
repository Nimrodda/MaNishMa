package org.codepond.imdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import org.codepond.imdemo.chat.ChatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.button).setOnClickListener(this)
        findViewById(R.id.button2).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent = Intent(this, ChatActivity::class.java)
        if (view.id == R.id.button2) {
            intent.putExtra(ChatActivity.USER_ID, "Nimrod")
            intent.putExtra(ChatActivity.CHAT_ID, "Nimrod-Joni")
        } else {
            intent.putExtra(ChatActivity.USER_ID, "Joni")
            intent.putExtra(ChatActivity.CHAT_ID, "Nimrod-Joni")
        }
        startActivity(intent)
    }
}
