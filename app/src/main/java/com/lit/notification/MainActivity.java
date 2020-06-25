package com.lit.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import com.google.android.material.textfield.TextInputEditText;

import static com.lit.notification.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity
{
    Button btn;
    NotificationManagerCompat notificationManager;
    EditText editText;

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

    private void sendNotification(String text)
    {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle("Reminder")
                .setContentText(text)
                .setLights(Color.RED, 100, 100)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify((int)(Math.random()*1000), notification);
        finish();
        System.exit(0);
    }
}