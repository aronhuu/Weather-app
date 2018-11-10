package iot.mdp.weather_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapTab extends Fragment {

    //Global variables
    MapView mMapView;
    private GoogleMap mMap;
    private List<String> codes = new ArrayList<String>();   //Array to store the record of selected cities postal code
    private List<String> cities = new ArrayList<String>();  //Array to store the record of selected cities name
    private List<String> states = new ArrayList<String>();  //Array to store the record of selected cities' sky state


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                //when the map is ready, inform the main activity and redirecting the pointer of the map tab
                ((MainActivity)getActivity()).setMapTab(MapTab.this);
                //then redraw the markers on the map with the record of the cities
                if(!codes.isEmpty()){
                    for(int i=0; i<codes.size();i++){
                        setLastPostalCodeOnMap(codes.get(i), cities.get(i), states.get(i));
                    }
                }

            }
        });

        return rootView;
    }

    //----------------------Map action functions--------------------------
    void setLastPostalCodeOnMap(String lastPostalCode, String lastCityName, String lastSkyState) {
        if (mMap != null) {

            Context context = getContext();
            //Obtain the latitude and longitude of last postal code pressed:
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addresses = geocoder.getFromLocationName(lastPostalCode + " " + lastCityName + " España", 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    LatLng place = new LatLng(address.getLatitude(), address.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(place).title(lastCityName).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(selectIcon(lastSkyState), 100, 100))));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));

                }
            } catch (IOException e) {
                // handle exception
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Map null");
        }
    }

    // Function that returns the corresponding icon to a sky state
    public Bitmap selectIcon(String lastSkyState){
        Bitmap bitmap;
        if (lastSkyState.contains("nuboso")) {
            if (lastSkyState.contains("luvia")) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nublado_precipitaciones_debiles);
            }
            else {
                if (lastSkyState.contains("nieve")) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nublado_con_nieve);
                }
                else{
                    if (lastSkyState.contains("tormenta")) {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nublado_precipitaciones_tormenta);
                    }
                    else{
                        if (lastSkyState.contains("Poco")) {
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poco_nublado);
                        }
                        else{
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nublado);
                        }
                    }
                }
            }
        }else{
            if (lastSkyState.contains("tormenta")) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.storm);
            }
            else{
                if (lastSkyState.contains("despejado")) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.despejado);
                }
                else{
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
                }
            }

        }

        return bitmap;
    }

    //Function that resizes the icon
    public Bitmap resizeMapIcons(Bitmap bitmap, int width, int height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return resizedBitmap;
    }

    //----------------------Lifecycle functions--------------------------
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        //Parsing the cities postal code information if it is stored in the preferences
        String s =sharedPref.getString("MapCodes", null);
        if(s!=null)
            if(!s.isEmpty())
                codes=new ArrayList<String>(Arrays.asList(s.replace("[", "").replace("]", "").split(", ")));
        //Parsing the cities name information if it is stored in the preferences
        s =sharedPref.getString("MapCities", null);
        if(s!=null)
            if(!s.isEmpty())
                cities=new ArrayList<String>(Arrays.asList(s.replace("[", "").replace("]", "").split(", ")));
        //Parsing the sky states information if it is stored in the preferences
        s =sharedPref.getString("MapStates", null);
        if(s!=null)
            if(!s.isEmpty())
                states=new ArrayList<String>(Arrays.asList(s.replace("[", "").replace("]", "").split(", ")));


    }

    //Function to add the last city information to the record of city information
    public void addEntry(String lastPostalCode, String lastCityName, String lastSkyState){
        codes.add(lastPostalCode);
        cities.add(lastCityName);
        states.add(lastSkyState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        //Store the record of selected cities information in the preferences
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        System.out.println(codes.toString());
        editor.putString("MapCodes", codes.toString());
        editor.putString("MapCities", cities.toString());
        editor.putString("MapStates", states.toString());
        editor.commit();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(getActivity().getApplicationContext(),"Tab2 destroyed",Toast.LENGTH_SHORT).show();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
