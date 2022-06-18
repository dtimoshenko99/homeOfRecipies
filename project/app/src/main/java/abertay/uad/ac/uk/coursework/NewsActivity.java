package abertay.uad.ac.uk.coursework;

import android.content.Intent;
import android.content.res.Configuration;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    CollectionReference col;
    private NewsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        db = FirebaseFirestore.getInstance();

        ImageView back = findViewById(R.id.newsArrowBack);


        setBackground();

        setUpRecyclerView();


        back.setOnClickListener(v -> startActivity(new Intent(NewsActivity.this, HomeActivity.class)));
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
                findViewById(R.id.newsLayout).setBackground(gradientLight);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                Drawable gradientNight = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.parseColor("#d9ccb9"),
                                Color.parseColor("#fc88aa"),
                                Color.parseColor("#ff82a9"),
                        });
                findViewById(R.id.newsLayout).setBackground(gradientNight);
                break;
        }
    }

    private void setUpRecyclerView() {
        Query query = db.collection("news").orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<News> options = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(query, News.class)
                .build();

        adapter = new NewsAdapter(options);
        int orientation = NewsActivity.this.getResources().getConfiguration().orientation;
        RecyclerView recyclerView = findViewById(R.id.news_rec_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(orientation == 1){
            recyclerView.setLayoutManager(new LinearLayoutManager(this){
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    // Disable predictive item animation support to prevent app from crashing
                    return false;
                }
            });
        } else if(orientation == 2)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false){
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    // Disable predictive item animation support to prevent app from crashing
                    return false;
                }
            });
        }
        recyclerView.setAdapter(adapter);
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