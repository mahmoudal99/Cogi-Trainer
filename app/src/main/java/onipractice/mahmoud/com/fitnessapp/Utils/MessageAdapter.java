package onipractice.mahmoud.com.fitnessapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import onipractice.mahmoud.com.fitnessapp.ChooseUserTypeActivity;
import onipractice.mahmoud.com.fitnessapp.Models.MessageModel;
import onipractice.mahmoud.com.fitnessapp.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<MessageModel> messageModelList;
    FirebaseAuth auth;
    String userID;
    Context context;

    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences cacheData;

    public MessageAdapter(List<MessageModel> list, Context appContext){
        this.messageModelList = list;
        this.context = appContext;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list_item, parent, false);



        cacheData = context.getSharedPreferences("Preferences", ChooseUserTypeActivity.MODE_PRIVATE);
        editor = cacheData.edit();

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView messageText;
        ImageView senderImg;
        LinearLayout linearLayout;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.textMessage);
            senderImg = (ImageView) itemView.findViewById(R.id.senderProfileImg);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.textLinLayout);


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        MessageModel c = messageModelList.get(position);
        auth = FirebaseAuth.getInstance();
        String current_userId = auth.getCurrentUser().getUid();
        String from_user = c.getFrom();
        Log.d("TEST", current_userId);

        if(current_userId.equals(c.getFrom())){

            holder.messageText.setBackgroundResource(R.drawable.chat_send_bar_bg);
            holder.messageText.setTextColor(Color.BLACK);
            holder.senderImg.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.linearLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.linearLayout.setLayoutParams(layoutParams);

        }

        String imageUriString = cacheData.getString(c.getFrom(), "");
        Uri imageUri = Uri.parse(imageUriString);
        Picasso.get().load(imageUri).into(holder.senderImg);

        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }
}
