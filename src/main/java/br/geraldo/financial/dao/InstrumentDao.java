package br.geraldo.financial.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.geraldo.financial.model.Instrument;

public class InstrumentDao {
	
	private Connection con;
	
	public InstrumentDao(Connection con){
		this.con = con;
	}
	
	public void createTable() throws SQLException{
		
		String sql = "CREATE TABLE INSTRUMENT_PRICE_MODIFIER(id int primary key, name varchar(20), multiplier double);";
		
		Statement st = con.createStatement();
		st.execute(sql);

	}

	public void addInstrument(int id, String name, double multiplier) throws SQLException{
		String sql = "INSERT INTO INSTRUMENT_PRICE_MODIFIER(id,name,multiplier) " +
				"values (?,?,?)";
		
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, id);
		st.setString(2, name);
		st.setDouble(3, multiplier);
		
		st.execute();
	}
	
	
	
	public void alterInstrument(Instrument ins) throws SQLException{
		String sql = "UPDATE INSTRUMENT_PRICE_MODIFIER SET MULTIPLIER=? WHERE ID=?";
		PreparedStatement st = con.prepareStatement(sql);
		st.setDouble(1, ins.getValue());
		st.setInt(2, ins.getId());
		
		st.execute();
	}
	

	public List<Instrument> getInstruments() throws SQLException{
		List<Instrument> list = new ArrayList<Instrument>();
		String sql = "SELECT * FROM INSTRUMENT_PRICE_MODIFIER";
		
		ResultSet rs = con.prepareStatement(sql).executeQuery();
		
		while(rs.next()){
			Instrument ins = new Instrument();
			ins.setId(rs.getInt(1));
			ins.setName(rs.getString(2));
			ins.setValue(rs.getDouble(3));
			list.add(ins);
		}
		
		return list;
	}

	
	public Instrument getInstrumentByName(String name) throws SQLException{
		
		String sql = "SELECT * FROM INSTRUMENT_PRICE_MODIFIER WHERE NAME = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, name);
		
		ResultSet rs = ps.executeQuery();
		Instrument ins = null;
		
		
		while(rs.next()){
			ins = new Instrument();
			ins.setId(rs.getInt(1));
			ins.setName(rs.getString(2));
			ins.setValue(rs.getDouble(3));
		}
		
		
		return ins;
		
	}

}
