package org.codeandmagic.android.mapswithdrawer;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by evelyne24 on 22/07/2013.
 */
public class BigMapFragment extends Fragment implements GoogleMap.OnMyLocationChangeListener, GoogleMap.OnInfoWindowClickListener {

    private static final int MAP_ZOOM = 13;
    private static final String SAVED_LOCATION = "saved_location";

    private GoogleMap map;
    private Location myLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreMyLocation(savedInstanceState);
        setupMap();
    }

    private void restoreMyLocation(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            myLocation = savedInstanceState.getParcelable(SAVED_LOCATION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVED_LOCATION, myLocation);
        super.onSaveInstanceState(outState);
    }

    private void setupMap() {
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
    }

    private void initMap() {
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            map = mapFragment.getMap();
        }

        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(true);
            map.setOnInfoWindowClickListener(this);

            if (myLocation == null) {
                setLocationTracking(true);
            } else {
                addMyLocationMarker();
            }
        } else {
            Toast.makeText(getActivity(), R.string.google_maps_not_supported, Toast.LENGTH_LONG).show();
        }

    }

    private void setLocationTracking(boolean tracking) {
        map.setMyLocationEnabled(tracking);
        map.setOnMyLocationChangeListener(tracking ? this : null);
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (myLocation == null) {
            myLocation = location;
            centerMapOnMyLocation();
            setLocationTracking(false);
            addMyLocationMarker();
        }
    }

    private void centerMapOnMyLocation() {
        map.moveCamera(CameraUpdateFactory.newLatLng(getMyLatLng()));
        map.animateCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM));
    }

    private void addMyLocationMarker() {
        map.addMarker(new MarkerOptions()
                .position(getMyLatLng())
                .title(getString(R.string.my_location))
                .snippet(getString(R.string.my_location_snippet))
        );
    }

    private LatLng getMyLatLng() {
        return new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                DetailFragment.getInstance(marker.getPosition())).addToBackStack(null).commit();
    }
}
