package onipractice.mahmoud.com.fitnessapp.Training;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.TrainingActivity;

public class WeightsActivity extends AppCompatActivity {

    int[] arms_drawables = {
            R.drawable.barbell_bench,
            R.drawable.decline_bench,
            R.drawable.best_over_barbell
    };

    CardView armsCardView, backCardView, shouldersCardView, absCardView, chestCardView, legsCardView;

    ImageView image1, image2, image3;

    SliderLayout sliderLayout;
    ImageView backArrow;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);

        armsCardView = (CardView) findViewById(R.id.armsCardView);
        chestCardView = (CardView) findViewById(R.id.chestCardView);

        armsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightDialog();
            }
        });

        chestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightDialog();
            }
        });

        init();
        setupWidgets();

        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :

        setSliderViews();
    }

    public void init(){

        backArrow = (ImageView) findViewById(R.id.backArrow);
    }

    public void setupWidgets(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightsActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageUrl("https://images.pexels.com/photos/547114/pexels-photo-547114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 1:
                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 2:
                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
                    break;
//                case 3:
//                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
//                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription("setDescription " + (i + 1));
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(WeightsActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
    }

    //---------- Change Weight Dialog ----------//

    private void weightDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WeightsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_weights_training, null);

        image1 = (ImageView) mView.findViewById(R.id.image1);
        image1.setImageResource(arms_drawables[0]);

        image2 = (ImageView) mView.findViewById(R.id.image2);
        image2.setImageResource(arms_drawables[1]);

        image3 = (ImageView) mView.findViewById(R.id.image3);
        image3.setImageResource(arms_drawables[2]);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }
}
