package jp.tetra2000.yokohamapm25;

public class ExInteger {
	public enum Type {
		Integer,
		Error,
		None
	}
	
	public final Type dataType;
	public int intValue;
	
	public ExInteger(String s) {
		if(s.equals("*")) {
			dataType = Type.Error;
		} else if(s.equals("-")) {
			dataType = Type.None;
		} else {
			dataType = Type.Integer;
			intValue = Integer.parseInt(s);
		}
	}
	
	@Override
	public String toString() {
		
		if(dataType == Type.Integer) {
			return String.valueOf(intValue);
			
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
