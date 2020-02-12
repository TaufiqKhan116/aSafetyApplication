package com.example.projectanika;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {
    private static EditText n1;
    private static EditText n2;
    private static EditText n3;
    private static EditText n4;
    private static Button inv;
    private static Button picker;
    private static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        n1 = (EditText) findViewById(R.id.n1);
        n2 = (EditText) findViewById(R.id.n2);
        n3 = (EditText) findViewById(R.id.n3);
        n4 = (EditText) findViewById(R.id.n4);
        inv = (Button) findViewById(R.id.invoke);
        picker = (Button) findViewById(R.id.picker);

        sharedPref = getSharedPreferences("numberCol", MODE_PRIVATE);

        n1.setText(sharedPref.getString("1", ""));
        n2.setText(sharedPref.getString("2", ""));
        n3.setText(sharedPref.getString("3", ""));
        n4.setText(sharedPref.getString("4", ""));

        picker.setOnClickListener(v ->
        {
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(i, 2019);
        });

        inv.setOnClickListener(v ->
                {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("1", n1.getText().toString()).commit();
                    editor.putString("2", n2.getText().toString()).commit();
                    editor.putString("3", n3.getText().toString()).commit();
                    editor.putString("4", n4.getText().toString()).commit();

                    Toast.makeText(Main3Activity.this, "Numbers saved", Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2019 && resultCode == RESULT_OK) {
            Cursor cursor = null;

            try {
                String phoneNo = null;
                Uri uri = data.getData();
                cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                phoneNo = cursor.getString(phoneIndex);

                if (n1.isFocused())
                {
                    n1.setText(phoneNo);
                    n1.clearFocus();
                }
                else if (n2.isFocused())
                {
                    n2.setText(phoneNo);
                    n2.clearFocus();
                }
                else if (n3.isFocused())
                {
                    n3.setText(phoneNo);
                    n3.clearFocus();
                }
                else if (n4.isFocused())
                {
                    n4.setText(phoneNo);
                    n4.clearFocus();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}