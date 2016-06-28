package br.geraldo.financial.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TreeMap;

import br.geraldo.financial.dao.InstrumentDao;
import br.geraldo.financial.enums.InstrumentEnum;
import br.geraldo.financial.factory.ConnectionFactory;
import br.geraldo.financial.model.Instrument;

public class CalculationModule  {
	
	private File file;
	private FileReader fr;
	private BufferedReader br;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.US);
	
	private Map<String, Map<Date,BigDecimal>> mapValues;
	private BigDecimal allMean = new BigDecimal(0);
	
	private static Map<String,BigDecimal> factorMap = new HashMap<String, BigDecimal>();
	
	private Date instrument3HighestValueDate;
	private Date instrument3LowestValueDate;

		
	/**
	 * CalculationModule Constructor
	 * Initializes the reader of the class by receiving a file name
	 * of a resource in the classpath
	 * 
	 * @String fileName
	 * 
	 */
	public CalculationModule(String fileName) throws FileNotFoundException{
		 file = new File(getClass().getClassLoader().getResource(fileName).getFile());
		 fr = new FileReader(file);
		 br = new BufferedReader(fr);
		 
		 mapValues = new HashMap<String, Map<Date,BigDecimal>>();
		 updateFactorMap();
	}

	
	/**
	 * 
	 * Method responsible for update the factorMap
	 * periodically
	 * 
	 */
	public static void updateFactorMap(){
			factorMap = new HashMap<String, BigDecimal>();
			try {
				Connection con = ConnectionFactory.getConnection();
				InstrumentDao dao = new InstrumentDao(con);
				List<Instrument> list = dao.getInstruments();
				
				for(Instrument ins : list){
					factorMap.put(ins.getName(), new BigDecimal(ins.getValue()));
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		
	}
	
	/**
	 * 
	 * Read the file and compute statistical "on the fly" information
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void Compute() throws IOException, ParseException{
		int i = 0;
		
		Timer time = new Timer();
		UpdateFactorsTask uft = new UpdateFactorsTask();
		time.schedule(uft, 0,100);
		
		double instrument3Hightest = 0;
		double instrument3Lowest = 99999999;
		
		for(String line = br.readLine(); line != null; line = br.readLine()){
			String instrumentName = line.substring(0, line.indexOf(","));
			
			Map<Date, BigDecimal> map;
			
			if(mapValues.containsKey(instrumentName)){
				map = mapValues.get(instrumentName);
			}else{
				if(contains(instrumentName)){
					map = new HashMap<Date, BigDecimal>();
				}else{
					map = new TreeMap<Date, BigDecimal>(Collections.reverseOrder());
				}
			}

			try {
				Double value = Double.parseDouble(line.substring(line.lastIndexOf(",")+1 , line.length()));
				
				if(instrumentName.equals(InstrumentEnum.INSTRUMENT3.getName())){
					if(instrument3Hightest < value){
						instrument3Hightest = value;
						instrument3HighestValueDate = sdf.parse(line.substring(line.indexOf(",")+1, line.lastIndexOf(",")).trim());
					}
					
					if(instrument3Lowest > value){
						instrument3Lowest = value;
						instrument3LowestValueDate = sdf.parse(line.substring(line.indexOf(",")+1, line.lastIndexOf(",")).trim());
					}
				}
				
				if(!factorMap.containsKey(instrumentName)){
					map.put(sdf.parse(line.substring(line.indexOf(",")+1, line.lastIndexOf(",")).trim()), new BigDecimal(value));
					allMean = allMean.add(new BigDecimal(Double.parseDouble(line.substring(line.lastIndexOf(",")+1 , line.length()))));
				}else{
					BigDecimal v = new BigDecimal(value).multiply(factorMap.get(instrumentName));
					map.put(sdf.parse(line.substring(line.indexOf(",")+1, line.lastIndexOf(",")).trim()), v);
					allMean = allMean.add(v);
				}
			} catch (ParseException e) {
				time.cancel();
				throw new ParseException("Invalid Date at line " + (i+1), i);
			}
			
			mapValues.put(line.substring(0, line.indexOf(",")), map);
			i++;
		}
		time.cancel();
		allMean = allMean.divide(new BigDecimal(i), 4, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getMean(){
		return this.allMean;
	}
	
	/**
	 * Check if the given instrumentName
	 * exists in the InstrumentNameEnum
	 * 
	 * @param instrumentName
	 * @return
	 */
	private boolean contains(String instrumentName) {

	    for (InstrumentEnum ins : InstrumentEnum.values()) {
	    	 if (ins.getName().equals(instrumentName)) {
	             return true;
	         }
	    }

	    return false;
	}
	
	public Map<String, BigDecimal> getSumNewestOtherInstruments(){
		Map<String, BigDecimal> otherInstrumentsMap = new HashMap<String, BigDecimal>();
		
		for(Entry<String, Map<Date,BigDecimal >> entry: mapValues.entrySet()){
			if(!contains(entry.getKey())){
				Map<Date, BigDecimal> map = entry.getValue();
				int i = 0;
				BigDecimal value = new BigDecimal(0);
				for(Entry<Date, BigDecimal> entry2: map.entrySet()){
					if(i<10){
						value = value.add(entry2.getValue());
					}
				}
				otherInstrumentsMap.put(entry.getKey(), value);
			}
		}
		
		return otherInstrumentsMap;
	}
	
	public Instrument getInstrument(String instrumentName, String strDate) throws ParseException{
		Date date = sdf.parse(strDate);
		
		Instrument ins = new Instrument();
		if(mapValues.containsKey(instrumentName)){
			ins.setName(instrumentName);
			ins.setValue(mapValues.get(instrumentName).get(date).doubleValue());
		}
		
		return ins;
	}
	
	public BigDecimal getMeanByMonthYear(InstrumentEnum instrument, String strDate,
			RoundingMode roundingMode) throws ParseException{
		
		BigDecimal mean = new BigDecimal(0);
		if(!mapValues.containsKey(instrument.getName())){
			return new BigDecimal(0);
		}
		Map<Date,BigDecimal> map = mapValues.get(instrument.getName());
		
		Date date = sdf.parse(strDate);
		
		int i = 0;
		for(Entry<Date, BigDecimal> entry : map.entrySet()){

			if( (date.getMonth() == entry.getKey().getMonth()) &&
					(date.getYear() == entry.getKey().getYear()) ){
				mean = mean.add(entry.getValue());
				i++;
			}
			
			
		}
		mean = mean.divide(new BigDecimal(i),4, roundingMode);
	
		return mean;
	}


	public Date getInstrument3HighestValueDate() {
		return instrument3HighestValueDate;
	}

	public Date getInstrument3LowestValueDate() {
		return instrument3LowestValueDate;
	}


	
	

}
