package com.example.ao.tabapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//public class Tab2 extends SupportMapFragment implements OnMapReadyCallback {

public class Tab2 extends Fragment{

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
                googleMap = mMap;

                // Add a marker in Sydney and move the camera
                LatLng place = new LatLng(-34, 151);
//                String[] str_coord = coordinates.split(",");
//        System.out.print(coordinates);

//        float lon = Float.parseFloat(str_coord[0]);
//        float lat = Float.parseFloat(str_coord[1]);
//        System.out.print(lat+":"+lon);
//        LatLng place = new LatLng(lat, lon);


                googleMap.addMarker(new MarkerOptions().position(place).title("Marker in "));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                googleMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
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
}

//    private GoogleMap mMap;
//    String coordinates,cameraName=null;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.tab2, container, false);
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
////                .findFragmentById(R.id.map);
//        getMapAsync(this);
//
//        return view;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng place = new LatLng(-34, 151);
//        String[] str_coord = coordinates.split(",");
////        System.out.print(coordinates);
//
////        float lon = Float.parseFloat(str_coord[0]);
////        float lat = Float.parseFloat(str_coord[1]);
////        System.out.print(lat+":"+lon);
////        LatLng place = new LatLng(lat, lon);
//
//        mMap.addMarker(new MarkerOptions().position(place).title("Marker in "+cameraName));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
//        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
//    }
//}
