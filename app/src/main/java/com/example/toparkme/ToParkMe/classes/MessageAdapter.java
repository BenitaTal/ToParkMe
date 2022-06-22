package com.example.toparkme.ToParkMe.classes;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.toparkme.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageAdapter extends ArrayAdapter<Chat> {
    public MessageAdapter(Context context, ArrayList<Chat> users) {
        super(context, 0, users);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Chat chat = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.message_user);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.message_text);
        TextView tvTime = (TextView) convertView.findViewById(R.id.message_time);

        // Populate the data into the template view using the data object
        tvName.setText(chat.getNickName());
        tvName.setTextColor(Color.MAGENTA);

        tvMessage.setText(chat.getMessage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm:ss", Locale.ENGLISH);
            tvTime.setText(df.format(chat.getTime()));
            tvTime.setTextColor(Color.BLACK);
        }


        // Return the completed view to render on screen
        return convertView;
    }
}
