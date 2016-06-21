package org.cn.core.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by chenning on 16-5-20.
 */
public class PermissionDispatcher {
    public static final String TAG = PermissionDispatcher.class.getSimpleName();

    public static void requestPermissions(final Object obj, String rationale, final int requestCode, final String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onPermissionsGranted(obj, requestCode);
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(obj), p) != PackageManager.PERMISSION_GRANTED) {
                list.add(p);
            }
        }
        if (!list.isEmpty()) {
            boolean shouldShowRationale = false;
            for (String perm : list) {
                shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(obj, perm);
            }
            if (shouldShowRationale) {
                new AlertDialog.Builder(getActivity(obj)).setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executePermissionsRequest(obj, permissions, requestCode);
                    }
                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPermissionsDenied(obj, requestCode);
                    }
                }).setMessage(rationale).setCancelable(false).show();
            } else {
                executePermissionsRequest(obj, permissions, requestCode);
            }
        } else {
            Log.i(TAG, "'" + permissions + "' permission has already been granted.");
            onPermissionsGranted(obj, requestCode);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Object obj) {
        boolean granted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                granted = false;
            }
        }
        if (granted) {
            onPermissionsGranted(obj, requestCode);
        } else {
            onPermissionsDenied(obj, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    private static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    private static void onPermissionsDenied(Object object, int requestCode) {
        Class clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PermissionDenied.class)) {
                if (method.getParameterTypes().length != 1) {
                    throw new RuntimeException("Cannot execute non-void method " + method.getName());
                }
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(object, requestCode);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

    private static void onPermissionsGranted(Object object, int requestCode) {
        Class clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PermissionGranted.class)) {
                if (method.getParameterTypes().length != 1) {
                    throw new RuntimeException("Cannot execute non-void method " + method.getName());
                }
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(object, requestCode);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

}
