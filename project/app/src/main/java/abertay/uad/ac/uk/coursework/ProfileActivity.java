package abertay.uad.ac.uk.coursework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences shared;
    SharedPreferences.Editor editor;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    Button signOut, deleteUser, update;
    String username, uID, usernameInput, email;
    EditText input;
    ImageView back;
    String regEx = "[^A-Za-z0-9]";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setBackground();

        // GET DATA FROM SHARED PREFERENCES AND SET EDITOR
        shared = getSharedPreferences("details", Context.MODE_PRIVATE);
        editor = shared.edit();
        username = shared.getString("username", "username");
        email = shared.getString("email", "email");

        // FIREBASE AUTH, FIRESTORE INIT
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // GET UI ELEMENTS AND SET TEXT
        deleteUser = findViewById(R.id.deleteBttn);
        update = findViewById(R.id.buttonUpdate);
        signOut = findViewById(R.id.signOutBttn);
        back = findViewById(R.id.arrowBackProfile);
        input = findViewById(R.id.inputUsername);
        input.setText(username);
        usernameInput = input.getText().toString();

        signOut.setOnClickListener(v -> signaway());

        back.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HomeActivity.class)));

        deleteUser.setOnClickListener(v -> new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("But why? :(")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteUserAndPosts()).setNegativeButton(android.R.string.no,null)
                .setIcon(R.drawable.ic_fork)
                .show());

        update.setOnClickListener(v -> updateUsername());


    }

    private void setBackground() {
        Drawable background = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                new int[]{
                        Color.parseColor("#e8b3b3"),
                        Color.parseColor("#df7782"),
                        Color.parseColor("#e95d22"),
                        Color.parseColor("#E95D22"),
                });
        findViewById(R.id.profileBackground).setBackground(background);
    }

    private void deleteUserAndPosts() {
        db.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot doc = task.getResult();
                        String id = doc.getDocuments().get(0).getId();
                        db.collection("users").document(id).delete()
                                .addOnSuccessListener(unused -> db.collection("posts").whereEqualTo("email",
                                        email)
                                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        WriteBatch batch = db.batch();
                                        for (DocumentSnapshot doc1 : queryDocumentSnapshots) {
                                            batch.delete(doc1.getReference());
                                        }
                                        batch.commit();
                                        currentUser.delete();
                                        LoginManager.getInstance().logOut();
                                    }else {
                                        currentUser.delete();
                                        LoginManager.getInstance().logOut();
                                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(this, "Error occurred, try again!", Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(this, "Error occurred, try again!", Toast.LENGTH_SHORT).show());
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, "Error occurred, try again!", Toast.LENGTH_SHORT).show());
    }

    private void updateUsername() {
        if(usernameInput.isEmpty()){
            input.setError("Please fill this field.");
        }else if (usernameInput.matches(regEx)){
            input.setError("Please use only numbers and letters.");
        }
        else {
            editor.putString("username", input.getText().toString()).apply();
            Map<String, Object> map = new HashMap<>();
            map.put("username", input.getText().toString());
            // QUERY TO FIND DOCUMENT ID WHERE USERNAME EQUALS OLD USERNAME
            db.collection("users").whereEqualTo("username", username).get().addOnCompleteListener(idTask -> {
                // GET TASK RESULT INTO SNAPSHOT VARIABLE
                QuerySnapshot snap = idTask.getResult();
                // GET DOCUMENT ID
                uID = snap.getDocuments().get(0).getId();
                // UPDATE DATABASE RECORD OF USER SET USERNAME
                db.collection("users").document(uID).set(map, SetOptions.merge()).addOnSuccessListener(unused -> {
                    Toast.makeText(ProfileActivity.this, "Username Updated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                });
            });
            updatePosts();
        }
    }

    private void updatePosts() {
        db.collection("posts").whereEqualTo("email", email)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                // INITIATE FIRESTORE BATCH
                WriteBatch batch = db.batch();
                // GET TASK RESULT INTO SNAPSHOT
                QuerySnapshot doc = task.getResult();
                // ITERATE THROUGH SNAPSHOT
                username = shared.getString("username", username);
                for(QueryDocumentSnapshot updateUserInPost : doc){
                    // GET DOCUMENT REFERENCE AND STORE NEW USERNAME IN BATCH
                    batch.update(updateUserInPost.getReference(), "username", username);
                }
                batch.commit();
            }
        });
    }


    private void signaway() {
        new AlertDialog.Builder(this)
                .setTitle("Decision")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }).setNegativeButton(android.R.string.no,null)
                .setIcon(R.drawable.ic_fork)
                .show();
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