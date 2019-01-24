package onipractice.mahmoud.com.fitnessapp.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.BigInteger;

import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.SignInActivity;
import onipractice.mahmoud.com.fitnessapp.Utils.FirebaseMethods;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ProfileActivity";

    // Widgets
    TextView emailTv, nameTv, surnameTv, confirm, cancel, genderTv, weightTv, heightTv, weightLabel, heightLabel, genderLabel;
    EditText passwordEt, emailEt;
    ImageView backArrow;
    Button maleBtn, femaleBtn;

    // Variables
    String name, surname, userId, passwordDialog, emailDialog, usernameDialog, weight, gender, height, newGender, user;
    int newWeight, newHeight;

    Context context;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference rootRef;
    private DatabaseReference uidRef;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context = EditProfileActivity.this;
        user = getIntent().getStringExtra("user");

        initialize();
        setUpWidgets();
        setUpFirebaseAuth();
        firebaseMethods = new FirebaseMethods(context);

        if(user.equals("trainer")){
            weightLabel.setVisibility(View.INVISIBLE);
            genderLabel.setVisibility(View.INVISIBLE);
            heightLabel.setVisibility(View.INVISIBLE);
        }


    }

    private void initialize(){

        emailTv = findViewById(R.id.emailTv);
        nameTv =  findViewById(R.id.nameTv);
        surnameTv = findViewById(R.id.surnameTv);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        weightTv = findViewById(R.id.weightTv);
        genderTv = findViewById(R.id.genderTv);
        heightTv = findViewById(R.id.heightTv);
        weightLabel = findViewById(R.id.weight);
        heightLabel = findViewById(R.id.height);
        genderLabel = findViewById(R.id.gender);
    }

    private void setUpWidgets(){

        emailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailDialog();

            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

    private void setUserSettings(String name, String surname){
        nameTv.setText(name);
        surnameTv.setText(surname);
        emailTv.setText(auth.getCurrentUser().getEmail().toString());

    }

    private void setUserPersonalDetails(String gender, String weight, String height){

        genderTv.setText(gender);
        weightTv.setText(weight);
        heightTv.setText(height);

    }


    private void getUserSettings(String id){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings").child(id);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    name = dataSnapshot.child("firstname").getValue(String.class);
                    surname = dataSnapshot.child("lastname").getValue(String.class);

                    setUserSettings(name, surname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getUserPersonalDetails(String id){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("personal_details").child(id);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    gender = dataSnapshot.child("gender").getValue(String.class);
                    height = dataSnapshot.child("height").getValue(String.class);
                    weight = dataSnapshot.child("weight").getValue(String.class);

                    setUserPersonalDetails(gender, weight, height);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }

    //---------- Email Dialog ----------//

    private void emailDialog(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_email, null);

        passwordEt = (EditText) mView.findViewById(R.id.confirm_password);
        emailEt = (EditText) mView.findViewById(R.id.confirm_email);

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
                passwordDialog = passwordEt.getText().toString();
                emailDialog = emailEt.getText().toString();
                dialog.dismiss();
                firebaseMethods.changeEmail(passwordDialog, emailDialog);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //---------- Change Gender Dialog ----------//

     private void genderDialog(){
         AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
         View mView = getLayoutInflater().inflate(R.layout.dialog_change_gender, null);

         maleBtn = (Button) mView.findViewById(R.id.maleBtn);
         femaleBtn = (Button) mView.findViewById(R.id.femaleBtn);
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

         maleBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 maleBtn.setBackgroundColor(getResources().getColor(R.color.outline));
                 maleBtn.setTextColor(getResources().getColor(R.color.white));
                 femaleBtn.setBackgroundColor(getResources().getColor(R.color.white));
                 femaleBtn.setTextColor(getResources().getColor(R.color.outline));

                 newGender = "male";
             }
         });

         femaleBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 maleBtn.setBackgroundColor(getResources().getColor(R.color.white));
                 maleBtn.setTextColor(getResources().getColor(R.color.outline));

                 femaleBtn.setBackgroundColor(getResources().getColor(R.color.outline));
                 femaleBtn.setTextColor(getResources().getColor(R.color.white));

                 newGender = "female";
             }
         });

         confirm.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 genderTv.setText(newGender);
                 firebaseMethods.updateGender(newGender);
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

    //---------- Change Weight Dialog ----------//

    private void weightDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_weight, null);

        final NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.heightPicker);


        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d(TAG, "onValueChange: ");
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

                firebaseMethods.updateWeight(String.valueOf(newWeight) + "kg");
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


    //---------- Change Height Dialog ----------//

    private void heightDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_height, null);

        final NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.heightPicker);

        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d(TAG, "onValueChange: ");
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
                firebaseMethods.updateHeight(String.valueOf(newHeight) + "cm");
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
                    getUserSettings(userId);
                    getUserPersonalDetails(userId);

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

    //---------- On Click Listeners ----------//

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.genderTv:
                genderDialog();
                break;

            case R.id.heightTv:
                heightDialog();
                break;

            case R.id.weightTv:
                weightDialog();
                break;

            default:
                break;
        }

    }

}

















