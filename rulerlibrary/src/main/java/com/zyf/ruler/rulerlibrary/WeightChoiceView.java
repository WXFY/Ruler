package com.zyf.ruler.rulerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.zyf.ruler.rulerlibrary.utils.DensityUtil;

/**
 * 弧形体重选择器
 * */
public class WeightChoiceView extends View{
    /**
     * 背景画笔
     * */
    private Paint mBgPaint;
    /**
     * 背景画笔颜色
     * */
    private int mBgPaintColor;
    /**
     * 字画笔
     * */
    private Paint mTextPaint;

    /**
     * 字体画笔颜色
     * */
    private int mTextPaintColor;


    /**
     * 字大小
     * */
    private float mTextSize;

    /**
     * 中间字大小
     * */
    private float mTextSelectSize;

    /**
     * 选中字体画笔颜色
     * */
    private int mTextSlectColor;

    /**
     * 上下文对象
     * */
    private Context context;

    /**
     * 宽
     * */
    private int width;
    /**
     * 高
     * */
    private int height;

    /**
     * 最大值
     * */
    private float max = 300;

    /**
     * 最小值
     * */
    private float min = 30;

    /**
     * 最小间隔
     * */
    private float spacing = 0.1f;


    /**
     * 刻度数量
     * */
    private int spacingCount;

    /**
     * 默认状态下，mSelectorValue所在的位置  位于尺子总刻度的位置
     * */
    private float mOffset;
    /**
     * 尺子刻度2条线之间的角度
     * */
    private float mLineSpaceWidth = 2;

    private int mPerValue;

    /**
     * 主要用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率。
     * */
    private VelocityTracker mVelocityTracker;

    /**
     * Scroller是一个专门用于处理滚动效果的工具类   用mScroller记录/计算View滚动的位置，再重写View的computeScroll()，完成实际的滚动
     * */
    private Scroller mScroller;
    private int mLastX,mMove,nextMove;
    /**
     * 所有刻度 共有多长
     * */
    private int mMaxOffset;
    /**
     * 用户选择的值
     * */
    private float mSelectorValue;
    private int mMinVelocity;

    /**
     * 是否弧度尺 true是圆弧刻度尺  false是直尺
     * */
    private boolean arc = true;
    /**
     * 2度的长度
     * */
    private float distance;

    private OnValueChangeListener listener;

    public WeightChoiceView(Context context) {
        this(context,null);
    }

