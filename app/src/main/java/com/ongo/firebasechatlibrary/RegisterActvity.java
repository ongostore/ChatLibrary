package com.ongo.firebasechatlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ongo.firebasechatlib.utils.SharedPref;

/**
 * Created by gufran khan on 22-03-2019.
 */

public class RegisterActvity extends AppCompatActivity {

    private EditText mobileET;
    private Button button;
    private String mobile = "";
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        SharedPref.init(mContext);
        mobileET = findViewById(R.id.mobileET);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = mobileET.getText().toString().trim();
                if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
                    Toast.makeText(RegisterActvity.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPref.write(OnGoConstants.PREF_MOBILE, mobile);
                    finish();
                    startActivity(new Intent(mContext, MainActivity.class));
                }
            }
        });

        if (!TextUtils.isEmpty(SharedPref.read(OnGoConstants.PREF_MOBILE, ""))) {
            finish();
            startActivity(new Intent(mContext, MainActivity.class));
        }
    }
}
