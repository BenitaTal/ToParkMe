package com.example.toparkme.ToParkMe.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.toparkme.R;
//import com.example.toparkme.ToParkMe.classes.Auth;
import com.example.toparkme.ToParkMe.classes.AppManager;
import com.example.toparkme.ToParkMe.fragment.MapsFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.type.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Activity_Free extends AppCompatActivity {

    private static final String BACKGROUND_URL = "https://pixabay.com/get/gdfef60270a5665c070066c788bc292cafa2d67e92c00caccc92058adbc2e5aa60bfae945a1dbf1b8b5d41a8bc6fec55dad3fc0f20e249cec8df4154f8989ff4f00692f34c40754ffe3c565f45b6c2fb6_1920.jpg";

    private MaterialButton favorite_back_btn;
    private ImageView main_IMG_glide;

    private Intent intent;
    private AppManager manager;
    private FirebaseUser fUser;
    private DatabaseReference reference;
    private String userID;

    private String currentMessages;
    private ArrayList<TableRow> tableRows;
    private TableLayout table;

    private MapsFragment mapsFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free);
        getRealTimeChat();
        setViews();
        setHeaders();
        getCurrentUsersData();


        favorite_back_btn.setOnClickListener(favoriteBackToHomeBtn);
    }


    private View.OnClickListener favoriteBackToHomeBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            returnToHomeActivity();
        }
    };


    private void returnToHomeActivity() {
        intent = new Intent(this, Activity_Home.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        favorite_back_btn = findViewById(R.id.park_favorite_back_btn);
        table = findViewById(R.id.park_TBL_favorite);
    }

    private void setViews() {
        setGlide();
        findViews();

    }

    private void setGlide(){
        main_IMG_glide = findViewById(R.id.park_IMG_background_free);
        Glide
                .with(this)
                .load(BACKGROUND_URL)
                .centerCrop()
                .into(main_IMG_glide);
    }


    public void getRealTimeChat(){

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Locations");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentMessages = snapshot.toString();
                AppManager[] managers = null;

                try {
                    managers = stringToManager(currentMessages);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                setTableWithData(managers);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Free.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public AppManager[] stringToManager(String snap) throws ParseException {
        AppManager[] managers;
        String str = snap.substring(40);
        int count = countSubStringInString(str,"GMT+");
        managers = new AppManager[count];
        String newStr = str;
        int lastIndex;

        for (int i = 0; i < count; i++) {
            lastIndex = newStr.indexOf("},") + 2;
            if (i == (count-1)){
                lastIndex = newStr.indexOf("}} }") + 4;
            }
            managers[i] = makeManagerFromString(i,count,newStr);
            newStr = newStr.substring(lastIndex);

        }

        return managers;
    }

    @NonNull
    private AppManager makeManagerFromString(int i, int count, String str) throws ParseException {
        AppManager theChat = new AppManager();
        int lastIndex = str.indexOf("},") + 2;
        if (i == (count-1)){
            lastIndex = str.indexOf("}} }") + 4;
        }
        int firstIndex = findFirstLetterPosition(str);
        String newStr = str.substring(firstIndex,lastIndex);
        theChat.getChat().setTime(makeDateFromString(newStr));
        theChat.getPerson().getParkingLocation().setAddressName(makeLocationFromString(newStr));

        return theChat;
    }

    private String makeLocationFromString(String str) {
        String message;
        int firstIndex = str.indexOf(",") + 14;
        int lastIndex = str.indexOf("longitude");
        String newStrMessage = str.substring(firstIndex,lastIndex);
        message = newStrMessage;
        return message;
    }


    private Date makeDateFromString(String str) throws ParseException {
        Date date;
        int lastIndex = str.indexOf("=");
        String newStrDate = str.substring(0,lastIndex);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        date = formatter.parse(newStrDate);
        return date;
    }

    public int findFirstLetterPosition(String input) {
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if ((c == 'M' || c == 'T' || c == 'W' || c == 'S' || c == 'F') ) {
                return i;
            }
        }
        return -1; // not found
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



    public void getCurrentUsersData(){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = fUser.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                manager = snapshot.getValue(AppManager.class);
                setTable();
                saveLocationData();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Free.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveLocationData(){
        FirebaseDatabase.getInstance().getReference("Locations")
                .child(manager.getChat().getTime().toString()).child("Parking Location")
                .setValue(manager.getPerson().getParkingLocation());

    }

    private void setHeaders() {
        TableRow tableRow = new TableRow(Activity_Free.this);

        MaterialTextView timeCol = new MaterialTextView(Activity_Free.this);
        MaterialTextView locationCol = new MaterialTextView(Activity_Free.this);

        timeCol.setText("תאריך ושעה");
        timeCol.setTextSize(10);
        timeCol.setPadding(16,16,16,16);

        locationCol.setText("כתובת");
        locationCol.setTextSize(10);
        locationCol.setPadding(16,16,16,16);

        tableRow.addView(timeCol);
        tableRow.addView(locationCol);

        table.addView(tableRow);
    }

    private void setTable(){
        TableRow tableRow = new TableRow(Activity_Free.this);
        MaterialTextView timeColData = new MaterialTextView(Activity_Free.this);
        MaterialTextView locationColData = new MaterialTextView(Activity_Free.this);
        locationColData.setTextIsSelectable(true);


        SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm:ss", Locale.ENGLISH);
        //manager.getChat().getTime()
        timeColData.setText(df.format(new Date().getTime()));

        locationColData.setText(manager.getPerson().getParkingLocation().getAddressName());

        timeColData.setPadding(16,16,16,16 );
        locationColData.setPadding(16,16,16,16);
        locationColData.setTextColor(Color.BLUE);

        tableRow.addView(timeColData);
        tableRow.addView(locationColData);

        table.addView(tableRow);

    }

    private void setTableWithData(AppManager[] managers){
        for (int j = 0; j < managers.length; j++) {

            TableRow tableRow = new TableRow(Activity_Free.this);
            MaterialTextView timeColData = new MaterialTextView(Activity_Free.this);
            MaterialTextView locationColData = new MaterialTextView(Activity_Free.this);
            locationColData.setTextIsSelectable(true);

            SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm:ss", Locale.ENGLISH);
            timeColData.setText(df.format(managers[j].getChat().getTime()));
            locationColData.setText(managers[j].getPerson().getParkingLocation().getAddressName());

            timeColData.setPadding(16,16,16,16 );
            locationColData.setPadding(16,16,16,16);
            locationColData.setTextColor(Color.BLUE);


            tableRow.addView(timeColData);
            tableRow.addView(locationColData);

            table.addView(tableRow);
        }

    }


}
