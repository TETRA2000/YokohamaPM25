package jp.tetra2000.yokohamapm25;

public class ExDouble {
	private enum Type {
		Double,
		Error,
		None
	}
	
	public final Type dataType;
	private double doubleValue;
	
	public ExDouble(String s) {
		if(s.equals("*")) {
			dataType = Type.Error;
		} else if (s.equals("-")) {
			dataType = Type.None;
		} else {
			dataType = Type.Double;
			doubleValue = Double.parseDouble(s);
		}
	}
	
	@Override
	public String toString() {
		
		if(dataType == Type.Double) {
			return String.valueOf(doubleValue);
			
		} else if(dataType == Type.None) {
			return "-";
		} else {
			return "*";
		}
	}
	
	@Override
	public boolean equals(Object object) {
		return object.toString().equals(this.toString());
	}
}
