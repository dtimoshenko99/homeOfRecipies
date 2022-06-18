package abertay.uad.ac.uk.coursework;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PostDone extends AppCompatActivity {

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_done);

        setBackground();
        btn = findViewById(R.id.postDone);

        btn.setOnClickListener(v -> startActivity(new Intent(PostDone.this, HomeActivity.class)));
    }

    private void setBackground() {
        Drawable gradientInsta = new GradientDrawable(GradientDrawable.Orientation.BR_TL,
                new int[]{
                        Color.parseColor("#e8b3b3"),
                        Color.parseColor("#df7782"),
                        Color.parseColor("#e95d22"),
                        Color.parseColor("#e95d22")
                });
        findViewById(R.id.postDoneBack).setBackground(gradientInsta);
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