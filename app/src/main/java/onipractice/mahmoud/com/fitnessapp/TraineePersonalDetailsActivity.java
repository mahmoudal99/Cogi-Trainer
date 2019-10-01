package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import onipractice.mahmoud.com.fitnessapp.Models.PersonalDetails;

public class TraineePersonalDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = "TraineePersonalDetails";

    int newWeight, newHeight;
    private Spinner weightSpinner, heightSpinner;

    private ArrayAdapter<CharSequence> weightAdapter;
    private ArrayAdapter<CharSequence> heightAdapter;

    private TextView confirm, cancel;
    private Button maleBtn, femaleBtn, nextBtn;
    private String weight, height, age, gender, userID;
    private Context context;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        context = TraineePersonalDetailsActivity.this;

        setWidgets();
        setUpFirebaseAuth();
        setUpSpinners();
        init();
    }

    private void init() {
        maleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleBtn.setBackgroundColor(getResources().getColor(R.color.outline));
                femaleBtn.setBackgroundColor(getResources().getColor(R.color.transparent));
                gender = "male";
            }
        });

        femaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleBtn.setBackgroundColor(getResources().getColor(R.color.transparent));
                femaleBtn.setBackgroundColor(getResources().getColor(R.color.outline));
                gender = "female";
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPersonalDetails(height, weight, age, gender);
                Intent intent = new Intent(context, TraineeHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setWidgets() {

        TextView heightET = (TextView) findViewById(R.id.heightTextView);
        TextView weightET = (TextView) findViewById(R.id.weightTv);
        TextView ageET = (TextView) findViewById(R.id.ageET);
        nextBtn = (Button) findViewById(R.id.nextBtn);

        weightSpinner = (Spinner) findViewById(R.id.weightSpinner);
        heightSpinner = (Spinner) findViewById(R.id.heightSpinner);

        maleBtn = (Button) findViewById(R.id.maleBtn);
        femaleBtn = (Button) findViewById(R.id.femaleBtn);

    }

    //---------------- Weight Dialog ----------------//

    private void weightDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TraineePersonalDetailsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_weight, null);

        final NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.heightPicker);

        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                newWeight = i;
            }
        });

        confirm = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancel = (TextView) mView.findViewById(R.id.dialogCancel);
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


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = String.valueOf(newWeight);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //---------------- Height Dialog ----------------//

    private void heightDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TraineePersonalDetailsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_height, null);

        final NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.heightPicker);

        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                newHeight = i;
            }
        });

        confirm = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancel = (TextView) mView.findViewById(R.id.dialogCancel);
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


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height = String.valueOf(newHeight);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setUpSpinners() {

        weightAdapter = ArrayAdapter.createFromResource(context, R.array.weightVariables, android.R.layout.simple_spinner_item);
        weightAdapter.setDropDownViewResource(android.R.layout.preference_category);
        weightSpinner.setAdapter(weightAdapter);
        weightSpinner.setOnItemSelectedListener(this);

        heightAdapter = ArrayAdapter.createFromResource(context, R.array.heightVariables, android.R.layout.simple_spinner_item);
        heightAdapter.setDropDownViewResource(android.R.layout.preference_category);
        heightSpinner.setAdapter(heightAdapter);
        heightSpinner.setOnItemSelectedListener(this);

    }


    //---------------- Firebase ----------------//

    private void addPersonalDetails(String height, String weight, String age, String gender) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("personal_details");

        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }

        PersonalDetails personalDetails = new PersonalDetails(height, weight, age, gender);
        uidRef.child(userID).setValue(personalDetails);
    }

    private void setUpFirebaseAuth() {
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "Connected");

                    userID = user.getUid();

                } else {
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
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    //---------------- Spinner ----------------//
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String itemSelected = parent.getItemAtPosition(position).toString();

        String weightType;
        if (itemSelected.equals("kg")) {
            weightType = "kg";
        } else if (itemSelected.equals("lb")) {
            weightType = "lb";
        }
        String heightType;
        if (itemSelected.equals("cm")) {
            heightType = "cm";
        } else if (itemSelected.equals("ft")) {
            heightType = "ft";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //---------------- On Click Listener ----------------//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.heightTextView:
                heightDialog();
                break;

            case R.id.weightTv:
                weightDialog();
                break;

            case R.id.ageET:
                weightDialog();
                break;

            default:
                break;
        }
    }
}
