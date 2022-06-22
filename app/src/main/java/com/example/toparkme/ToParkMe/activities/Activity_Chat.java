package com.example.toparkme.ToParkMe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.toparkme.R;
import com.example.toparkme.ToParkMe.classes.Chat;
import com.example.toparkme.ToParkMe.classes.AppManager;
import com.example.toparkme.ToParkMe.classes.MessageAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Activity_Chat extends AppCompatActivity {


    private MaterialButton chat_back_btn;
    private MaterialButton chat_submit_btn;
    private EditText chat_search_et;
    private ListView chat_list_lv;


    private Intent intent;
    private AppManager manager;
    private FirebaseUser fUser;
    private DatabaseReference reference;
    private String userID;

    private ArrayList<Chat> listOfData;
    private String currentMessages;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCurrentUsersData();
        setContentView(R.layout.activity_chat);
        getRealTimeChat();
        findViews();

        chat_back_btn.setOnClickListener(chatBackToHomeBtn);
        chat_submit_btn.setOnClickListener(chatSubmitBtn);
    }


    public void getRealTimeChat(){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentMessages = snapshot.toString();
                Chat[] chats = null;

                try {
                    chats = stringToChats(currentMessages);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                listOfData = new ArrayList<Chat>(Arrays.asList(chats));
                Collections.sort(listOfData,Chat.dateComparator);
                adapter = new MessageAdapter(Activity_Chat.this, listOfData);
                chat_list_lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Chat.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getCurrentUsersData(){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = fUser.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                manager = snapshot.getValue(AppManager.class);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Chat.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveChatData(){
        FirebaseDatabase.getInstance().getReference("Chat")
                .child(manager.getChat().getTime().toString()).child("message")
                .setValue(manager.getChat().getMessage());
        FirebaseDatabase.getInstance().getReference("Chat")
                .child(manager.getChat().getTime().toString()).child("nickName")
                .setValue(manager.getChat().getNickName());

    }

    public Chat[] stringToChats(String snap) throws ParseException {
        Chat[] theChat;
        String str = snap.substring(35);
        int count = countSubStringInString(str,"GMT+");
        theChat = new Chat[count];
        String newStr = str;
        int lastIndex;
        //todo
        for (int i = 0; i < count; i++) {
            lastIndex = newStr.indexOf("},") + 2;
            if (i == (count-1)){
                lastIndex = newStr.indexOf("}} }") + 4;
            }
            theChat[i] = makeChatFromString(i,count,newStr);
            newStr = newStr.substring(lastIndex);

        }

        return theChat;
    }

    private Chat makeChatFromString(int i,int count,String str) throws ParseException {
        Chat theChat = new Chat();
        int lastIndex = str.indexOf("},");
        if (i == (count-1)){
            lastIndex = str.indexOf("}} }");
        }
        int firstIndex = findFirstLetterPosition(str);
        String newStr = str.substring(firstIndex,lastIndex);
        theChat.setTime(makeDateFromString(newStr));
        theChat.setNickName(makeNickNameFromString(newStr));
        theChat.setMessage(makeMessageFromString(newStr));

        return theChat;
    }

    public int findFirstLetterPosition(String input) {
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if ((c >= 'A' && c <= 'Z') ) {
                return i;
            }
        }
        return -1; // not found
    }

    private String makeMessageFromString(String str) {
        String message;
        int firstIndex = str.indexOf("message=") + 8;
        String newStrMessage = str.substring(firstIndex);
        message = newStrMessage;
        return message;
    }

    private String makeNickNameFromString(String str) {
        String nickName;
        int lastIndex = str.indexOf(",");
        int firstIndex = str.indexOf("nickName=") + 9;
        String newStrNickName = str.substring(firstIndex,lastIndex);
        nickName= newStrNickName;
        return nickName;
    }

    private Date makeDateFromString(String str) throws ParseException {
        Date date;
        int lastIndex = str.indexOf("=");
        String newStrDate = str.substring(0,lastIndex);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH);
        date = formatter.parse(newStrDate);
        return date;
    }

    public int countSubStringInString(String snap, String chars){
        int index = snap.indexOf(chars);
        int count = 0;
        while (index != -1) {
            count++;
            snap = snap.substring(index + 1);
            index = snap.indexOf(chars);
        }
        return count;
    }

    private View.OnClickListener chatSubmitBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard(view);
            manager.getChat().setTheAllMessage(chat_search_et.getText().toString(),manager.getChat().getNickName());
            saveChatData();

            listOfData.add(manager.getChat());
            adapter.notifyDataSetChanged();

            chat_search_et.setText("");
            getRealTimeChat();

        }
    };


    private View.OnClickListener chatBackToHomeBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard(view);
            returnToHomeActivity();
        }
    };


    private void returnToHomeActivity() {
        intent = new Intent(this, Activity_Home.class);
        startActivity(intent);
        finish();
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }


    private void findViews() {
        chat_back_btn = findViewById(R.id.park_chat_back_btn);
        chat_submit_btn = findViewById(R.id.park_chat_send_btn);
        chat_search_et = findViewById(R.id.park_chat_input_et);
        chat_list_lv = findViewById(R.id.park_list_of_messages);
    }

}
