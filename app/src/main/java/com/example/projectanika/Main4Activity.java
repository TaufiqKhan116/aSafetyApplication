package com.example.projectanika;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main4Activity extends AppCompatActivity
{

    private static TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        info = (TextView) findViewById(R.id.infoText);

        String studentInfo = "This is a software development project by\n Anika Tasnim and Madiha Binte Zakir\n Roll : 1703125, 1703151\n Department of CSE,\n Rajshashi University Of Engineering And Technology\n\n";
        String teacherInfo = "Supervised by\n Prof. Dr. Md. Ali Hossain\n Department of CSE,\n Rajshashi University Of Engineering And Technology\n\n";
        String generalInfo = "This is a tiny step towards safety of people(Specially women) as a protest against recent occurrences of violence.\nLet's say no to any classification of violence and try to make the world a better and safe place for us and our followings.\n\n";
        String nb          = "N.B. Using this app without the consent of coders of this app is not permitted and highly discouraged.\n";
        String contacts    = "Phone : +8801972064882  E-mail : anika.sagc@gmail.com";

        info.setText(studentInfo + teacherInfo + generalInfo + nb + contacts);
    }
}
