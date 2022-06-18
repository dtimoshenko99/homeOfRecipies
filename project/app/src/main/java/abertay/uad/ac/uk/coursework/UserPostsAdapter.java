package abertay.uad.ac.uk.coursework;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserPostsAdapter extends FirestoreRecyclerAdapter<Post, UserPostsAdapter.UserPostsHolder> {


    private OnItemClickListener listener;


    public UserPostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserPostsHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Post model) {
        holder.textViewName.setText(model.getRecipeName());
        holder.setRecipeImage(model.getPictureUrl());

    }

    @NonNull
    @Override
    public UserPostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new UserPostsHolder(v);
    }


    class UserPostsHolder extends RecyclerView.ViewHolder{
        public TextView textViewName;
        public ImageView imageView;
        public UserPostsHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });

        }

        public void setRecipeImage(String downloadUrl){
            Glide.with(itemView.getContext()).load(downloadUrl).centerCrop().into(imageView);
        }

    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
