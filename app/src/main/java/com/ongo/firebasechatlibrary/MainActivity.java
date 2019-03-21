package com.ongo.firebasechatlibrary;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.ongo.firebasechatlib.activities.ChatActivity;
import com.ongo.firebasechatlib.activities.ChatListActivity;
import com.ongo.firebasechatlib.utils.SharedPref;
import com.ongo.firebasechatlibrary.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mContext = this;
        SharedPref.init(mContext);
        setListeners();
        callPlaceAutocompleteActivityIntent();
    }

    private void setListeners() {
        activityMainBinding.chatBTN1.setOnClickListener(this);
        activityMainBinding.chatBTN2.setOnClickListener(this);
        activityMainBinding.userBTN1.setOnClickListener(this);
        activityMainBinding.userBTN2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatBTN1:
                Bundle bundle = new Bundle();
                bundle.putString(OnGoConstants.SELECTED_USER_ID, "123");
                bundle.putString(OnGoConstants.SELECTED_USER_PROFPIC, "https://www.oddsshark.com/sites/default/files/conversion_images/osnet_device_andriod.jpg");
                bundle.putString(OnGoConstants.SELECTED_USER_USERNAME, "User A");
                bundle.putLong(OnGoConstants.FIREBASE_K_UNREADCOUNT, 1);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtras(bundle);
                SharedPref.write(OnGoConstants.PREF_USER_ID, "1234");
                SharedPref.write(OnGoConstants.PREF_USER_NAME, "User B");
                startActivity(intent);
                break;
            case R.id.chatBTN2:
                Bundle bundle1 = new Bundle();
                bundle1.putString(OnGoConstants.SELECTED_USER_ID, "1234");
                bundle1.putString(OnGoConstants.SELECTED_USER_PROFPIC, "https://www.oddsshark.com/sites/default/files/conversion_images/osnet_device_andriod.jpg");
                bundle1.putString(OnGoConstants.SELECTED_USER_USERNAME, "User B");
                bundle1.putLong(OnGoConstants.FIREBASE_K_UNREADCOUNT, 1);

                Intent intent1 = new Intent(MainActivity.this, ChatActivity.class);
                intent1.putExtras(bundle1);
                SharedPref.write(OnGoConstants.PREF_USER_ID, "123");
                SharedPref.write(OnGoConstants.PREF_USER_NAME, "User A");
                startActivity(intent1);
                break;
            case R.id.userBTN1:
                Intent intent2 = new Intent(MainActivity.this, ChatListActivity.class);
                SharedPref.write(OnGoConstants.PREF_USER_ID, "1234");
                SharedPref.write(OnGoConstants.PREF_USER_NAME, "User B");
                startActivity(intent2);
                break;
            case R.id.userBTN2:
                Intent intent3 = new Intent(MainActivity.this, ChatListActivity.class);
                SharedPref.write(OnGoConstants.PREF_USER_ID, "123");
                SharedPref.write(OnGoConstants.PREF_USER_NAME, "User A");
                startActivity(intent3);
                break;

        }
    }
    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, 11);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }
}
