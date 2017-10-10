package com.locadoc_app.locadoc.UI.HomePage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;

import java.util.List;
import java.util.Map;

public class GoogleMapFragment extends Fragment
        implements  OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    public interface GoogleMapFragmentListener{
        void requestFocus();
        Location getLastKnownLoc();
    }

    MapView mMapView;
    private GoogleMap mMap;
    private Circle circleShown;
    private static final int DEFAULT_ZOOM = 17;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.e("LocAdoc", e.toString());
        }

        mMapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(circleShown != null){
                    circleShown.remove();
                    circleShown = null;
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            mMap.setMyLocationEnabled(true);
        }
        loadMarker();

        GoogleMapFragmentListener listener = (GoogleMapFragmentListener) getActivity();
        Location loc = listener.getLastKnownLoc();
        if(loc != null){
            focusCamera(loc);
        } else{
            listener.requestFocus();
        }
    }

    public void loadMarker()
    {
        List<Area> allArea = AreaSQLHelper.getAllRecord(Credential.getPassword());
        for(int i = 0; i < allArea.size(); i++){
            Area area = allArea.get(i);
            addMarker(area);
        }
    }

    public void addMarker(Area area){
        LatLng latLng = new LatLng(Double.parseDouble(area.getLatitude()), Double.parseDouble(area.getLongitude()));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(area.getName());
        markerOptions.snippet(area.getDescription());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(markerOptions);
    }

    public void focusCamera (Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    }

    public void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (circleShown != null){
            circleShown.remove();
        }

        Log.d("LocAdoc", marker.getTitle());
        Log.d("LocAdoc", "pass: " + Credential.getPassword().getPassword()+
                ", salt: " + Credential.getPassword().getSalt());
        Area area = AreaSQLHelper.getAreaName(marker.getTitle(), Credential.getPassword());

        Log.d("LocAdoc", "START TEST");
        Log.d("LocAdoc", "id: " + area.getAreaId());
        if(area.getName() != null){
            Log.d("LocAdoc", area.getName());
        }
        if(area.getRadius() != null){
            Log.d("LocAdoc", area.getRadius());
        }
        if(area.getLatitude() != null){
            Log.d("LocAdoc", area.getLatitude());
        }
        if(area.getLongitude() != null){
            Log.d("LocAdoc", area.getLongitude());
        }
        if(area.getDescription() != null){
            Log.d("LocAdoc", area.getDescription());
        }
        Log.d("LocAdoc", "STOP TEST");

        circleShown = mMap.addCircle(new CircleOptions()
                .center(marker.getPosition())
                .radius(Integer.parseInt(area.getRadius()))
                .strokeColor(Color.argb(25,30,100,255))
                .fillColor(Color.argb(90,135,206,250)));
        return false;
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