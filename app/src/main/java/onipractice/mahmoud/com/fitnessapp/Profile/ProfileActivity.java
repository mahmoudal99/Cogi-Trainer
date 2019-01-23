package onipractice.mahmoud.com.fitnessapp.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;
import onipractice.mahmoud.com.fitnessapp.ChoosePreferenceActivity;
import onipractice.mahmoud.com.fitnessapp.Trainer.PersonalTrainerHomeActivity;
import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.SignInActivity;
import onipractice.mahmoud.com.fitnessapp.TraineeHomeActivity;

public class ProfileActivity extends AppCompatActivity  {

    private static final String TAG = "ProfileActivity";

    private static int RESULT_LOAD_IMG = 1;

    // Widgets
    TextView emailEt, nameEt, surnameEt, usernameEt, editProfileTv, editProfImgTv, changePasswordTv, logoutTv;
    CircleImageView profile;
    ImageView backArrow;

    String name, surname, username, userId, image, current_user, user;

    Context context;

    //Shared Preference
    SharedPreferences.Editor editor;
    SharedPreferences cacheData;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference rootRef;
    private DatabaseReference uidRef;
    private StorageReference imageStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = ProfileActivity.this;

        initialize();
        setUpWidgets();
        setUpFirebaseAuth();

        // Get Intent
        user = getIntent().getStringExtra("user");

        // Shared Preference
        cacheData = getSharedPreferences("Preferences", ChoosePreferenceActivity.MODE_PRIVATE);
        editor = cacheData.edit();

        setProfileImg();

        imageStorageRef = FirebaseStorage.getInstance().getReference();

    }

    private void setWidgets(String username, String name, String surname){

        usernameEt.setText(username);
        nameEt.setText(name);
        surnameEt.setText(surname);
        emailEt.setText(auth.getCurrentUser().getEmail());
        current_user = auth.getCurrentUser().getUid();

    }

    private void initialize(){

        emailEt = findViewById(R.id.emailEt);
        nameEt =  findViewById(R.id.nameEt);
        surnameEt = findViewById(R.id.surnameEt);
        usernameEt = findViewById(R.id.usernameEt);
        changePasswordTv = findViewById(R.id.changePasswordTv);
        editProfileTv = findViewById(R.id.editProfileTv);
        editProfImgTv = findViewById(R.id.editProfImgTv);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        profile = (CircleImageView) findViewById(R.id.profile);
        logoutTv = (TextView) findViewById(R.id.logoutTv);
    }

    private void setUpWidgets(){

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user.equals("trainer")){
                    Intent intent = new Intent(context, PersonalTrainerHomeActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(context, TraineeHomeActivity.class);
                    startActivity(intent);
                }
            }
        });

        editProfileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        changePasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChangePasswordActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);

            }
        });

        editProfImgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), RESULT_LOAD_IMG);
            }
        });

        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent signOutIntent = new Intent(context, SignInActivity.class);
                startActivity(signOutIntent);
                finish();
            }
        });
    }



    private void setProfileImg(){
        current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String imageUriString = cacheData.getString(current_user, "");
        Uri imageUri = Uri.parse(imageUriString);
        Picasso.get().load(imageUri).into(profile);

        if(cacheData.getString(current_user, "").isEmpty()){
            Toast.makeText(ProfileActivity.this, "Empty", Toast.LENGTH_LONG)
                    .show();
        }
    }

    //---------- Set User Information ----------//

    private void setUserInfo(final String id){

        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("user_account_settings").child(id);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("firstname").getValue(String.class);
                surname = dataSnapshot.child("lastname").getValue(String.class);
                username = dataSnapshot.child("username").getValue(String.class);
                image = dataSnapshot.child("image").getValue(String.class);

                setWidgets(username, name, surname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }


    //---------- Set User Information ----------//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{

            if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data){

                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage)
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            }

        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                profile.setImageURI(resultUri);

                current_user = auth.getCurrentUser().getUid();

                final StorageReference filepath = imageStorageRef.child("profile_images").child(current_user + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    Picasso.get().load(downloadUrl).into(profile);
                                    editor.putString(current_user, downloadUrl.toString());
                                    editor.apply();

                                    rootRef = FirebaseDatabase.getInstance().getReference();
                                    uidRef = rootRef.child("user_account_settings").child(current_user).child("image");
                                    uidRef.setValue(downloadUrl);
                                }
                            });
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.d(TAG, error.toString());
            }
        }
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
                    setUserInfo(userId);

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

}
