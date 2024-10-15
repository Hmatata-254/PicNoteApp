package com.example.picnote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Half;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateActivity extends AppCompatActivity {

    private TextInputEditText etUpdateTopic , etUpdateDesc, etUpdateDate;
    private TextView btnSave;
    private ImageView btnUpdateImg;
    String topic , desc, date;
    String imageUrl;
    String key,oldImageURL;
    Uri uri;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        pd = new ProgressDialog(this);
        pd.setMessage("Updating");
        auth = FirebaseAuth.getInstance();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                        if(o.getResultCode() == RESULT_OK){
                            Intent data = o.getData();
                            uri = data.getData();
                            btnUpdateImg.setImageURI(uri);
                        }else{
                            Toast.makeText(UpdateActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(btnUpdateImg);

            etUpdateTopic.setText(bundle.getString("Topic"));
            etUpdateDate.setText(bundle.getString("Date"));
            etUpdateDesc.setText(bundle.getString("Description"));

            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }

        reference = FirebaseDatabase.getInstance().getReference("user").child(auth.getCurrentUser().getUid()).child("user data").child(key);

        // String memoId = reference.push().getKey();

        btnUpdateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();
                Intent intent = new Intent(UpdateActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        });




    }

    private void saveData() {

        storageReference = FirebaseStorage.getInstance().getReference().child("user images").child(uri.getLastPathSegment());

        pd.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri uriImage = uriTask.getResult();
                imageUrl = uriImage.toString();
                updateData();
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UpdateActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateData() {

        topic = etUpdateTopic.getText().toString().trim();
        date = etUpdateDate.getText().toString().trim();
        desc = etUpdateDesc.getText().toString().trim();


        DataClass dataClass = new DataClass(imageUrl,topic,date,desc);

        reference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    StorageReference sto = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                    sto.delete();
                    pd.dismiss();
                    Toast.makeText(UpdateActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UpdateActivity.this,"Unable to update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initViews(){

        etUpdateTopic = findViewById(R.id.etUpdateTopic);
        etUpdateDate = findViewById(R.id.etUpdateDate);
        etUpdateDesc = findViewById(R.id.etUpdateDesc);

        btnSave = findViewById(R.id.btnSave);
        btnUpdateImg = findViewById(R.id.btnUpdateImg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //take me to home
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}