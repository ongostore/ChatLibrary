package com.ongo.firebasechatlib.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ongo.firebasechatlib.R;
import com.ongo.firebasechatlib.adapter.ChatListAdapter;
import com.ongo.firebasechatlib.dto.UserDto;
import com.ongo.firebasechatlib.utils.ChatOnGoConstants;
import com.ongo.firebasechatlib.utils.SharedPref;
import com.ongo.firebasechatlib.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


/**
 * Created by sarathokcy on 4/16/2018.
 */

public class ChatListActivity extends AppCompatActivity {
    private Context mContext;
    private RecyclerView mrecyclerview;
    private SwipeRefreshLayout swipe_view;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChatListAdapter chatlistAdapter;
    private TextView  nodata;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        mContext = this;
        SharedPref.init(mContext);
        swipe_view = findViewById(R.id.swipe_view);
        mrecyclerview = findViewById(R.id.recycler_view);
        nodata = findViewById(R.id.no_data_tv);

        swipe_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getUsersFromFirebase();
            }
        });

        getUsersFromFirebase();
    }


    /***
     * gets the chat list from firebase
     */

    private void getUsersFromFirebase() {
        Utils.showProgress(mContext, true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UsersChat");
        DatabaseReference userDataRef = databaseReference.
                child(SharedPref.read(ChatOnGoConstants.PREF_USER_ID, ""))
                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS);

        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Utils.hideProgress(mContext);
                    if (dataSnapshot != null ? true : false) {

                        ArrayList<UserDto> userDtos = new ArrayList<UserDto>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserDto userDto = new UserDto();
                            userDto.email = snapshot.child("email").getValue() == null ? "" : snapshot.child("email").getValue().toString();
                            userDto.name = snapshot.child("name").getValue() == null ? "" : snapshot.child("name").getValue().toString();
                            userDto.imageUrl = snapshot.child("image_url").getValue() == null ? "" : snapshot.child("image_url").getValue().toString();
                            userDto.uid = snapshot.getKey();
                            userDto.msg_id = snapshot.child("msg_id").getValue() == null ? "" : snapshot.child("msg_id").getValue().toString();
                            userDto.last_time_stamp = snapshot.child("last_time_stamp").getValue() == null ? "" : snapshot.child("last_time_stamp").getValue().toString();
                            userDto.message = snapshot.child("lastmessage").getValue() == null ? "" : snapshot.child("lastmessage").getValue().toString();

                            userDtos.add(userDto);
                        }
                        if (userDtos.size() == 0) {
                            nodata.setVisibility(View.VISIBLE);
                            mrecyclerview.setVisibility(View.GONE);
                        }
                        // Collections.sort(userDtos, new chatComparator());
                        loadDataToAdapter(userDtos);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDataToAdapter(ArrayList<UserDto> userDtos) {
        Collections.sort(userDtos, new DateComparator());
        chatlistAdapter = new ChatListAdapter(userDtos, mContext);
        RecyclerView.LayoutManager mLayoutManager = null;
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        mrecyclerview.setLayoutManager(mLayoutManager);
        mrecyclerview.setItemAnimator(new DefaultItemAnimator());
        mrecyclerview.setAdapter(chatlistAdapter);

    }


    public class DateComparator implements Comparator<UserDto> {
        @Override
        public int compare(UserDto lhs, UserDto rhs) {
            try {
                String lhsTime = lhs.getLast_time_stamp();
                String rhsTime = rhs.getLast_time_stamp();
                SimpleDateFormat format = new SimpleDateFormat(ChatOnGoConstants.TIME_MMDDYYYY_HH_MM_SS);
                Date lhsDate = null;
                Date rhsDate = null;
                try {
                    lhsDate = format.parse(lhsTime);
                    rhsDate = format.parse(rhsTime);
                    Log.d("TAG", "compare: " + lhsDate.toString() + " " + rhsDate.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (lhsDate.before(rhsDate)) {
                    return 1;
                } else return -1;
            } catch (Exception e) {
                Log.d("test", "compare: " + e.getLocalizedMessage());
                return 0;

            }
        }
    }
}
