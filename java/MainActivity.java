package com.example.ao.tabapplication;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

public class MainActivity extends AppCompatActivity implements Communicator{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Tab1 tab1=null;
    Tab2 tab2=null;
    private boolean flipped=false;
    Bundle bundle;


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

    }

    private void setUpViewPager(ViewPager viewPager) {
        if(!flipped) {
            TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());
            tab1 = new Tab1();
            tab2 = new Tab2();
            tabViewPagerAdapter.addFragment(tab1, "Weather");
            tabViewPagerAdapter.addFragment(tab2, "Map");
            viewPager.setAdapter(tabViewPagerAdapter);
            viewPager.setPageTransformer(true, new CubeOutTransformer());
        }

    }

    public void setTab1(Tab1 t1){
        if(flipped)
            tab1=t1;
    }

    public void setTab2(Tab2 t2){
        if(flipped) {
            tab2 = t2;
            TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());
            tabViewPagerAdapter.addFragment(tab1, "Weather");
            tabViewPagerAdapter.addFragment(tab2, "Map");
            viewPager.setAdapter(tabViewPagerAdapter);
            viewPager.setPageTransformer(true, new CubeOutTransformer());
        }

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
                if (tab1!=null)
                    tab1.weatherArrayAdapter.getFilter().filter(newText);
                else
                    System.out.println("Tab1 null");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                if (tab1!=null)
                    tab1.refreshInfo();
                else
                    System.out.println("Tab1 null");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void answer(String lastPostalCode, String lastCityName, String lastSkyState) {
        tab2.addEntry(lastPostalCode, lastCityName, lastSkyState);
        tab2.setLastPostalCodeOnMap(lastPostalCode, lastCityName, lastSkyState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(getApplicationContext(),"Main resumed",Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        flipped=sharedPref.getBoolean("flipped",false);
        setUpViewPager(viewPager);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(getApplicationContext(),"Main stopped",Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("flipped",true);
        editor.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(getApplicationContext(),"Main restart",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(getApplicationContext(),"Main destroyed",Toast.LENGTH_SHORT).show();


    }
}
