package com.example.picnote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.util.Calendar;

public class UploadActivity extends AppCompatActivity {

    private ImageView btnUploadImg;
    private TextView btnSave;
    private TextInputEditText etUploadTopic , etUploadDate , etUploadDescription;
    private ProgressDialog pd;
    Uri uri;
    String imageURL;
    private FirebaseAuth auth;
   private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Saving");

        reference = FirebaseDatabase.getInstance().getReference("user").child(auth.getCurrentUser().getUid()).child("user data");


        ActivityResultLauncher<Intent>activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                        if(o.getResultCode() == RESULT_OK){
                            Intent data = o.getData();
                            uri = data.getData();
                            btnUploadImg.setImageURI(uri);
                        }else {
                            Toast.makeText(UploadActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnUploadImg.setOnClickListener(new View.OnClickListener() {
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
            }
        });
    }

    private void saveData() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user images").child(uri.getLastPathSegment());

        pd.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while(!uriTask.isComplete());
                Uri uriImage = uriTask.getResult();
                imageURL = uriImage.toString();
                uploadData();
                pd.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.dismiss();
            }
        });


    }

    private void uploadData() {

        String topic = etUploadTopic.getText().toString();
        String description = etUploadDescription.getText().toString();
        String date = etUploadDate.getText().toString();

        DataClass dataClass = new DataClass(imageURL,topic,date,description);


        String currentDate = DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());

        //String memoryId = reference.push().getKey();

        reference.child(currentDate).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UploadActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initViews(){


        btnUploadImg = findViewById(R.id.btnUploadImg);
        btnSave = findViewById(R.id.btnSave);

        etUploadTopic = findViewById(R.id.etUploadTopic);
        etUploadDescription = findViewById(R.id.etUploadDescription);
        etUploadDate = findViewById(R.id.etUploadDate);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}