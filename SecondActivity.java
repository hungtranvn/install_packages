package com.example.luckynumber;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class SecondActivity extends AppCompatActivity {

    TextView welcomeTxt, luckyNumberTxt;
    Button sharedBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        welcomeTxt = findViewById(R.id.textview2);
        luckyNumberTxt = findViewById(R.id.luckynumber);
        sharedBtn = findViewById(R.id.sharedbtn);

        Intent i = getIntent();
        String userName = i.getStringExtra("name");

        // Generating random number
        int randomNumber = generateRandomNumber();
        luckyNumberTxt.setText(""+randomNumber);
        sharedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareData(userName, randomNumber);
            }
        });
    }

    private void shareData(String userName, int randomNumber) {
        // Implicit intent
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, userName + "got lucky number today!");
        i.putExtra(Intent.EXTRA_TEXT, "Your lucky number is: "+randomNumber);
        startActivity(Intent.createChooser(i, "Choose a Platform"));
    }

    public int generateRandomNumber() {
        Random random = new Random();
        int upper_limit = 1000;
        return random.nextInt(upper_limit);
    }
}

