package com.ongo.firebasechatlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ongo.firebasechatlib.activities.ChatActivity;
import com.ongo.firebasechatlib.utils.ChatOnGoConstants;
import com.ongo.firebasechatlib.utils.SharedPref;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mobileET, nameET;
    private Button button;
    private String mobile = "", name = "";
    private Context mContext;
    private String myMobile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        SharedPref.init(mContext);
        mobileET = findViewById(R.id.mobileET);
        nameET = findViewById(R.id.nameET);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = mobileET.getText().toString().trim();
                name = nameET.getText().toString().trim();
                if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
                    Toast.makeText(mContext, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    myMobile = SharedPref.read(OnGoConstants.PREF_MOBILE, "");
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(ChatOnGoConstants.SELECTED_USER_ID, mobile);
                    intent.putExtra(ChatOnGoConstants.SELECTED_USER_USERNAME, name);
                    intent.putExtra(ChatOnGoConstants.SELECTED_USER_PROFPIC, "");
                    SharedPref.write(OnGoConstants.PREF_USER_ID, myMobile);
                    SharedPref.write(OnGoConstants.PREF_USER_NAME, name);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
    }
}
