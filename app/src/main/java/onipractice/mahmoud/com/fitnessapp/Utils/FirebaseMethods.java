package onipractice.mahmoud.com.fitnessapp.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
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

import onipractice.mahmoud.com.fitnessapp.DietActivity;
import onipractice.mahmoud.com.fitnessapp.Models.DietModel;
import onipractice.mahmoud.com.fitnessapp.Models.TrainingDayModel;
import onipractice.mahmoud.com.fitnessapp.Models.UserDetails;
import onipractice.mahmoud.com.fitnessapp.SignInActivity;
import onipractice.mahmoud.com.fitnessapp.TimetableActivity;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    private static int counter = 0;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    DatabaseReference uidRef;
    private String userID;

    private Context context;


    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences cacheData;
    SharedPreferences dietData;
    SharedPreferences.Editor dietEditor;

    public FirebaseMethods(Context context) {
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        context = context;

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }
        cacheData = context.getSharedPreferences("Counter", TimetableActivity.MODE_PRIVATE);
        editor = cacheData.edit();

        dietData = context.getSharedPreferences("DietCounter", DietActivity.MODE_PRIVATE);
        dietEditor = dietData.edit();

    }

    public void changeEmail(String password, final String newEmail){

        AuthCredential credential = EmailAuthProvider
                .getCredential(auth.getCurrentUser().getEmail(), password);

        auth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            auth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    if (task.isSuccessful()) {
                                        try {

                                            if (task.getResult().getSignInMethods().size() == 1) {
                                                Log.d(TAG, "onComplete: that email is already in use.");

                                                Toast.makeText(context, "That email is already in use", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d(TAG, "onComplete: That email is available.");

                                                //////////////////////the email is available so update it
                                                auth.getCurrentUser().updateEmail(newEmail)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    auth.signOut();
                                                                    Intent intent = new Intent(context, SignInActivity.class);
                                                                    context.startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }

                                        } catch (NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                        }
                                    }
                                }
                            });
                        }else {
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }
                    }
                });
    }

    public void updateUsername(String username){

        myRef = FirebaseDatabase.getInstance().getReference();
        uidRef = myRef.child("user_account_settings");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        uidRef.child(userID).child("username").setValue(username);
    }

    public void checkIfUsernameExists(final String username){

        Toast.makeText(context, username, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child("user_account_settings")
                .orderByChild("username")
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    //add the username
                    updateUsername(username);
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    if(singleSnapshot.exists())
                    {
                        Log.d(TAG, "Username already exists");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateHeight(String height){

        myRef = FirebaseDatabase.getInstance().getReference();
        uidRef = myRef.child("personal_details");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        uidRef.child(userID).child("height").setValue(height);
    }

    public void updateWeight(String weight){

        myRef = FirebaseDatabase.getInstance().getReference();
        uidRef = myRef.child("personal_details");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        uidRef.child(userID).child("weight").setValue(weight);
    }

    public void updateGender(String gender){

        myRef = FirebaseDatabase.getInstance().getReference();
        uidRef = myRef.child("personal_details");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        uidRef.child(userID).child("gender").setValue(gender);
    }

    public void addUser(String id, String username,String firstname,String lastname, String email){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("user_account_settings");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        UserDetails user = new UserDetails(username, firstname, lastname, "false", "Null", email, "default");

        uidRef.child(id).setValue(user);

    }


    public void addWorkoutDay(String id, String day, String time,String workout){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("workout_timetable");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        TrainingDayModel trainingDayModel = new TrainingDayModel(day, time, workout);

        if (!cacheData.getBoolean("firstTime", false)) {

            editor.putInt("workout1", 0);
            editor.apply();

            editor.putBoolean("firstTime", true);
            editor.commit();

        }

        counter = cacheData.getInt("workout1", 0);
        counter += 1;
        editor.putInt("workout1", counter);
        editor.apply();
        uidRef.child(id).child("workout" + cacheData.getInt("workout1", 0)).setValue(trainingDayModel);

    }

    public void addDietDay(String id,String day, String mealType, String food){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("diet");

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }

        DietModel dietModel = new DietModel(mealType, food, day);

        uidRef.child(id).child(day).child(mealType).setValue(dietModel);

    }

    public void registerNewEmail(final String email, String password, final String firstname, final String lastname, final String username){

        auth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                        }

                        else if(task.isSuccessful()){
                            //send verificaton email

                            FirebaseUser user = auth.getCurrentUser();
                            String id = user.getUid();

                            addUser(id, username, firstname, lastname, email);
                            sendVerificationEmail();
                        }
                    }

                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override

                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "Success");
                            }else{
                                Log.d(TAG, "Fail");
                            }
                        }

                    });
        }
    }

    public void changePassword(String password, final String newPassword){

        AuthCredential credential = EmailAuthProvider
                .getCredential(auth.getCurrentUser().getEmail(), password);

        auth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(context, "Password Changed", Toast.LENGTH_SHORT).show();
                            auth.getCurrentUser().updatePassword(newPassword);
                            auth.signOut();
                            Intent intent = new Intent(context, SignInActivity.class);
                            context.startActivity(intent);

                        }else {


                        }

                    }
                });
    }
}


































