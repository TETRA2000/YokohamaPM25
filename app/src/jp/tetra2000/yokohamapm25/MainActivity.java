package jp.tetra2000.yokohamapm25;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.TimeZone;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;

public class MainActivity extends Activity {
	private static final int SELECT_SPOT = 0;
	public static final String TIME_ZONE_JAPAN = "GMT+9:00";
	public static final String PREF_KEY = "spot_id";
	
	private static final String FILE_NAME = AsyncDownloader.FILE_NAME;

	// スポットid
	private int spotId = -1;
	// データマネージャー
	private volatile DataManager mDataManager;
	// データ取得先
	private URL mUrl = null;
	
//	// ローカルにデータが有るか
//	// TODO これでデータマネージャーの使用を決める。
//	private volatile boolean hasLocalData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// グラフボタンにリスナ登録
		Button button = (Button)findViewById(R.id.buttonGraph);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(! hasLocalData())
					return;
				
				Intent intent = new Intent(MainActivity.this, GraphActivity.class);
				
				startActivity(intent);
			}
		});
		
		// 保存したスポットIDを取得
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		spotId = sharedPref.getInt(PREF_KEY, -1);
		
		// 初期設定が済んでいない場合
		if(spotId == -1) {
			
			Intent intent = new Intent(getApplicationContext(), FirstSettingActivity.class);
			startActivityForResult(intent, SELECT_SPOT);
		}
		// 初期設定済み
		else {
			
			File file = new File(getFilesDir(), FILE_NAME);
			mDataManager = new DataManager(file);
			
			// ローカルにデータが残っている場合
			if(mDataManager.hasData()) {
				
				//　日本のタイムゾーンに設定
				TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE_JAPAN);
				TimeZone.setDefault(timeZone);
				
				// 最新データの取得時刻を元に、更新するか判断
				Date today = new Date();
				Date updateDay = mDataManager.getToday();
				
				if(today.getYear() == updateDay.getYear()
						&& today.getMonth() == updateDay.getMonth()
						&& today.getDay() == updateDay.getDay()
						&& today.getHours() == mDataManager.getLatestTime()) {
					
					// 画面を書き換え
					updateScreen();
					
					// トーストを表示
					Toast.makeText(this, R.string.this_is_the_latest_value, Toast.LENGTH_SHORT).show();
					
				} else {
					// 古い場合でも、画面は書き換える。
					updateScreen();
					
					// データを更新
					refleshData();
				}
				
			} else {
				// スポット名更新に必要
				updateScreen();
				
				// ローカルにデータがない場合
				refleshData();
			}
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO テキストをXMLファイルに格納
		
		switch(item.getItemId()) {
		// スポット削除
		case R.id.reset_spot:
			AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("確認");
			builder.setMessage("測定局を削除します。\nよろしいですか？");
			builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	
	            	// 設定を削除
	            	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	            	SharedPreferences.Editor editor = sharedPref.edit();
	            	editor.remove(PREF_KEY);
	            	editor.commit();
	            	
	    			// 古いデータファイルを削除
	    			// TODO あとで吟味
	    			File dataFile = new File(getFilesDir(), FILE_NAME);
	    			dataFile.delete();
	            	
	            	finish();
	            	startActivity(new Intent(getApplicationContext(), MainActivity.class));
	            }
	        });
			builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	
	            }
	        });
			
			dialog = builder.create();
			dialog.show();
			
			return true;
		}
		
		return false;
	}
	
	private boolean hasLocalData() {
		return mDataManager!=null && mDataManager.hasData();
	}
	
	private void refleshData() {
		
		// 必要になった時に初めてURLの取得をする
		if(mUrl == null) {
			// リソースを参照し、URLを取得
			Resources res = getResources();
			TypedArray ta = res.obtainTypedArray(R.array.spot_urls);
			
			try {
				mUrl = new URL(ta.getString(spotId));
			}
			catch (MalformedURLException e) {
				
				// 内部リソースを用いているため起り得ない。
			}
			ta.recycle();
		}
		
		// TODO 要確認
		AsyncDownloader downloader = new AsyncDownloader();
		downloader.execute(mUrl);
	}
	
	// 画面内の数値や文字列を更新
	public void updateScreen() {
		
		// スポット名を更新
		if(spotId != -1) {
			TextView spotName = (TextView)findViewById(R.id.spotName);
			Resources res = getResources();
			TypedArray ta = res.obtainTypedArray(R.array.spots);
			
			spotName.setText(ta.getString(spotId));
			
			ta.recycle();
		}
		
		// データがない場合はここで終了
		if(!hasLocalData())
			return;
		
		if(mDataManager!=null && mDataManager.hasData()) {
			// 測定を更新
			TextView textView =  (TextView)findViewById(R.id.value);
			textView.setText(mDataManager.getLatestValue().toString());
			
			// 更新時刻
			textView = (TextView)findViewById(R.id.updateTime);
			textView.setText(mDataManager.getLatestTime() + "時");
			
			// 本日最高
			textView = (TextView)findViewById(R.id.todayMax);
			textView.setText(mDataManager.getTodayMax() + "");
			
//			// 前日最高
//			textView = (TextView)findViewById(R.id.yesterdayMax);
//			textView.setText(mDataManager.getYesterdayMax() + "");
			
			// 本日平均
			textView = (TextView)findViewById(R.id.todayAve);
			textView.setText(mDataManager.getTodayAve().toString());
			
			// 前日平均
			textView = (TextView)findViewById(R.id.yesterdayAve);
			textView.setText(mDataManager.getYesterdayAve().toString());
		}
	}
	
	// 初期設定用
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode != RESULT_OK) {
			
			// 設定されなかった場合は終了
			finish();
		}
		else {
			// 初期設定が正常に完了した場合
			
			// TODO　ここもあとで確認
			
			// FirstSettingActivityから返される値
			spotId = data.getIntExtra(PREF_KEY, -1);
			
			// スポット名を更新する
			updateScreen();
			
			// ここでデータを更新
			refleshData();
		}
	}
	
	
	
	// ダウンロード用非同期クラス
	class AsyncDownloader extends AsyncTask<URL, Integer, File> {
		public static final String FILE_NAME = "data.csv";
		private static final String CACHE_NAME = "cache";
		
		private AlertDialog mDialog;
		
		@Override
		protected  void onPreExecute () {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage(R.string.loading);
			mDialog = builder.create();
			mDialog.show();
		}
		
		// このメソッドは非同期実行される
		@Override
		protected File doInBackground(URL... params) {
			try {
				// ダウンロード用キャッシュファイル
		        File cacheFile = File.createTempFile(CACHE_NAME, null, getCacheDir());
		        /*
		         * キャッシュに書いてから書き直す理由は、
		         * キャンセルによるファイルの破損を考慮して。
		         */
		        
				
				// 先頭のURLのみ使用
				URLConnection connection = params[0].openConnection();
				InputStream is = connection.getInputStream();
				
				FileOutputStream fos = new FileOutputStream(cacheFile, false);
				
				// ダウンロード処理
				int b;
				while((b =is.read()) != -1) {
					if(isCancelled()) {
						// キャンセルされた場合はファイルを削除
						cacheFile.delete();
						
						is.close();
						fos.close();
						
						return null;
					}
					
					fos.write(b);
				}
				
				is.close();
				fos.close();
				
				FileReader fr = new FileReader(cacheFile);
				BufferedReader br = new BufferedReader(fr);
				
				// 書き込み用データファイル
				File dataFile = new File(getFilesDir(), FILE_NAME);
				
				FileWriter fw = new FileWriter(dataFile, false);
				BufferedWriter bw = new BufferedWriter(fw);
				
				// キャッシュからの書き直し
				while((b = br.read()) != -1) {
					bw.write(b);
				}
				
				bw.flush();
				br.close();
				bw.close();
				
				// キャッシュを削除
				cacheFile.delete();
				
				return dataFile;
				
			} catch (IOException e) {
				// TODO あとで書く
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(File file) {
			// ダイアログを消去
			mDialog.dismiss();
			
			if(file != null) {
				// データ取得が成功
				mDataManager = new DataManager(file);
				updateScreen();
				
				// トーストを表示
				Toast.makeText(MainActivity.this, R.string.updated, Toast.LENGTH_SHORT).show();
			} else {
				// 通信失敗の場合
				
				// トーストを表示
				Toast.makeText(MainActivity.this, R.string.updated_failed, Toast.LENGTH_SHORT).show();
			}
			
		}
	}
}
