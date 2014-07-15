package tjhs.meet.meetupversion10.meetupversion10.UI;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import tjhs.meet.meetupversion10.meetupversion10.R;


public class Sphere extends View {

    private String mText;

    private float mRadius;

    private float centerX;

    private float centerY;

    private RelativeLayout.LayoutParams mParams;

    private int mColor;

    private Paint mPaint;

    private Canvas mCanvas;

    private String[] who;

    private int time;

    private Location location;

    public Sphere(Context context) {
        super(context);
        init(context, null);
    }

    public Sphere(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Sphere(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public Sphere (Context context, float x, float y, float radius, int color, AttributeSet attrs){
        super(context);
        centerX = x;
        centerY = y;
        mRadius = radius;
        mColor = color;

        mParams = new RelativeLayout.LayoutParams((int)mRadius*2, (int)mRadius*2);
        mParams.topMargin = (int)(centerY-mRadius);
        mParams.leftMargin =  (int)(centerX-mRadius);

        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(canvas.getHeight()/2, canvas.getWidth()/2, mRadius, mPaint);

        Paint mWordPaint = new Paint();
        mWordPaint.setColor(Color.WHITE);
        mWordPaint.setTextAlign(Paint.Align.CENTER);
        mWordPaint.setTextSize(35);
        canvas.drawText(mText, mRadius, mRadius+mWordPaint.getTextSize()/4, mWordPaint);
    }

    @Override
    protected void onMeasure(int width, int height){
        setMeasuredDimension((int)mRadius*2, (int)mRadius*2);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setText(String string){
        mText = string;
        invalidate();
        requestLayout();
    }

    public RelativeLayout.LayoutParams getParams(){
        return mParams;
    }

}
