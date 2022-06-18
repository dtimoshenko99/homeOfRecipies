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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences shared;
    SharedPreferences.Editor editor;
    EditText inputEmail, inputPassword, inputUsername, inputConfPass;
    Button registerButton;
    String patt = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String passPatt = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    String usernamePatt = "[^A-Za-z0-9]";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FirebaseFirestore db;
    ImageView back;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setBackground();

        // FIREBASE AUTH AND FIRESTORE INSTANCES
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // GET SHARED INSTANCES AND ASSIGN EDITOR
        shared = getSharedPreferences("details",
                Context.MODE_PRIVATE);
        editor = shared.edit();

        // UI ELEMENTS
        progress = findViewById(R.id.registerProgress);
        progress.setVisibility(View.INVISIBLE);
        inputEmail = findViewById(R.id.registerEmail);
        inputPassword = findViewById(R.id.registerPass);
        inputUsername = findViewById(R.id.registerName);
        inputConfPass = findViewById(R.id.registerConfPass);
        back = findViewById(R.id.signUpBack);
        registerButton = findViewById(R.id.usernameSignUpBttn);


        registerButton.setOnClickListener(view -> userSignUp());

        back.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

    }

    private void setBackground() {
        Drawable gradientInsta = new GradientDrawable(GradientDrawable.Orientation.TR_BL,
                new int[]{
                        Color.parseColor("#d4edf4"),
                        Color.parseColor("#f6c2c2"),
                });
        findViewById(R.id.registerBack).setBackground(gradientInsta);
    }

    private void userSignUp() {

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confPass = inputConfPass.getText().toString();

        // CHECK EMAIL INPUT AGAINSTS PATTERN
        if(!email.matches(patt))
        {
            inputEmail.setError("Please enter correct email.");
        }else if (password.isEmpty())
        {
            inputPassword.setError("Please enter password.");
        } else if(!password.equals(confPass))
        {
            inputConfPass.setError("Passwords entered don't match.");
        }else if(!inputPassword.getText().toString().matches(passPatt)){
            inputPassword.setError("Password needs to have: at least 1 number, 1 lower case, 1 uppercase, one of the following characters: ! @ # $ ( ), 8-20 characters ");
        } else if(inputUsername.getText().toString().matches(usernamePatt))
        {
            inputUsername.setError("Only letters and numbers please!");
        }
        else{
            progress.setVisibility(View.VISIBLE);

            // FIREBASE AUTHORISATION
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // PUT EMAIL AND USERNAME INTO SHARED PREFERENCES AND
                            editor.putString("email", email).apply();
                            editor.putString("username", inputUsername.toString()).apply();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            createRecord(uid);
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        } else {
                        }
                    });
        }
    }

    private void createRecord(String uid) {
        Map<String, Object> user = new HashMap<>();
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString();
        user.put("email", email);
        user.put("userID", uid);
        user.put("username", username);

        // CREATE FIRESTORE RECORD WITH USER ID
        db.collection("users").document(uid).set(user).addOnSuccessListener(unused -> {
            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            Intent main = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(main);
        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "error:"+e, Toast.LENGTH_LONG).show());
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