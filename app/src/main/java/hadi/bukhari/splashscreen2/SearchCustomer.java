package hadi.bukhari.splashscreen2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SearchCustomer extends AppCompatActivity {


    //Declaring Variables
    private EditText edtSearchCustomerPhone;
    private Button btnSearchCustomer;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener customerRecordListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer);


        //hide Action Bar
        getSupportActionBar().hide();


        //Initializing Variables
        edtSearchCustomerPhone = findViewById(R.id.edtSearchCustomerPhone);
        btnSearchCustomer = findViewById(R.id.btnSearch);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Customers Record");


        //Setting Listeners
        btnSearchCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {


                    String customerPhone = edtSearchCustomerPhone.getText().toString();


                    ProgressDialog progressDialog = new ProgressDialog(SearchCustomer.this);
                    progressDialog.setMessage("Searching...");
                    progressDialog.show();

                    if (isValidationSuccessful(customerPhone)) {

                        customerRecordListener = databaseReference.child(customerPhone).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    String customerName = snapshot.child("Name").getValue() + "";
                                    String customerPhone = snapshot.child("Phone").getValue() + "";
                                    String customerNumber = snapshot.child("CustomerNumber").getValue() + "";

                                    Intent transitionToSearchedCustomer = new Intent(SearchCustomer.this, SearchedCustomer.class);
                                    transitionToSearchedCustomer.putExtra("NAME", customerName);
                                    transitionToSearchedCustomer.putExtra("PHONE", customerPhone);
                                    transitionToSearchedCustomer.putExtra("NUMBER", customerNumber);

                                    progressDialog.dismiss();

                                    startActivity(transitionToSearchedCustomer);

                                    finish();


                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                                progressDialog.dismiss();
                                Log.e("SearchCustomer", error.getMessage());

                            }
                        });


                    }

                }catch (Exception e){

                    Log.e("SearchCustomer", e.getMessage());

                }

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (customerRecordListener != null)
            databaseReference.removeEventListener(customerRecordListener);

    }

    private boolean isValidationSuccessful(String customerPhone){

        if (customerPhone.isEmpty()){

            Toast.makeText(this, "Customer Phone is empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}