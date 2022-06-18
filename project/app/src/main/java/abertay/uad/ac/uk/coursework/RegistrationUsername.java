package abertay.uad.ac.uk.coursework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationUsername extends AppCompatActivity {

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    Button continueSignUp;
    EditText usernameInput;
    String email, name, id;
    TextView nameWelcome;
    ProgressBar progress;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_username);

        // SET BACKGROUND GRADIENT
        setBackground();

        // GET SHARED PREFERENCES
        shared = getSharedPreferences("details",
                Context.MODE_PRIVATE);
        editor = shared.edit();

        // FIREBASE AUTH AND FIRESTORE GET INSTANCES
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI ELEMENTS
        usernameInput = findViewById(R.id.usernameInputBox);
        progress = findViewById(R.id.usernameSignUpProgress);
        progress.setVisibility(View.INVISIBLE);
        nameWelcome = findViewById(R.id.emailSignUpTxt);
        continueSignUp = findViewById(R.id.usernameSignUpBttn);

        // GET DATA FROM PREVIOUS INTENT
        getIntentFun();

        nameWelcome.setText("Welcome, "+name);

        continueSignUp.setOnClickListener(v -> {
            progress.setVisibility(View.VISIBLE);
            // FUNCTION TO CHECK IF USERNAME ALREADY EXISTS
            checkUsername();
        });

        // ON ARROW BACK SIGN USER OUT AND SEND THEM BACK TO LOGIN SCREEN
        ImageView arrow = findViewById(R.id.usernameBackArrow);
        arrow.setOnClickListener(v -> {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(RegistrationUsername.this, LoginActivity.class));
        });

    }

    // GET INFO FROM LAST INTENT
    private void getIntentFun() {
        Intent signUp = getIntent();
        email = signUp.getStringExtra("userEmail");
        name = signUp.getStringExtra("name");
        id = signUp.getStringExtra("uID");
    }

    // SET ACTIVITY BACKGROUND GRADIENT
    private void setBackground() {
        Drawable gradientLight = new GradientDrawable(GradientDrawable.Orientation.BR_TL,
                new int[]{
                        Color.parseColor("#ff82a9"),
                        Color.parseColor("#E5E5E5"),
                        Color.parseColor("#d9ccb9"),
                });
        findViewById(R.id.userSignUpBack).setBackground(gradientLight);
    }

    // INSERT NEW USER INTO FIRESTORE
    private void insertUser() {
        // CREATE MAP OF VALUES TO INSERT
        Map<String, Object> user = new HashMap<>();
        user.put("username", usernameInput.getText().toString());
        user.put("userID", id);
        user.put("email", email);
        db.collection("users").document(id).set(user).addOnSuccessListener(unused -> {
            editor.putString("username", usernameInput.getText().toString()).apply();
            startActivity(new Intent(RegistrationUsername.this,  HomeActivity.class));
            progress.setVisibility(View.INVISIBLE);
        }).addOnFailureListener(e -> {
            progress.setVisibility(View.INVISIBLE);
            Toast.makeText(RegistrationUsername.this, "Sorry, try again!", Toast.LENGTH_SHORT).show();
        });
    }

    // CHECK IF USERNAME ALREADY EXISTS IN DB
    private void checkUsername() {
        db.collection("users").whereEqualTo("username", usernameInput.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult().isEmpty()){
                        insertUser();
                    }else{
                        usernameInput.setError("Username already exists!");
                        progress.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try{
            super.onRestoreInstanceState(savedInstanceState);
        }catch(Exception e){
            savedInstanceState = null;
        }
    }
}