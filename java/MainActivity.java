package iot.mdp.weather_app;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

public class MainActivity extends AppCompatActivity implements Communicator{

    //Global Variables
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private WeatherTab weatherTab = null;
    private MapTab mapTab = null;
    private boolean flipped = false;  //Flag that indicates if the phone has already changed its orientation



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find objects from the layout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //Associating the tablayout with the viewpager
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
            //Creating the tabs and adding to the viewpager using an adapter
            TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());
            weatherTab = new WeatherTab();
            mapTab = new MapTab();
            tabViewPagerAdapter.addFragment(weatherTab, "Weather");
            tabViewPagerAdapter.addFragment(mapTab, "Map");
            viewPager.setAdapter(tabViewPagerAdapter);
            viewPager.setPageTransformer(true, new CubeOutTransformer());
        }

    }
    //----------------------Menu bar functions--------------------------
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        //Listener for the searchview to filter the data of the listview
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (weatherTab!=null)
                    weatherTab.weatherArrayAdapter.getFilter().filter(newText);
                else
                    System.out.println("WeatherTab null");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                //Refresh the information of the listview, downloading again the xml files
                if (weatherTab!=null)
                    weatherTab.refreshInfo();
                else
                    System.out.println("WeatherTab null");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    //----------------------Communicator interface--------------------------
    @Override
    public void answer(String lastPostalCode, String lastCityName, String lastSkyState) {
        mapTab.addEntry(lastPostalCode, lastCityName, lastSkyState);
        mapTab.setLastPostalCodeOnMap(lastPostalCode, lastCityName, lastSkyState);
    }

    //----------------------Tab communication functions--------------------------
    public void setWeatherTab(WeatherTab tab1){
        if(flipped)
            //to redirect the pointer of the weather tab when the fragment has recreated
            weatherTab=tab1;
    }

    public void setMapTab(MapTab tab2){
        if(flipped) {
            //to redirect the pointer of the map tab when the fragment has recreated
            mapTab = tab2;
            //adding to the viewpager
            TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());
            tabViewPagerAdapter.addFragment(weatherTab, "Weather");
            tabViewPagerAdapter.addFragment(mapTab, "Map");
            viewPager.setAdapter(tabViewPagerAdapter);
            viewPager.setPageTransformer(true, new CubeOutTransformer());
        }

    }

    //----------------------Lifecycle functions--------------------------
    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(getApplicationContext(),"Main resumed",Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        flipped=sharedPref.getBoolean("flipped",false);
        setUpViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (isFinishing()) {
            //Closing app
            editor.putBoolean("flipped",false);
        } else {
            //It's an orientation change.
            editor.putBoolean("flipped",true);
        }
        editor.commit();
    }
}
