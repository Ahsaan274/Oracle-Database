package com.example.oracle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oracle.utils.PreferenceUtils;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import static com.example.oracle.LoginActivity.createConnection;

public class WelcomePage extends AppCompatActivity {

    private Connection connection;
    private FusedLocationProviderClient client;
    private TextView textViewName;
    public String nameFromIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        try {
            this.connection = createConnection();
        }
        catch (Exception e) {
            Toast.makeText(WelcomePage.this, ""+e,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        textViewName = (TextView) findViewById(R.id.abc);
        Intent intent = getIntent();
        if (intent.hasExtra("EMAIL")){
            nameFromIntent = getIntent().getStringExtra("EMAIL");
            textViewName.setText("Welcome " + nameFromIntent);
        }else{
        }
    }
    public void dataPut() throws SQLException {
        /*CallableStatement callableStatement;
        callableStatement = connection.prepareCall("call signin(?)");*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.log_out:
                /*PreferenceUtils.savePassword(null, this);
                PreferenceUtils.saveEmail(null, this);*/
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
