package abertay.uad.ac.uk.coursework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences shared;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Button recipes, addRecipe, news, mRecipes;
    ImageView profileView;
    TextView profile;
    String email, userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // FIRESTORE
        db = FirebaseFirestore.getInstance();


        // UI ELEMENTS
        mRecipes = findViewById(R.id.myRecipes);
        recipes = findViewById(R.id.browseRecipes);
        addRecipe = findViewById(R.id.addRecipe);
        news = findViewById(R.id.newsButton);
        profile = findViewById(R.id.profileText);
        profileView = findViewById(R.id.profileImage);

        // SET BACKGROUND GRADIENT
        setBackground();

        // GET SHARED PREFERENCES
        shared = getSharedPreferences("details", Context.MODE_PRIVATE);
        email = shared.getString("email", "email");
        userName = shared.getString("username", "username");

        // GET CURRENT USER FROM FIREBASE AUTH
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        checkUser();


    }

    private void checkUser() {
        if(user == null){
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }else if(userName.isEmpty() || userName.equals("notset"))
        {
            db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                String tempUsername = Objects.requireNonNull(task.getResult().getDocuments().get(0).get("username")).toString();
                shared.edit().putString("username", tempUsername).apply();
            });
        }
    }

    private void setBackground() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                Drawable gradientLight = new GradientDrawable(GradientDrawable.Orientation.BR_TL,
                        new int[]{
                                Color.parseColor("#ff82a9"),
                                Color.parseColor("#E5E5E5"),
                                Color.parseColor("#d9ccb9"),
                        });
                findViewById(R.id.background).setBackground(gradientLight);

                String logoBlack = "@drawable/ic_new_logo_small";
                int imageResource = getResources().getIdentifier(logoBlack, null, getPackageName());
                ImageView logoNight = findViewById(R.id.logoHome);
                Drawable res = getResources().getDrawable(imageResource);
                logoNight.setImageDrawable(res);

                profile.setTextColor(ContextCompat.getColor(this, R.color.black));

                String profileBlack = "@drawable/ic_profile";
                int profileImage = getResources().getIdentifier(profileBlack, null, getPackageName());
                Drawable profileBlackDraw = getResources().getDrawable(profileImage);
                profileView.setImageDrawable(profileBlackDraw);

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                Drawable gradientNight = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.parseColor("#d9ccb9"),
                                Color.parseColor("#fc88aa"),
                                Color.parseColor("#ff82a9"),
                        });
                findViewById(R.id.background).setBackground(gradientNight);

                String logoWhite = "@drawable/ic_home_logo";
                int imageResource1 = getResources().getIdentifier(logoWhite, null, getPackageName());
                ImageView logoLight = findViewById(R.id.logoHome);
                Drawable res1 = getResources().getDrawable(imageResource1);
                logoLight.setImageDrawable(res1);

                break;
        }
    }

    public void buttonHandler(View v){
        switch (v.getId()){
            case R.id.newsButton:
                startActivity(new Intent(HomeActivity.this, NewsActivity.class));
                break;
            case R.id.browseRecipes:
                startActivity(new Intent(HomeActivity.this, UserPosts.class));
                break;
            case R.id.addRecipe:
                startActivity(new Intent(HomeActivity.this, PostActivity.class));
                break;
            case R.id.myRecipes:
                startActivity(new Intent(HomeActivity.this, MyRecipesActivity.class));
                break;
            case R.id.profileImage:
            case R.id.profileText:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
        }
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