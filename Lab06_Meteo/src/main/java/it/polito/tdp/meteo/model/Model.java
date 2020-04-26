package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	int passo=1;
	MeteoDAO dao;
	List<Citta> leCitta;
	List<Citta> bestSequenza;
	
	public Model() {
		this.dao=new MeteoDAO();
		leCitta=dao.getAllCitta();
	}

	public List<Citta> getLeCitta(){
		return leCitta;
	}
	
	// of course you can change the String output with what you think works best
	public double getUmiditaMedia(int mese, String localita) {
		double result=0.0;
		result = this.calcolaAvgUmiditaCitta(dao.getAllRilevamentiLocalitaMese(mese, localita));
		return result;
	}
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese) {
		
		List<Citta>parziale=new ArrayList<>();
		
		this.bestSequenza=null;
		
		cerca(parziale, 0, mese);
		
		return bestSequenza;
	}
	
	//RICORSIVA
	private void cerca(List<Citta> parziale, int livello, int mese){
		
		//CASO TERMINALE
		if(livello == NUMERO_GIORNI_TOTALI) {
			
			double costo=calcolaCosto(parziale);
			
			if(bestSequenza==null) {
				bestSequenza=new ArrayList<>(parziale);
			}
			
			if(costo<calcolaCosto(bestSequenza)) {
				bestSequenza.clear();
				
				for(Citta aggiungi: parziale)
					bestSequenza.add(aggiungi);
			}
				
		}else {
			
			for(Citta prova: leCitta) {
				prova.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, prova.getNome()));
				//System.out.println(prova.getRilevamenti());
				if(aggiuntaValida(prova, parziale)) {
					parziale.add(prova);
					cerca(parziale, livello+1, mese);
					parziale.remove(parziale.size()-1);
				}
			}
		}
	}

	
	private double calcolaCosto(List<Citta> parziale) {
		double costo=0;
		
		for(int giorno=1; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			//IN QUALE CITTA MI TROVO QUEL GIORNO
			Citta c= parziale.get(giorno-1);
			//CHE UMIDITA' HO RILEVATO
			costo += c.getRilevamenti().get(giorno-1).getUmidita();
		}
		
		for(int giorno=2; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			if(!parziale.get(giorno-2).equals(parziale.get(giorno-1)))
				costo += COST;
		}
		
		return costo;
	}

	private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {
		int conta=0;
		
		for(Citta precedente: parziale) {
			if(precedente.equals(prova))
				conta++;
		}
		
		if(conta>=NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		if(parziale.size()==0)
			return true;
		
		//SE SONO NEL SECONDO O TERZO GIORNO TORNO VERO O FALSO IN BASE IL RISULTATO DELL'EQUALS RISPETTANDO 
		//IL VINCOLO CHE DEVE STARE ALMENO 3 GIORNI SU UNA CITTA' SCELTA
		if(parziale.size()==1 || parziale.size()==2) 
			return(  parziale.get(parziale.size()-1).equals(prova)  );
		
		//SE STO PIU' DI TRE GIORNI POSSO COMUNQUE STARCI
		if(parziale.get(parziale.size()-1).equals(prova))
			return true;
		
		//POSSO CAMBIARE CITTA' SOLO SE SONO STATO ALMENO 3 GIORNI SULLA PRECENDETE CITTA'
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2))  && parziale.get((parziale.size()-2)).equals(parziale.get(parziale.size()-3)))
			return true;
		
		return false;
	}

	public double calcolaAvgUmiditaCitta(List<Rilevamento> rilevamenti) {
		int somma=0;
		int count=0;
		
		for(Rilevamento r: rilevamenti) {
			somma += r.getUmidita();
			count++;
		}
		
		return (somma/count) ;
	}
}
