package onipractice.mahmoud.com.fitnessapp.Messaging;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import onipractice.mahmoud.com.fitnessapp.Models.MessageModel;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.Utils.GetTimeAgo;
import onipractice.mahmoud.com.fitnessapp.Utils.MessageAdapter;

public class TextingFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private String messageReceiverId;
    private TextView receiverTv, lastSeenTv;

    RecyclerView messagesRecyclerView;
    FirebaseAuth auth;
    String user_name;
    ImageView sendBtn;
    EditText messageEt;
    FirebaseUser user;
    DatabaseReference rootRef;
    String online_status;

    MessageAdapter messageAdapter;
    LinearLayoutManager linearLayoutManager;
    List<MessageModel> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_text, container, false);

        receiverTv = (TextView) view.findViewById(R.id.receiver_name);
        lastSeenTv = (TextView) view.findViewById(R.id.lastSeenTv);
        sendBtn = (ImageView) view.findViewById(R.id.sendBtn);
        messageEt = (EditText) view.findViewById(R.id.messageEt);
        messageReceiverId = getActivity().getIntent().getStringExtra("user_id");

        linearLayoutManager = new LinearLayoutManager(getContext());

        messagesRecyclerView = (RecyclerView) view.findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);

        loadMessages();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    user_name = ds.child("user_account_settings").child(messageReceiverId).child("firstname").getValue(String.class);
                    receiverTv.setText(user_name);

                    online_status = dataSnapshot.child("online").getValue(String.class);

                }

                if(online_status.equals("true")){

                    lastSeenTv.setText("Online");

                }else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online_status);
                    String lastSeenTime = getTimeAgo.GetTimeAgo(lastTime, getContext());
                    lastSeenTv.setText(lastSeenTime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException());
            }
        };

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ValueEventListener chatValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    if(!ds.child("Chat").child(user.getUid()).hasChild(messageReceiverId)){

                        Map chatAddMap = new HashMap();
                        chatAddMap.put("seen", false);
                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                        Map charUserMap = new HashMap();
                        charUserMap.put("Chat/" + user.getUid() + "/" + messageReceiverId, chatAddMap);
                        charUserMap.put("Chat/" + messageReceiverId + "/" + user.getUid(), chatAddMap);

                        rootRef.child("Chat").child(user.getUid()).updateChildren(charUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError != null){
                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                }
                            }
                        });

                    }
                }

                // Declare adapter and set here

                // OR... adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException());
            }
        };

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return view;

    }

    private void sendMessage(){

        String messageToSend = messageEt.getText().toString();

        if(!TextUtils.isEmpty(messageToSend)){
            String current_user_ref = "messages/" + user.getUid() + "/" + messageReceiverId;
            String message_receiver_ref = "messages/" + messageReceiverId + "/" + user.getUid();

            DatabaseReference user_message_push = rootRef.child("messages").child(user.getUid()).child(messageReceiverId).push();
            String push_id = user_message_push.getKey();


            Map messageMap = new HashMap();
            messageMap.put("message", messageToSend);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", user.getUid());

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(message_receiver_ref + "/" + push_id, messageMap);

            messageEt.setText("");
            messageEt.setHint("Send Message");

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });
        }
    }


    private void loadMessages(){

        auth = FirebaseAuth.getInstance();
        final String userID = auth.getCurrentUser().getUid();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    MessageModel messageModel = dataSnapshot.child(userID).child(messageReceiverId).getValue(MessageModel.class);
                    list.add(messageModel);
                }

                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException());
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}



















