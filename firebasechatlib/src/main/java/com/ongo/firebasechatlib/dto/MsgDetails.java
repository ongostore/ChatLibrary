package com.ongo.firebasechatlib.dto;

/**
 * Created by Designer on 8/20/2016.
 */
public class MsgDetails {

    public String msg_id;
    public String msg_to_userid;
    public String msg_last;
    public String msg_upload_time;
    public String msg_del_time;
    public String msg_to_username;
    public String msg_to_userimg;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getMsg_to_userimg() {
        return msg_to_userimg;
    }

    public void setMsg_to_userimg(String msg_to_userimg) {
        this.msg_to_userimg = msg_to_userimg;
    }

    public String getMsg_del_time() {
        return msg_del_time;
    }

    public void setMsg_del_time(String msg_del_time) {
        this.msg_del_time = msg_del_time;
    }

    public String getMsg_last() {
        return msg_last;
    }

    public void setMsg_last(String msg_last) {
        this.msg_last = msg_last;
    }

    public String getMsg_to_id() {
        return msg_to_userid;
    }

    public String getMsg_to_userid() {
        return msg_to_userid;
    }

    public void setMsg_to_userid(String msg_to_userid) {
        this.msg_to_userid = msg_to_userid;
    }

    public String getMsg_upload_time() {
        return msg_upload_time;
    }

    public void setMsg_upload_time(String msg_upload_time) {
        this.msg_upload_time = msg_upload_time;
    }

    public void setMsg_to_id(String msg_to_id) {
        this.msg_to_userid = msg_to_id;
    }

    public String getMsg_to_username() {
        return msg_to_username;
    }

    public void setMsg_to_username(String msg_to_username) {
        this.msg_to_username = msg_to_username;
    }

    public MsgDetails(String id, String to_id, String msg_last,String upload, String del_time,
                      String username, String userimg){
        this.msg_id = id;
        this.msg_to_userid = to_id;
        this.msg_last = msg_last;
        this.msg_upload_time = upload;
        this.msg_del_time = del_time;
        this.msg_to_username = username;
        this.msg_to_userimg = userimg;

    }
}
