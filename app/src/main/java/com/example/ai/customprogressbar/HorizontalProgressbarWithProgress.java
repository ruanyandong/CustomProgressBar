package com.example.ai.customprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class HorizontalProgressbarWithProgress extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE=10;//sp
    private static final int DEFAULT_TEXT_COLOR=0xFFFC00D1;

    private static final int DEFAULT_COLOR_UNREACH=0xFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH=2;//dp

    private static final int DEFAULT_COLOR_REACH=0xFFFC00D1;
    private static final int DEFAULT_HEIGHT_REACH=2;//dp

    private static final int DEFAULT_TEXT_OFFSET=10;//dp

    protected int mTextSize=sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor=DEFAULT_TEXT_COLOR;

    protected int mUnReachColor=DEFAULT_COLOR_UNREACH;
    protected int mUnReachHeight=dp2px(DEFAULT_HEIGHT_UNREACH);

    protected int mReachColor=DEFAULT_COLOR_REACH;
    protected int mReachHeight=dp2px(DEFAULT_HEIGHT_REACH);

    protected int mTextOffset=dp2px(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint=new Paint();
    /**
     * 当前控件的宽度减去padding的值
     */
    protected int mRealWidth;


    public HorizontalProgressbarWithProgress(Context context) {
        this(context,null);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainStyledAttrs(attrs);
    }

    /**
     * 获取声明的属性
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs) {

        TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.HorizontalProgressbarWithProgress);

        mTextSize=(int)ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_text_size,mTextSize);

        mTextColor=ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_text_color,mTextColor);

        mTextOffset=(int)ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_text_offset,mTextOffset);

        mUnReachColor=ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_color,mUnReachColor);

        mUnReachHeight=(int)ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_height,mUnReachHeight);

        mReachColor=ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_reach_color,mReachColor);

        mReachHeight=(int)ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_reach_height,mReachHeight);

        ta.recycle();
        /**
         * 设置了画笔的字体大小，也就有了字体的高度等信息，后面可以获取
         */
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 宽度的测量不作具体处理，要求用户必须设置一个精确值
         */
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthVal=MeasureSpec.getSize(widthMeasureSpec);

        int height=measureHeight(heightMeasureSpec);

        /**
         * 确定view的宽和高
         */
        setMeasuredDimension(widthVal,height);

        mRealWidth=getMeasuredWidth()-getPaddingLeft()-getPaddingRight();

    }

    /**
     * 测量进度条的高度
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int result=0;

        int mode=MeasureSpec.getMode(heightMeasureSpec);
        int size=MeasureSpec.getSize(heightMeasureSpec);

        if (mode==MeasureSpec.EXACTLY){
            result=size;
        }else{
            /**
             * 在Android中绘制文本都是从baseline处开始的，从baseline往上至至文本最高处的距离称之为ascent(上坡度)，baseline至文本最低处的距离称之为descent(下坡度)。
             * baseline是基线，baseline以上是负值，baseline以下是正值，因此ascent和top都是负值，descent和bottom都是正值。
             文本的实际高度应该就是descent-asscent,但是一般都是以top-bottom作为文本的高度。
             */
            int textHeight=(int)(mPaint.descent()-mPaint.ascent());
            /**
             * getPaddingTop()字体的top
             * getPaddingBottom()字体的bottom
             */
            result=getPaddingTop()+getPaddingBottom()+Math.max(Math.max(mReachHeight,mUnReachHeight),Math.abs(textHeight));

            if (mode==MeasureSpec.AT_MOST){

                result=Math.min(result,size);

            }
        }

        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        /**
         * 不调用父类的方法，就可以完全自己控制画图
         */
       //super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(),getHeight()/2);

        /**
         * 判断是否需要绘制UnReachBar，false为需要，true为不需要
         */
        boolean noNeedDrawUnReach=false;

        String text=getProgress()+"%";

        int textWidth=(int)mPaint.measureText(text);

        //==================draw reachBar================
        /**
         * reachBar的进度值，0~1.0之间的一个值
         */
        float radio=getProgress()*1.0f/getMax();

        /**
         * 进度条长度
         */
        float progressX=radio*mRealWidth;

        if (progressX+textWidth>=mRealWidth){

            progressX=mRealWidth-textWidth;
            noNeedDrawUnReach=true;
        }


        /**
         *reachBar的最终长度，需要减去字体和进度条之间的偏移值的二分之一
         */
        float endX=progressX-mTextOffset/2;

        if (endX>0){
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);

            canvas.drawLine(0,0,endX,0,mPaint);
        }



        //===========draw text===================
        mPaint.setColor(mTextColor);
        /**
         * 文本的基线要往view的中心向下移动一点，才能在中心
         */
        int y=(int)(-(mPaint.descent()+mPaint.ascent())/2);

        canvas.drawText(text,progressX,y,mPaint);

        //===========draw unReachBar========
        if (!noNeedDrawUnReach){
            float start=progressX+mTextOffset/2+textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start,0,mRealWidth,0,mPaint);
        }

        canvas.restore();
    }

    /**
     * dp转化为px
     */
    protected int dp2px(int dpVal){
        /**
         * // 获取屏幕密度（方法1）
         int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
         int screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）


         // 获取屏幕密度（方法2）
         DisplayMetrics dm = new DisplayMetrics();
         dm = getResources().getDisplayMetrics();

         float density  = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
         int densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）
         float xdpi = dm.xdpi;
         float ydpi = dm.ydpi;

         screenWidth  = dm.widthPixels;      // 屏幕宽（像素，如：480px）
         screenHeight = dm.heightPixels;     // 屏幕高（像素，如：800px）


         // 获取屏幕密度（方法3）
         dm = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(dm);

         density  = dm.density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
         densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）
         xdpi = dm.xdpi;
         ydpi = dm.ydpi;

         int screenWidthDip = dm.widthPixels;        // 屏幕宽（dip，如：320dip）
         int screenHeightDip = dm.heightPixels;      // 屏幕宽（dip，如：533dip）

         screenWidth  = (int)(dm.widthPixels * density + 0.5f);      // 屏幕宽（px，如：480px）
         screenHeight = (int)(dm.heightPixels * density + 0.5f);     // 屏幕高（px，如：800px）
         */
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,getResources().getDisplayMetrics());

    }

    /**
    public class DensityUtil {

     // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
        public static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

     //根据手机的分辨率从 px(像素) 的单位 转成为 dp
        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }*/


}
