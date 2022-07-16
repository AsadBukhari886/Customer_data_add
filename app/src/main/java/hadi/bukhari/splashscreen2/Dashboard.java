package hadi.bukhari.splashscreen2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    //Declaring Variables
    private Button btnNewCustomer, btnSearchCustomer;
    private TextView txtLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //hide Action Bar
        getSupportActionBar().hide();

        //Initializing Variables
        btnNewCustomer = findViewById(R.id.btnNewCustomer);
        btnSearchCustomer = findViewById(R.id.btnSearchCustomer);
        txtLogOut = findViewById(R.id.txtLogOut);


        //Setting Listeners
        btnNewCustomer.setOnClickListener(Dashboard.this);
        btnSearchCustomer.setOnClickListener(Dashboard.this);
        txtLogOut.setOnClickListener(Dashboard.this);

    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.btnNewCustomer){
            //Transition to NewCustomer Activity
            startActivity(new Intent(Dashboard.this, MainActivity.class));


        }else if(v.getId() == R.id.btnSearchCustomer){
            //Transition to SearchCustomer Activity
            startActivity(new Intent(Dashboard.this, SearchCustomer.class));


        }else if(v.getId() == R.id.txtLogOut){
            //LogOut the User
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Dashboard.this, LoginActivity.class));
            finish();

        }


    }
}