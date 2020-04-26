package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		List<Rilevamento> rilevamenti= new ArrayList <Rilevamento>();
		
		String s1="2013-"+mese+"-01";
		String s2="2013-"+mese+"-31";
		
		Date data1=Date.valueOf(s1);
		Date data2=Date.valueOf(s2);
		
		
		String sql="SELECT localita, DATA, umidita FROM situazione "
				   + "WHERE DATA>=? AND DATA<=? and localita=?";
		
		try {
			Connection conn= ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDate(1, data1);
			st.setDate(2, data2);
			st.setString(3, localita);
			
			ResultSet rs= st.executeQuery();
			
			while(rs.next()) {
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return rilevamenti;
	}
	
	//CREATO PEPPE
	public List<Citta> getAllCitta(){
		String sql = "SELECT DISTINCT Localita FROM situazione ORDER BY Localita ASC";
		List<Citta>leCitta=new ArrayList<>();
		
		try {
			Connection conn=ConnectDB.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			ResultSet rs=st.executeQuery();
			
			while(rs.next()) {
				leCitta.add(new Citta(rs.getString("Localita")));
			}
			conn.close();
			return leCitta;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

/*
 * POTREI CALCOLARE LA MEDIA CON UNA SEMPLICE QUERY SQL AL VOLO:
 * 
 * "SELECT  AVG(Umidita) AS u  FROM situazione WHERE Localita='Milano' AND MONTH(data)=2"
 */
	
	
	
	
	
	/*public double getAvgRilevamentiLocalitaMese(int mese, String localita){
		List<Rilevamento> rilevamenti= new ArrayList <Rilevamento>();
		double result=0.0;
		int count=0;
		
		String s1="2013-"+mese+"-01";
		String s2="2013-"+mese+"-31";
		Date data1=Date.valueOf(s1);
		Date data2=Date.valueOf(s2);
		
		
		String sql="SELECT localita, DATA, umidita FROM situazione "
				   + "WHERE DATA>=? AND DATA<=? AND localita=?";
		
		try {
			Connection conn= ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDate(1, data1);
			st.setDate(2, data2);
			st.setString(3, localita);
			
			ResultSet rs= st.executeQuery();
			
			while(rs.next()) {
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
				count++;
			}
			
			for(Rilevamento r: rilevamenti) {
				result += r.getUmidita();
			}
			result= result/count;
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return result;
	}*/
}
