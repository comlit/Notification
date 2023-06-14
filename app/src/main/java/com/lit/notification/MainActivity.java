package com.lit.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import static com.lit.notification.App.CHANNEL_1_ID;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    Button btn;
    NotificationManagerCompat notificationManager;
    EditText editText;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    ArrayList<String> reminders;
    SharedPreferences sharedPref;
    View parentLayout;
    String text;

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEND:
                    sendNotification(editText.getText().toString());
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = NotificationManagerCompat.from(this);
        btn = findViewById(R.id.button);
        editText = findViewById(R.id.editTxt);
        recyclerView = findViewById(R.id.recyclerView);
        parentLayout = findViewById(android.R.id.content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        String reminderstr = sharedPref.getString("reminder", "");
        reminders = new ArrayList<String>(Arrays.asList(reminderstr.split(";&%/=;")));

        adapter = new CustomAdapter(reminders);
        recyclerView.setAdapter(adapter);
        enableSwipeToDeleteAndUndo();

        editText.setOnEditorActionListener(editorListener);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendNotification(editText.getText().toString());
            }
        });
    }

    private void sendNotification(String pText)
    {
        text = pText;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentText(text)
                .setLights(Color.RED, 100, 100)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .build();
        notificationManager.notify((int)(Math.random()*1000), notification);
        finish();
        //System.exit(0);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final String item = adapter.getData().get(position);

                adapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(parentLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sharedPref.edit();
        String temp = "";
        if(text != null)
            temp = text + ";&%/=;" + String.join(";&%/=;", reminders);
        else
            temp = String.join(";&%/=;", reminders);
        editor.putString("reminder", temp);
        editor.apply();
    }
}