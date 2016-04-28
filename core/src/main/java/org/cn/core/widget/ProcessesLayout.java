package org.cn.core.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import org.cn.core.R;

public class ProcessesLayout extends FrameLayout {

    private static final String TAG_PROGRESS = "progress";
    private static final String TAG_EMPTY = "empty";
    private static final String TAG_ERROR = "error";

    private View mCurrentView;
    private View mContentView;
    private View mProgressView;
    private View mEmptyView;
    private View mErrorView;

    private int progressResId;
    private int emptyResId;
    private int errorResId;

    public enum Type {
        PROGRESS, EMPTY, ERROR, CONTENT
    }

    public ProcessesLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ProcessesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProcessesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProcessesLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = ctx.obtainStyledAttributes(attrs, R.styleable.ProcessesLayout);
            progressResId = array.getResourceId(R.styleable.ProcessesLayout_progress, R.layout.model_progress);
            emptyResId = array.getResourceId(R.styleable.ProcessesLayout_empty, R.layout.model_empty);
            errorResId = array.getResourceId(R.styleable.ProcessesLayout_error, R.layout.model_error);
            array.recycle();
        } else {
            progressResId = R.layout.model_progress;
            emptyResId = R.layout.model_empty;
            errorResId = R.layout.model_error;
        }
        mProgressView = LayoutInflater.from(ctx).inflate(progressResId, null);
        addView(mProgressView);
        mCurrentView = mProgressView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = this.getChildAt(getChildCount() - 1);
        if (mContentView != null) {
            // mContentView.setVisibility(View.GONE);
        }
    }

    public void showContent() {
        show(Type.CONTENT, mContentView, true);
    }

    public void showProgress() {
        if (mProgressView == null) {
            mProgressView = LayoutInflater.from(getContext()).inflate(progressResId, null);
            mProgressView.setTag(TAG_PROGRESS);
        }
        show(Type.ERROR, mProgressView, true);
    }

    public void showEmpty() {
        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(getContext()).inflate(emptyResId, null);
            mEmptyView.setTag(TAG_EMPTY);
        }
        show(Type.ERROR, mEmptyView, true);
    }

    public void showError() {
        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(getContext()).inflate(errorResId, null);
            mErrorView.setTag(TAG_ERROR);
        }
        show(Type.ERROR, mErrorView, true);
    }

    public View getView(Type type, int id) {
        switch (type) {
            case PROGRESS: {
                if (mProgressView != null) {
                    return mProgressView.findViewById(id);
                }
                break;
            }
            case EMPTY: {
                if (mEmptyView != null) {
                    return mEmptyView.findViewById(id);
                }
                break;
            }
            case ERROR: {
                if (mErrorView != null) {
                    return mErrorView.findViewById(id);
                }
                break;
            }
            default:
                if (mContentView != null) {
                    return mContentView.findViewById(id);
                }
                break;
        }
        return null;
    }

    private void show(Type type, View target, boolean animate) {
        if (target == null || target == mCurrentView) {
            return;
        }
        mCurrentView.clearAnimation();
        target.clearAnimation();
        if (animate) {
            mCurrentView.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            target.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }

        if (type == Type.EMPTY || type == Type.ERROR) {
            if (mContentView != null) {
                mContentView.setVisibility(View.GONE);
            }
        } else {
            if (mContentView != null) {
                mContentView.setVisibility(View.VISIBLE);
            }
        }

        if (mCurrentView != null) {
            mCurrentView.setVisibility(View.GONE);
        }
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
        if (mErrorView != null) {
            mErrorView.setVisibility(View.GONE);
        }
        if (mProgressView != null) {
            mProgressView.setVisibility(View.GONE);
        }
        target.setVisibility(View.VISIBLE);
        mCurrentView = target;
    }
}
