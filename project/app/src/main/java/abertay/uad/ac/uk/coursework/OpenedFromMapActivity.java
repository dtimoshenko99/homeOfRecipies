package abertay.uad.ac.uk.coursework;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OpenedFromMapActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView recipe;
    TextView user;
    String rDesc, rImage;
    TextView country, desc;
    ImageView image;
    String rUsername, rName;
    double lat, lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_from_map);

        setBackground();
        db = FirebaseFirestore.getInstance();

        // GET DATA FROM PREVIOUS ACTIVITY
        Intent intent = getIntent();
        rName = intent.getStringExtra("recipeName");
        rUsername = intent.getStringExtra("rUsername");

        recipe = findViewById(R.id.oNameField);
        user = findViewById(R.id.oMapUsername);
        desc = findViewById(R.id.oMapDesc);
        image = findViewById(R.id.oMapImage);
        country = findViewById(R.id.oMapCountry);
        desc.setMovementMethod(new ScrollingMovementMethod());

        user.setText(rUsername);
        recipe.setText(rName);
        ImageView back = findViewById(R.id.oMapBack);

        // FUNCTION TO POPULATE TEXTVIEWS WITH DATA FROM FIRESTORE
        populateFields();

        back.setOnClickListener(v -> startActivity(new Intent(OpenedFromMapActivity.this, RecipeMapActivity.class)));



    }

    private void populateFields() {
        db.collection("posts").whereEqualTo("username", rUsername).whereEqualTo("recipeName", rName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Post post = doc.toObject(Post.class);
                    rDesc = post.getRecipeDesc();
                    rImage = post.getPictureUrl();
                    lat = post.getLatitude();
                    lng = post.getLongtitude();
                }
                Glide.with(OpenedFromMapActivity.this).load(rImage).into(image);
                Geocoder geocoder = new Geocoder(OpenedFromMapActivity.this, Locale.getDefault());
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(lat, lng, 1);
                    String coutry = address.get(0).getCountryName();
                    country.setText(coutry);
                } catch (IOException e) {
                    e.printStackTrace();
                    country.setText("Can't get location. Sorry! :(");
                }

                desc.setText(rDesc);
            }
        });
    }

    private void setBackground() {
        Drawable gradientInsta = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.parseColor("#d9ccb9"),
                        Color.parseColor("#d9ccb9"),
                        Color.parseColor("#e57949"),
                        Color.parseColor("#e95d22")
                });
        findViewById(R.id.oMapPost).setBackground(gradientInsta);
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