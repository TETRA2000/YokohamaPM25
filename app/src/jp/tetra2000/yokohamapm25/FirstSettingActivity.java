package jp.tetra2000.yokohamapm25;

import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FirstSettingActivity extends Activity {
	private static final String PREF_KEY = MainActivity.PREF_KEY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_setting);
		
		// ListViewにクリックリスナを登録
		ListView lv = (ListView)findViewById(R.id.spotList);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// プリファレンスを更新
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt(PREF_KEY, (int)id);
				editor.commit();
				
				// 結果をセットして終了
				Intent intent = new Intent();
				intent.putExtra(PREF_KEY, (int)id);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
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
