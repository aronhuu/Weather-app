package dte.masteriot.mdp.listviewcountries.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import dte.masteriot.mdp.listviewcountries.Adapter.WeatherArrayAdapter;
import dte.masteriot.mdp.listviewcountries.Model.Weather;
import dte.masteriot.mdp.listviewcountries.Model.WeatherData;
import dte.masteriot.mdp.listviewcountries.R;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    ListView lv;
    TextView selectionText;

    WeatherArrayAdapter weatherArrayAdapter;

    String [] places ={"Alcala","Madrid","Moncloa"};
    String [] maxTemp ={"20","21","22"};
    String [] minTemp ={"10","12","13"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView)findViewById(R.id.listView);
        selectionText = (TextView)findViewById(R.id.selection);

        lv.setOnItemClickListener(this);

        WeatherData weatherData = new WeatherData(places,maxTemp,minTemp);

        weatherArrayAdapter = new WeatherArrayAdapter( this, weatherData.getWeatherList() );
        lv.setAdapter(weatherArrayAdapter);

        lv.setChoiceMode( ListView.CHOICE_MODE_SINGLE );


    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String items = "";

        SparseBooleanArray checked= lv.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if ( checked.valueAt(i) ) {
               int pos = checked.keyAt(i);
               items = items + " " + lv.getItemAtPosition(pos);
            }
        }

       selectionText.setText( items );
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
//                if (newText.isEmpty()) {
//                    weatherArrayAdapter.getFilter().filter("");
//                }
//                else {
                    weatherArrayAdapter.getFilter().filter(newText);
//                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}