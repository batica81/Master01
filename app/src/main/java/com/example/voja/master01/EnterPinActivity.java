package com.example.voja.master01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;


public class EnterPinActivity extends Activity {

    private EditText enterPINEditText;
    private String pin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        enterPINEditText = (EditText) findViewById(R.id.editText3);
        Button aktiviraj = (Button) findViewById(R.id.button);

        aktiviraj.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    getPin();

                } catch (Exception e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                }
            }
        });
    }

    public void displayExceptionMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void getPin() {
        pin = String.valueOf(enterPINEditText.getText());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message", pin);
        enterPINEditText.setText("");
        startActivity(intent);
    }
}
