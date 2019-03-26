package com.ongo.firebasechatlib.activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ongo.firebasechatlib.R;
import com.ongo.firebasechatlib.adapter.ChatAdapter;
import com.ongo.firebasechatlib.async_tasks.SendNotificationWithPayLoadAsync;
import com.ongo.firebasechatlib.dto.ChatDto;
import com.ongo.firebasechatlib.utils.ChatOnGoConstants;
import com.ongo.firebasechatlib.utils.SharedPref;
import com.ongo.firebasechatlib.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatActivity extends AppCompatActivity {
    private ImageView imgSend;
    private EditText etMessage;
    private Context mContext;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEdit;
    private FirebaseDatabase firebaseDatabase;
    public String msgId = null;
    public String lastmessage = null;
    private String userName;
    private Firebase reference1;
    private ImageView back_img;
    private TextView tvname, tvStatus;
    private String selectedUserId = "", selectedUserProfPic = "", selectedUserName = "";
    private ImageView profile_Image;
    private Long selectedUserUnread;
    private String mallId;
    private Firebase reference2;
    private Firebase reference3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.fragment_chat_new);
        SharedPref.init(mContext);
        mPref = getSharedPreferences(ChatOnGoConstants.PREF_NAME, MODE_PRIVATE);
        mPrefEdit = mPref.edit();

        recyclerView = (RecyclerView) findViewById(R.id.rvData);
        profile_Image = (ImageView) findViewById(R.id.userimgclient);
        tvname = (TextView) findViewById(R.id.tvname);
        back_img = (ImageView) findViewById(R.id.imageBack);
        etMessage = (EditText) findViewById(R.id.etMessage);
        imgSend = (ImageView) findViewById(R.id.imgSend);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userName = SharedPref.read(ChatOnGoConstants.PREF_USER_NAME, "");

        Bundle bundle = getIntent().getExtras();
        selectedUserId = bundle.getString(ChatOnGoConstants.SELECTED_USER_ID);                    //client (userid(ex:1907))
        selectedUserProfPic = bundle.getString(ChatOnGoConstants.SELECTED_USER_PROFPIC);
        selectedUserName = bundle.getString(ChatOnGoConstants.SELECTED_USER_USERNAME);
        selectedUserUnread = bundle.getLong(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT);

        mallId = mPref.getString(ChatOnGoConstants.PREF_USER_ID, "");                     //102

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference()
                .child("UsersChat")
                .child(selectedUserId)                                           //userid(ex:1907)
                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)
                .child(mallId);                                                //102

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT)) {
                    Long unreadCount = (Long) dataSnapshot.child(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT).getValue();

                    if (unreadCount != 0) {
                        selectedUserUnread = unreadCount;
                    } else {
                        selectedUserUnread = Long.valueOf(0);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // to tell selected user that logged in user have read all messages sent by selected user

        /*FirebaseDatabase.getInstance().getReference()
                .child("UsersChat")
                .child(Utils.getMallId(mContext))              // 102
                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)
                .child(selectedUserId)                      //1907
                .child(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT)
                .setValue(0);*/

        FirebaseDatabase.getInstance().getReference()
                .child(ChatOnGoConstants.BASIC_PROFILE)
                .child(mallId)
                .child(ChatOnGoConstants.USER_STATUS)
                .setValue("offline");
        FirebaseDatabase.getInstance().getReference()
                .child(ChatOnGoConstants.BASIC_PROFILE)
                .child(selectedUserId)
                .child(ChatOnGoConstants.USER_STATUS)

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            /*Toast.makeText(mContext, ""+dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();*/
                            tvStatus.setText(dataSnapshot.getValue().toString());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        tvname.setText(selectedUserName);
        if (selectedUserProfPic != null && !selectedUserProfPic.isEmpty()) {
            Picasso.with(mContext).load(selectedUserProfPic).placeholder(R.drawable.ic_launcher).into(profile_Image);
        }


        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etMessage.getText().toString() != null && etMessage.getText().toString().length() > 0) {

                    reference1 = new Firebase(ChatOnGoConstants.FIREBASE_PATH + ChatOnGoConstants.FIREBASE_T_MESSAGES + "/" + msgId);


//                    DatabaseReference databaseReference = firebaseDatabase.getReference()
//                            .child(ChatOnGoConstants.FIREBASE_T_MESSAGES).child(msgId).child(System.currentTimeMillis() + "");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("message", etMessage.getText().toString());
                    hashMap.put("senderId", mallId);
                    hashMap.put("name", SharedPref.read(ChatOnGoConstants.PREF_USER_NAME, ""));
                    //hashMap.put("image_url", SharedPref.read(ChatOnGoConstants.PREF_PROFILE_IMAGE, ""));
                    hashMap.put("timeStamp", System.currentTimeMillis() + "");
                    hashMap.put("read", false);
//                    databaseReference.setValue(hashMap);
                    reference1.push().setValue(hashMap);


                    /****************** Notification hashmap for chat *************/

                    HashMap<String, Object> hashNoti = new HashMap<>();

                    hashNoti.put("notiType", "chat");
                    hashNoti.put("title", "New Message");
                    hashNoti.put("notiDesc", etMessage.getText().toString());
                    hashNoti.put("publicUrl", "");
                    hashNoti.put("body", etMessage.getText().toString());
                    hashNoti.put("imageUrl", selectedUserProfPic);
                    hashNoti.put("sound", "default");
                    hashNoti.put("time", "");

                    JSONObject payload = new JSONObject();
                    try {
                        payload.put("id", mallId);
                        payload.put("image", selectedUserProfPic);
                        payload.put("senderName", mPref.getString(ChatOnGoConstants.Name, ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (payload != JSONObject.NULL) {
                        try {
                            hashNoti.put("payload", Utils.toMap(payload));
                        } catch (JSONException e) {
                            Log.e("map exp is", ">>>>>>>>>>>" + e.getLocalizedMessage());
                        }
                    } else {
                        hashMap.put("payload", "");
                    }

                    hashNoti.put("senderId", mallId);
                    //hashNoti.put("timeStamp", Utils.getCurrentTime(ChatOnGoConstants.TIME_MMDDYYYY_HH_MM_SS));
                    hashNoti.put("timeStamp", System.currentTimeMillis() + "");

                    /**************** this reference2 is to create the notification msgs for clients  *********/
                    reference2 = new Firebase(ChatOnGoConstants.FIREBASE_PATH + ChatOnGoConstants.FIREBASE_T_NOTIFICATION + "/" +
                            getString(R.string.mall_id));
                    reference2.push().setValue(hashNoti);
                    /**************** this reference2 is to create the notification msgs for user itself  *********/
                    reference3 = new Firebase(ChatOnGoConstants.FIREBASE_PATH + ChatOnGoConstants.FIREBASE_T_NOTIFICATION + "/" +
                            selectedUserId);
                    reference3.push().setValue(hashNoti);

                    FirebaseDatabase.getInstance().getReference("UsersChat")
                            .child(mallId)
                            .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                            .child(selectedUserId)
                            .child(ChatOnGoConstants.FIREBASE_T_MESSAGE)
                            .setValue(etMessage.getText().toString());

                    FirebaseDatabase.getInstance().getReference("UsersChat")
                            .child(selectedUserId)
                            .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                            .child(mallId)
                            .child(ChatOnGoConstants.FIREBASE_T_MESSAGE)
                            .setValue(etMessage.getText().toString());
                       /* FirebaseDatabase.getInstance().getReference("UsersChat")
                                .child(selectedUserId)
                                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                                .child(mallId)
                                .child(ChatOnGoConstants.FIREBASE_K_LASTTIMESTAMP)
                                .setValue(Utils.getCurrentTime(ChatOnGoConstants.TIME_MMDDYYYY_HH_MM_SS));*/

                    etMessage.setText("");


                        /*FirebaseDatabase.getInstance().getReference("UsersChat")
                                .child(mallId)
                                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                                .child(selectedUserId)
                                .child(ChatOnGoConstants.FIREBASE_K_LASTTIMESTAMP)
                                .setValue(Utils.getCurrentTime(ChatOnGoConstants.TIME_MMDDYYYY_HH_MM_SS));*/
                    FirebaseDatabase.getInstance().getReference("UsersChat")
                            .child(mallId)
                            .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                            .child(selectedUserId)
                            .child("msg_id")
                            .setValue(msgId);
                        /*FirebaseDatabase.getInstance().getReference("UsersChat")
                                .child(mallId)
                                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                                .child(selectedUserId)
                                .child("imageUrl")
                                .setValue(selectedUserProfPic);*/
                        /*FirebaseDatabase.getInstance().getReference("UsersChat")
                                .child(mallId)
                                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                                .child(selectedUserId)
                                .child("name")
                                .setValue(selectedUserName);
                        FirebaseDatabase.getInstance().getReference("UsersChat")
                                .child(mallId)
                                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)      //
                                .child(selectedUserId)
                                .child("userId")
                                .setValue(selectedUserId);*/


                    /*FirebaseDatabase.getInstance().getReference()
                            .child("UsersChat")
                            .child(selectedUserId)                     //1907
                            .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)
                            .child(Utils.getMallId(mContext))           //102
                            .child(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT)
                            .setValue(++selectedUserUnread);*/


//                    new SendNotificationToTopicAsync(mContext.getString(R.string.fcm_auth_key),
//                            selectedUserId,
//                            mPref.getString(ChatOnGoConstants.Name, "")+" "
//                                    +"has sent Message",selectedUserName
//                            ,selectedUserId,selectedUserProfPic).execute();


                    new SendNotificationWithPayLoadAsync(mContext.getString(R.string.fcm_auth_key),
                            selectedUserId, hashNoti).execute();

                   /* new SendNotificationToTopicAsync(mContext.getString(R.string.fcm_auth_key), selectedUserId,
                            mPref.getString(ChatOnGoConstants.Name, "") + " "
                                    + "sent a new message"
                            , mPref.getString(ChatOnGoConstants.Name, "")
                            , mallId
                            , selectedUserProfPic
                            , selectedUserUnread
                    ).execute();*/
//                    Toast.makeText(mContext, "Posted", Toast.LENGTH_SHORT).show();


                } else {
                    Utils.toast("please enter some text", mContext);
                }
            }
        });

         getUserChatId();
    }


    private void getUserChatId() {
        final DatabaseReference databaseRefSelectedUser = firebaseDatabase.getReference(ChatOnGoConstants.FIREBASE_T_USERS)
                .child(mPref.getString(ChatOnGoConstants.PREF_USER_ID, ""))
                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS).child(selectedUserId);

        final DatabaseReference databaseRefLoggedInUser = firebaseDatabase.getReference(ChatOnGoConstants.FIREBASE_T_USERS)
                .child(selectedUserId)
                .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS).child(mPref.getString(ChatOnGoConstants.PREF_USER_ID, ""));

        databaseRefSelectedUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.child(ChatOnGoConstants.FIREBASE_K_MSG_ID).getValue() != null) {
                    // get chat id with user
                    msgId = dataSnapshot.child(ChatOnGoConstants.FIREBASE_K_MSG_ID).getValue().toString();

                    // here create chat for user or retrieve all chats and show in recycler
                    getChatMessages(msgId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseRefSelectedUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.child(ChatOnGoConstants.FIREBASE_K_MSG_ID).getValue() != null) {

                } else {
                    // create child for chat id
                    msgId = System.currentTimeMillis() + "";
                    HashMap<String, Object> haspMap = new HashMap<>();
                    haspMap.put(ChatOnGoConstants.FIREBASE_K_MSG_ID, msgId);
                    haspMap.put(ChatOnGoConstants.FIREBASE_K_U_ID, selectedUserId);
                    haspMap.put(ChatOnGoConstants.FIREBASE_K_NAME, selectedUserName);
                    haspMap.put(ChatOnGoConstants.FIREBASE_K_IMAGE_URL, selectedUserProfPic);
                    //haspMap.put(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT, 0);
                    haspMap.put(ChatOnGoConstants.FIREBASE_T_MESSAGE, lastmessage);
                    haspMap.put(ChatOnGoConstants.FIREBASE_K_U_UID, mallId);
                    databaseRefSelectedUser.setValue(haspMap);
                    // insert values for selected user

                    HashMap<String, Object> haspMapSelectedUser = new HashMap<>();
                    haspMapSelectedUser.put(ChatOnGoConstants.FIREBASE_K_MSG_ID, msgId);
                    haspMapSelectedUser.put(ChatOnGoConstants.FIREBASE_K_U_ID, mallId);
                    haspMapSelectedUser.put(ChatOnGoConstants.FIREBASE_K_NAME, userName);
                    haspMapSelectedUser.put(ChatOnGoConstants.FIREBASE_K_IMAGE_URL, SharedPref.read(ChatOnGoConstants.PREF_PROFILE_IMAGE, ""));
                    // haspMapSelectedUser.put(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT, 0);
                    haspMapSelectedUser.put(ChatOnGoConstants.FIREBASE_T_MESSAGE, lastmessage);
                    haspMap.put(ChatOnGoConstants.FIREBASE_K_U_UID, mallId);


                    databaseRefLoggedInUser.setValue(haspMapSelectedUser);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages(final String msgId) {
        DatabaseReference databaseChatRef = firebaseDatabase.getReference(ChatOnGoConstants.FIREBASE_T_MESSAGES)
                .child(msgId);
        databaseChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // collect al data and form recycler view from here
                ArrayList<ChatDto> chatDtos = new ArrayList<ChatDto>();
                if (dataSnapshot != null)
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ChatDto chatDto = new ChatDto();
                        try {
                            chatDto.msgId = msgId;
                            chatDto.key = dataSnapshot1.getKey();
                            chatDto.message = dataSnapshot1.child("message").getValue().toString();
//                            chatDto.name = dataSnapshot1.child("name").getValue().toString();
                            chatDto.senderId = dataSnapshot1.child("senderId").getValue().toString();
                            //chatDto.image = dataSnapshot1.child("image_url").getValue().toString();
                            chatDto.timeStamp = dataSnapshot1.child("timeStamp").getValue().toString();
                            if (dataSnapshot1.hasChild("read"))
                                chatDto.read = (boolean) dataSnapshot1.child("read").getValue();
                            else chatDto.read = false;

                        } catch (Exception e) {

                        }
                        chatDtos.add(chatDto);
                    }
                setAdapterForRV(chatDtos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void setAdapterForRV(ArrayList<ChatDto> chatDtos) {
        RecyclerView.LayoutManager layoutManager;


        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(chatDtos, mContext, selectedUserId);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        if (chatDtos != null && chatDtos.size() > 0)
            recyclerView.scrollToPosition(chatDtos.size() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();


    }


   /* public void uploadFileToFirebase(String filePath) throws FileNotFoundException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        File file = new File(filePath);
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://stdfacchat.appspot.com");
        StorageReference mountainsRef = storageRef.child("files/" + file.getName());


        InputStream stream = new FileInputStream(file);

        UploadTask task = mountainsRef.putStream(stream);


        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

//                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                final String url = String.valueOf(downloadUrl);
                Log.i(TAG, "onSuccess: " + downloadUrl.toString() + "  " + url);
                // Toast.makeText(Home.this, "Uploaded file", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();

            }
        });

    }
*/
}
