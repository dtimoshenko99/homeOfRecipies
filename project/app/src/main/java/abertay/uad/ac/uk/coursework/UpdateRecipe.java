package abertay.uad.ac.uk.coursework;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateRecipe extends AppCompatActivity {

    FirebaseFirestore db;
    Button update;
    ImageView img, back;
    TextView countryTxt;
    EditText nameInput,recipeDesc;
    String docId, desc, pic, name;
    Map<String, Object> data;
    String regEx = "[^A-Za-z0-9]";
    double lng, lat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        setBackground();

        // INITIATE FIRESTORE
        db = FirebaseFirestore.getInstance();

        getIntentFun();

        data = new HashMap<>();

        // UI ELEMENTS
        back = findViewById(R.id.arrowBackUpdate);
        img = findViewById(R.id.imageUpdateView);
        countryTxt = findViewById(R.id.locationText);
        nameInput = findViewById(R.id.nameInput);
        recipeDesc = findViewById(R.id.recipeUpdateInput);
        update = findViewById(R.id.postUpdateButton);

        // SET TEXT TO UI TEXTFIELDS
        nameInput.setText(name);
        recipeDesc.setText(desc);

        // SET IMAGE AND LOCATION TEXT
        setImageAndLocation();

        // GO BACK TO PREVIOUS ACTIVITY
        back.setOnClickListener(v -> startActivity(new Intent(UpdateRecipe.this, MyRecipesActivity.class)));

        update.setOnClickListener(v -> {
            // CHECK FIELDS AND UPDATE DATA
            checkFields();
        });

    }

    // CHECK IF FIELDS ARE EMPTY AND UPDATE FIRESTORE
    private void checkFields() {
            if(nameInput.toString().isEmpty()) {
                nameInput.setError("Please fill this field");
            }else if(recipeDesc.toString().isEmpty()) {
                recipeDesc.setError("Please fill this field");
            } else if(nameInput.getText().toString().matches(regEx)){
                nameInput.setError("Please enter only letters and numbers.");
            } else if(recipeDesc.getText().toString().matches(regEx))
            {
                recipeDesc.setError("Please enter only letters and numbers.");
            }else {
                data.put("recipeName", nameInput.getText().toString());
                data.put("recipeDesc", recipeDesc.getText().toString());
                db.collection("posts").document(docId)
                        .set(data, SetOptions.merge()).addOnSuccessListener(unused -> startActivity(new Intent(UpdateRecipe.this, MyRecipesActivity.class)));
            }
    }

    // SET IMAGE WITH GLIDE LIBRARY AND POSITION WITH GEOCODER
    private void setImageAndLocation() {
        Glide.with(UpdateRecipe.this).load(pic).into(img);
        Geocoder geocoder = new Geocoder(UpdateRecipe.this, Locale.getDefault());
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(lat, lng, 1);
            String coutry = address.get(0).getCountryName();
            countryTxt.setText(coutry);
        } catch (IOException e) {
            e.printStackTrace();
            countryTxt.setText("Can't get location. Sorry! :(");
        }
    }

    // SET BACKGROUND GRADIENT
    private void setBackground() {
        Drawable gradientInsta = new GradientDrawable(GradientDrawable.Orientation.TR_BL,
                new int[]{
                        Color.parseColor("#d4edf4"),
                        Color.parseColor("#f6c2c2"),
                });
        findViewById(R.id.postUpdate).setBackground(gradientInsta);
    }

    // GET DATA FROM PREVIOUS INTENT
    private void getIntentFun() {
        Intent intent = getIntent();
        pic = intent.getStringExtra("recipeUrl");
        name = intent.getStringExtra("recipeName");
        desc = intent.getStringExtra("recipeDesc");
        lat = intent.getDoubleExtra("latitude", 1);
        lng = intent.getDoubleExtra("longtitude", 1);
        docId = intent.getStringExtra("docID");
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