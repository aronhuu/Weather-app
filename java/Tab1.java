package com.example.ao.tabapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by pc on 27/10/2017.
 */

public class Tab1 extends Fragment {

    ListView lv;
    TextView selectionText;

    WeatherArrayAdapter weatherArrayAdapter;

    String [] places ={"Alcala","Madrid","Moncloa"};
    String [] maxTemp ={"20","21","22"};
    String [] minTemp ={"10","12","13"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab1, container, false);

        lv = (ListView)view.findViewById(R.id.listView);
        selectionText = (TextView)view.findViewById(R.id.selection);

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
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
        });

        WeatherData weatherData = new WeatherData(places,maxTemp,minTemp);

        weatherArrayAdapter = new WeatherArrayAdapter( view.getContext(), weatherData.getWeatherList() );
        lv.setAdapter(weatherArrayAdapter);

        lv.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        return view;
    }



}
