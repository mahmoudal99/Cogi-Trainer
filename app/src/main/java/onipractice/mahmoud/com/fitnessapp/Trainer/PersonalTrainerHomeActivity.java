package onipractice.mahmoud.com.fitnessapp.Trainer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;
import onipractice.mahmoud.com.fitnessapp.Client.ClientRequestsActivity;
import onipractice.mahmoud.com.fitnessapp.Client.ClientsActivity;
import onipractice.mahmoud.com.fitnessapp.Profile.SettingsActivity;
import onipractice.mahmoud.com.fitnessapp.R;

public class PersonalTrainerHomeActivity extends AppCompatActivity {

    private CircleImageView profileImg;
    private ImageView messengerImageView;
    private CardView clientsCardView, clientRequestsCardView;

    // Firebase
    private DatabaseReference userDatabase;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_trainer_home);

        context = PersonalTrainerHomeActivity.this;

        FirebaseAuth authentication = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user_account_settings").child(authentication.getCurrentUser().getUid());
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                userDatabase.child("online").setValue("true");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        initialize();
        setUpWidgets();


    }

    private void initialize() {

        profileImg = (CircleImageView) findViewById(R.id.profileImg);
        clientRequestsCardView = (CardView) findViewById(R.id.requestsCard);
        clientsCardView = (CardView) findViewById(R.id.clientsCard);
        messengerImageView = (ImageView) findViewById(R.id.messageIv);

    }

    private void setUpWidgets() {

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SettingsActivity.class);
                intent.putExtra("user", "trainer");
                startActivity(intent);

            }
        });

        clientsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ClientsActivity.class);
                startActivity(intent);

            }
        });

        clientRequestsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClientRequestsActivity.class);
                startActivity(intent);
            }
        });

        messengerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ClientsActivity.class);
                startActivity(intent);

            }
        });
    }
}
