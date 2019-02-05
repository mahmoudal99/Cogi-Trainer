package onipractice.mahmoud.com.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import onipractice.mahmoud.com.fitnessapp.Models.FoodItemModel;
import onipractice.mahmoud.com.fitnessapp.Utils.FoodAdapter;

public class SearchFoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView heading;
    private ImageView backArrow, confirm_selection;
    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    private String foodCatgeory, mealChosen;
    private Spinner foodSpinner, mealSpinner;

    private FoodAdapter adapter;
    private List<FoodItemModel> foodList;
    private ArrayAdapter<CharSequence> foodListAdapter;
    private ArrayAdapter<CharSequence> mealListAdapter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        initialize();

        heading.setText(getIntent().getStringExtra("meal"));

        mealChosen = getIntent().getStringExtra("meal");
        setUpSpinners();

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        Toast.makeText(SearchFoodActivity.this, getIntent().getStringExtra("day"), Toast.LENGTH_SHORT).show();

        foodList = new ArrayList<>();
        adapter = new FoodAdapter(this, foodList, user.getUid(), getIntent().getStringExtra("day"), mealChosen);
        mResultList.setAdapter(adapter);

        setUpWidgets();

//        confirm_selection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String searchText = " ";
//                //6. SELECT * FROM Artists WHERE name = "A%"
//                Query query6 = FirebaseDatabase.getInstance().getReference(foodCatgeory)
//                        .orderByChild("foodName")
//                        .startAt(searchText)
//                        .endAt(searchText + "\uf8ff");
//
//                query6.addListenerForSingleValueEvent(valueEventListener);
//            }
//        });
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.heading);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        foodSpinner = (Spinner) findViewById(R.id.foodSpinner);
        mealSpinner = (Spinner) findViewById(R.id.mealSpinner);
//        confirm_selection = (ImageView) findViewById(R.id.confirm_selection);
        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
    }

    private void setUpWidgets() {
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();
                Toast.makeText(SearchFoodActivity.this, foodCatgeory, Toast.LENGTH_SHORT).show();
                Query query6 = FirebaseDatabase.getInstance().getReference(foodCatgeory)
                        .orderByChild("foodName")
                        .startAt(searchText)
                        .endAt(searchText + "\uf8ff");

                query6.addListenerForSingleValueEvent(valueEventListener);

            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFoodActivity.this, DietActivity.class);
                startActivity(intent);
            }
        });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            foodList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodItemModel foodItem = snapshot.getValue(FoodItemModel.class);
                    foodList.add(foodItem);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private void setUpSpinners() {
        foodListAdapter = ArrayAdapter.createFromResource(SearchFoodActivity.this, R.array.foodCategories, android.R.layout.simple_spinner_item);
        foodListAdapter.setDropDownViewResource(android.R.layout.preference_category);
        foodSpinner.setAdapter(foodListAdapter);
        foodSpinner.setOnItemSelectedListener(this);

        mealListAdapter = ArrayAdapter.createFromResource(SearchFoodActivity.this, R.array.meals, android.R.layout.simple_spinner_item);
        mealListAdapter.setDropDownViewResource(android.R.layout.preference_category);
        mealSpinner.setAdapter(mealListAdapter);
        mealSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String chosen = parent.getItemAtPosition(position).toString();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (chosen.equals("Breakfast")) {
            mealChosen = "Breakfast";
            heading.setText(mealChosen);
            adapter = new FoodAdapter(this, foodList, user.getUid(), getIntent().getStringExtra("day"), mealChosen);
            mResultList.setAdapter(adapter);

        } else if (chosen.equals("Lunch")) {
            mealChosen = "Lunch";
            heading.setText(mealChosen);
            adapter = new FoodAdapter(this, foodList, user.getUid(), getIntent().getStringExtra("day"), mealChosen);
            mResultList.setAdapter(adapter);

        } else {
            mealChosen = "Dinner";
            heading.setText(mealChosen);
            adapter = new FoodAdapter(this, foodList, user.getUid(), getIntent().getStringExtra("day"), mealChosen);
            mResultList.setAdapter(adapter);
        }

        if (chosen.equals("Fruit_Veg")) {
            foodCatgeory = "Fruit_Veg";
        } else if (chosen.equals("SeaFood")) {
            foodCatgeory = "SeaFood";
        } else if (chosen.equals("Meat")) {
            foodCatgeory = "Meat";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
