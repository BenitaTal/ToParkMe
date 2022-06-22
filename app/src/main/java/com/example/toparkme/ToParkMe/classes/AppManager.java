package com.example.toparkme.ToParkMe.classes;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

public class AppManager {

    private Person person;
    private Chat chat;



    public AppManager(Person person) {
        setPerson(person);
        setChat(new Chat());
    }

    public AppManager() {
        setPerson(new Person());
        setChat(new Chat());
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }


}
