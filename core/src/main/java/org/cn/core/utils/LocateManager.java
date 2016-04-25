package org.cn.core.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class LocateManager {
    public static final String TAG = LocateManager.class.getSimpleName();

    public void getLocation(Context ctx) {
        try {

            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                StringBuffer sb = new StringBuffer("Last Location -->");
                sb.append("Provider: ").append(location.getProvider());
                sb.append("Latitude: ").append(location.getLatitude());
                sb.append("Longitude: ").append(location.getLongitude());

                Log.d(TAG, sb.toString());
            }

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setSpeedRequired(true);
            criteria.setBearingRequired(false);
            criteria.setAltitudeRequired(false);

            lm.requestSingleUpdate(criteria, new SimpleLocationListener(), Looper.getMainLooper());

            lm.requestLocationUpdates(1000 * 5, 0, criteria, new SimpleLocationListener(), Looper.getMainLooper());
        } catch (Throwable e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    class SimpleLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged(...)");
            StringBuffer sb = new StringBuffer();
            sb.append("Provider: ").append(location.getProvider());
            sb.append(", Latitude: ").append(location.getLatitude());
            sb.append(", Longitude: ").append(location.getLongitude());

            Log.d(TAG, sb.toString());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged(...)");
            if (status != LocationProvider.AVAILABLE) {

            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled(...)");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled(...)");
        }
    }

}
