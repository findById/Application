package org.cn.application.ui.suspension;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by chenning on 16-5-31.
 */
public class FloatLayout extends FrameLayout {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams layoutParams;

    private float lastX;
    private float lastY;

    private float statusBarHeight;

    public FloatLayout(Context context) {
        super(context);
        init(context);
    }

    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context ctx) {
        statusBarHeight = getInternalDimensionSizeByKey(ctx, "status_bar_height");
    }

    public void setLayoutParams(WindowManager.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public void setWindowManager(WindowManager mWindowManager) {
        this.mWindowManager = mWindowManager;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = event.getX();
                lastY = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                update(event.getRawX(), event.getRawY() - statusBarHeight);
                break;
            }
            case MotionEvent.ACTION_UP: {
                update(event.getRawX(), event.getRawY() - statusBarHeight);
                break;
            }
            default:
                break;
        }
        return true;
    }

    public void update(float x, float y) {
        if (layoutParams != null && mWindowManager != null) {
            layoutParams.x = (int) (x - lastX);
            layoutParams.y = (int) (y - lastY);
            mWindowManager.updateViewLayout(this, layoutParams);
        }
    }

    public static int getInternalDimensionSizeByKey(Context ctx, String key) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
