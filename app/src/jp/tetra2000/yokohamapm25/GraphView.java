package jp.tetra2000.yokohamapm25;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.*;

public class GraphView extends View {
	private static final String TIME_ZONE_JAPAN = "GMT+9:00";
	
	private int mPadding;
	private int mTitleFont;
	private int mIndexFont;
	
	// Integer.MAX_VALUE は念のために用意
	private static final int[] TOP_INDEX = {Integer.MAX_VALUE, 10000, 1000, 500, 200, 100, 50};
	private static final int SEPARATE = 5;
	
	private int mMaxIndex ;
	
	private ArrayList<ExInteger> mValues;
	private Date mDay;
	private DateFormat mFormat;
	private String mDayText;
	
	private Paint mLinePaint;
	private Paint mTitlePaint;
	private Paint mIndexPaint;
	private Paint mGraphPaint;
	private Rect mDayBound;
	
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Resources res = context.getResources();
		
		mPadding = (int) Math.round(res.getDimension(R.dimen.graphPadding));
		mTitleFont = (int) Math.round(res.getDimension(R.dimen.graphTitleFont));
		mIndexFont = (int) Math.round(res.getDimension(R.dimen.graphIndexFont));
		
		//　日本のタイムゾーンに設定
		TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE_JAPAN);
		TimeZone.setDefault(timeZone);
		
		Locale.setDefault(Locale.JAPAN);
		mFormat = DateFormat.getDateInstance();
		
		mTitlePaint = new Paint();
		mTitlePaint.setColor(Color.BLACK);
		mTitlePaint.setTextSize(mTitleFont);
		mTitlePaint.setAntiAlias(true);
		
		mIndexPaint = new Paint();
		mIndexPaint.setColor(Color.BLACK);
		mIndexPaint.setTextSize(mIndexFont);
		mIndexPaint.setAntiAlias(true);
		
		mGraphPaint = new Paint();
		mGraphPaint.setColor(Color.RED);
		mGraphPaint.setAntiAlias(true);
		mGraphPaint.setStrokeWidth(res.getDimension(R.dimen.graphLine));
		
		mLinePaint = new Paint();
		mLinePaint.setColor(Color.GRAY);
		mLinePaint.setStrokeWidth(res.getDimension(R.dimen.graphBackLine));
	}
	
	public void setValues(ArrayList<ExInteger> values, Date day) {
		mValues = values;
		mDay = day;
		
		// 最大値を探す
		int max = 0;
		for(ExInteger value : values) {
			if(value.dataType == ExInteger.Type.Integer) {
				int n = value.intValue;
				if(n > max) {
					max = n;
				}
			}
		}
		
		// インデックスの値を変更
		for(int index : TOP_INDEX) {
			if(max <= index) {
				mMaxIndex = index;
			}
		}
		
		mDayText = mFormat.format(mDay);
		mDayBound = new Rect();
		mTitlePaint.getTextBounds(mDayText, 0, mDayText.length(), mDayBound);
		
		invalidate();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		// データがない場合は終了
		if(mValues == null)
			return;
		
		Rect bounds = canvas.getClipBounds();
		int width = bounds.right - bounds.left;
		int height = bounds.bottom - bounds.top;
		
		// グラフの左下の座標
		int xZero = mPadding;
		int yZero = height - mPadding;
		
		int xEnd = width - mPadding;
		int yEnd = mPadding;
		
		float xInterval = (float)(xEnd - xZero) / 24;
		float yInterval = (float)(yZero - yEnd) / SEPARATE;
		float yScale = (float)(yZero - yEnd) / mMaxIndex;
		
		Rect textBounds = new Rect();
		
		for(int i=0; i<24+1; i++) {
			float x = xZero + i*xInterval;
			canvas.drawLine(x, yZero, x, yEnd, mLinePaint);
			
			if(i!=0 && i%3 == 0) {
				String text = ""+i;
				mIndexPaint.getTextBounds(text, 0, text.length(), textBounds);
				
				canvas.drawText(text,
						x - textBounds.right/2,
						height + textBounds.top,
						mIndexPaint);
			}
		}
		
		int interval = mMaxIndex / SEPARATE;
		for(int i=0; i<SEPARATE + 1; i++) {
			float y = yZero - i*yInterval;
			canvas.drawLine(xZero, y, xEnd, y, mLinePaint);
			
			if(i!=0) {
				int lavelNum = i*interval;
				String text = lavelNum + "";
				mIndexPaint.getTextBounds(text, 0, text.length(), textBounds);
				
				if(100 <= lavelNum) {
					mIndexPaint.setColor(Color.RED);
					mIndexPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
				}
				
				canvas.drawText(text,
						0,
						y - textBounds.top/2,
						mIndexPaint);
						
				mIndexPaint.setColor(Color.BLACK);
			}
			
		}
		
		
		int len = mValues.size();
		for(int i=1; i<len; i++) {
			ExInteger value1 = mValues.get(i-1);
			ExInteger value2 = mValues.get(i);
			
			// 測定値がない部分はグラフを切る
			if(value1.dataType != ExInteger.Type.Integer ||
				value2.dataType != ExInteger.Type.Integer)
				continue;
			
			
			int v1 = value1.intValue;
			int v2 = value2.intValue;
			
			// 測定値が0以下の場合は0に直す
			if(v1<0) v1=0;
			if(v2<0) v2=0;
			
			int x1 = (int) (xZero + Math.round(i*xInterval));
			int x2 = (int) (xZero + Math.round((i+1)*xInterval));
			float y1 = yZero - (float)yScale*v1;
			float y2 = yZero - (float)yScale*v2;
			
			canvas.drawLine(x1, y1, x2, y2, mGraphPaint);
		}
		
		
		canvas.drawText(mDayText, width>>1, 0 - mDayBound.top, mTitlePaint);
		
	}
}
