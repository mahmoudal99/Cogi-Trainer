package onipractice.mahmoud.com.fitnessapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import onipractice.mahmoud.com.fitnessapp.Training.CardioActivity;
import onipractice.mahmoud.com.fitnessapp.Training.StretchingActivity;
import onipractice.mahmoud.com.fitnessapp.Training.WeightsActivity;

public class TrainingActivity extends AppCompatActivity {

    ImageView backArrow;
    CardView cardioCardView, weightsCardView,trainingCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        init();
        setupWidgets();
    }

    public void init(){

        backArrow = (ImageView) findViewById(R.id.backArrow);
        cardioCardView = (CardView) findViewById(R.id.cardioCadView);
        weightsCardView = (CardView) findViewById(R.id.weightsCardView);
        trainingCardView = (CardView) findViewById(R.id.stretchingCardView);
    }

    public void setupWidgets(){

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingActivity.this, TraineeHomeActivity.class);
                startActivity(intent);
            }
        });

        cardioCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingActivity.this, CardioActivity.class);
                startActivity(intent);
            }
        });

        weightsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingActivity.this, WeightsActivity.class);
                startActivity(intent);
            }
        });

        trainingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingActivity.this, StretchingActivity.class);
                startActivity(intent);
            }
        });

    }
}




















