package onipractice.mahmoud.com.fitnessapp.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import onipractice.mahmoud.com.fitnessapp.Models.FoodItemModel;
import onipractice.mahmoud.com.fitnessapp.R;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ArtistViewHolder> {

    private Context mCtx;
    private List<FoodItemModel> artistList;
    private String userId;
    private String mealType;
    FirebaseMethods firebaseMethods;
    private String day;
    private String meal;
    StringBuilder sb;

    public FoodAdapter(Context mCtx, List<FoodItemModel> artistList, String userId, String day, String mealType) {

        sb = new StringBuilder();
        firebaseMethods = new FirebaseMethods(mCtx);
        this.mCtx = mCtx;
        this.artistList = artistList;
        this.day = day;
        this.userId = userId;
        this.mealType = mealType;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.food_item, parent, false);

        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtistViewHolder holder, int position) {

        final FoodItemModel artist = artistList.get(position);
        holder.textViewName.setText(artist.getFoodName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.food_selected.setVisibility(View.VISIBLE);
                sb.append(artist.getFoodName());
                sb.append(" ");
                firebaseMethods.addDietDay(userId, day, mealType, String.valueOf(sb));
            }
        });

        holder.food_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.food_selected.setVisibility(View.INVISIBLE);
                int index = sb.indexOf(artist.getFoodName());
                sb.delete(index, artist.getFoodName().length());
            }
        });




    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView food_selected;

        public ArtistViewHolder(@NonNull View itemView) {

            super(itemView);
            textViewName = itemView.findViewById(R.id.food_name);
            food_selected = itemView.findViewById(R.id.food_selected);

        }

    }
}
