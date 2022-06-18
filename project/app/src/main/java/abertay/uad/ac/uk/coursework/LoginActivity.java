package abertay.uad.ac.uk.coursework;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    String[] permissions = {
            Manifest.permission.INTERNET
    };
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    EditText inputEmail, inputPassword;
    TextView create;
    private FirebaseAuth mAuth;
    Button emailLogin;
    ProgressBar progress;
    CallbackManager mCallbackManager;
    LoginButton loginButton;
    FirebaseFirestore db;
    FirebaseUser user;
    String patt = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String passPatt = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        shared = getSharedPreferences("details",
                Context.MODE_PRIVATE);
        editor = shared.edit();
        checkUser();
        setContentView(R.layout.activity_login);

        if (!Utils.checkAllPermissions(this, permissions)) {
            requestPermissions(permissions, 0);
        }
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        mCallbackManager = CallbackManager.Factory.create();

        db = FirebaseFirestore.getInstance();

        progress = findViewById(R.id.loginProgress);
        progress.setVisibility(View.INVISIBLE);


        create = findViewById(R.id.createAccount);
        emailLogin = findViewById(R.id.buttonLogin);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        emailLogin = findViewById(R.id.buttonLogin);

        emailLogin.setOnClickListener(view -> signIn());

        create.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Error occurred, try again!", LENGTH_SHORT).show();
            }
        });


    }

    private void checkUser() {
        if (user != null) {
            db = FirebaseFirestore.getInstance();
            db.collection("users").whereEqualTo("email", user.getEmail()).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            QuerySnapshot doc = task.getResult();
                            String username = Objects.requireNonNull(doc.getDocuments().get(0).get("username")).toString();
                            editor.putString("username",username).apply();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        }
                    }).addOnFailureListener(e -> Toast.makeText(this, ""+e.getLocalizedMessage(), LENGTH_SHORT).show());

        } else if(user == null){
            editor.putString("username", "notset");
            editor.putString("email", "notset");
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        String id = user.getUid();
                        String email = user.getEmail();
                        String name = user.getDisplayName();
                        editor.putString("email", email).apply();
                        db.collection("users").whereEqualTo("email", email)
                                .get().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful() && task1.getResult().isEmpty()){
                                        progress.setVisibility(View.INVISIBLE);
                                        Intent i = new Intent(LoginActivity.this, RegistrationUsername.class);
                                        i.putExtra("uID", id);
                                        i.putExtra("userEmail", email);
                                        i.putExtra("name", name);
                                        editor.putString("hui", email).apply();
                                        startActivity(i);
                                    }else if(task1.isSuccessful() && !task1.getResult().isEmpty()){
                                        QuerySnapshot doc = task1.getResult();
                                        String username = doc.getDocuments().get(0).get("username").toString();
                                        editor.putString("username", username).apply();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, ""+e.getLocalizedMessage(), LENGTH_SHORT).show());


                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void signIn () {
        if (!inputEmail.getText().toString().matches(patt)) {
            inputEmail.setError("Please enter correct email.");
        } else if (inputPassword.getText().toString().isEmpty()) {
            inputPassword.setError("Please enter your password.");
        } else if(!inputPassword.getText().toString().matches(passPatt))
        {
            inputPassword.setError("Password needs to have: at least 1 number, 1 lower case, 1 uppercase, one of the following characters: ! @ # $ ( ), 8-20 characters ");
        }
        else {
            progress.setVisibility(View.VISIBLE);
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            editor.putString("email", email).apply();
                            progress.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(this, HomeActivity.class));
                        }
                    }).addOnFailureListener(e -> {
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Authentication failed.",
                        LENGTH_SHORT).show();
                Toast.makeText(this, ""+e.getLocalizedMessage(), LENGTH_SHORT).show();
                    });
        }
    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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

