package com.learning.dev.srikar.chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchForDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupUIV();

        switch(getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK){

            case Configuration.UI_MODE_NIGHT_YES:
                switchForDarkMode.setChecked(true);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                switchForDarkMode.setChecked(false);
                break;

            default:
                switchForDarkMode.setChecked(false);

        }

        switchForDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){

                    Toast.makeText(getApplicationContext(), "Dark mode should be ON!", Toast.LENGTH_LONG).show();

                }

                else {
                    Toast.makeText(getApplicationContext(), "Dark mode should be OFF!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) ;
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUIV(){

        switchForDarkMode = findViewById(R.id.SAswitchForDarkTheme);

    }
}
