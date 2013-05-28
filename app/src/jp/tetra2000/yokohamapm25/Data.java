package jp.tetra2000.yokohamapm25;

import java.util.ArrayList;
import java.util.Date;

public class Data {
	public static final int DAY_LABEL_INDEX = 0;
	public static final  int FIRST_DATA_INDEX = 1;
	public static final int DAYLY_AVARAGE_INDEX = 25;
	public static final int VALID_TIME_INDEX = 26;
	
	// 測定日
	public ArrayList<Date> dayList = new ArrayList<Date>();
	// 測定値
	public ArrayList<ArrayList<ExInteger>> valueList = new ArrayList<ArrayList<ExInteger>>();
	// 日平均
	public ArrayList<ExDouble> avarageList = new ArrayList<ExDouble>();
	// 有効時間
	public ArrayList<ExInteger> validList = new ArrayList<ExInteger>();
	
	public void addData(Date date, ArrayList<ExInteger> values, ExDouble avarage, ExInteger valid) {		
		
		dayList.add(date);
		valueList.add(values);
		avarageList.add(avarage);
		validList.add(valid);
	}
	
	// 最新の測定値
	public ExInteger getLatestValue() {
		// 最新日のデータ群
		ArrayList<ExInteger> latestValues = valueList.get(valueList.size()-1);
		// 最新のデータ
		return latestValues.get(latestValues.size()-1);
	}
	
	// 最新の測定値の測定時刻
	public int getLatestTime() {
		// 最新日のデータ群
		ArrayList<ExInteger> latestValues = valueList.get(valueList.size()-1);
		return latestValues.size();
	}
	
	// 今日の最高値
	public int getTodayMax() {
		// 最新日のデータ群
		ArrayList<ExInteger> latestValues = valueList.get(valueList.size()-1);
		
		int max = 0;
		for(ExInteger data: latestValues) {
			int buf;
			if(data.dataType == ExInteger.Type.Integer && (buf = data.intValue) > max) {
				max = buf;
			}
		}
		
		return max;
	}
	
	// 今日の最高値
	public int getYesterdayMax() {
		// 最新日の前日のデータ群
		ArrayList<ExInteger> values = valueList.get(valueList.size()-1 -1);
		
		int max = 0;
		for(ExInteger data: values) {
			int buf;
			if(data.dataType == ExInteger.Type.Integer && (buf = data.intValue) > max) {
				max = buf;
			}
		}
		
		return max;
	}
	
	// 今日の平均
	public ExDouble getTodayAve() {
		ExDouble value = avarageList.get(avarageList.size() - 1);
		return value;
	}
	
	// 前日の平均
	public ExDouble getYesterdayAve() {
		ExDouble value = avarageList.get(avarageList.size() - 1 - 1);
		return value;
	}
	
	// 最新の測定日の日付
	public Date getToday() {
		return dayList.get(dayList.size()-1);
	}
	
	public Date getYesterday() {
		return dayList.get(dayList.size()-1 -1);
	}
	
	// 今日の測定値すべて
	public ArrayList<ExInteger> getTodayValues() {
		return valueList.get(valueList.size()-1);
	}
	
	// 昨日の測定値すべて
	public ArrayList<ExInteger> getYesterdayValues() {
		return valueList.get(valueList.size()-1 -1);
	}
}