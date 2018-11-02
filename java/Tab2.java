package com.example.ao.tabapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class Tab2 extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap googleMap;


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
            public void onMapReady(GoogleMap mMap) {
             googleMap=mMap;
            }
        });

        return rootView;
    }


    void setLastPostalCodeOnMap(String lastPostalCode, String lastCityName, String lastSkyState){

        if(googleMap!=null)
        {
            Context context = getContext();
            //Obtain the latitude and longitude of last postal code pressed:
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addresses = geocoder.getFromLocationName(lastPostalCode + " " + lastCityName + " España", 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    LatLng place = new LatLng(address.getLatitude(), address.getLongitude());

                    //googleMap.addMarker(new MarkerOptions().position(place).title("Marker in "));
                    googleMap.addMarker(new MarkerOptions().position(place).title(lastCityName).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(selectIcon(lastSkyState), 100, 100))));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));

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

    public Bitmap resizeMapIcons(Bitmap bitmap, int width, int height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
            }
        });
//        ((Communicator)getActivity()).answer("21005", "Almonte", "nuboso");
//        MapFragment mapFragment;
//        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
//        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

    }
}

////    private GoogleMap mMap;
////    String coordinates,cameraName=null;
////
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        View view = inflater.inflate(R.layout.tab2, container, false);
////
////        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//////                .findFragmentById(R.id.map);
////        getMapAsync(this);
////
////        return view;
////    }
////
////    @Override
////    public void onMapReady(GoogleMap googleMap) {
////        mMap = googleMap;
////
////        // Add a marker in Sydney and move the camera
////        LatLng place = new LatLng(-34, 151);
////        String[] str_coord = coordinates.split(",");
//////        System.out.print(coordinates);
////
//////        float lon = Float.parseFloat(str_coord[0]);
//////        float lat = Float.parseFloat(str_coord[1]);
//////        System.out.print(lat+":"+lon);
//////        LatLng place = new LatLng(lat, lon);
////
////        mMap.addMarker(new MarkerOptions().position(place).title("Marker in "+cameraName));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
////        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
////    }
////}
