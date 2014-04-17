package jp.tetra2000.yokohamapm25;

import java.io.File;

import com.google.analytics.tracking.android.EasyTracker;

import jp.tetra2000.yokohamapm25.MainActivity.AsyncDownloader;
import android.os.Bundle;
import android.app.Activity;

/*
 * 2日分のグラフを表示する。
 */
public class GraphActivity extends Activity {
	private static final String FILE_NAME = AsyncDownloader.FILE_NAME;
	
	private DataManager mManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		File file = new File(getFilesDir(), FILE_NAME);
		mManager = new DataManager(file);
		
		// 今日のグラフ
		GraphView gView = (GraphView)findViewById(R.id.todayGraph);
		gView.setValues(mManager.getTodayValues(), mManager.getToday());
		
		// 昨日のグラフ
		gView = (GraphView)findViewById(R.id.yesterdayGraph);
		gView.setValues(mManager.getYesterdayValues(), mManager.getYesterday());
	}
	
	@Override
	public void onStart() {
		super.onStart();
	    EasyTracker.getInstance().activityStart(this); // Add this method.
	}
	
	@Override
	public void onStop() {
		super.onStop();
	    EasyTracker.getInstance().activityStop(this); // Add this method.
	}
}
