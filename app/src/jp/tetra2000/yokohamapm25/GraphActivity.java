package jp.tetra2000.yokohamapm25;

import java.io.File;

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
}
