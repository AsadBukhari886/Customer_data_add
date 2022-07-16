package hadi.bukhari.splashscreen2;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring Variables
    private EditText edtCustomerName, edtCustomerPhone;
    private TextView txtCustomerPicture;
    private DatabaseReference databaseReference;
    private int noOfCustomer;
    private ValueEventListener customersRecordListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri uriOfPicture;
    private boolean isReadPermission;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //hide Action Bar
        getSupportActionBar().hide();


        //Initializing Variables
        isReadPermission = false;
        edtCustomerName = findViewById(R.id.edtCustomerName);
        edtCustomerPhone = findViewById(R.id.edtCustomerPhone);
        txtCustomerPicture = findViewById(R.id.txtCustomerPicture);
        Button btnAddNewCustomer = findViewById(R.id.btnAddNewCustomer);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Customers Record");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Customers Pictures");



        //Setting Listeners
        btnAddNewCustomer.setOnClickListener(MainActivity.this);
        txtCustomerPicture.setOnClickListener(MainActivity.this);


        // Read from the database

        try {

            customersRecordListener = databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.exists()){


                        // Get no of customers present in database
                        noOfCustomer = (int) snapshot.getChildrenCount();


                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }

            });

        }catch (Exception e){

            Log.e("CustomersRecordListener", e.getMessage());

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customersRecordListener != null){

            databaseReference.removeEventListener(customersRecordListener);

        }
    }

    private boolean isValidationSuccessful(String customerName, String customerPhone, Uri customerPicture){


        if (customerName.isEmpty() || customerPhone.isEmpty()){


            Toast.makeText(MainActivity.this, "Customer Name/Phone is empty", Toast.LENGTH_SHORT).show();
            return false;


        }


        if (uriOfPicture == null){


            Toast.makeText(MainActivity.this, "Please select customer picture", Toast.LENGTH_SHORT).show();
            return false;


        }


        return true;


    }




    @Override
    public void onClick(View v) {






        if (v.getId() == R.id.btnAddNewCustomer){
            //Add New Customer Functionality


            String customerName = edtCustomerName.getText().toString();
            String customerPhone = edtCustomerPhone.getText().toString();



            //If validation of inputs is successful, upload data to database
            if (isValidationSuccessful(customerName, customerPhone, uriOfPicture)) {

                uploadCustomerData(customerName, customerPhone, uriOfPicture);

            }


        }else if(v.getId() == R.id.txtCustomerPicture){
            //Customer Picture Functionality

            // Get Storage Read Permission From User
            getReadPermission();

            // Prompt User to choose picture of customer if user has granted Read Permission
            if (isReadPermission) {
                promptUserToSelectCustomerPicture();
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK){


            if (data != null){

                uriOfPicture = data.getData();
                String path = uriOfPicture.getPath();

                if (path.length() <= 20) {

                    txtCustomerPicture.setText(uriOfPicture.getPath());

                }else{


                    txtCustomerPicture.setText(path.substring(0, 20) + "...");


                }

            }


        }

    }

    private void uploadCustomerData(String customerName, String customerPhone, Uri uriOfPicture){


        try {


            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Adding New Customer ...");
            progressDialog.show();


            storageReference.child(customerPhone).putFile(uriOfPicture).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        HashMap<String, String> customerData = new HashMap<>();
                        customerData.put("Name", customerName);
                        customerData.put("Phone", customerPhone);
                        customerData.put("CustomerNumber", String.valueOf(noOfCustomer));

                        databaseReference.child(customerPhone).setValue(customerData);

                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "New Customer Added", Toast.LENGTH_SHORT).show();
                        clearFields();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "New Customer Addition Failed", Toast.LENGTH_SHORT).show();
                    clearFields();
                    Log.e("PictureUpload", e.getMessage());

                }
            });

        }catch (Exception e){

            Log.e("AddNewCustomer", e.getMessage());

        }

    }


    private void getReadPermission(){

        // Permission for Read External Storage
        if (Build.VERSION.SDK_INT >= 23){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                isReadPermission = true;
                Log.v("PERMISSION","Permission is granted!");

            }else {


                Log.v("Permission","Permission is revoked!");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3000);

            }
        }else {

            isReadPermission = true;
            Log.v("Tag","Permission is granted!");

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 3000 && grantResults.length > 0 ){

            isReadPermission = true;

            promptUserToSelectCustomerPicture();

        }

    }


    private void clearFields(){

        edtCustomerName.setText("");
        edtCustomerPhone.setText("");
        txtCustomerPicture.setText("Choose Customer Picture");


    }


    private void promptUserToSelectCustomerPicture(){


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1000);


    }

}

















