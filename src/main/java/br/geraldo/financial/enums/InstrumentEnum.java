package br.geraldo.financial.enums;

public enum InstrumentEnum {
	
	INSTRUMENT1("INSTRUMENT1"),
	INSTRUMENT2("INSTRUMENT2"),
	INSTRUMENT3("INSTRUMENT3");
	
	private final String name;
	
	InstrumentEnum(String nameOp) {
		name = nameOp;
	}
	
	public String getName(){
		return name;
	}
	

}
