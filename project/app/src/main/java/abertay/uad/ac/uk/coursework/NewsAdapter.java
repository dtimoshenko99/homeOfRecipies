package abertay.uad.ac.uk.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsAdapter extends FirestoreRecyclerAdapter<News, NewsAdapter.NewsHolder> {

    public NewsAdapter(@NonNull FirestoreRecyclerOptions<News> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsHolder holder, int position, @NonNull News model) {
        holder.newsName.setText(model.getNewsName());
        holder.newsDesc.setText(model.getNewsDesc());
        Timestamp ts = model.getTimestamp();
        Long timeD = ts.getSeconds();
        SimpleDateFormat formater = new SimpleDateFormat(" HH:mm dd-MM-yyyy");
        String dateString = formater.format(new Date(timeD * 1000L));
        holder.newsDate.setText("Date:"+dateString);
    }


    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsAdapter.NewsHolder(v);
    }

    class NewsHolder extends RecyclerView.ViewHolder{
        public TextView newsName, newsDesc, newsDate;


        public NewsHolder(@NonNull View itemView){
            super(itemView);
            newsName = itemView.findViewById(R.id.news_name);
            newsDesc = itemView.findViewById(R.id.news_desc);
            newsDate = itemView.findViewById(R.id.news_date);
        }

    }

}
