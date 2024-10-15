package com.example.picnote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etRegEmail , etRegUserName, etRegPass1,etRegPass2;
    private TextView btnCreateAc , btnToLog;
    private ProgressDialog pd;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        pd = new ProgressDialog(this);
        pd.setMessage("Creating account");
        auth = FirebaseAuth.getInstance();

        btnToLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnCreateAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.show();

                String email = Objects.requireNonNull(etRegEmail.getText()).toString();
                String userNme = Objects.requireNonNull(etRegUserName.getText()).toString();
                String pass1 = Objects.requireNonNull(etRegPass1.getText()).toString();
                String pass2 = Objects.requireNonNull(etRegPass2.getText()).toString();

                if(TextUtils.isEmpty(email)){
                    etRegEmail.setError("Email required");
                } else if (TextUtils.isEmpty(userNme)) {
                    etRegUserName.setError("User name required");
                }else if (TextUtils.isEmpty(pass1))
                    etRegPass2.setError("Password required");
                else if (pass1.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(email,userNme,pass1,pass2);
                }
            }
        });
    }

    private void registerUser(String etRegEmail, String etRegUserName, String etRegPass1, String etRegPass2) {

        auth.createUserWithEmailAndPassword(etRegEmail,etRegPass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                reference = FirebaseDatabase.getInstance().getReference("user").child(auth.getCurrentUser().getUid());

               HelperClass helperClass = new HelperClass(etRegEmail,etRegPass1,etRegUserName);


                reference.setValue(helperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    public void initViews(){

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegUserName = findViewById(R.id.etRegUserName);
        etRegPass1 = findViewById(R.id.etRegPass1);
        etRegPass2 = findViewById(R.id.etRegPass2);

        btnCreateAc = findViewById(R.id.btnCreateAc);
        btnToLog = findViewById(R.id.btnToLog);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
       //do not do anything when back is pressed
    }
}