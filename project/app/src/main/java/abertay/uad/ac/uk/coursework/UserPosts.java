package abertay.uad.ac.uk.coursework;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



public class UserPosts extends AppCompatActivity {
    FirebaseFirestore db;
    private UserPostsAdapter adapter;
    RecyclerView rec;
    ImageView mapOpen, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        // SET BACKGROUND GRADIENT
        setBackground();

        // UI ELEMENTS
        back = findViewById(R.id.newsArrowBack);
        mapOpen = findViewById(R.id.openMapButton);

        // FIRESTORE INSTANCE
        db = FirebaseFirestore.getInstance();

        // SET RECYCLERVIEW
        setUpRecyclerView();

        // OPEN MAP INTENT
        mapOpen.setOnClickListener(v -> startActivity(new Intent(UserPosts.this, RecipeMapActivity.class)));

        // GO BACK TO PREVIOUS INTENT
        back.setOnClickListener(v -> startActivity(new Intent(UserPosts.this, HomeActivity.class)));
    }

    // SET BACKGROUND GRADIENT
    private void setBackground() {
        Drawable gradientInsta = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                new int[]{
                        Color.parseColor("#d9ccb9"),
                        Color.parseColor("#017890"),
                });
        findViewById(R.id.postsLayout).setBackground(gradientInsta);
    }

    // SET UP RECYCLERVIEW
    private void setUpRecyclerView() {
        // SET QUERY TO RETRIEVE ALL POSTS
        Query query = db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
        // ASSIGN QUERY TO FIRESTORE RECYCLERVIEW AND POST CLASS
        FirestoreRecyclerOptions<Post>options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        // SET UP ADAPTER
        adapter = new UserPostsAdapter(options);
        // GET CURRENT ORIENTATION OF DEVICE
        int orientation = UserPosts.this.getResources().getConfiguration().orientation;
        // GET RECYCLERVIEW AND SET FIXED SIZE TO TRUE
        rec = findViewById(R.id.recycler_view);
        rec.setHasFixedSize(true);
        // HANDLE ORIENTATION CHANGE CRASHES
        if(orientation == 1){
            rec.setLayoutManager(new LinearLayoutManager(this){
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    // DISABLE PREDICTIVE ITEM ANIMATION SUPPORT TO PREVENT APP FROM CRASHING
                    return false;
                }
            });
        } else if(orientation == 2)
        {
            rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false){
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    // DISABLE PREDICTIVE ITEM ANIMATION SUPPORT TO PREVENT APP FROM CRASHING
                    return false;
                }
            });
        }
        rec.setAdapter(adapter);

        // ON ITEM CLICK OPEN ACTIVITY AND PASS ITEM'S DATA
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            Post post = documentSnapshot.toObject(Post.class);
            String url = post.getPictureUrl();
            String desc = post.getRecipeDesc();
            String name = post.getRecipeName();
            String username = post.getUsername();
            double longtitude = post.getLongtitude();
            double latitude = post.getLatitude();
            Intent i = new Intent(UserPosts.this, OpenedPostActivity.class);
            i.putExtra("url", url);
            i.putExtra("desc", desc);
            i.putExtra("name", name);
            i.putExtra("username", username);
            i.putExtra("longtitude", longtitude);
            i.putExtra("latitude", latitude);
            startActivity(i);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // START ADAPTER ON ACTIVITY START
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // STOP ADAPTER ON ACTIVITY STOP
        adapter.stopListening();
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