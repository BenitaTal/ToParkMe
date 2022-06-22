package com.example.toparkme.ToParkMe.classes;

import java.util.Comparator;
import java.util.Date;

public class Chat {
    private String message;
    private Date time;
    private String nickName;

    public Chat() {
        this.message = "message";
        this.time = new Date();
        this.nickName = "nickName";
    }

    public Chat(Date time) {
        this.message = "message";
        this.time = time;
        this.nickName = "nickName";

    }

    public Chat(String message ,Date time, String nickName) {
        setNickName(message);
        setTime(time);
        setNickName(nickName);

    }

    public void setTheAllMessage(String message,String nickName){
        setMessage(message);
        this.time = new Date();
        setNickName(nickName);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public static Comparator<Chat> dateComparator = new Comparator<Chat>() {
        @Override
        public int compare(Chat jc1, Chat jc2) {
            if (jc2.getTime().compareTo(jc1.getTime()) < 0){
                return 1;
            }
            return -1;
        }
    };
}
