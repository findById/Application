package org.cn.core.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * Created by chenning on 16-6-21.
 */
public class PickView extends View {
    // private static final String TAG = PickView.class.getSimpleName();

    private static final int ITEM_COUNT = 5;
    private static final int ITEM_HEIGHT = 44;
    private static final int ITEM_TEXT_SIZE = 20;

    private int itemTextSize;

    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private Paint mLinePaint;

    private ArrayList<String> values;
    private int oldPosition = 0;
    private int position;
    private float itemHeight;
    private float scrollY;
    private float lastY;
    private boolean needMatch = false;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangedListener listener;

    public PickView(Context context) {
        super(context);
        init(context, null);
    }

    public PickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PickView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        // setBackgroundColor(Color.argb(0xA0, 0xF0, 0xF0, 0xF0));

        itemTextSize = ITEM_TEXT_SIZE;

        mScroller = new Scroller(ctx);

        int textSize = dp2Pixels(ctx, itemTextSize);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(textSize);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setAntiAlias(true);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.argb(0xFF, 0xFF, 0xFF, 0xFF));

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.argb(0xFF, 0xCC, 0xCC, 0xCC));

        values = new ArrayList<String>() {
            {
                for (int i = 0; i < 100; i++) {
                    add(String.valueOf(i));
                }
            }
        };

        itemHeight = ITEM_HEIGHT * getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastY = event.getY();
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);
                }
                scrolling(event.getY() - lastY);
                lastY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                lastY = mScroller.getCurrY();
                float velocity = mVelocityTracker.getYVelocity();
                if (velocity == 0) {
                    rematchScroll();
                } else {
                    needMatch = true;
                    mScroller.fling(0, 0, 0, (int) velocity, 0, 0, -1000, 1000);
                }
                mVelocityTracker.clear();
                break;
            }
            default:
                break;
        }
        return true;
    }

    private void scrolling(float y) {
        if (oldPosition != position) {
            oldPosition = position;
        }
        scrollY += y;
        while (scrollY > itemHeight) {
            scrollY -= itemHeight;
            position = getPrePosition(position, 1);
        }
        while (scrollY < 0) {
            scrollY += itemHeight;
            position = getNextPosition(position, 1);
        }
        if (oldPosition != position && listener != null) {
            listener.onValueChanged(this, values, oldPosition, position);
        }
        invalidate();
    }

    private void rematchScroll() {
        mScroller.startScroll(0, mScroller.getCurrY(), 0, (int) (itemHeight / 2 - scrollY));
        needMatch = false;
        invalidate();
    }

    @Override
    public void computeScroll() {
        // super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrolling(mScroller.getCurrY() - lastY);
            lastY = mScroller.getCurrY();
        } else if (needMatch && scrollY != itemHeight / 2) {
            rematchScroll();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (values != null && !values.isEmpty()) {
            drawItems(canvas);
        }
    }

    private void drawItems(Canvas canvas) {
        float y = getHeight() / 2F - itemHeight / 2 - 20;

        mTextPaint.setColor(Color.argb(0xFF, 0xFF, 0x00, 0x00));
        drawItem(canvas, position, y);
        int count = (ITEM_COUNT - 1) / 2;
        for (int i = 1; i <= count; i++) {
            if (i == 1) {
                mTextPaint.setColor(Color.argb(0xFF, 0x7A, 0x7A, 0x7A)); // 内环
            } else {
                mTextPaint.setColor(Color.argb(0xFF, 0xE0, 0xE0, 0xE0)); // 外环
            }
            drawItem(canvas, getPrePosition(position, i), y - itemHeight * i);
        }
        for (int i = 1; i <= count; i++) {
            if (i == 1) {
                mTextPaint.setColor(Color.argb(0xFF, 0x7A, 0x7A, 0x7A)); // 内环
            } else {
                mTextPaint.setColor(Color.argb(0xFF, 0xE0, 0xE0, 0xE0)); // 外环
            }
            drawItem(canvas, getNextPosition(position, i), y + itemHeight * i);
        }
    }

    private void drawItem(Canvas canvas, int position, float y) {
        String text = values.get(position);
        if (TextUtils.isEmpty(text) || text.length() == 1) {
            text = "0" + text;
        }
        float x = (getWidth() - mTextPaint.measureText(text)) / 2;
        y += getTextHeight(mTextPaint, text);
        // Log.d(TAG, "draw item: " + text + ", x: " + x + ", y: " + y);
        if (position == this.position) {
            canvas.drawLine(0, y - 22, getWidth(), y - 21, mLinePaint);
            canvas.drawRect(0, y - 20, getWidth(), y + itemHeight - 20, mBackgroundPaint);
        }
        canvas.drawText(text, x, y + itemHeight / 2, mTextPaint);
        if (position == this.position) {
            canvas.drawLine(0, y + itemHeight - 20, getWidth(), y + itemHeight - 21, mLinePaint);
        }
    }

    private float getTextHeight(Paint paint, String text) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        float height = (float) Math.ceil(fm.descent - fm.ascent);
        Rect rect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), rect);
        return (height + rect.height()) / 2F;
    }

    private int getPrePosition(int position, int positive) {
        for (int i = 0; i < positive; i++) {
            if (position == 0) {
                position = values.size() - 1;
            } else {
                position--;
            }
        }
        return position;
    }

    private int getNextPosition(int position, int positive) {
        for (int i = 0; i < positive; i++) {
            position++;
            if (position >= values.size()) {
                position = 0;
            }
        }
        return position;
    }

    private int dp2Pixels(Context ctx, float dp) {
        return (int) (dp * ctx.getResources().getDisplayMetrics().density + 0.5F);
    }

    public interface OnValueChangedListener {
        void onValueChanged(PickView view, ArrayList<String> data, int oldPosition, int position);
    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
        this.listener = listener;
    }

    public void setItemHeight(float height) {
        this.itemHeight = height;
        requestLayout();
    }

    public void setItemTextSize(int itemTextSize) {
        this.itemTextSize = itemTextSize;
        requestLayout();
    }

    public void setValues(ArrayList<String> values) {
        if (this.values != null && !this.values.isEmpty()) {
            this.values.clear();
        }
        this.values = values;
    }

    public void setSelectedPosition(int position) {
        this.position = position;
        invalidate();
    }

    public int getSelectedPosition() {
        return position;
    }
}