    public WeightChoiceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WeightChoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray ty = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeightChoiceView,0,0);
        try {
            mBgPaintColor = ty.getColor(R.styleable.WeightChoiceView_mBgPaintColor, Color.GRAY);
            mTextPaintColor = ty.getColor(R.styleable.WeightChoiceView_mTextPaintColor, Color.BLACK);
            mTextSlectColor = ty.getColor(R.styleable.WeightChoiceView_mTextSelectColor, Color.BLACK);
            mTextSize = ty.getDimension(R.styleable.WeightChoiceView_mTextSize, DensityUtil.dpToPx(14f));
            mTextSelectSize = ty.getDimension(R.styleable.WeightChoiceView_mTextSelectSize, DensityUtil.dpToPx(18f));
            min = ty.getFloat(R.styleable.WeightChoiceView_min, 30f);
            max = ty.getFloat(R.styleable.WeightChoiceView_max, 200f);
            mSelectorValue = ty.getFloat(R.styleable.WeightChoiceView_mSelectorValue, (max-min)/2);
            spacing = ty.getFloat(R.styleable.WeightChoiceView_spacing, 0.1f);
            arc = ty.getBoolean(R.styleable.WeightChoiceView_arc, true);
        }finally {
            ty.recycle();
        }
        initView();
        if(arc){
            mLineSpaceWidth = 2;
        }else {
            mLineSpaceWidth = DensityUtil.dpToPx(10);
        }
        mPerValue = (int) (spacing * 10.0f);
        spacingCount =(int) (((max*10-min*10)/mPerValue)+1);
        mOffset =  ((min-mSelectorValue) / mPerValue * mLineSpaceWidth * 10);
        mMaxOffset = (int) (-(spacingCount - 1) * mLineSpaceWidth);
    }

    private void initView() {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextPaintColor);

        mMinVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();

        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measureSize(widthMeasureSpec);
        height = measureSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
        height = width/2;
        distance = (float) (height * 2 * Math.sin(1 * Math.PI / 180));
    }
    private int measureSize(int measureSpec) {
        int length;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(mode == MeasureSpec.EXACTLY){
            length = size;
        }else{
            length = (int) DensityUtil.dpToPx(180);
            if(mode == MeasureSpec.AT_MOST){
                length = Math.min(length,size);
            }
        }
        return length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width/2,2*height);
        drawBg(canvas);
        if(arc){
            drawScale(canvas);
        }else {
            drawScaleRuler(canvas);
        }

    }



    /**20
     * 画背景
     * */
    private void drawBg(Canvas canvas) {
        mBgPaint.setColor(mBgPaintColor);
        mBgPaint.setStrokeWidth(5);
        if(arc){
            mBgPaint.setColor(Color.parseColor("#D6E6FF"));
            mBgPaint.setStyle(Paint.Style.FILL);canvas.drawCircle(0,0,height*2- DensityUtil.dpToPx(10),mBgPaint);
            mBgPaint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawCircle(0,0,height*1.4f,mBgPaint);

            mBgPaint.setStyle(Paint.Style.STROKE);
            mBgPaint.setColor(mBgPaintColor);
            canvas.drawCircle(0,0,height*2- DensityUtil.dpToPx(10),mBgPaint);
            canvas.drawCircle(0,0,height*1.4f,mBgPaint);

        }else{
            float distance = (height*2- DensityUtil.dpToPx(10)-height*1.4f)/2;
            mBgPaint.setColor(Color.parseColor("#D6E6FF"));
            mBgPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(-width/2,-height-height/2-distance,
                    width/2,-height-height/2+distance,mBgPaint);


            mBgPaint.setStyle(Paint.Style.STROKE);
            mBgPaint.setColor(mBgPaintColor);
            canvas.drawLine(-width/2,-height-height/2+distance,width/2,-height-height/2+distance,mBgPaint);
            canvas.drawLine(-width/2,-height-height/2-distance,width/2,-height-height/2-distance,mBgPaint);


        }
    }
    /**
     * 直尺画刻度和值
     * */
    private void drawScaleRuler(Canvas canvas) {
        mBgPaint.setColor(mBgPaintColor);
        float left;
        float distance = (height*2- DensityUtil.dpToPx(10)-height*1.4f)/2;
        for (int i = 0; i < spacingCount; i++) {
            left = mOffset + i * mLineSpaceWidth;

            if (left < -width/2 || left > width/2) {
                continue;  //  先画默认值在正中间，左右各一半的view。  多余部分暂时不画(也就是从默认值在中间，画旁边左右的刻度线)
            }
            float scaleHeight;
            if (i % 10 == 0) {
                String value = String.valueOf((int) (min + i * mPerValue / 10));
                int textWidth;
                if(left ==0){
                    mTextPaint.setTextSize(mTextSelectSize);
                    mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    mTextPaint.setColor(mTextSlectColor);
                    textWidth = (int)mTextPaint.measureText(value);
                }else {
                    mTextPaint.setTextSize(mTextSize);
                    mTextPaint.setColor(mTextPaintColor);
                    mTextPaint.setTypeface(Typeface.DEFAULT);
                    textWidth = (int)mTextPaint.measureText(value);
                }
                canvas.drawText(value,
                        (left-textWidth/2)
                        ,(-height-height/2+15f)
                        ,mTextPaint);
                scaleHeight = DensityUtil.dpToPx(20);
                mBgPaint.setStrokeWidth(5f);
            }else if(i % 5 == 0){
                scaleHeight = DensityUtil.dpToPx(15);
                mBgPaint.setStrokeWidth(2f);
            } else {
                mBgPaint.setStrokeWidth(2f);
                scaleHeight = DensityUtil.dpToPx(10);
            }
            canvas.drawLine(left, -height-height/2+distance, left, -height-height/2+distance-scaleHeight, mBgPaint);
            canvas.drawLine(left, -height-height/2-distance, left, -height-height/2-distance+scaleHeight, mBgPaint);
        }
    }

    /**
     * 画刻度和值
     * */
    private void drawScale(Canvas canvas) {
        mBgPaint.setColor(mBgPaintColor);
        float radiusScale = height*2- DensityUtil.dpToPx(10);
        for (int i = 0; i < spacingCount; i++) {
            float angle = mOffset + i * mLineSpaceWidth+270f;
            if(angle>360.0||angle<180.0){
                continue;
            }
            float scaleHeight;
            if (i % 10 == 0) {
                String value = String.valueOf((int) (min + i * mPerValue / 10));
                int textWidth;
                if(angle==270){
                    mTextPaint.setTextSize(mTextSelectSize);
                    mTextPaint.setColor(mTextSlectColor);
                    mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    textWidth = (int)mTextPaint.measureText(value);
                }else {
                    mTextPaint.setTextSize(mTextSize);
                    mTextPaint.setColor(mTextPaintColor);
                    mTextPaint.setTypeface(Typeface.DEFAULT);
                    textWidth = (int)mTextPaint.measureText(value);
                }
                canvas.drawText(value,
                        (float)((radiusScale-((radiusScale-height*1.3f)/2))*Math.cos((angle)*Math.PI/180))-textWidth/2
                        ,(float)(((radiusScale-((radiusScale-height*1.3f)/2))*Math.sin((angle)*Math.PI/180)))
                        ,mTextPaint);
                scaleHeight = DensityUtil.dpToPx(20);
                mBgPaint.setStrokeWidth(5f);
            }else if(i % 5 == 0){
                scaleHeight = DensityUtil.dpToPx(15);
                mBgPaint.setStrokeWidth(2f);
            } else {
                mBgPaint.setStrokeWidth(2f);
                scaleHeight = DensityUtil.dpToPx(10);
            }
            canvas.drawLine((float)(radiusScale*Math.cos((angle)*Math.PI/180))
                    ,(float)(radiusScale*Math.sin((angle)*Math.PI/180)),
                    (float)((radiusScale-scaleHeight)*Math.cos((angle)*Math.PI/180)),
                    (float)((radiusScale-scaleHeight)*Math.sin((angle)*Math.PI/180)),
                    mBgPaint);

            canvas.drawLine((float)(height*1.4f*Math.cos((angle)*Math.PI/180))
                    ,(float)(height*1.4f*Math.sin((angle)*Math.PI/180)),
                    (float)((height*1.4f+scaleHeight)*Math.cos((angle)*Math.PI/180)),
                    (float)((height*1.4f+scaleHeight)*Math.sin((angle)*Math.PI/180)),
                    mBgPaint);


        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                mMove = (mLastX - xPosition);
                if(mMove!=nextMove){
                    nextMove = mMove;
                    changeMoveAndValue();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker();
                return false;
            default:
                break;
        }
        mLastX = xPosition;
        return true;
    }

    /**
     * 滑动后的操作
     */
    private void changeMoveAndValue() {
        if(arc){
            mOffset -= (mMove/distance);
        }else {
            mOffset -=mMove;
        }
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
            mMove = 0;
            mScroller.forceFinished(true);
        } else if (mOffset >= 0) {
            mOffset = 0;
            mMove = 0;
            mScroller.forceFinished(true);
        }
        mSelectorValue = min + Math.round(Math.abs(mOffset) * 1.0f / mLineSpaceWidth) * mPerValue / 10.0f;
        notifyValueChange();
        postInvalidate();
    }


    /**
     * 滑动结束后，若是指针在2条刻度之间时，改变mOffset 让指针正好在刻度上。
     */
    private void countMoveEnd() {
        if(arc){
            mOffset -= (mMove/distance);
        }else {
            mOffset -=mMove;
        }
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
        } else if (mOffset >= 0) {
            mOffset = 0;
        }

        mLastX = 0;
        mMove = 0;
        mSelectorValue = min + Math.round(Math.abs(mOffset) * 1.0f / mLineSpaceWidth) * mPerValue / 10.0f;
        mOffset = (min - mSelectorValue) * 10.0f / mPerValue * mLineSpaceWidth;

        notifyValueChange();
        postInvalidate();
    }

    private void countVelocityTracker() {
        mVelocityTracker.computeCurrentVelocity(1000);  //初始化速率的单位
        float xVelocity = mVelocityTracker.getXVelocity(); //当前的速度
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {     //mScroller.computeScrollOffset()返回 true表示滑动还没有结束
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove = (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }

    /**
     * 回调用户选择的值
     * */
    private void notifyValueChange() {
        if (null != listener) {
            listener.onValueChange(mSelectorValue);
        }
    }
    /**
     * 滑动后的回调
     */
    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public OnValueChangeListener getListener() {
        return listener;
    }

    public void setListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    /**
     *
     * @param selectorValue 未选择时 默认的值 滑动后表示当前中间指针正在指着的值
     * @param minValue   最大数值
     * @param maxValue   最小的数值
     * @param per   最小单位  如 1:表示 每2条刻度差为1.   0.1:表示 每2条刻度差为0.1 在demo中 身高mPerValue为1  体重mPerValue 为0.1
     */
    public void setValue(float selectorValue, float minValue, float maxValue, float per) {
        this.mSelectorValue = selectorValue;
        this.max = maxValue;
        this.min = minValue;
        this.mPerValue = (int) (per * 10.0f);
        this.spacingCount = ((int) ((max * 10 - min * 10) / mPerValue)) + 1;
        mMaxOffset = (int) (-(spacingCount - 1) * mLineSpaceWidth);
        mOffset = (min - mSelectorValue) / mPerValue * mLineSpaceWidth * 10;
        invalidate();
        setVisibility(VISIBLE);
    }
}
