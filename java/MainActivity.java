package com.example.ao.tabapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements Communicator{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Tab1 tab1;
    Tab2 tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setUpViewPager(viewPager);

//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        String s =sharedPref.getString("cities", null);
//        if(s!=null)
//            tab1.setCity(s.replace("[", "").replace("]", "").split(", "));
//        s =sharedPref.getString("tMax", null);
//        if(s!=null)
//            tab1.tMax=s.replace("[", "").replace("]", "").split(", ");
//        s =sharedPref.getString("tMin", null);
//        if(s!=null)
//            tab1.tMin=s.replace("[", "").replace("]", "").split(", ");


    }

    private void setUpViewPager(ViewPager viewPager) {
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());
        tab1 = new Tab1();
        tab2 = new Tab2();
        tabViewPagerAdapter.addFragment(tab1, "Weather");
        tabViewPagerAdapter.addFragment(tab2, "Map");
        viewPager.setAdapter(tabViewPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tab1.weatherArrayAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void answer(String lastPostalCode, String lastCityName, String lastSkyState) {
        tab2.setLastPostalCodeOnMap(lastPostalCode, lastCityName, lastSkyState);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
