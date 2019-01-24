package onipractice.mahmoud.com.fitnessapp.Client;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

import onipractice.mahmoud.com.fitnessapp.Messaging.ChatActivity;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.TraineeHomeActivity;
import onipractice.mahmoud.com.fitnessapp.Trainer.AddTrainerActivity;

public class ClientProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Variables
    String name, surname, userId, user_id, currentState, referenceString, userType;

    // Widgets
    Button sendClientRequestBtn, deleteClientRequestBtn;
    ImageView backArrow, chat_img;
    TextView nameTv, surnameTv;

    Context context;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference clientDatabaseRef;
    DatabaseReference clientListDatabaseRef;
    DatabaseReference databaseNotificationsRef;
    DatabaseReference rootRef;
    DatabaseReference uidRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        referenceString = getIntent().getStringExtra("reference");
        user_id = getIntent().getStringExtra("user_id");

        userType = getIntent().getStringExtra("user");

        currentState = "not_friends";

        initialize();
        setUpWidgets();
        setUpFirebaseAuth();
        setUserInfo(referenceString);

        clientListDatabaseRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){

                    chat_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(ClientProfileActivity.this , ChatActivity.class);
                            intent.putExtra("user_id", user_id);
                            intent.putExtra("user", "client");
                            startActivity(intent);
                        }
                    });
                }else {
                    Toast.makeText(ClientProfileActivity.this, "Not Clients", Toast.LENGTH_SHORT).show();
                    chat_img.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setUserDetails(String name, String surname){

        nameTv.setText(name);
        surnameTv.setText(surname);

    }

    private void initialize(){

        // Widgets
        backArrow = (ImageView) findViewById(R.id.backArrow);
        chat_img = (ImageView) findViewById(R.id.chat_img);
        chat_img = (ImageView) findViewById(R.id.chat_img);

        sendClientRequestBtn = (Button) findViewById(R.id.sendFriendRequest);
        deleteClientRequestBtn = (Button) findViewById(R.id.deleteFriendRequest);

        nameTv = (TextView) findViewById(R.id.nameTv);
        surnameTv = (TextView) findViewById(R.id.surnameTv);

        // Firebase
        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        clientDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        clientListDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        databaseNotificationsRef = FirebaseDatabase.getInstance().getReference().child("notifications");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setUpWidgets(){

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userType.equals("trainee")){

                    Intent intent = new Intent(ClientProfileActivity.this, AddTrainerActivity.class);
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(ClientProfileActivity.this, ClientsActivity.class);
                    startActivity(intent);
                }

            }
        });

        //---------- Client Requests ----------//

        sendClientRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendClientRequestBtn.setEnabled(false);

                // Not Friends
                if(currentState.equals("not_friends")){

                    notFriendsState();

                }

                // Request Sent
                // Cancel Friend Request

                if(currentState.equals("request_sent")){

                    requestSentState();

                }

                // Client Request Received

                if(currentState.equals("req_received")){

                    requestReceivedState();

                }

                // Delete Request

                if (currentState.equals("Friends")) {

                   deleteRequestState();

                }

            }
        });

    }

    //---------- Client Requests ----------//

    private void notFriendsState(){

        DatabaseReference notificationReference = rootRef.child("notifications").child(user_id).push();
        String newNotificationId = notificationReference.getKey();

        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", user.getUid());
        notificationData.put("type", "request");

        Map requestMap = new HashMap();
        requestMap.put("Friend_req/" + user.getUid() + "/" + user_id + "/request_type", "sent");
        requestMap.put("Friend_req/" + user_id  + "/" + user.getUid() + "/request_type", "received");
        requestMap.put("trainer_requests/"+ user_id + "/" + user.getUid() + "/traineeId", user.getUid());
        requestMap.put("notifications/" + user_id  + "/" + newNotificationId, notificationData);

        rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError != null){
                    Toast.makeText(context, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                }else {

                    currentState = "request_sent";
                    sendClientRequestBtn.setText("Cancel Friend Request");
                }

                sendClientRequestBtn.setEnabled(true);

            }
        });

    }

    private void requestSentState(){

        clientDatabaseRef.child(user.getUid()).child(user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        clientDatabaseRef.child(user_id).child(user.getUid()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        sendClientRequestBtn.setEnabled(true);
                                        currentState = "not_friends";
                                        sendClientRequestBtn.setText("Send Friend Request");

                                        deleteClientRequestBtn.setVisibility(View.INVISIBLE);
                                        deleteClientRequestBtn.setEnabled(false);

                                    }
                                });

                    }
                });
    }

    private void requestReceivedState(){
        String current_user_ref = "Friends/" + user.getUid() + "/" + user_id;
        String message_receiver_ref = "Friends/" + user_id;

        Map trainerMap = new HashMap();
        trainerMap.put("traineeId", user_id);

        Map traineeMap = new HashMap();
        traineeMap.put("trainerId", user.getUid());

        Map friendsMap = new HashMap();
        friendsMap.put(current_user_ref, trainerMap);
        friendsMap.put(message_receiver_ref, traineeMap);

        friendsMap.put("Friend_req/" + user_id + "/" + user.getUid(), null);
        friendsMap.put("Friend_req/" + user.getUid() + "/" + user_id, null);
        friendsMap.put("trainer_requests/"+ user_id + "/" + user.getUid() + "/traineeId", null);

        rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError == null){

                    sendClientRequestBtn.setEnabled(true);
                    currentState = "friends";
                    sendClientRequestBtn.setText("Unfriend this person");

                    deleteClientRequestBtn.setVisibility(View.INVISIBLE);
                    deleteClientRequestBtn.setEnabled(false);

                }else {
                    String error = databaseError.getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void deleteRequestState(){
        Map unfriendMap = new HashMap();
        unfriendMap.put("Friends/" + user.getUid() + "/" + user_id, null);
        unfriendMap.put("Friends/" + user_id + "/" + user.getUid(), null);

        rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError == null){

                    currentState = "not_friends";
                    sendClientRequestBtn.setText("Send friend request");

                    deleteClientRequestBtn.setVisibility(View.INVISIBLE);
                    deleteClientRequestBtn.setEnabled(false);


                }else {

                    String error = databaseError.getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                }
                sendClientRequestBtn.setEnabled(true);

            }
        });
    }

    //---------- Set Client Information ----------//

    private void setUserInfo(String reference){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child(reference).child(user_id);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    name = ds.child("firstname").getValue(String.class);
                    surname = ds.child("lastname").getValue(String.class);

                    setUserDetails(name, surname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        clientDatabaseRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){
                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                    if(req_type.equals("received")){

                        currentState = "req_received";
                        sendClientRequestBtn.setText("Accept Friend Request");
                        deleteClientRequestBtn.setVisibility(View.VISIBLE);
                        deleteClientRequestBtn.setEnabled(true);


                    }else if(req_type.equals("sent")){
                        currentState = "req_sent";
                        sendClientRequestBtn.setText("Cancel Friend Request");
                        deleteClientRequestBtn.setVisibility(View.INVISIBLE);
                        deleteClientRequestBtn.setEnabled(false);

                    }
                }else {

                    clientListDatabaseRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(user_id)){

                                currentState = "friends";
                                sendClientRequestBtn.setText("Unfriend this person");
                                deleteClientRequestBtn.setVisibility(View.INVISIBLE);
                                deleteClientRequestBtn.setEnabled(false);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //---------- Firebase ----------//

    private void setUpFirebaseAuth()
    {
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Log.d(TAG, "Connected");
                    userId = user.getUid();

                }else {
                    Log.d(TAG, "signed out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
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
