package onipractice.mahmoud.com.fitnessapp.Jogging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.TraineeHomeActivity;

public class JoggingActivity extends AppCompatActivity {

    CardView joggingCardView;
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging);

        initialize();
        setUpWidgets();
    }

    private void initialize() {
        joggingCardView = (CardView) findViewById(R.id.joggingCardView);
        backArrow = (ImageView) findViewById(R.id.backArrow);
    }

    private void setUpWidgets() {

        joggingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(JoggingActivity.this, JoggingTrackingActivity.class);
                startActivity(intent);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoggingActivity.this, TraineeHomeActivity.class);
                startActivity(intent);
            }
        });
    }
}




























