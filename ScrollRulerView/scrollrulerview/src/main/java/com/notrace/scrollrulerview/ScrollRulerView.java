package com.notrace.scrollrulerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * create by notrace 2016/4/20
 */
public class ScrollRulerView extends View {

    private int mMinModDivider;

    private float mDensity;
    private int mValue, mMaxValue;
    private int mModType;
    private float mLineDivider;
    private int mTextColor, mHintColor;

    private int mLastX, mMove;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    TypedArray mTypedArray;

    private float halfHeight, halfWidth;

    public ScrollRulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private Paint linePaint;
    private Paint middleLinePaint;
    private TextPaint textPaint;

    /**
     */
    private void init(AttributeSet attrs) {
        mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollRulerView);
        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(0xFFECF0F1);

        middleLinePaint = new Paint();
        middleLinePaint.setStrokeWidth(2);
        middleLinePaint.setColor(0xFFF65348);


        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(mTypedArray.getDimension(R.styleable.ScrollRulerView_srTextSize, 18 * mDensity));
        mTextColor = mTypedArray.getColor(R.styleable.ScrollRulerView_srTextColor, 0xFF000000);
        mHintColor = mTypedArray.getColor(R.styleable.ScrollRulerView_srTextColorHint, 0xFFBEC3C7);

        mLineDivider = mTypedArray.getDimension(R.styleable.ScrollRulerView_srDivider, 10 * mDensity);

        mMaxValue = mTypedArray.getInt(R.styleable.ScrollRulerView_srMaxValue, 50000);
        mValue = mTypedArray.getInt(R.styleable.ScrollRulerView_srValue, 1000);

        mModType = mTypedArray.getInteger(R.styleable.ScrollRulerView_srType, 10);

        mMinModDivider = mTypedArray.getInt(R.styleable.ScrollRulerView_srMinModDivider, 1);

