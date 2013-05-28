package jp.tetra2000.yokohamapm25;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/*
 * data.csvファイルから、Dataオブジェクトを作成。
 * 最新の測定値などを管理する。
 * 
 * ("data.csv"が存在しない場合、このクラスは
 * 意味を持たないため、データ取得のメソッドが呼ばれたら
 * 例外を吐いたほうがいいかもしれない。)
 */
public class DataManager{	
	private static final int USE_DAY_RANGE = 2;
	// BufferedReader#mark用
	private static final int MARK_LIMIT = 1024 * 1024; // 1MB
	private static final String TIME_ZONE_JAPAN = "GMT+9:00";
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	private boolean hasData;
	private Data mData;
	
	/*
	 * データファイルの有無を検証し、
	 * Dataオブジェクトの準備をする。
	 */
	public DataManager(File file) {
		// フラグを書き換え
		hasData = file.exists();
		
		if(hasData) { // データが有る場合は初期化
			if((mData = setUpData(file)) == null) { // ファイルの読み込みに失敗した場合
				// ファイルに問題がある可能性
				file.delete();
				hasData = false;
			}
		}
	}
	
	// クラス外から確認する用途のみ
	public boolean hasData() {
		return hasData;
	}	
	
	// 最新の値
	public ExInteger getLatestValue() {
		if(hasData) {
			return mData.getLatestValue();
			
		} else {
			// データがない場合
			return null;
		}
	}
	
	// 最新の値の更新時間(24時)
	public int getLatestTime() {
		if(hasData) {
			return mData.getLatestTime();
		} else {
			// データがない場合
			return -1;
		}
	}
	
	public Date getToday() {
		if(hasData) {
			return mData.getToday();
		} else {
			// データがない場合
			return null;
		}
	}
	
	public Date getYesterday() {
		if(hasData) {
			return mData.getYesterday();
		} else {
			// データがない場合
			return null;
		}
	}
	
	public int getTodayMax() {
		if(hasData) {
			return mData.getTodayMax();
		} else {
			// データがない場合
			return -1;
		}
	}
	
	public int getYesterdayMax() {
		if(hasData) {
			return mData.getYesterdayMax();
		} else {
			// データがない場合
			return -1;
		}
	}
	
	public ExDouble getYesterdayAve() {
		if(hasData) {
			return mData.getYesterdayAve();
		} else {
			// データがない場合
			return null;
		}
	}
	
	public ExDouble getTodayAve() {
		if(hasData) {
			return mData.getTodayAve();
		} else {
			// データがない場合
			return null;
		}
	}
	
	public ArrayList<ExInteger> getTodayValues() {
		if(hasData) {
			return mData.getTodayValues();
		} else {
			// データがない場合
			return null;
		}
	}
	
	public ArrayList<ExInteger> getYesterdayValues() {
		if(hasData) {
			return mData.getYesterdayValues();
		} else {
			// データがない場合
			return null;
		}
	}
	
	// Dataオブジェクトを作成する。
	private Data setUpData(File file) {
		
		if(file.length() == 0) {
			return null;
		}
		
		Data data = new Data();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			// 先頭をマーク
			br.mark(MARK_LIMIT);
			
			// ファイルの行数をカウント
			int lineNum = 0;
			String line;
			while((line = br.readLine()) != null && line.length() > 0) {
				lineNum ++;
			}
			
			// 位置をリセット
			br.reset();
			
			// 日付フォーマット
			DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.JAPAN);
			df.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_JAPAN));
			
			for(int i=0; i<lineNum - USE_DAY_RANGE; i++) {
				// 不要な行を読み飛ばす
				br.readLine();
			}
			
			for(int i=0; i<USE_DAY_RANGE; i++) {
				line = br.readLine();
				
				String[] datas = line.split(",");
				
				Date day = df.parse(datas[0]);
				
				// 測定値をパース
				ArrayList<ExInteger> values = new ArrayList<ExInteger>();
				
				for(int j=Data.FIRST_DATA_INDEX; j<Data.DAYLY_AVARAGE_INDEX; j++) {
					
					if((datas[j]).equals("-")) {
						// 測定値が存在しなくなったら、ループから抜ける
						break;						
					}
					
					// データを追加
					values.add(new ExInteger(datas[j]));
				}
				
				// 日平均
				ExDouble avarage = new ExDouble(datas[Data.DAYLY_AVARAGE_INDEX]);
				
				// 有効時間
				ExInteger valid = new ExInteger(datas[Data.VALID_TIME_INDEX]);
				
				// 一日分のデータを追加
				data.addData(day, values, avarage, valid);
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			/* 
			 * あり得ないはずなので、わざとアプリを落とす。
			 * エラーレポートを受けて確認する。
			 */
			throw new RuntimeException("NoDatafile");
			
		} catch (ParseException e) {
			/* 
			 * データフォーマットの仕様変更は予測できない。
			 */
			throw new RuntimeException("IllegalDataFormat");
			
		} catch (IOException e) {
			/*
			 * 着信などの割り込みも考えられるので、アプリは継続する。
			 * nullを返す。
			 * ※呼び出し元で要確認
			 */
			return null;
		}
		
		return data;
	}
}

