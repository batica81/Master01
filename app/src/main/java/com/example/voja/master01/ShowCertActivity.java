package com.example.voja.master01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowCertActivity extends Activity {

    private TextView myTextView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cert);

        myTextView3 = (TextView) findViewById(R.id.textView3);
        Intent intent = getIntent();
        String intentMessage = intent.getStringExtra("message");
        myTextView3.setText(intentMessage);
    }
}
