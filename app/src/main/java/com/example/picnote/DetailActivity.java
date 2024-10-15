package com.example.picnote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.microedition.khronos.opengles.GL;

public class DetailActivity extends AppCompatActivity {

    private TextView detTopic,detDescription , detDate;
    private ImageView detImage;
    private ImageView btnDelete,btnEdit;
    private FirebaseAuth auth;
    String key = "";
    String imageUlr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        auth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            detTopic.setText(bundle.getString("Topic"));
            detDate.setText(bundle.getString("Date"));
            detDescription.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            imageUlr = bundle.getString("Image");

            Glide.with(this).asBitmap().load(bundle.getString("Image")).into(detImage);
        }



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(auth.getCurrentUser().getUid()).child("user data");

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUlr);

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setCancelable(false);
                builder.setMessage("Delete memory?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child(key).removeValue();
                                Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                finish();
                            }
                        });
                    }
                });



                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);

                intent.putExtra("Topic",detTopic.getText().toString());
                intent.putExtra("Image",imageUlr);
                intent.putExtra("Key",key);
                intent.putExtra("Date",detDate.getText().toString());
                intent.putExtra("Description",detDescription.getText().toString());

                startActivity(intent);

                Toast.makeText(DetailActivity.this, "Please update all fields", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void initViews(){

        detTopic = findViewById(R.id.detTopic);
        detDescription = findViewById(R.id.detDescription);
        detDate = findViewById(R.id.detDate);
        detImage = findViewById(R.id.detImage);
        btnEdit = findViewById(R.id.btnEdit);

        btnDelete = findViewById(R.id.btnDelete);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //take me to home activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}