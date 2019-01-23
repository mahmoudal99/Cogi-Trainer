package onipractice.mahmoud.com.fitnessapp.Training;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.TrainingActivity;

public class CardioActivity extends AppCompatActivity {

    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        init();
        setupWidgets();
    }

    public void init(){

        backArrow = (ImageView) findViewById(R.id.backArrow);
    }

    public void setupWidgets(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardioActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        });
    }
}
