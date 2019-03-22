package com.ongo.firebasechatlib.async_tasks;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotificationWithPayLoadAsync extends AsyncTask<Void, Void, Void> {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private String authKey;
    private String messaage, topic;
   /* private final Long unreadCount;
    private String messaage, topic;
    private final String name;
    private final String profPic;
    private final String userId;*/

    HashMap<String, Object> hashMap = new HashMap<>();

    /**
     * @param authKey
     * @param topic
     * @param message
     */

    public SendNotificationWithPayLoadAsync(String authKey, String topic, HashMap<String, Object> hashMap) {
       /* this.authKey = authKey;
        this.messaage = message;
        this.topic = topic;
        this.name=name;
        this.profPic=profPic;
        this.userId=userId;
        this.unreadCount=unreadCount;*/

        this.authKey = authKey;
        this.topic = topic;
        this.hashMap = hashMap;

    }

    String post(String json) throws IOException {

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", authKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    String result;

    @Override
    protected Void doInBackground(Void... voids) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/" + topic);
            JSONObject jsonNotification = new JSONObject(hashMap);

            /*jsonNotification.put("body", messaage);
            jsonNotification.put("title", "New Message");
            jsonNotification.put("sound", "default");
            jsonNotification.put(ChatOnGoConstants.SELECTED_USER_USERNAME, name);
            jsonNotification.put(ChatOnGoConstants.SELECTED_USER_PROFPIC, profPic);
            jsonNotification.put(ChatOnGoConstants.SELECTED_USER_ID, userId);
            jsonNotification.put(ChatOnGoConstants.FIREBASE_K_UNREADCOUNT, unreadCount);*/

            jsonObject.put("data", jsonNotification);
            Log.e("jsonobj", ">>>>>>>>>>>>>....." + jsonObject.toString());
            result = post(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("SendNotificationWithPayLoadAsync", "onPostExecute: " + result);
    }
}
