package com.msh.pushnotification;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity
{
    String phoneNumber, token;
    Service service = Service.getInstance();
    User user = User.getInstance();
    Activity activity;
    Context context;
    Typeface tf, tf_bold;
    int NOTIFICATION_REQUEST_CODE = 100;
    String[] permission = new String[] { Manifest.permission.POST_NOTIFICATIONS };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(activity, permission, NOTIFICATION_REQUEST_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        context = this;

        user.setContext(context);

        tf = user.getTypeface(context, 0);
        tf_bold = user.getTypeface(context, 1);

        TextView txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setTypeface(tf_bold);

        TextView txtMessage = findViewById(R.id.txtMessage);
        txtMessage.setTypeface(tf);
        txtMessage.setText(user.getValue("message"));

        user.setValue("message", "");

        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setTypeface(tf);

        Button btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setTypeface(tf_bold);

        btn_ok.setOnClickListener(v -> {
                phoneNumber = phoneNumberEditText.getText().toString();
                token = user.getValue("userToken");

            if (phoneNumber.equals(""))
                Toast.makeText(context, "شماره تلفن را وارد کنید", Toast.LENGTH_LONG).show();
            else
                new sentToken().execute();
        });

        checkNotificationPermission();
    }

    void checkNotificationPermission()
    {
        int checkVal = checkCallingOrSelfPermission(permission[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            if (checkVal != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(activity, permission, NOTIFICATION_REQUEST_CODE);
            }
        }
    }

    private class sentToken extends AsyncTask<Intent, Void, Void>
    {
        String receiveData = "";

        @Override
        protected void onPreExecute()
        {

        }

        protected Void doInBackground(Intent... intents)
        {
            receiveData = service.sendParamsInBody(phoneNumber, token);
            return null;
        }

        protected void onPostExecute(Void param)
        {
            Log.d("pushNotif", "receiveData: " + receiveData);
            Toast.makeText(context, "ثبت انجام شد", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        user.setValue("message", "");
    }
}