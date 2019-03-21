package com.ongo.firebasechatlib.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.ongo.firebasechatlib.R;
import com.ongo.firebasechatlib.dto.ChatDto;
import com.ongo.firebasechatlib.utils.OnGoConstants;
import com.ongo.firebasechatlib.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**

 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {


    private final ArrayList<ChatDto> chatDtoArrayList;
    private Context mContext;
    SharedPreferences mPref;
    String selectedUserId;

    public ChatAdapter(ArrayList<ChatDto> chatDtoArrayList, Context mContext, String selectedUserId) {
        this.mContext = mContext;
        this.chatDtoArrayList = chatDtoArrayList;
        this.selectedUserId = selectedUserId;
        mPref = mContext.getSharedPreferences(OnGoConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_new, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        final String message = chatDtoArrayList.get(position).message;
        final String time = chatDtoArrayList.get(position).timeStamp;
        final String image = chatDtoArrayList.get(position).image;

        holder.imgDelete.setVisibility(View.GONE);
        holder.tvMessage.setText(Html.fromHtml(message));
        if (time != null && !time.isEmpty()) {
            holder.tvmsgtime.setText(Utils.getTimeFromMilliseconds(time));
        }
        holder.imgDelete.setVisibility(View.GONE);
        if (message != null && !message.isEmpty()) {
            holder.tvMessage.setText(Html.fromHtml(message));
        }
        if (image != null && !image.isEmpty()) {
            Picasso.with(mContext).load(image).placeholder(R.drawable.ic_launcher).into(holder.image_r);
        }
        /*Picasso.with(mContext).load(image)*//*.placeholder(R.mipmap.load_small)*//*.into(holder.image_r);*/


        if (chatDtoArrayList.get(position).senderId.equalsIgnoreCase(mPref.getString(OnGoConstants.PREF_USER_ID, ""))) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();

            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            holder.cardView.setLayoutParams(params);
            /*holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.chatmsg_green));*/
            //holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgDelete.setVisibility(View.GONE);

            if (chatDtoArrayList.get(position).read) {
                holder.imgStausSent.setVisibility(View.VISIBLE);
                holder.imgMsgStatus.setVisibility(View.GONE);
            }

        } else {
            // which means other user sent me a message which i have to set status for
            FirebaseDatabase.getInstance().getReference(OnGoConstants.FIREBASE_T_MESSAGES)
                    .child(chatDtoArrayList.get(position).msgId)
                    .child(chatDtoArrayList.get(position).key).child(OnGoConstants.READ).setValue(true);
        }


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage("Are you sure you want to Delete Msg?");
                builder.setTitle("Delete Messaging");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        FirebaseDatabase.getInstance().getReference(OnGoConstants.FIREBASE_T_MESSAGES)
                                .child(chatDtoArrayList.get(position).msgId)
                                .child(chatDtoArrayList.get(position).key).setValue(null);

                        FirebaseDatabase.getInstance().getReference(OnGoConstants.FIREBASE_T_USERS)
                                .child(selectedUserId)
                                .child(OnGoConstants.FIREBASE_K_MSGGRPIDS)
                                .child(mContext.getString(R.string.mall_id))
                                .child(OnGoConstants.FIREBASE_T_MESSAGE).setValue(null);

                        FirebaseDatabase.getInstance().getReference(OnGoConstants.FIREBASE_T_USERS)
                                .child(mContext.getString(R.string.mall_id))
                                .child(OnGoConstants.FIREBASE_K_MSGGRPIDS)
                                .child(selectedUserId)
                                .child(OnGoConstants.FIREBASE_T_MESSAGE).setValue(null);
                        dialog.dismiss();
                    }


                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button

                        dialog.dismiss();
                    }
                });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                //alert.setTitle("AlertDialogExample");
                alert.show();


            }
        });

    }


    @Override
    public int getItemCount() {
        return chatDtoArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvmsgtime;
        ImageView image_r;
        CardView cardView;

        ImageView imgDelete, imgStausSent, imgMsgStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvmsgtime = (TextView) itemView.findViewById(R.id.tvmsgtime);
            image_r = (ImageView) itemView.findViewById(R.id.image_r);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);
            imgStausSent = (ImageView) itemView.findViewById(R.id.staussent);
            imgMsgStatus = (ImageView) itemView.findViewById(R.id.imgMsgStatus);

        }
    }


}
