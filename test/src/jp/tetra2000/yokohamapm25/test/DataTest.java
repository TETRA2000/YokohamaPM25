package jp.tetra2000.yokohamapm25.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import jp.tetra2000.yokohamapm25.Data;
import jp.tetra2000.yokohamapm25.ExDouble;
import jp.tetra2000.yokohamapm25.ExInteger;
import android.test.AndroidTestCase;

public class DataTest extends AndroidTestCase {	
	private Data mData;
	
	@Override
	public void setUp() {
		mData = new Data();
	}
	
	public void testAdd() {
		try {
			Random random = new Random();
			
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");;
			Date day = df.parse("2013/2/20");
			
			// 測定値をパース
			ArrayList<ExInteger> values = new ArrayList<ExInteger>();
			
			int sum = 0;
			for(int i=Data.FIRST_DATA_INDEX; i<Data.DAYLY_AVARAGE_INDEX; i++) {			
				int n = random.nextInt(100);
				sum += n;
				values.add(new ExInteger(String.valueOf(n)));
			}
			
			// 日平均
			ExDouble avarage = new ExDouble(String.valueOf((double)sum/24));
			
			// 有効時間
			ExInteger valid = new ExInteger("24");
			
			// 一日分のデータを追加
			mData.addData(day, values, avarage, valid);
			
			assertTrue(mData.dayList.get(0).equals(day));
			assertEquals(mData.valueList.get(0), values);
			assertEquals(mData.avarageList.get(0), avarage);
			assertEquals(mData.validList.get(0), valid);
		} catch (ParseException e) {
			fail();
		}
	}
	
}
