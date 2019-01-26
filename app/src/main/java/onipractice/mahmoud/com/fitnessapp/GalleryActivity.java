package onipractice.mahmoud.com.fitnessapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import onipractice.mahmoud.com.fitnessapp.Models.MyTrainerModel;

public class GalleryActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    RecyclerView usersList;
    DatabaseReference rootRef;
    DatabaseReference uidRef;
    DatabaseReference usersReference;
    ImageView chat_img;
    CircleImageView trainerProfileImg;
    String name, surname, online_status, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        setUpFirebaseAuth();

        usersList = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        usersReference = FirebaseDatabase.getInstance().getReference().child("user_account_settings");
    }

    @Override
    public void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("Friends").child(user.getUid());

        FirebaseRecyclerOptions<MyTrainerModel> options =
                new FirebaseRecyclerOptions.Builder<MyTrainerModel>()
                        .setQuery(uidRef, MyTrainerModel.class)
                        .build();

        FirebaseRecyclerAdapter<MyTrainerModel, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MyTrainerModel, UsersViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull MyTrainerModel model) {

                user_id = getRef(position).getKey();
                Log.d("POSITION", String.valueOf(position));

                usersReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        name = dataSnapshot.child("path").getValue(String.class);
                        Uri uri = Uri.parse(name);
                        holder.setPath(uri);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.progress_gallery_item, parent, false);

                return new UsersViewHolder(view);
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
        public void setPath(Uri uri){
            Log.d("PATHDDD", uri.toString());
            ImageView userName = (ImageView) view.findViewById(R.id.ivGallery);
            userName.setImageURI(uri);
        }

    }

    private void setUpFirebaseAuth()
    {
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Log.d("ATG", "Connected");

                }else {
                    Log.d("ATG", "signed out");
                }
            }
        };
    }


    @Override
    public void onStop() {

        super.onStop();
        if (authStateListener != null)
        {
            auth.removeAuthStateListener(authStateListener);
        }
    }

}
