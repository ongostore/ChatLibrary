package com.ongo.firebasechatlib.dto;

public class UserDto {
    public String name;
    public String email;
    public String imageUrl;
    public String msg_id;
    public String message;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String uid;
    public boolean isChecked;

    public String getLast_time_stamp() {
        return last_time_stamp;
    }

    public void setLast_time_stamp(String last_time_stamp) {
        this.last_time_stamp = last_time_stamp;
    }

    public String last_time_stamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