//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import android.content.Intent;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class MainActivity extends AppCompatActivity {
//    //my custom coding
//    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
//    //my custom coding end here
//    // creating variables for
//    // EditText and buttons.
//    private EditText employeeNameEdt, employeePhoneEdt, employeeAddressEdt;
//    private Button sendDatabtn;
//
//    // creating a variable for our
//    // Firebase Database.
//    FirebaseDatabase firebaseDatabase;
//
//    // creating a variable for our Database
//    // Reference for Firebase.
//    DatabaseReference databaseReference;
//
//    // creating a variable for
//    // our object class
//    EmployeeInfo employeeInfo;
//    EmployeeInfo getdetails;
//    String getName,getPhone,getAddress;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//
//
//
//
//        // initializing our edittext and button
//        employeeNameEdt = findViewById(R.id.idEdtEmployeeName);
//        employeePhoneEdt = findViewById(R.id.idEdtEmployeePhoneNumber);
//        employeeAddressEdt = findViewById(R.id.idEdtEmployeeAddress);
//
//        // below line is used to get the
//        // instance of our FIrebase database.
//        firebaseDatabase = FirebaseDatabase.getInstance();
////        String public name2,phone2,address2;
//        // below line is used to get reference for our database.
////        databaseReference = firebaseDatabase.getReference("EmployeeInfo");
//
//        // initializing our object
//        // class variable.
//        employeeInfo = new EmployeeInfo();
//        getdetails=new EmployeeInfo();
//
//        sendDatabtn = findViewById(R.id.idBtnSendData);
//
//
//        // adding on click listener for our button.
//        sendDatabtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // getting text from our edittext fields.
//                String setname = employeeNameEdt.getText().toString();
//                String setphone = employeePhoneEdt.getText().toString();
//                String setaddress = employeeAddressEdt.getText().toString();
//                databaseReference = firebaseDatabase.getReference(setphone);
//
//                // below line is for checking weather the
//                // edittext fields are empty or not.
//                if (TextUtils.isEmpty(setname) || TextUtils.isEmpty(setphone) || TextUtils.isEmpty(setaddress)) {
//                    // if the text fields are empty
//                    // then show the below message.
//                    Toast.makeText(MainActivity.this, "Please fill all data fields.", Toast.LENGTH_SHORT).show();
//                } else {
//                    // else call the method to add
//                    // data to our database.
//                    addDatatoFirebase(setname, setphone,setaddress);
//                }
//            }
//        });
//    }
//
//    /** Called when the user taps the Send button */
//    public void openActivity(View view) {
//        Intent intent = new Intent( MainActivity.this, displayDetails2.class);
////        EditText editText = (EditText) findViewById(R.id.searchNumber);
////        String message = editText.getText().toString();
////        databaseReference=firebaseDatabase.getReference().child(message);
////        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                for (DataSnapshot dataSnapshot:snapshot.getChildren() )
////              getdetails=dataSnapshot.getValue(EmployeeInfo.class);
////                 getName=getdetails.getEmployeeName();
////                getPhone=getdetails.getEmployeeContactNumber();
////                getAddress=getdetails.getEmployeeAddress();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
////
////        intent.putExtra(EXTRA_MESSAGE, getName);
//
//        startActivity(intent);
//
//    }
////asad
//
//    private void addDatatoFirebase(String name, String phone, String address) {
//        // below 3 lines of code is used to set
//        // data in our object class.
//        employeeInfo.setEmployeeName(name);
//        employeeInfo.setEmployeeContactNumber(phone);
//        employeeInfo.setEmployeeAddress(address);
//
//        // we are use add value event listener method
//        // which is called with database reference.
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // inside the method of on Data change we are setting
//                // our object class to our database reference.
//                // data base reference will sends data to firebase.
//                databaseReference.setValue(employeeInfo);
//
//                // after adding this data we are showing toast message.
//                Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // if the data is not added or it is cancelled then
//                // we are displaying a failure toast message.
//                Toast.makeText(MainActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
