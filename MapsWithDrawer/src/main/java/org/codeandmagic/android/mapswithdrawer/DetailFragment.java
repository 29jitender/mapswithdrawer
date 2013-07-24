package org.codeandmagic.android.mapswithdrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by evelyne24 on 22/07/2013.
 */
public class DetailFragment extends SherlockFragment {

    public static final String LAT_LNG = "lat_lng";
    private static final int MINI_MAP_ZOOM = 15;
    private GoogleMap map;

    public static DetailFragment getInstance(LatLng latLng) {
        Bundle args = new Bundle();
        args.putParcelable(LAT_LNG, latLng);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detail_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }

    private void setupMap() {
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mini_map);
        if (mapFragment == null) {
            GoogleMapOptions options = new GoogleMapOptions()
                    .compassEnabled(false)
                    .rotateGesturesEnabled(false)
                    .tiltGesturesEnabled(false)
                    .scrollGesturesEnabled(false)
                    .zoomControlsEnabled(false)
                    .zoomGesturesEnabled(false);
            mapFragment = SupportMapFragment.newInstance(options);
            fragmentManager.beginTransaction().replace(R.id.mini_map, mapFragment).commit();
        }
    }

    private void initMap() {
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mini_map);
            map = mapFragment.getMap();
        }
        if (map != null) {
            LatLng latLng = getArguments().getParcelable(LAT_LNG);
            centerMapOnMyLocation(latLng);
        } else {
            Toast.makeText(getActivity(), R.string.google_maps_not_supported, Toast.LENGTH_LONG).show();
        }
    }

    private void centerMapOnMyLocation(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(MINI_MAP_ZOOM));
    }

    /**
     * From StackOverflow:
     * http://stackoverflow.com/questions/14083950/duplicate-id-tag-null-or-parent-id-with-another-fragment-for-com-google-androi
     * <p/>
     * If you don't remove the MapFragment manually, it will hang around so that it doesn't cost a lot of resources to
     * recreate/show the map view again. It seems that keeping the underlying MapView is great for switching
     * back and forth between tabs, but when used in fragments this behavior causes a duplicate MapView to be created
     * upon each new MapFragment with the same ID. The solution is to manually remove the MapFragment
     * and thus recreate the underlying map each time the fragment is inflated.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.mini_map);
        if (fragment != null && fragment.isResumed()) {
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragmentManager.popBackStack();
        }
    }
}
