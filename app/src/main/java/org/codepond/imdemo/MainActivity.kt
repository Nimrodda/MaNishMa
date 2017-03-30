package org.codepond.imdemo

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.View

import org.codepond.imdemo.chat.ChatActivity

class MainActivity : BaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.button).setOnClickListener {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_PARTICIPANT_JID, "user1@localhost/rLMACndayan")
            startActivity(intent)
        }

    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {

    }

    override fun onServiceDisconnected(name: ComponentName) {

    }
}
