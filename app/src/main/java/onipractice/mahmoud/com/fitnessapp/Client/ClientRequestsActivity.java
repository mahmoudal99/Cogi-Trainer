package onipractice.mahmoud.com.fitnessapp.Client;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import onipractice.mahmoud.com.fitnessapp.Models.ClientModel;
import onipractice.mahmoud.com.fitnessapp.Trainer.PersonalTrainerHomeActivity;
import onipractice.mahmoud.com.fitnessapp.R;

public class ClientRequestsActivity extends AppCompatActivity {

    ImageView backArrow;
    RecyclerView usersList;
    DatabaseReference rootRef;
    DatabaseReference uidRef;
    DatabaseReference clientReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_requests);

        usersList = (RecyclerView) findViewById(R.id.clientsRecyclerView);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(ClientRequestsActivity.this));
    }

    @Override
    public void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("trainer_requests").child(user.getUid());

        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientRequestsActivity.this, PersonalTrainerHomeActivity.class);
                startActivity(intent);
            }
        });

        FirebaseRecyclerOptions<ClientModel> options =
                new FirebaseRecyclerOptions.Builder<ClientModel>()
                        .setQuery(uidRef, ClientModel.class)
                        .build();

        FirebaseRecyclerAdapter<ClientModel, ClientRequestsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClientModel, ClientRequestsActivity.UsersViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull final ClientRequestsActivity.UsersViewHolder holder, int position, @NonNull ClientModel model) {

                final String user_id = getRef(position).getKey();

                clientReference = rootRef.child("user_account_settings").child(model.traineeId);
                clientReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("firstname").getValue().toString();
                        final String surname = dataSnapshot.child("lastname").getValue().toString();
                        holder.setName(name);
                        holder.setSurname(surname);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent friendIntent = new Intent(ClientRequestsActivity.this, ClientProfileActivity.class);
                        friendIntent.putExtra("user_id", user_id);
                        friendIntent.putExtra("reference", "user_account_settings");
                        startActivity(friendIntent);
                    }
                });
            }

            @NonNull
            @Override
            public ClientRequestsActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trainers_list_item, parent, false);

                return new ClientRequestsActivity.UsersViewHolder(view);
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