        leftShaderPaint = new Paint();
        rightShaderPaint = new Paint();

        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /**
     *
     * @param listener
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return mValue == mTempValue ? mTempValue : mValue;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        onInitSize();
        super.onLayout(changed, left, top, right, bottom);
    }

    float textWidth;
    float yText;
    float lineMaxHeight, lineMinHeight;
    private Paint leftShaderPaint, rightShaderPaint;
    private RectF leftShaderRF, rightShaderRF;

    /**
     */
    private void onInitSize() {

        if (textPaint != null)
            textWidth = Layout.getDesiredWidth("0", textPaint);
        //the y position of text
        yText = (getHeight() / 2);

        lineMaxHeight = getHeight() -
                mTypedArray.getDimension(R.styleable.ScrollRulerView_srMaxHeightLine, mDensity * 30);
        lineMinHeight = getHeight() -
                mTypedArray.getDimension(R.styleable.ScrollRulerView_srMinHeightLine, mDensity * 15);

        halfWidth = getWidth() / 2;
        halfHeight = getHeight() / 2;

        float shaderWidth = 50 * mDensity;

        LinearGradient leftShader = new LinearGradient(0, 0, shaderWidth, 0, new int[]{0xFFF0F0F0, 0x00FFFFFF}, null, Shader.TileMode.CLAMP);
        leftShaderPaint.setShader(leftShader);
        leftShaderRF = new RectF(0, 0, shaderWidth, getHeight());
        LinearGradient rightShader = new LinearGradient(getWidth() - shaderWidth, 0, getWidth(), 0, new int[]{0x00FFFFFF, 0xFFF0F0F0}, null, Shader.TileMode.CLAMP);
        rightShaderPaint.setShader(rightShader);
        rightShaderRF = new RectF(getWidth() - shaderWidth, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawScaleLine(canvas);
        drawMiddleLine(canvas);
    }


    /**

     *
     * draw ruler line
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        int drawCount = 0;
        float xPosition = 0;

        for (int i = 0; drawCount <= 4 * getWidth(); i++) {
            int newView = mValue + i * mMinModDivider;
            int numSize = String.valueOf(newView).length();
            textPaint.setColor(newView == mValue ? mTextColor : mHintColor);
            xPosition = (halfWidth - mMove) + i * mLineDivider;
            if (xPosition + getPaddingRight() < getWidth() && isEffectiveRange(newView)) {
                if (newView % mModType == 0) {
                    canvas.drawLine(xPosition, getWidth() - getPaddingBottom(), xPosition, lineMaxHeight, linePaint);
                    canvas.drawText(String.valueOf(newView), xPosition - (textWidth * numSize / 2), yText, textPaint);
                } else {
                    canvas.drawLine(xPosition, getWidth() - getPaddingBottom(), xPosition, lineMinHeight, linePaint);
                }
            }

            xPosition = (halfWidth - mMove) - i * mLineDivider;
            newView = mValue - i * mMinModDivider;
            numSize = String.valueOf(newView).length();
            if (xPosition > getPaddingLeft() && isEffectiveRange(newView)) {
                if (newView % mModType == 0 || newView == 0) {
                    canvas.drawLine(xPosition, getWidth() - getPaddingBottom(), xPosition, lineMaxHeight, linePaint);
                    canvas.drawText(String.valueOf(newView), xPosition - (textWidth * numSize / 2), yText, textPaint);
                } else {
                    canvas.drawLine(xPosition, getWidth() - getPaddingBottom(), xPosition, lineMinHeight, linePaint);
                }
            }

            drawCount += 2 * mLineDivider;
        }
        canvas.restore();
    }

    /**
     *
     * is the value is effective
     * @param value
     *
     * @return
     */
    private boolean isEffectiveRange(int value) {
        return value >= 0 && value <= mMaxValue;
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag() {

        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     *
     * @param value
     * @param xPosition
     * @param textWidth
     *
     * @return
     */
    private float countLeftStart(int value, float xPosition, float textWidth) {
        float xp = 0f;
        if (value < 20) {
            xp = xPosition - (textWidth * 1 / 2);
        } else {
            xp = xPosition - (textWidth * 2 / 2);
        }
        return xp;
    }

    /**
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        canvas.save();

        canvas.drawLine(halfWidth, 0, halfWidth, getHeight(), middleLinePaint);


        canvas.drawRect(leftShaderRF, leftShaderPaint);
        canvas.drawRect(rightShaderRF, rightShaderPaint);


        canvas.drawLine(0, 0, getWidth(), 0, linePaint);
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), linePaint);


        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (thread != null) {
            thread.stop = true;
        }
        attemptClaimDrag();
        int action = event.getAction();
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mScroller.forceFinished(true);

                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker(event);
                return false;
            // break;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    private void changeMoveAndValue() {
        int tValue = (int) (mMove / (mLineDivider));
        if (Math.abs(tValue) > 0) {
            mValue += tValue * mMinModDivider;
            mMove -= tValue * mLineDivider;

            if (!isEffectiveRange(mValue)) {
                mValue = mValue < 0 ? 0 : mMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }

//            if (mValue < 0 || mValue > mMaxValue) {
//                mValue = mValue <= 0 ? 0 : mMaxValue;
//                mMove = 0;
//                mScroller.forceFinished(true);
//            }
            notifyValueChange();
        }
        postInvalidate();
    }

    private void countMoveEnd() {
        int roundMove = Math.round(mMove / (mLineDivider));
        mValue += roundMove * mMinModDivider;
        mValue = mValue < mMinModDivider ? mMinModDivider : mValue;
        mValue = mValue > mMaxValue ? mMaxValue : mValue;

        mLastX = 0;
        mMove = 0;

        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
//            if (mModType == MOD_TYPE_ONE) {
            mListener.onValueChange(mValue);
//            }
//            if (mModType == MOD_TYPE_HALF) {
//                mListener.onValueChange(mValue / 2f);
//            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }


    public void setValue(int value) {
        if (value % mMinModDivider == 0) {
            mValue = value;
        } else {
            mValue = (value / 100) * 100;
        }
        postInvalidate();
        mLastX = 0;
        mMove = 0;
        if (thread != null) {
            thread.stop = true;
        }
//        notifyValueChange();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        if (maxValue < mMinModDivider) {
            maxValue = mMinModDivider;
        }
        if (maxValue % mMinModDivider == 0) {
            mMaxValue = maxValue;
        } else {
            mMaxValue = (maxValue / 100) * 100;
        }
        postInvalidate();
        mLastX = 0;
        mMove = 0;
        if (thread != null) {
            thread.stop = true;
        }
//        notifyValueChange();
    }

    public interface OnValueChangeListener {
        public void onValueChange(int value);
    }

    /** tenmp value */
    private int mTempValue;
    /**
     * Anima thread
     */
    private AnimaThread thread;

    /**
     * start animation
     */
    public void startAnim() {
        if (mValue > mMinModDivider) {
            mTempValue = mValue;
            mValue -= 5000;
            mValue = mValue < 0 ? 0 : mValue;

            if (thread != null) {
                thread.stop = true;
            }
            thread = new AnimaThread();
            thread.start();
        }
    }

    class AnimaThread extends Thread {
        boolean stop = false;

        @Override
        public void run() {
            mLastX = 0;
            mMove = 0;
            int mod = mMinModDivider * 2;
            while (!stop) {
                mValue += mod;
                if (mValue >= mTempValue) {
                    mValue = mTempValue;
                    stop = true;
                }
                postInvalidate();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}