package com.ongo.firebasechatlib.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ongo.firebasechatlib.R;
import com.ongo.firebasechatlib.activities.ChatActivity;
import com.ongo.firebasechatlib.dto.UserDto;
import com.ongo.firebasechatlib.utils.ChatOnGoConstants;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**

 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    private Context mContext;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences mPref;
    private SharedPreferences.Editor mPref_Editor;
    ArrayList<UserDto> chatDtoArrayList;
    DatabaseReference dRchat;
    private boolean isEdit = false;
    String userid;

    public Set<String> selectedUids = new HashSet<>();

    public ChatListAdapter(ArrayList<UserDto> chatDtoArrayList, Context mContext) {

        this.mContext = mContext;

        this.chatDtoArrayList = chatDtoArrayList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        mPref = mContext.getSharedPreferences(ChatOnGoConstants.PREF_NAME, Context.MODE_PRIVATE);
        mPref_Editor = mPref.edit();

    }

    public void setEditable(boolean editable) {
        isEdit = editable;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chatlist_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {

          /*  final String imageUrl = jsonObject.getString("Image");
            final String userId = jsonObject.getString("id");*/
            String mallId = mPref.getString(ChatOnGoConstants.PREF_USER_ID, "");
//............................
            firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference reference = firebaseDatabase.getReference()
                    .child("UsersChat")
                    .child(mallId)
                    .child(ChatOnGoConstants.FIREBASE_K_MSGGRPIDS)
                    .child(chatDtoArrayList.get(position).uid);


            if (isEdit) {
                holder.check_box.setVisibility(View.VISIBLE);
            } else {
                holder.check_box.setVisibility(View.GONE);
            }
//checkbox
            holder.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    chatDtoArrayList.get(position).isChecked = isChecked;
                    if (isChecked) {
                        selectedUids.add(chatDtoArrayList.get(position).uid);
                    } else {
                        selectedUids.remove(chatDtoArrayList.get(position).uid);
                    }

                }
            });


            final String imageUrl = chatDtoArrayList.get(position).getImageUrl();
            final String name = chatDtoArrayList.get(position).getName();

            if (imageUrl != null & !imageUrl.isEmpty()) {


                Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.ic_launcher).into(holder.profile_image);

            } /*else {

                Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.ic_launcher).into(holder.profile_image);
            }*/
            //final String name = jsonObject.getString("Name");

            dRchat = firebaseDatabase.getReference("Messages").child(chatDtoArrayList.get(position).getMsg_id());
            Query lastQuery = dRchat.orderByKey().limitToLast(1);
            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String message = "";
                    String timestamp = "";
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        message = snapshot.child("message").getValue().toString();
                        timestamp = snapshot.child("timeStamp").getValue().toString();
                        Log.e(">>>>>>>", "last message " + message);
                        holder.tvRecentChat.setText(message);
                        SimpleDateFormat spf = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
                        Date newDate1 = null;
                        try {
                            newDate1 = spf.parse(timestamp);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        spf = new SimpleDateFormat("dd MMM");
                        if(newDate1!=null){
                        String date1 = spf.format(newDate1);
                        holder.tvtime.setText(date1);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            holder.tvUsername.setText(name);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        if (dataSnapshot != null ? true : false) {
                           /* if (dataSnapshot.hasChild(RBAConstants.FIREBASE_K_LASTTIMESTAMP)) {
                                String time = dataSnapshot.child(RBAConstants.FIREBASE_K_LASTTIMESTAMP).getValue().toString();

                                holder.tvtime.setText(time);
                            }*/

                            if (dataSnapshot.hasChild(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT)) {
                                Long unreadCount = (Long) dataSnapshot.child(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT).getValue();
                                if (unreadCount != 0) {
                                    holder.tvCount.setVisibility(View.VISIBLE);
                                    holder.tvCount.setText(unreadCount + "");
                                } else {
                                    holder.tvCount.setVisibility(View.GONE);
                                }
                            }
                          /*  if (dataSnapshot.hasChild(RBAConstants.FIREBASE_T_MESSAGE)) {
                                String lastmessage = dataSnapshot.child(RBAConstants.FIREBASE_T_MESSAGE).getValue().toString();

                                holder.tvRecentChat.setText(lastmessage);
                            }*/
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //.......................................
         /*   DatabaseReference databaseReference = firebaseDatabase.getReference(RBAConstants.FIREBASE_T_MESSAGES).child(chatDtoArrayList.get(position).msg_id);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    try {
                        if (dataSnapshot != null ? true : false) {
                            String message=null;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                message = snapshot.child("message").getValue().toString();

                            }


                            if (message.startsWith("#*<>#")) {

                                String couponMessageArr[] = message.split("#\\*<>#");


                                if (couponMessageArr != null ? couponMessageArr.length >= 2 : false){
                                    holder.tvRecentChat.setText(couponMessageArr[2]);

                                }else{
                                    holder.tvRecentChat.setText(message);

                                }

                            }else{
                                holder.tvRecentChat.setText(message);
                            }
                        }
                    } catch (Exception e) {


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
*/

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ChatActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString(ChatOnGoConstants.SELECTED_USER_ID, chatDtoArrayList.get(position).uid);
                    bundle.putString(ChatOnGoConstants.SELECTED_USER_PROFPIC, imageUrl);
                    bundle.putString(ChatOnGoConstants.SELECTED_USER_USERNAME, name);
                    bundle.putLong(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT, Long.parseLong(holder.tvCount.getText().toString()));
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chatDtoArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvRecentChat, tvtime, tvUsername, tvCount;
        /*CardView cardView;*/
        ImageView profile_image;
        public CheckBox check_box;

        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = (ImageView) itemView.findViewById(R.id.profile_image);
            /*cardView = (CardView) itemView.findViewById(R.id.cardView);*/
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvtime = (TextView) itemView.findViewById(R.id.tvtime);
            tvCount = (TextView) itemView.findViewById(R.id.tvCount);
            tvRecentChat = (TextView) itemView.findViewById(R.id.tvRecentChat);
            check_box = (CheckBox) itemView.findViewById(R.id.check_box);
        }
    }

    public void deleteItems() {


        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference userDataRef = firebaseDatabase.getReference("UsersChat").child(mPref.getString(ChatOnGoConstants.PREF_USER_ID, "")).child("msg_grp_ids");

        if (selectedUids.size() > 0) {
            Iterator<String> iterator = selectedUids.iterator();

            if (iterator != null)
                do {
                    String uid = iterator.next();
                    userDataRef.child(uid).removeValue();
                }
                while (iterator.hasNext());

        }


    }

}