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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OpenedPostActivity extends AppCompatActivity {

    ImageView image, back;
    TextView usernameTxt, desc, recipeCountry, nametxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_post);

        setBackground();

        image = findViewById(R.id.recipeImage);
        nametxt = findViewById(R.id.nameField);
        back = findViewById(R.id.openedPostBack);
        usernameTxt = findViewById(R.id.usernameField);
        desc = findViewById(R.id.descField);
        recipeCountry = findViewById(R.id.recipeCountry);
        desc.setMovementMethod(new ScrollingMovementMethod());

        getAndSetData();

        back.setOnClickListener(v -> startActivity(new Intent(OpenedPostActivity.this, UserPosts.class)));

    }

    private void getAndSetData() {
        // GET DATA FROM PREVIOUS ACTIVITY
        Intent intent = getIntent();
        String picture = intent.getStringExtra("url");
        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        String description = intent.getStringExtra("desc");
        Double lat = intent.getDoubleExtra("latitude", 0);
        Double lng = intent.getDoubleExtra("longtitude", 0);

        // SET FIELDS WITH DATA
        usernameTxt.setText(username);
        nametxt.setText(name);
        desc.setText(description);
        // SET PICTURE
        Glide.with(this).load(picture).centerCrop().into(image);

        // USE GEOCODER TO SET LOCATION FIELD
        Geocoder geocoder = new Geocoder(OpenedPostActivity.this, Locale.getDefault());
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(lat, lng, 1);
            String coutry = address.get(0).getCountryName();
            recipeCountry.setText(coutry);
        } catch (IOException e) {
            e.printStackTrace();
            recipeCountry.setText("Can't get location. Sorry! :(");
        }
    }

    private void setBackground() {
        Drawable gradientInsta = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.parseColor("#d9ccb9"),
                        Color.parseColor("#d9ccb9"),
                        Color.parseColor("#e57949"),
                        Color.parseColor("#e95d22")
                });
        findViewById(R.id.openedPost).setBackground(gradientInsta);
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