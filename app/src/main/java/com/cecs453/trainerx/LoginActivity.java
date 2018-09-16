package com.cecs453.trainerx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private FirebaseFirestore db;
    public static final String MY_PREFERENCES = "TrainerXPreferences";
    private PinView pinView;
    private CollectionReference citiesRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        citiesRef = db.collection("staff");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login with PIN");
        toolbar.setTitleTextColor(Color.WHITE);

        pinView = findViewById(R.id.firstPinView);
        pinView.setAnimationEnable(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ((task.isSuccessful())) {
                    addPinView();
                } else {
                    Toast.makeText(LoginActivity.this, "Cannot login into database", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void addPinView() {
        pinView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String pinCode = pinView.getText().toString();
                    if (!TextUtils.isEmpty(pinCode)) {
                        Query query = citiesRef.whereEqualTo("pinCode", pinCode);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                                    DocumentSnapshot documentSnapshot = documentSnapshots.get(0);
                                    String id = documentSnapshot.getId();
                                    String name = documentSnapshot.getString("name");
                                    addToSharedPreferences(id, name);
                                    Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, ClientViewActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                                    pinView.setText("");
                                }
                            }
                        });
                    }
                }
                return false;
            }
        });
    }

    private void addToSharedPreferences(String id, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.contains("id") || sharedPreferences.contains("name")) {
            editor.remove("id");
            editor.remove("name");
        }
        editor.putString("id", id);
        editor.putString("name", name);
        editor.apply();
    }
}