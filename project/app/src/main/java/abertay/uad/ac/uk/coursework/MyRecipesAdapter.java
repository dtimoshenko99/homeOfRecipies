package abertay.uad.ac.uk.coursework;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyRecipesAdapter extends FirestoreRecyclerAdapter<Post, MyRecipesAdapter.RecipeHolder> {

    private OnItemClickListener listener;

    public MyRecipesAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipeHolder holder, int position, @NonNull Post model) {
        holder.recipeName.setText(model.getRecipeName());
        holder.setRecipeImage(model.getPictureUrl());
        Timestamp ts = model.getTimestamp();
        Long timeD = ts.getSeconds();
        SimpleDateFormat formater = new SimpleDateFormat(" HH:mm dd-MM-yyyy");
        String dateString = formater.format(new Date(timeD * 1000L));
        holder.recipeDate.setText("Date Posted:"+dateString);
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recipes_layout, parent, false);
        return new MyRecipesAdapter.RecipeHolder(v);
    }

    class RecipeHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView recipeName, recipeDate;
        public ImageView imageView;
        public RecipeHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDate = itemView.findViewById(R.id.date_posted);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setRecipeImage(String downloadUrl){
            Glide.with(itemView.getContext()).load(downloadUrl).centerCrop().into(imageView);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1,"Delete item");
            MenuItem update = menu.add(Menu.NONE, 2, 2, "Update item");
            delete.setOnMenuItemClickListener(this);
            update.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getBindingAdapterPosition();
            if(position != RecyclerView.NO_POSITION && listener != null){
                switch(item.getItemId()){
                    case 1:
                        listener.deleteClick(getSnapshots().getSnapshot(position), position);
                        return true;
                    case 2:
                        listener.updateClick(getSnapshots().getSnapshot(position), position);
                        return true;

                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void deleteClick(DocumentSnapshot documentSnapshot,int position);
        void updateClick(DocumentSnapshot documentSnapshot,int position);
    }

    public void setOnItemClickListener(MyRecipesAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
