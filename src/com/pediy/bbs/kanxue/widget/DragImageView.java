package com.pediy.bbs.kanxue.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;



public class DragImageView extends ImageView { 

	 private static final int TOUCH_NONE           = 0;     
	 private static final int TOUCH_MULTI_POINT = 1;     
	 private static final int TOUCH_SINGLE_POINT = 2;   
	  
	 private PointF      startPnt = new PointF(); 
	 private PointF      endPnt = new PointF(); 
	 private PointF      midPnt = new PointF();
	 private PointF      curPnt = new PointF();
	 private boolean[]  bValid = new boolean[2];  
	 
	 private int             mode = 0;   
	 private float         oldDist  = 0; 

	 private  Matrix matrix =    new Matrix();
	 private  Matrix savedMatrix = new Matrix();
		 

 
	public DragImageView(Context context) {
		super(context);
	}

	public DragImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	} 
 
	private float Spacing (PointF pnt1,  PointF pnt2 ) {  
		
        float x = pnt1.x - pnt2.x;  
        float y = pnt1.y - pnt2.y;  
        return FloatMath.sqrt(x * x + y * y);  
    }   
	
	   private void MidPoint(PointF midPnt, PointF pnt1,  PointF pnt2) {  
	         float x = pnt1.x + pnt2.x;  
	         float y = pnt1.y + pnt2.y;  
	         
	         midPnt.set(x / 2, y / 2);  
	     }  
	 
	public boolean onTouchEvent(MotionEvent event) {
		 
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {

			matrix.set(getImageMatrix());

			mode = TOUCH_SINGLE_POINT;
			startPnt.set(event.getX(), event.getY());
			bValid[0] = true;

			midPnt.set(startPnt);
		}
			break;
	 
		case MotionEvent.ACTION_POINTER_DOWN: {
			int pntPos = event.getActionIndex();
			if (pntPos < 2) {
				mode = TOUCH_MULTI_POINT;
				endPnt.set(event.getX(pntPos), event.getY(pntPos));
				bValid[pntPos] = true;

				if (bValid[1] && bValid[0]) {
					oldDist = Spacing(startPnt, endPnt);
					MidPoint(midPnt, startPnt, endPnt);
				}
			}

		}
			break;

		case MotionEvent.ACTION_MOVE: {

			int nCount = event.getPointerCount(); 
			if (mode == TOUCH_SINGLE_POINT) { 

				startPnt.set(event.getX(), event.getY());
				
				curPnt.x = startPnt.x - midPnt.x;
				curPnt.y = startPnt.y - midPnt.y;
				
				if (Math.abs(curPnt.y) > 8f || Math.abs(curPnt.x) > 8f) {
					midPnt.set(startPnt); 
					matrix.postTranslate(curPnt.x, curPnt.y);
					setImageMatrix(matrix);
				}
				

			} else if (mode == TOUCH_MULTI_POINT) {

				 
				if (nCount > 1) {
					startPnt.set(event.getX(0), event.getY(0)); 
					endPnt.set(event.getX(1), event.getY(1)); 
				}
				
				float newDist = Spacing(startPnt, endPnt);

				if (Math.abs(newDist - oldDist) > 8f) {
					float scale_temp = newDist / oldDist;

					matrix.postScale(scale_temp, scale_temp, midPnt.x,midPnt.y);
					setImageMatrix(matrix);

					oldDist = newDist;
				}
			}

		}
			break;
		case MotionEvent.ACTION_UP: {
			for (int i = 0; i < bValid.length; i++) {
				bValid[i] = false;
			}
			mode = TOUCH_NONE;
		} 
			break;

 
		case MotionEvent.ACTION_POINTER_UP:
			int pntPos = event.getActionIndex();
			if (pntPos < 2) {
				bValid[pntPos] = false; 
				
				if(pntPos ==0 ){
					midPnt.set(event.getX(1), event.getY(1) );
				}else{
					midPnt.set(event.getX(0), event.getY(0) );
				}
				mode = TOUCH_SINGLE_POINT;
				
			}
			break;
		}

		return true;
	}

}
