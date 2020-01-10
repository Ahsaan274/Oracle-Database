package com.example.oracle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oracle.utils.NetworkHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@192.168.10.100:1521:odb";
    private static final String DEFAULT_USERNAME = "tApp";
    private static final String DEFAULT_PASSWORD = "101171";
    private Connection connection;

    LocationManager service;
    private FusedLocationProviderClient client;
    private static final int REQUEST_LOCATION = 1;

    private boolean isNetworkOk;
    public static final String TAG = "MyTag";
    private TextView mLog;
    TextView forgetTxtView;
    Button btn ;
    EditText edUserId, edUserName,edUserPass;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.login);
        edUserId = findViewById(R.id.userId);
        edUserPass = findViewById(R.id.password);
        forgetTxtView = findViewById(R.id.forgetPass);
        forgetTxtView.setText(Html.fromHtml("<i><u>Forget Password</u></i>?   "));
        //  mLog = findViewById(R.id.chkInternet);
        client = LocationServices.getFusedLocationProviderClient(this);

        NetworkIsConnected();
        requestPermission();
        showGpsSettings(this);
        getLongLat();


        if (android.os.Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginValid();
            }
        });
        try {
            this.connection = createConnection();
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void logOutput(String data){
        Log.d(TAG,"logOutput:"+data);
        mLog.setText(data+"\n");
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void NetworkIsConnected() {
        isNetworkOk = NetworkHelper.isNetworkAvailable(this);
        if(isNetworkOk == true){
            /*String connected = "Connected";
            logOutput("Network is :"+connected);*/
        }
        else {
            /*String connected2 = "Not connected";
            logOutput("Network is :"+connected2);*/
            AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
            a_builder.setMessage("Close this app, connect to internet first !!!")
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog alert = a_builder.create();
            alert.setTitle("Internet Connectivity required !!!");
            alert.show();


        }
    }

    public void LoginValid(){
        try {
            CallableStatement callableStatement;
            callableStatement = connection.prepareCall("call signin(?,?,?)");

            callableStatement.setString(1,edUserId.getText().toString());
            //callableStatement.setString(2,edUserName.getText().toString());
            callableStatement.setString(2,edUserPass.getText().toString());
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.execute();
            if (!callableStatement.getString(3).equals("False")){
                Toast.makeText(MainActivity.this, "Successfully LogIn", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,WelcomePage.class);
                startActivity(intent);
                finish();
                return;
            }
            else if(edUserId.getText().toString().equals("")){
                edUserId.setError("Can't be empty");
            }
            else if(edUserPass.getText().toString().equals("")){
                edUserPass.setError("Can't be empty");
            }
            else {
                Toast.makeText(MainActivity.this, "Invalid Email & Password", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(DEFAULT_DRIVER, DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
    private void getLongLat() {
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    TextView txtV = findViewById(R.id.Lat);
                    txtV.setText(valueOf("T: "+location.getLatitude()));
                    TextView txtV2 = findViewById(R.id.Lng);
                    txtV2.setText(valueOf("G: "+location.getLongitude()));
                }
            }
        });
    }
    public  void showGpsSettings(Context context){
        service = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
            a_builder.setMessage("To continue, turn on device location, which uses Google's location service.")
                    .setCancelable(false)
                    .setPositiveButton("Go to Settings",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Close the app", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alert = a_builder.create();
            alert.setTitle("");
            alert.show();



        } else {
            getDeviceLoc();
        }
    }
    private Location getDeviceLoc() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = service.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                return location;
            } else if (location1 != null) {
                return location1;
            } else if (location2 != null) {
                return location2;
            } else {
                Toast.makeText(this, "Unable to trace your location", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
}