package jp.tetra2000.yokohamapm25.test;

import jp.tetra2000.yokohamapm25.ExInteger;
import android.test.AndroidTestCase;

public class ExIntegerTest extends AndroidTestCase {
	private ExInteger mNum1 = new ExInteger("1");
	private ExInteger mNum2 = new ExInteger("2");
	private ExInteger mError = new ExInteger("*");
	private ExInteger mNone = new ExInteger("-");
	
	public void testToString() {
		assertEquals(mNum1.toString(), "1");
		assertEquals(mNum2.toString(), "2");
		assertEquals(mError.toString(), "*");
		assertEquals(mNone.toString(), "-");
	}
	
	public void testEquals() {
		assertEquals(mNum1, new ExInteger("1"));
		assertEquals(mNum2, new ExInteger("2"));
		assertEquals(mError, new ExInteger("*"));
		assertEquals(mNone, new ExInteger("-"));
	}
}
