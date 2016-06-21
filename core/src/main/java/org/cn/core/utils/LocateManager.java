package org.cn.core.utils;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public class LocateManager {
    public static final String TAG = LocateManager.class.getSimpleName();

    private static LocationManager mLocationManager;

    public static boolean isRestart = false;

    private static void init(Context ctx) {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public static void isProviderEnabled(final Context ctx) {
        init(ctx);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog).setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openSettings(ctx);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).setMessage("需要打开定位服务").show();
        }
    }

    private static void openSettings(Context ctx) {
        isRestart = true;
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // The Android SDK doc says that the location settings activity
            // may not be found. In that case show the general settings.

            // General settings activity
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                ctx.startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    public static void getLocation(Context ctx) {
        isRestart = false;
        try {
            init(ctx);

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
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                isProviderEnabled(ctx);
                return;
            }

            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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

            mLocationManager.requestSingleUpdate(criteria, new LocationListener() {
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
            }, Looper.getMainLooper());

            // mLocationManager.requestLocationUpdates(1000 * 5, 0, criteria, new SimpleLocationListener(), Looper.getMainLooper());
        } catch (Throwable e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    static class SimpleLocationListener implements LocationListener {

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
