package org.codepond.imdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.codepond.imdemo.chat.ChatActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        if (view.getId() == R.id.button2) {
            intent.putExtra(ChatActivity.USER_ID, "Nimrod");
        }
        else {
            intent.putExtra(ChatActivity.USER_ID, "Joni");
        }
        startActivity(intent);
    }
}
