package onipractice.mahmoud.com.fitnessapp.Trainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import onipractice.mahmoud.com.fitnessapp.Client.ClientProfileActivity;
import onipractice.mahmoud.com.fitnessapp.Models.TrainerModel;
import onipractice.mahmoud.com.fitnessapp.R;

public class AddTrainerActivity extends AppCompatActivity {

    RecyclerView usersList;

    // Firebase
    DatabaseReference rootRef;
    DatabaseReference uidRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trainer);

        usersList = (RecyclerView) findViewById(R.id.recyclerView);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(AddTrainerActivity.this));
    }

    @Override
    public void onStart() {
        super.onStart();

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("trainer");

        FirebaseRecyclerOptions<TrainerModel> options =
                new FirebaseRecyclerOptions.Builder<TrainerModel>()
                        .setQuery(uidRef, TrainerModel.class)
                        .build();

        FirebaseRecyclerAdapter<TrainerModel, AddTrainerActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TrainerModel, AddTrainerActivity.UsersViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull AddTrainerActivity.UsersViewHolder holder, int position, @NonNull TrainerModel model) {
                holder.setName(model.getName());
                holder.setSurname(model.lastname);

                final String user_id = getRef(position).getKey();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent friendIntent = new Intent(AddTrainerActivity.this, ClientProfileActivity.class);
                        friendIntent.putExtra("user_id", user_id);
                        friendIntent.putExtra("reference", "trainer");
                        startActivity(friendIntent);
                    }
                });
            }

            @NonNull
            @Override
            public AddTrainerActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trainers_list_item, parent, false);

                return new AddTrainerActivity.UsersViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        usersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View view;

        public UsersViewHolder(View itemView){
            super(itemView);
            view = itemView;
        }

        public void setName(String name){
            TextView userName = (TextView) view.findViewById(R.id.userName);
            userName.setText(name);
        }

        public void setSurname(String lastname){
            TextView surname = (TextView) view.findViewById(R.id.lastname);
            surname.setText(lastname);
        }

    }
}
