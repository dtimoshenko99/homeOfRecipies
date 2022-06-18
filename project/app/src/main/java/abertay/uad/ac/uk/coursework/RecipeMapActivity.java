package abertay.uad.ac.uk.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class RecipeMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private Map<Double, Double> postPos;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_map);
        ImageView back = findViewById(R.id.arrowMapBack);

        // GOOGLE API MAP INIT
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        postPos = new ArrayMap<>();
        db = FirebaseFirestore.getInstance();


        back.setOnClickListener(v -> startActivity(new Intent(RecipeMapActivity.this, UserPosts.class)));


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // GET LATITUDE AND LONGITUDE FROM FIRESTORE RECORDS
        db.collection("posts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Post post = doc.toObject(Post.class);
                    double lat = post.getLatitude();
                    double lng = post.getLongtitude();
                    postPos.put(lat, lng);

                    // SET MARKERS ON POSITIONS ON MAP
                    LatLng marker = new LatLng(lat, lng);
                    googleMap.addMarker(new MarkerOptions()
                            .position(marker)
                            .title(post.getRecipeName())
                            .snippet(post.getUsername()));
                }
            }
        });

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // ON MARKER WINDOW CLICK INITIATE NEW INTENT AND PASS VARIABLES TO NEW ACTIVITY
        Intent i = new Intent(this, OpenedFromMapActivity.class);
        i.putExtra("recipeName", marker.getTitle());
        i.putExtra("rUsername", marker.getSnippet());
        startActivity(i);
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
