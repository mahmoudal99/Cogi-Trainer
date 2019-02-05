package onipractice.mahmoud.com.fitnessapp.Messaging;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import onipractice.mahmoud.com.fitnessapp.Client.ClientsActivity;
import onipractice.mahmoud.com.fitnessapp.Models.MessageModel;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.TraineeHomeActivity;
import onipractice.mahmoud.com.fitnessapp.Utils.GetTimeAgo;
import onipractice.mahmoud.com.fitnessapp.Utils.MessageAdapter;

public class MessengerActivity extends AppCompatActivity {

    //Variables
    private String user_name, userType, messageReceiverId;
    private static final int TOTAL_MESSAGES_LOAD = 10;
    private int currentPage = 1;

    private EditText messageEt;
    private TextView receiverTv, lastSeenTv;
    private SwipeRefreshLayout refreshLayout;
    private FirebaseUser user;

    private MessageAdapter messageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<MessageModel> list = new ArrayList<>();

    //New Solution
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    //Firebase
    private DatabaseReference rootRef;
    private RecyclerView messagesRecyclerView;
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceiverId = getIntent().getStringExtra("user_id");
        refreshLayout = findViewById(R.id.refreshLayout);

        userType = getIntent().getStringExtra("user");

        ImageView backArrow = findViewById(R.id.backArrow);
        receiverTv = findViewById(R.id.receiver_name);
        lastSeenTv = findViewById(R.id.lastSeenTv);
        ImageView sendBtn = findViewById(R.id.sendBtn);
        messageEt = findViewById(R.id.messageEt);

        messageAdapter = new MessageAdapter(list, MessengerActivity.this);
        linearLayoutManager = new LinearLayoutManager(this);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);

        if (Build.VERSION.SDK_INT >= 11) {
            messagesRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        messagesRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                messagesRecyclerView.smoothScrollToPosition(
                                        messagesRecyclerView.getAdapter().getItemCount() - 1);
                            }
                        }, 100);
                    }
                }
            });
        }

        loadMessages();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userType.equals("client")) {
                    Intent intent = new Intent(MessengerActivity.this, ClientsActivity.class);
                    startActivity(intent);
                } else if (userType.equals("trainee")) {
                    Intent intent = new Intent(MessengerActivity.this, TraineeHomeActivity.class);
                    startActivity(intent);
                }
            }
        });

        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("user_account_settings").child(messageReceiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("firstname")) {

                    user_name = dataSnapshot.child("firstname").getValue(String.class);
                    receiverTv.setText(user_name);

                    String online_status = dataSnapshot.child("online").getValue().toString();

                    if (online_status.equals("true")) {
                        lastSeenTv.setText("Online");
                    } else {
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long lastTime = Long.parseLong(online_status);
                        String lastSeenTime = GetTimeAgo.GetTimeAgo(lastTime, getApplicationContext());
                        lastSeenTv.setText(lastSeenTime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rootRef.child("Chat").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(messageReceiverId)) {

                    Map<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map<String, Object> charUserMap = new HashMap<String, Object>();
                    charUserMap.put("Chat/" + user.getUid() + "/" + messageReceiverId, chatAddMap);
                    charUserMap.put("Chat/" + messageReceiverId + "/" + user.getUid(), chatAddMap);

                    rootRef.updateChildren(charUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

                messagesRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messagesRecyclerView.smoothScrollToPosition(
                                messagesRecyclerView.getAdapter().getItemCount() - 1);
                    }
                }, 100);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });
    }

    private void loadMoreMessages() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        DatabaseReference messageRef = rootRef.child("messages").child(user.getUid()).child(messageReceiverId);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot != null) {

                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    String messageKey = dataSnapshot.getKey();

                    if (!mPrevKey.equals(messageKey)) {
                        list.add(itemPos++, messageModel);
                    } else {
                        mPrevKey = mLastKey;
                    }

                    if (itemPos == 1) {
                        mLastKey = messageKey;
                    }

                    messageAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                    linearLayoutManager.scrollToPositionWithOffset(10, 0);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void sendMessage() {

        String messageToSend = messageEt.getText().toString();
        if (!TextUtils.isEmpty(messageToSend)) {
            String current_user_ref = "messages/" + user.getUid() + "/" + messageReceiverId;
            String message_receiver_ref = "messages/" + messageReceiverId + "/" + user.getUid();

            DatabaseReference user_message_push = rootRef.child("messages").child(user.getUid()).child(messageReceiverId).push();
            String push_id = user_message_push.getKey();

            Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("message", messageToSend);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", user.getUid());

            Map<String, Object> messageUserMap = new HashMap<>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(message_receiver_ref + "/" + push_id, messageMap);

            messageEt.setText("");
            messageEt.setHint("Send Message");

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage());
                    }
                }
            });
        }
    }

    private void loadMessages() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();

        DatabaseReference messageRef = rootRef.child("messages").child(userID).child(messageReceiverId);
        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_MESSAGES_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot != null) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);

                    itemPos++;

                    if (itemPos == 1) {
                        String messageKey = dataSnapshot.getKey();
                        mLastKey = messageKey;
                        mPrevKey = messageKey;
                    }

                    list.add(messageModel);
                    messageAdapter.notifyDataSetChanged();
                    messagesRecyclerView.scrollToPosition(list.size());
                    refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}





















