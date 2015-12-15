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
import android.view.Event;
import android.view.MatrixUtil;
import android.view.GestureRecorder;
import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import android.app.FragmentManager;
import android.os.UserHandle;
import android.view.Util;
import android.widget.Toast;

/**
 * Created by sahaj on 11/4/15.
 */
public class MultiTouchView extends View {

    private final String TAG ="MultiTouchView";
    private static final int SIZE = 60;
    public static final String PREFS_NAME = "MyPrefsFile";
    private SparseArray<PointF> mActivePointers;
    private Paint mPaint;
    private int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW};

    private Paint textPaint;
    private TextView output;
    private ArrayList<Float> initialXCoordinates;
    private ArrayList<Float> initialYCoordinates;
    private ArrayList<Float> finalXCoordinates;
    private ArrayList<Float> finalYCoordinates;
    private float threshold = 10.0f;
    private float initialRadius;
    private float finalRadius;
    private static GestureRecorder gestureRecorder;
    private static Util gestureUtil;
    private Event storeEvent = null;
    private Context context;
    long startTime, endTime;
    float startX, startY, endX, endY;
    int numberOfMove = 0;
    private SaveGesturesCallback mSaveGesturesCallback;
    //private static int motion = -1;
    private int motion = 0;
    private int maxFingers =0;
    private boolean pointerUp;   
    private boolean hasFailed;   

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        Log.d(TAG, "Inside Init View");
        gestureRecorder = GestureRecorder.getInstance();
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
        finalXCoordinates = new ArrayList<Float>();
        finalYCoordinates = new ArrayList<Float>();
        pointerUp = false;
        hasFailed = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG,"Inside onTouchEvent");
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();
        int numFingers = event.getPointerCount();
        if(numFingers > maxFingers)
            maxFingers = numFingers;
        float focusX = 0.0f, focusY = 0.0f;


        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN: {
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);

                initialXCoordinates.add(event.getX(pointerIndex));
                initialYCoordinates.add(event.getY(pointerIndex));

                startTime = System.currentTimeMillis();

                storeEvent = gestureRecorder.storeEvent(storeEvent,event);
                Log.d(TAG,"After storing event:"+storeEvent);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                if(pointerUp){
                    pointerUp = false;
                    Toast.makeText(context,"Gesture failed.",Toast.LENGTH_SHORT).show();
                    hasFailed = true;
                    break;

                }
                // We have a new pointer. Lets add it to the list of pointers9
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);

                initialXCoordinates.add(event.getX(pointerIndex));
                initialYCoordinates.add(event.getY(pointerIndex));

                storeEvent = gestureRecorder.storeEvent(storeEvent,event);
                Log.d(TAG,"After storing event:"+storeEvent);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                
                numberOfMove++;
                break;
            }

            case MotionEvent.ACTION_UP: {
                //Log.d(TAG,"Inside action up");
                mActivePointers.remove(pointerId);
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);

                endTime = System.currentTimeMillis();

                finalXCoordinates.add(event.getX(pointerIndex));
                finalYCoordinates.add(event.getY(pointerIndex));

                if(hasFailed){
                    numberOfMove = 0;
                    initialXCoordinates = new ArrayList<Float>();
                    initialYCoordinates = new ArrayList<Float>();
                    finalXCoordinates = new ArrayList<Float>();
                    finalYCoordinates = new ArrayList<Float>();
                    numberOfMove = 0;
                    maxFingers = 0;
                    startTime =0;
                    endTime = 0;
                    pointerUp = false;
                    hasFailed = false;
                    break;
                }
                gestureUtil = new Util(startTime, endTime, numberOfMove);
                motion = gestureUtil.help(initialXCoordinates, initialYCoordinates, finalXCoordinates
                        , finalYCoordinates);
                storeEvent = gestureRecorder.storeEvent(storeEvent,event);
                Log.d(TAG,"After storing event:"+storeEvent);
                Log.d(TAG, "Before null check for mSaveGesturesCallback");
                if(mSaveGesturesCallback != null) {
                    Log.d(TAG, "mSaveGesturesCallback is not null");
                    mSaveGesturesCallback.fireSaveGestureEvent();
                }
                if(storeEvent != null){
                    //storeEvent = gestureRecorder.storeGesture(storeEvent,motion);
                }
                //storeEvent = null;
                numberOfMove = 0;
                initialXCoordinates = new ArrayList<Float>();
                initialYCoordinates = new ArrayList<Float>();
                finalXCoordinates = new ArrayList<Float>();
                finalYCoordinates = new ArrayList<Float>();
                numberOfMove = 0;
                maxFingers = 0;
                startTime =0;
                endTime = 0;
                pointerUp = false;
                hasFailed = false;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                pointerUp= true;
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.remove(pointerId);

                finalXCoordinates.add(event.getX(pointerIndex));
                finalYCoordinates.add(event.getY(pointerIndex));
    
                storeEvent = gestureRecorder.storeEvent(storeEvent,event);
                Log.d(TAG,"After storing event:"+storeEvent);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                Log.d(TAG,"Inside action cancel");
                mActivePointers.remove(pointerId);
                Log.d("Presentation", "Cancelled" + pointerId);
                break;
            }
        }
        invalidate();

        return true;
    }

    private String getCoordinates(MotionEvent event, int pointerIndex) {
        return event.getX(pointerIndex) + "," + event.getY(pointerIndex);
    }

    public float getRadius(PointF points[]) {
        float radius = 0.0f;
        float[][] A = new float[3][3];
        float[] B = new float[3];
        for (int i = 0; i < 3; i++) {
            A[i][0] = points[i].x;
            A[i][1] = points[i].y;
            A[i][2] = 1;

            B[i] = points[i].x * points[i].x + points[i].y * points[i].y;
        }


/*        B[0] = (one.x * one.x + one.y * one.y);
        B[1] = (two.x * two.x + two.y * two.y);
        B[2] = (three.x * three.x + three.y * three.y);*/

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
        //Log.d(TAG,"Inside onDraw");
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

    public void setBaseFragment(SaveGesturesCallback saveGesturesCallback) {
        mSaveGesturesCallback = saveGesturesCallback;
    }

    public void saveEvent(String gestureName, String actionType) {
        gestureRecorder.storeGesture(storeEvent,motion, gestureName, actionType);
        Intent intent = new Intent();
        intent.setAction("android.view.GestureReceiver");
       
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        Bundle bundle = new Bundle();
        bundle.putSerializable("gesture", gestureRecorder.getRootEvent());
        intent.putExtras(bundle);
       // if (intent.resolveActivity(getPackageManager()) != null) {
        context.sendBroadcastAsUser(intent, new UserHandle(UserHandle.USER_CURRENT));
        storeEvent = null;
        Log.d("MultiTouchView","Broadcast Successfully Sent");
       // gestureRecorder.reloadRoot();
        //}
    }

    public void eventCancelSave() {
        gestureRecorder.clearStoredEvents();
    }    
}