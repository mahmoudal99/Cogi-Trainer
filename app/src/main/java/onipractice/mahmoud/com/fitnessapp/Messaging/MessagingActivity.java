package onipractice.mahmoud.com.fitnessapp.Messaging;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import onipractice.mahmoud.com.fitnessapp.R;
import onipractice.mahmoud.com.fitnessapp.Utils.SectionsPagerAdapter;

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        setUpViewPager();
    }


    private void setUpViewPager()
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TextingFragment());
        adapter.addFragment(new TrainersListFragment());

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));

        tabLayout.getTabAt(0).setText("Chat");
        tabLayout.getTabAt(1).setText("Trainers");
    }
}
