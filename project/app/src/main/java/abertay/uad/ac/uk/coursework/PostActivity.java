package abertay.uad.ac.uk.coursework;

import static abertay.uad.ac.uk.coursework.Utils.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {
    private final static int PLACE_PICKER_REQUEST = 999;
    private final static int CAMERA_PIC_REQUEST = 400;
    private final static int PERMISSION_REQUEST_CODE = 40;

    // PERMISSIONS NEEDED FOR INTENT
    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };

    Intent requestFileIntent;
    //FirebaseAUTH and Firestore var init
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    //Current user
    String currentUser;
    String email;
    String recipeName;
    String picURL;
    String username;
    String country;
    //UI elements init
    Button postButton;
    ImageView imageView, back, locationBttn, cameraBttn, gallery;
    EditText name, recipeText;
    ProgressBar progressBar;
    double longtitude, latitude;
    TextView locationTxt;
    //Storage variables
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageTask uploadStorage;
    String regEx = "[^A-Za-z0-9]";


    //filepath
    Uri filepath;
    Uri path;
    String imageFilePath;
    Uri photoURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Utils.checkAllPermissions(this,permissions)){
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
        setContentView(R.layout.activity_post);

        // SET BACKGROUND GRADIENT DEPENDING ON SYSTEM THEME SET (DARKMODE/LIGHTMODE)
        setBackground();

        //UI ELEMENTS
        gallery = findViewById(R.id.addPhoto);
        cameraBttn = findViewById(R.id.cameraButton);
        postButton = findViewById(R.id.postButton);
        imageView = findViewById(R.id.postPicture);
        back = findViewById(R.id.arrowBackPost);
        progressBar = findViewById(R.id.postRecipeProgress);
        progressBar.setVisibility(View.INVISIBLE);
        locationBttn = findViewById(R.id.locationSet);
        locationTxt = findViewById(R.id.locationText);


        //INIT IMPLICIT INTENT TO HANDLE IMAGE PICKING
        requestFileIntent = new Intent(Intent.ACTION_PICK);
        requestFileIntent.setType("image/jpg");

        //INIT AUTH, FIRESTORE AND STORAGE+STORAGE REFERENCE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        email = user.getEmail();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //get current user ID
        checkUser();

        //INIT UI ELEMENTS
        name = findViewById(R.id.nameInput);
        recipeText = findViewById(R.id.recipeInput);

        // BUTTON TO OPEN PLACE PICKER INTENT
        locationBttn.setOnClickListener(v -> placePickerIntent());

        // BUTTON TO OPEN CAMERA INTENT
        cameraBttn.setOnClickListener(v -> {
            try {
                openCameraIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // BUTTON TO OPEN GALLERY INTENT
        gallery.setOnClickListener(v -> requestFile());

        // BUTTON TO START UPLOAD AND POST PROCESS
        postButton.setOnClickListener(v -> initiateUpload());

        // BUTTON TO GO BACK
        back.setOnClickListener(v -> startActivity(new Intent(PostActivity.this, HomeActivity.class)));

    }

    private void checkUser() {
        if (user.getUid() == null) {
            Toast.makeText(PostActivity.this, "Not logged in", Toast.LENGTH_LONG).show();
        } else {
            currentUser = user.getUid();
            db.collection("users").document(currentUser)
                    .addSnapshotListener((documentSnapshot, e) -> username = documentSnapshot.getString("username"));
        }
    }

    private void placePickerIntent() {
        // CREATE A NEW BUILDER FOR AN INTENT TO LAUNCH PLACE PICKER UI
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            // START PLACEPICKER INTENT
            startActivityForResult(builder.build(PostActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void initiateUpload() {
        String nm = name.getText().toString();
        String txt = recipeText.getText().toString();
        // CHECK IF ALL NECESSARY FIELDS, VARIABLES ARE NOT EMPTY
        if (nm.isEmpty() || txt.isEmpty() || path == null || locationTxt.getText() == "Set country here") {
            Log.d(TAG, "initiateUpload: "+nm);
            Log.d(TAG, "initiateUpload: "+txt);
            Log.d(TAG, "initiateUpload: "+path);
            Log.d(TAG, "initiateUpload: "+locationTxt);
            Toast.makeText(PostActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();

        } else if (nm.matches(regEx)){
            name.setError("Please enter only letters and numbers.");
            Toast.makeText(PostActivity.this, ""+nm, Toast.LENGTH_LONG).show();
        } else if(txt.matches(regEx)){
            recipeText.setError("Please enter only letters and numbers.");
        }
        else {
            // IF UPLOAD TASK IS RUNNING
            if (uploadStorage != null && uploadStorage.isInProgress()) {
                Toast.makeText(PostActivity.this, "Please wait, upload running", Toast.LENGTH_SHORT).show();
            } else {
                // UPLOAD FUNCTION FOR PICTURE UPLOAD TO DATABASE
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        // IF APP RECEIVED PATH TO IMAGE PROCEED WITH UPLOADING
        if (filepath != null) {
            // GET REFERENCE OF THE STORAGE
            StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString());
            // CREATE UPLOAD TASK
            UploadTask uploadTask = ref.putFile(filepath);
            // EXECUTE
            uploadStorage = uploadTask.addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(this::postRecipe);
            }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, "Exception:" + e, Toast.LENGTH_LONG).show()).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE));
        }
        else {
        }
    }


    private void postRecipe(Uri uri) {
        // GET TEXT FROM FIELDS
        recipeName = name.getText().toString();
        String recipeTxt = recipeText.getText().toString();
        // CREATE A MAP TO HOLD UPLOAD DATA
        Map<String, Object> recipe = new HashMap<>();
        picURL = uri.toString();
        recipe.put("recipeName", recipeName);
        recipe.put("recipeDesc", recipeTxt);
        recipe.put("timestamp", FieldValue.serverTimestamp());
        recipe.put("pictureUrl", picURL);
        recipe.put("username", username);
        recipe.put("email", email);
        recipe.put("longtitude", longtitude);
        recipe.put("latitude", latitude);

        // CREATE AN EMPTY DOCUMENT IN POSTS DB AND PUT DATA
        db.collection("posts").document().set(recipe).addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.INVISIBLE);
            // GO TO POST_DONE ACTIVITY AFTER SUCCESS
            startActivity(new Intent(PostActivity.this, PostDone.class));
        }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, "Error:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());

    }

    protected void requestFile() {
        startActivityForResult(requestFileIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);

        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case RESULT_OK:
                        filepath = returnIntent.getData();
                        imageView.setImageURI(filepath);
                        path = filepath;
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;
            case PLACE_PICKER_REQUEST:
                switch (resultCode) {
                    case RESULT_OK:
                        // GET FULL ADDRESS OF THE PLACE SELECTED
                        Place place = PlacePicker.getPlace(PostActivity.this, returnIntent);
                        // AS WE ARE ONLY INTERESTED IN LATITUDE AND LONGTITUDE GET THEM FROM PLACEPICKER
                        latitude = place.getLatLng().latitude;
                        longtitude = place.getLatLng().longitude;
                        // CONSTRUCT A GEOCODER TO REVERSE GEOCODE THE LAT AND LNG PROVIDED BY PLACEPICKER
                        Geocoder geocoder = new Geocoder(PostActivity.this, Locale.getDefault());
                        try {
                            // CREATE A LIST OF ADDRESSES TO PASS THE GEOCODER RESULT
                            List<Address> address = geocoder.getFromLocation(latitude, longtitude, 1);
                            // GET THE FIRST AND ONLY RECORD FROM LIST AND GET COUNTRY NAME FROM IT
                            country = address.get(0).getCountryName();
                            locationTxt.setText(country);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(PostActivity.this, "Please pick a location.", Toast.LENGTH_SHORT).show();
                        break;
                }

            case PERMISSION_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(PostActivity.this, "Can't post anything without permitting!", Toast.LENGTH_LONG).show();
                        break;
                }
                break;

            case CAMERA_PIC_REQUEST:
                switch (resultCode) {
                    case RESULT_OK:
                        // PLACE IMAGE INTO IMAGEVIEW WITH GLIDE LIBRARY
                        Glide.with(this).load(imageFilePath).into(imageView);
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;
        }
    }
    private File createImageFile() throws IOException {

        // GET CURRENT TIMESTAMP
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        // CREATE IMAGE FILE STRING
        String imageFileName = "IMG_" + timeStamp + "_";

        // GET STORAGE FILEPATH OF PICTURES
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // CREATE A FILE WITH FILENAME .JPG IN STORAGE DIRECTORY
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        // GET URI
        filepath = Uri.fromFile(image);
        path = Uri.fromFile(image);
        return image;
    }

    private void openCameraIntent() throws IOException {

        // CONSTRUCT NEW INTENT FOR TAKING PICTURES
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        // CALL FUNCTION TO CREATE IMAGE
            File photoFile = createImageFile();
            if (photoFile != null) {
                // GET URI FOR THE FILE USING FILEPROVIDER (
                photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                // SET URI FILEPATH FOR INTENT OUTPUT
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, CAMERA_PIC_REQUEST);
            }
    }

    private void setBackground() {

        // GET CURRENT DEVICE MODE (DARK OR LIGHT)
        // SET BACKGROUND GRADIENT BY CREATING NEW DRAWABLE
        // PASSING COLORS INTO IT, SPECIFYING DIRECTION OF START -> END
        // SETTING BACKGROUND OF THE ACTIVITY
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                Drawable gradientLight = new GradientDrawable(GradientDrawable.Orientation.BR_TL,
                        new int[]{
                                Color.parseColor("#d4edf4"),
                                Color.parseColor("#f6c2c2"),
                        });
                findViewById(R.id.postBack).setBackground(gradientLight);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                Drawable gradientNight = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.parseColor("#d4edf4"),
                                Color.parseColor("#f6c2c2")
                        });
                findViewById(R.id.postBack).setBackground(gradientNight);
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