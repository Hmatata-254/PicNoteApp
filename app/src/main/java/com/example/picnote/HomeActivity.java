package com.example.picnote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton btnFab;
    private List<DataClass> dataClass;
    private DatabaseReference reference;
    private ProgressDialog pd;
    private Adapter adapter;
    private FirebaseAuth auth;
    private SearchView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        dataClass = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new Adapter(this, dataClass);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        reference = FirebaseDatabase.getInstance().getReference("user").child(auth.getCurrentUser().getUid()).child("user data");
        pd.show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataClass.clear();

                for(DataSnapshot snap : snapshot.getChildren()){
                    DataClass dataClass = snap.getValue(DataClass.class);
                    dataClass.setKey(snap.getKey());
                    HomeActivity.this.dataClass.add(dataClass);
                }

                adapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                pd.dismiss();
            }
        });



        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });


        btnSearch.clearFocus();
        btnSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    public void searchList(String text){

        ArrayList<DataClass> searchList = new ArrayList<>();

        for(DataClass dt : dataClass){

            if(dt.getDataTopic().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dt);
            }
        }

        adapter.searchData(searchList);
    }


    public void initViews(){

        btnFab = findViewById(R.id.btnFab);
        btnSearch = findViewById(R.id.btnSearch);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //do not do anything
    }
}