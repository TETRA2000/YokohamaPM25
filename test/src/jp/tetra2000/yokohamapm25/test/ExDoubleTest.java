package jp.tetra2000.yokohamapm25.test;

import jp.tetra2000.yokohamapm25.ExDouble;
import android.test.AndroidTestCase;

public class ExDoubleTest extends AndroidTestCase {
	private ExDouble mNum1 = new ExDouble("1.0");
	private ExDouble mNum2 = new ExDouble("2.0");
	private ExDouble mError = new ExDouble("*");
	private ExDouble mNone = new ExDouble("-");
	
	public void testToString() {
		assertEquals(mNum1.toString(), "1.0");
		assertEquals(mNum2.toString(), "2.0");
		assertEquals(mError.toString(), "*");
		assertEquals(mNone.toString(), "-");
	}
	
	public void testEquals() {
		assertEquals(mNum1, new ExDouble("1.0"));
		assertEquals(mNum2, new ExDouble("2.0"));
		assertEquals(mNum1, new ExDouble("1"));
		assertEquals(mNum2, new ExDouble("2"));
		assertEquals(mError, new ExDouble("*"));
		assertEquals(mNone, new ExDouble("-"));
	}
}
