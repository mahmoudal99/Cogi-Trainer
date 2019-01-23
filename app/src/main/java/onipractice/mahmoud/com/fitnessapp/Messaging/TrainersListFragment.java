package onipractice.mahmoud.com.fitnessapp.Messaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import onipractice.mahmoud.com.fitnessapp.Client.ClientProfileActivity;
import onipractice.mahmoud.com.fitnessapp.Models.TrainerModel;
import onipractice.mahmoud.com.fitnessapp.R;

public class TrainersListFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    RecyclerView usersList;
    DatabaseReference rootRef;
    DatabaseReference uidRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trainee_list, container, false);

        usersList = (RecyclerView) view.findViewById(R.id.recyclerView);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onStart() {
        super.onStart();

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings");

        FirebaseRecyclerOptions<TrainerModel> options =
                new FirebaseRecyclerOptions.Builder<TrainerModel>()
                        .setQuery(uidRef, TrainerModel.class)
                        .build();

        FirebaseRecyclerAdapter<TrainerModel, TrainersListFragment.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TrainerModel, TrainersListFragment.UsersViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull TrainersListFragment.UsersViewHolder holder, int position, @NonNull TrainerModel model) {
                holder.setName(model.getName());
                holder.setSurname(model.lastname);

                final String user_id = getRef(position).getKey();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent friendIntent = new Intent(getContext(), ClientProfileActivity.class);
                        friendIntent.putExtra("user_id", user_id);
                        startActivity(friendIntent);
                    }
                });
            }

            @NonNull
            @Override
            public TrainersListFragment.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trainers_list_item, parent, false);

                return new TrainersListFragment.UsersViewHolder(view);
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



















