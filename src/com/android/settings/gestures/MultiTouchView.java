package com.android.settings.gestures;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sahaj on 11/4/15.
 */
public class MultiTouchView extends View {
    private static final int SIZE = 60;
    public static final String PREFS_NAME = "MyPrefsFile";
    private SparseArray<PointF> mActivePointers;
    private Paint mPaint;
    private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW };

    private Paint textPaint;
    private TextView output;
    private ArrayList<Float> initialXCoordinates;
    private ArrayList<Float> initialYCoordinates;
    private float threshold = 10.0f;
    private float initialRadius;
    private float finalRadius;
    private static GestureRecorder gestureRecorder;
    private Event storeEvent = null;
    private Context context;


    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        gestureRecorder = new GestureRecorder(context);
        mActivePointers = new SparseArray<PointF>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // set painter color to a color you like
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
        output = new TextView(getContext());
        initialXCoordinates = new ArrayList<Float>();
        initialYCoordinates = new ArrayList<Float>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();
        int numFingers = event.getPointerCount();


        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:{
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);
                //Log.d("Presentation", "1st Finger ID : " + pointerId + ". Location (" + getCoordinates(event, pointerIndex) + ")");
        //        Log.d("Presentation", "Stored Event AD: " + storeEvent );
                storeEvent = gestureRecorder.storeEvent(storeEvent,MotionEvent.ACTION_DOWN,false,context);
        //        Log.d("Presentation", "Stored Event : " + storeEvent.getAction() );
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Lets add it to the list of pointers

                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);
                //Log.d("Presentation", "Finger with ID : " + pointerId + ". Location (" + getCoordinates(event, pointerIndex) + ")");
          //      Log.d("Presentation", "Stored Event APD : " + storeEvent );
                storeEvent = gestureRecorder.storeEvent(storeEvent,MotionEvent.ACTION_POINTER_DOWN,false,context);
           //     Log.d("Presentation", "Stored Event : " + storeEvent.getAction() );
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
             //   Log.d("Presentation", "Stored Event :AM " + storeEvent );
                storeEvent = gestureRecorder.storeEvent(storeEvent,MotionEvent.ACTION_MOVE,false,context);
             //   Log.d("Presentation", "Stored Event : " + storeEvent.getAction() );
                //for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                //  Log.d("Presentation",""+i+" Finger moved. Coordinates : ("+event.getX(i)+","+event.getY(i)+")");
                //}

                break;
            }
            case MotionEvent.ACTION_UP:{
                mActivePointers.remove(pointerId);
                //Log.d("Presentation", "Last finger removed with ID : " + pointerId + ". Location (" + getCoordinates(event, pointerIndex) + ")");
            //    Log.d("Presentation", "Stored Event :AU " + storeEvent );
                storeEvent = gestureRecorder.storeEvent(storeEvent,MotionEvent.ACTION_UP,true,context);
                storeEvent = null;
            //    Log.d("Presentation", "Stored Event : " + storeEvent.getAction() );
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                mActivePointers.remove(pointerId);
                //Log.d("Presentation", "Finger removed with ID : " + pointerId + ". Location (" + getCoordinates(event, pointerIndex) + ")");
            //    Log.d("Presentation", "Stored Event :APU " + storeEvent );
                storeEvent = gestureRecorder.storeEvent(storeEvent,MotionEvent.ACTION_POINTER_UP,false,context);
            //    Log.d("Presentation", "Stored Event : " + storeEvent.getAction() );
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointers.remove(pointerId);
                Log.d("Presentation", "Cancelled" + pointerId);
                break;
            }
        }
        invalidate();

        return true;
    }

    private String getCoordinates(MotionEvent event, int pointerIndex){
        return event.getX(pointerIndex) + "," + event.getY(pointerIndex);
    }

    public float getRadius(PointF one,PointF two,PointF three){
        float radius = 0.0f;
        float [][]A = new float[3][3];
        A[0][0] = one.x; A[0][1] = one.y; A[0][2] = 1;
        A[1][0] = two.x; A[1][1] = two.y; A[1][2] = 1;
        A[2][0] = three.x; A[2][1] = three.y; A[2][2] = 1;
        float []B = new float[3];
        B[0] = (one.x*one.x + one.y*one.y);
        B[1] = (two.x*two.x + two.y*two.y);
        B[2] = (three.x*three.x + three.y*three.y);
        A = MatrixUtil.invert(A);
        float []answer = new float[3];
        for(int i=0;i<A.length;i++){
            answer[i] = A[i][0]*B[0] + A[i][1]*B[1] + A[i][2]*B[2] ;
        }
        System.out.println(Arrays.toString(answer));
        float temp1 = Math.abs(answer[0])/2;
        float temp2 = Math.abs(answer[1])/2;
        float rhs = (answer[2] + temp1*temp1 + temp2*temp2);
        radius = (float)Math.sqrt(rhs);
        return radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all pointers
        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
            PointF point = mActivePointers.valueAt(i);
            if (point != null)
                mPaint.setColor(colors[i % 9]);
            canvas.drawCircle(point.x, point.y, SIZE, mPaint);
        }
        canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40 , textPaint);
    }
}
