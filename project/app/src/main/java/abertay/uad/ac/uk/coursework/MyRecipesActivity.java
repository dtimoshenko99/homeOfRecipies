package abertay.uad.ac.uk.coursework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class MyRecipesActivity extends AppCompatActivity{

    private MyRecipesAdapter adapter;
    SharedPreferences shared;
    FirebaseFirestore db;
    FirebaseStorage storage;
    String email, id, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        setBackground();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        shared = getSharedPreferences("details",
                Context.MODE_PRIVATE);
        email = shared.getString("email", "email");
        ImageView back = findViewById(R.id.newsArrowBack);



        setUpRecyclerView();

        back.setOnClickListener(v -> startActivity(new Intent(MyRecipesActivity.this, HomeActivity.class)));



    }

    private void setBackground() {
        Drawable gradientInsta = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                new int[]{
                        Color.parseColor("#d9ccb9"),
                        Color.parseColor("#017890"),
                });
        findViewById(R.id.myRecipesLayout).setBackground(gradientInsta);
    }

    private void setUpRecyclerView() {
        // SET QUERY TO RETRIEVE ALL POSTS
        Query query = db.collection("posts").whereEqualTo("email", email).orderBy("timestamp", Query.Direction.DESCENDING);
        // ASSIGN QUERY TO FIRESTORE RECYCLERVIEW AND POST CLASS
        FirestoreRecyclerOptions<Post>options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();
        // SET UP ADAPTER
        adapter = new MyRecipesAdapter(options);
        // GET CURRENT ORIENTATION OF DEVICE
        int orientation = MyRecipesActivity.this.getResources().getConfiguration().orientation;
        // GET RECYCLERVIEW AND SET FIXED SIZE TO TRUE
        RecyclerView recyclerView = findViewById(R.id.my_recipies_recycler);
        recyclerView.setHasFixedSize(true);
        // HANDLE ORIENTATION CHANGE CRASHES
        if(orientation == 1){
            recyclerView.setLayoutManager(new LinearLayoutManager(this){
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    // DISABLE PREDICTIVE ITEM ANIMATION SUPPORT TO PREVENT APP FROM CRASHING
                    return false;
                }
            });
        } else if(orientation == 2)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false){
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    // DISABLE PREDICTIVE ITEM ANIMATION SUPPORT TO PREVENT APP FROM CRASHING
                    return false;
                }
            });
        }
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyRecipesAdapter.OnItemClickListener() {
            @Override
            public void deleteClick(DocumentSnapshot documentSnapshot, int position){
                Post post = documentSnapshot.toObject(Post.class);
                id = documentSnapshot.getId();
                url = post.getPictureUrl();

                // BUILD NEW ALERT DIALOG
                new AlertDialog.Builder(MyRecipesActivity.this)
                        .setTitle("Decision")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            storage.getReferenceFromUrl(url).delete().addOnSuccessListener(unused -> {
                                db.collection("posts").document(id).delete();
                                Toast.makeText(MyRecipesActivity.this, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> Toast.makeText(MyRecipesActivity.this, "Failed to delete...Try again", Toast.LENGTH_SHORT).show());
                        }).setNegativeButton(android.R.string.no,null)
                        .setIcon(R.drawable.ic_fork)
                        .show();
            }

            @Override
            public void updateClick(DocumentSnapshot documentSnapshot, int position) {
                Post post = documentSnapshot.toObject(Post.class);
                String id = documentSnapshot.getId();
                Intent i = new Intent(MyRecipesActivity.this, UpdateRecipe.class);
                i.putExtra("recipeUrl", post.getPictureUrl());
                i.putExtra("recipeName", post.getRecipeName());
                i.putExtra("recipeDesc", post.getRecipeDesc());
                i.putExtra("latitude", post.getLatitude());
                i.putExtra("longtitude", post.getLongtitude());
                i.putExtra("docID", id);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
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