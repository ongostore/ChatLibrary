package com.ongo.firebasechatlib.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gufran khan on 23-10-2018.
 */

public class Utils {

    private static ProgressDialog mProgressDialog;
    public static String getTimeFromMilliseconds(String milliseconds) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ChatOnGoConstants.TIME_MMDDYYYY_HH_MM_SS);
            long milliSeconds = Long.parseLong(milliseconds);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return "";
    }
    public static HashMap<String, Object> toMap(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
    public static void toast(String msg, Context ctx) {
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        try {
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(16);
            /*setFont(toastTV, roboto, "TextView");*/
            toast.show();
        } catch (Exception e) {
        } finally {
            toast.show();
        }
    }
    public static void hideProgress(Context mContext) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                mProgressDialog.dismiss();

            }
        }

    }

    public static void showProgress(Context mContext, Boolean val) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(val);
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (mProgressDialog != null ? !mProgressDialog.isShowing() : false)
            mProgressDialog.show();
    }
}
