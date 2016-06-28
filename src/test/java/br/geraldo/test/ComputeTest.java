package br.geraldo.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import br.geraldo.financial.core.CalculationModule;
import br.geraldo.financial.enums.InstrumentEnum;
import br.geraldo.financial.model.Instrument;

public class ComputeTest {
	
	@Test
	public void testCompute() throws ParseException{
		
		try {
			
			CalculationModule cm = new CalculationModule("example_input.txt");
			long start = System.nanoTime();
			cm.Compute();
			
			BigDecimal bd = cm.getMeanByMonthYear(InstrumentEnum.INSTRUMENT2, "01-Nov-2014"
					,RoundingMode.HALF_UP);
			
			long end = System.nanoTime() - start;
			double seconds = end / 1000000000.0;
			
			System.out.println("Mean For November 2014 : " + bd);
			System.out.println("General Mean : " + cm.getMean());
			System.out.println("Evaluated in  : " + seconds + " seconds.");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test(expected = ParseException.class)
	public void testDateValidationCompute() throws ParseException{
		
		try {
			CalculationModule cm = new CalculationModule("example_input_dateError.txt");
			cm.Compute();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testOtherInstruments() throws ParseException{
		try {
			CalculationModule cm = new CalculationModule("example_input_otherInstruments.txt");
			cm.Compute();
			
			Map<String, BigDecimal> map = cm.getSumNewestOtherInstruments();
			
			for(Entry<String, BigDecimal> entry : map.entrySet()){
				System.out.println(entry.getKey() + " :: " + entry.getValue());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLowestHighestInstrument3() throws ParseException{
		try {
			CalculationModule cm = new CalculationModule("example_input.txt");
			cm.Compute();
			
			System.out.println("Highest: " + cm.getInstrument3HighestValueDate());
			System.out.println("Lowest: " + cm.getInstrument3LowestValueDate());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGetInstrument() throws ParseException{
		try {
			CalculationModule cm = new CalculationModule("example_input.txt");
			cm.Compute();
			
			Instrument ins = cm.getInstrument(InstrumentEnum.INSTRUMENT3.getName(), "26-Jun-1998");
			System.out.println("Highest: " + ins.getValue());
			ins = cm.getInstrument(InstrumentEnum.INSTRUMENT3.getName(), "28-Oct-2011");
			System.out.println("Lowest: " + ins.getValue());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
