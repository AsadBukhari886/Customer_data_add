package hadi.bukhari.splashscreen2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.squareup.picasso.Picasso;

import java.io.ObjectInputStream;

public class SearchedCustomer extends AppCompatActivity {

    //Declaring Variables
    private TextView txtSearchedCustomerName, txtSearchedCustomerPhone, txtSearchedCustomerNumber;
    private ImageView imgSearchedCustomer;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ObjectInputStream.GetField Picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_customer);
        //hide Action Bar
        getSupportActionBar().hide();

        //Initializing Variables
        txtSearchedCustomerName = findViewById(R.id.txtSearchedCustomerName);
        txtSearchedCustomerPhone = findViewById(R.id.txtSearchedCustomerPhoneNo);
        txtSearchedCustomerNumber = findViewById(R.id.txtSearchedCustomerNo);
        imgSearchedCustomer = findViewById(R.id.imgSearchedCustomer);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Customers Pictures").child(getIntent().getStringExtra("PHONE"));


        //Setting Values
        txtSearchedCustomerNumber.setText(getIntent().getStringExtra("NUMBER"));
        txtSearchedCustomerPhone.setText(getIntent().getStringExtra("PHONE"));
        txtSearchedCustomerName.setText(getIntent().getStringExtra("NAME"));



        //Displaying Customer Picture in Image View

        try {


            ProgressDialog progressDialog = new ProgressDialog(SearchedCustomer.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

//                    Picasso.get().load(uri).into(imgSearchedCustomer);

                    Log.i("PICTURE-URI", uri + "");

                    progressDialog.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {

                    Log.e("SearchedCustomer", e.getMessage());
                    progressDialog.dismiss();

                }
            });

        }catch (Exception e){

            Log.e("SearchedCustomer", e.getMessage());

        }

    }
}