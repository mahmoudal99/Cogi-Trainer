package onipractice.mahmoud.com.fitnessapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import onipractice.mahmoud.com.fitnessapp.Models.DietDayObject;

public class DietActivity extends AppCompatActivity {

    private static final String TAG = "DietActivity";

    //Firebase
    DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    FloatingActionButton floatingActionButton;
    TextView confirm, cancel;
    ImageView backArrow, delete;
    ListView list;
    TextView breakfastTv, lunchTv, dinnerTv, dayTv, itemBreakfastTv, itemLunchTv, itemDinnerTv, itemDayTv;
    boolean[] checkedItems = new boolean[7];

    UsersAdapter adapter;
    FirebaseUser user;
    ArrayList<DietDayObject> arrayList;
    DietDayObject object;

    String day, selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        setWidgets();
        setUpFirebaseAuth();
        createObject();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDietDayDialog();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietActivity.this, TraineeHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setWidgets() {
        backArrow = (ImageView) findViewById(R.id.backArrow);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        list = (ListView) findViewById(R.id.list);
    }


    // Diet Dialog

    private void addDietDayDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DietActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_diet_day, null);

        confirm = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancel = (TextView) mView.findViewById(R.id.dialogCancel);
        breakfastTv = (TextView) mView.findViewById(R.id.breakfastTv);
        lunchTv = (TextView) mView.findViewById(R.id.lunchTv);
        dinnerTv = (TextView) mView.findViewById(R.id.dinnerTv);
        dayTv = (TextView) mView.findViewById(R.id.dayTv);

        day = " ";
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

        breakfastTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietActivity.this, SearchFoodActivity.class);
                if (day.equals(" ")) {
                    Toast.makeText(DietActivity.this, "Please Select a day", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("day", day);
                    intent.putExtra("meal", "Breakfast");
                    startActivity(intent);
                }
            }
        });

        dayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayDialog();
            }
        });

        lunchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietActivity.this, SearchFoodActivity.class);
                if (day.equals(" ")) {
                    Toast.makeText(DietActivity.this, "Please Select a day", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("day", day);
                    intent.putExtra("meal", "Lunch");
                    startActivity(intent);
                }

            }
        });

        dinnerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietActivity.this, SearchFoodActivity.class);
                if (day.equals(" ")) {
                    Toast.makeText(DietActivity.this, "Please Select a day", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("day", day);
                    intent.putExtra("meal", "Dinner");
                    startActivity(intent);
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createObject();
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

    private void dayDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DietActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_insert_speciality, null);

        confirm = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancel = (TextView) mView.findViewById(R.id.dialogCancel);

        final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday", "Sunday"};

        mBuilder.setMultiChoiceItems(days, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked) {
                    selectedDay = days[which];
                }
            }
        });

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = selectedDay;
                createObject();
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

    private void createObject() {
        arrayList = new ArrayList<DietDayObject>();
        adapter = new UsersAdapter(DietActivity.this, arrayList);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("diet").child(user.getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    object = new DietDayObject();
                    object.breakfast = ds.child("Breakfast").child("food").getValue(String.class);
                    object.lunch = ds.child("Lunch").child("food").getValue(String.class);
                    object.dinner = ds.child("Dinner").child("food").getValue(String.class);
                    object.day = ds.child("Breakfast").child("day").getValue(String.class);
                    arrayList.add(object);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

    }

    public class UsersAdapter extends ArrayAdapter<DietDayObject> {

        public UsersAdapter(Context context, ArrayList<DietDayObject> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final DietDayObject object = getItem(position);

            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.diet_item, parent, false);

            }

            itemBreakfastTv = convertView.findViewById(R.id.diet_breakfast_Tv);
            itemLunchTv = convertView.findViewById(R.id.diet_lunch_Tv);
            itemDinnerTv = convertView.findViewById(R.id.diet_dinner_Tv);
            itemDayTv = convertView.findViewById(R.id.dayTv);
            delete = convertView.findViewById(R.id.delete);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.remove(object);
                    adapter.notifyDataSetChanged();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference uidRef = rootRef.child("diet").child(user.getUid());
                    uidRef.child(object.day).setValue(null);
                }
            });

            itemBreakfastTv.setText(object.breakfast);
            itemLunchTv.setText(object.lunch);
            itemDinnerTv.setText(object.dinner);
            itemDayTv.setText(object.day);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DietActivity.this, SearchFoodActivity.class);
                    intent.putExtra("day", object.day);
                    intent.putExtra("meal", "Breakfast");
                    startActivity(intent);
                }
            });

            return convertView;

        }

    }

                /*
    ------------------------------------------Firebase----------------------------------------------------
     */

    private void setUpFirebaseAuth() {
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "Connected");

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}
























