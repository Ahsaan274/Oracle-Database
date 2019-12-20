package com.example.oracle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class MainActivity extends AppCompatActivity {

    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@192.168.10.100:1521:odb";
    private static final String DEFAULT_USERNAME = "market";
    private static final String DEFAULT_PASSWORD = "101171";
    private Connection connection;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 15) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        TextView tv = (TextView) findViewById(R.id.hello);
        try {
            this.connection = createConnection();
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();

            Statement stmt=connection.createStatement();
            StringBuffer stringBuffer = new StringBuffer();
            ResultSet rs=stmt.executeQuery("select * from mobileUsers");

            while(rs.next()) {
                //stringBuffer.append( rs.getString(1)+"\n");
                String userId = rs.getString(1);
                String userPassword = rs.getString(2);
                String userName = rs.getString(3);
                String fkSecurityLevel = rs.getString(4);
                String isLock = rs.getString(5);
                String status = rs.getString(6);
                String cellNo = rs.getString(7);
                String alt = rs.getString(8);
                String email = rs.getString(9);
                String createdBy = rs.getString(10);
                String editedBy = rs.getString(11);

                stringBuffer.append( "userId:"+userId+"\n"+"user Password:"+userPassword+"\n"+
                        "user Name:"+userName+"\n"+"fkSecurityLevel"+fkSecurityLevel+ "\n"
                        +"IsLock:" +isLock+"\n"+"Status:"+status+"\n"+"Cell No:"+cellNo+"\n"+
                        "Alt:"+alt+"\n"+"Email:"+email+"\n"+"CreatedBy:"+createdBy +"\n"
                        +"EditedBy:"+editedBy+"\n");
            }
            tv.setText(stringBuffer.toString());
            connection.close();
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, ""+e,
                    Toast.LENGTH_SHORT).show();
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
}