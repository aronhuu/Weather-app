package com.iot.mdp.weather_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class WeatherArrayAdapter extends ArrayAdapter <Weather> implements Filterable {
    private  ArrayList <Weather> originalData;
    private ArrayList <Weather> filteredData;
    private Context mContext;
    private LayoutInflater inflater;
    ItemFilter mFilter = new ItemFilter();

    public WeatherArrayAdapter(Context context, ArrayList<Weather> weathers ) {
        super( context, 0, weathers );
        originalData = weathers;
        filteredData=weathers;
        mContext = context;
        inflater  = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ) {

        View newView = convertView;

        if ( newView == null ) {
            newView = inflater.inflate(R.layout.weather_list_item, parent, false);
        }

        TextView max    = (TextView) newView.findViewById(R.id.maxTemp);
        TextView min = (TextView) newView.findViewById(R.id.minTemp);
        TextView loc = (TextView) newView.findViewById(R.id.location);
        ImageView check = (ImageView) newView.findViewById(R.id.check);

        Weather weather = filteredData.get(position);

        loc.setText(weather.getLocation());
        max.setText("Max: "+weather.getMaxTemp() + "ºC");
        min.setText("Min: "+weather.getMinTemp() + "ºC");

        if(weather.getCheck())  check.setImageResource(R.drawable.selected);
        else   check.setImageResource(R.drawable.non_selected);

        return newView;
    }


    public int getCount() {
        return filteredData.size();
    }

    public int filtered(){
        int result = -1;
        if(filteredData.size()!= originalData.size()) result = getCount();
        return result;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override

    public Weather getItem(int position) {
        if(filtered()!=-1) return filteredData.get(position);
        else return originalData.get(position);
    }
    @Override

    public long getItemId(int position) {
        if(filtered()!=-1) return filteredData.indexOf(originalData.get(position));
        else return position;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Weather> list = originalData;

            int count = list.size();
            final ArrayList<Weather> nlist = new ArrayList<Weather>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getLocation();
                if (filterableString.toLowerCase().contains(filterString.toLowerCase())) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Weather>) results.values;
            notifyDataSetChanged();
        }

    }
}
