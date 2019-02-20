package de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.service;

import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.controller.DataSync;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.dataaccess.ArtikelRepository;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.dataaccess.AusleiheRepository;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.dataaccess.BenutzerRepository;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.dto.ReservationDTO;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.modell.Artikel;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.modell.Ausleihe;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.modell.Benutzer;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.modell.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AusleiheManager {

	DataSync sync = new DataSync();

    @Autowired
    BenutzerRepository benutzerRepo;

    @Autowired
    ArtikelRepository artikelRepo;

    @Autowired
    AusleiheRepository ausleiheRepo;

    public List<Ausleihe> getAllAusleihe() {
        return ausleiheRepo.findAll();
    }

   public Ausleihe erstelleAusleihe(Long benutzerId, Long artikelId, Calendar ausleihStartdatum, Calendar ausleihRueckgabedatum){
        Ausleihe ausleihe = new Ausleihe();
        Benutzer benutzer = benutzerRepo.findBenutzerByBenutzerId(benutzerId);
        ausleihe.setBenutzer(benutzer);
        Artikel artikel = artikelRepo.findArtikelByArtikelId(artikelId);
        ausleihe.setArtikel(artikel);
        ausleihe.setAusleihRueckgabedatum(ausleihRueckgabedatum);
        ausleihe.setAusleihStartdatum(ausleihStartdatum);
        ausleihe.setAusleihStatus(Status.ANGEFRAGT);
        ausleihe = ausleiheRepo.save(ausleihe);
        setzeAusleiheBenutzer(benutzerId,ausleihe);
        setzeAusleiheArtikel(artikelId,ausleihe);
        return ausleihe;
    }

    public void setzeAusleiheBenutzer(Long benutzerId, Ausleihe ausleihe){
        Benutzer b = benutzerRepo.findBenutzerByBenutzerId(benutzerId);
        if(b.getAusgeliehen()==null) b.setAusgeliehen(new ArrayList<Ausleihe>());
        b.getAusgeliehen().add(ausleihe);
        benutzerRepo.save(b);
    }

    public void setzeAusleiheArtikel(Long artikelId, Ausleihe ausleihe){
        Artikel a = artikelRepo.findArtikelByArtikelId(artikelId);
        if(a.getAusgeliehen()==null) a.setAusgeliehen(new ArrayList<Ausleihe>());
        a.getAusgeliehen().add(ausleihe);
        artikelRepo.save(a);
    }

    public Ausleihe getAusleiheById(Long ausleiheId){
        return ausleiheRepo.findAusleiheByAusleihId(ausleiheId);
    }

    public void bestaetigeAusleihe(Ausleihe ausleihe){
        ausleihe.setAusleihStatus(Status.BESTAETIGT);
        loescheKollidierendeAnfragen(ausleihe);
        ReservationDTO r1 = sync.kautionReserviern(ausleihe.getBenutzer().getBenutzerName(), ausleihe.getArtikel().getBenutzer().getBenutzerName(), ausleihe.getArtikel().getArtikelKaution());
        ausleihe.setReservationsId(r1.getId());
        ausleiheRepo.save(ausleihe);
    }

    private void loescheKollidierendeAnfragen(Ausleihe ausleihe) {

		Artikel artikel = ausleihe.getArtikel();
		List<Ausleihe> ausleihList = artikel.getAusgeliehen();
		for(Ausleihe a : ausleihList) {
			if(a.getAusleihId() != ausleihe.getAusleihId()) {
				if(kollidiertMitAusleihe(a, ausleihe)) {
					a.setAusleihStatus(Status.ABGELEHNT);
					ausleiheRepo.save(a);
				}
			}
		}
		List<Ausleihe> neueListe = new ArrayList<Ausleihe>();
		for(Ausleihe a: ausleihList) {
			if(!a.getAusleihStatus().equals(Status.ABGELEHNT)) {
				neueListe.add(a);
			}
		}
		artikel.setAusgeliehen(neueListe);
		artikelRepo.save(artikel);
	}

	private boolean kollidiertMitAusleihe(Ausleihe a, Ausleihe ausleihe) {
		Calendar endDatum = ausleihe.getAusleihRueckgabedatum();
    	Calendar startDatum = ausleihe.getAusleihStartdatum();
		if(a.getAusleihStartdatum().before(endDatum) && a.getAusleihStartdatum().after(startDatum)){
			return true;
		}
		if(a.getAusleihRueckgabedatum().after(startDatum) && a.getAusleihRueckgabedatum().before(endDatum)) {
			return true;
		}
		if(a.getAusleihStartdatum().equals(startDatum) || a.getAusleihStartdatum().equals(endDatum)) {
			return true;
		}
		if(a.getAusleihRueckgabedatum().equals(startDatum)|| a.getAusleihRueckgabedatum().equals(endDatum)) {
			return true;
		}
		return false;
	}

	public void bearbeiteAusleihe() {
        // Ausleihestatus schon ergänzt in Model etc?
        // Dann bearbeite in Ausleihe nur den Status und aktualisiere
    }

    public boolean isAusgeliehen (Long artikelId, Calendar startDatum, Calendar endDatum) {

        Artikel artikel = artikelRepo.findArtikelByArtikelId(artikelId);
        for(Ausleihe ausleihe : artikel.getAusgeliehen()) {
        	if(!ausleihe.getAusleihStatus().equals(Status.ANGEFRAGT) && !ausleihe.getAusleihStatus().equals(Status.BEENDET)) {

				if (startDatum.before(ausleihe.getAusleihStartdatum()) && (endDatum.after(ausleihe.getAusleihStartdatum()))) {
					return true;
				}
				if (startDatum.before(ausleihe.getAusleihRueckgabedatum()) && endDatum.after(ausleihe.getAusleihRueckgabedatum())) {
					return true;
				}
				if (startDatum.equals(ausleihe.getAusleihStartdatum()) || endDatum.equals(ausleihe.getAusleihRueckgabedatum()) || startDatum.equals(ausleihe.getAusleihRueckgabedatum()) || endDatum.equals(ausleihe.getAusleihStartdatum())) {
					return true;
				}
				if(startDatum.after(ausleihe.getAusleihStartdatum()) && startDatum.before(ausleihe.getAusleihRueckgabedatum())){
					return true;
				}
			}
        }
        return false;
    }

	public void lehneAusleiheAb(Ausleihe ausleihe) {
		ausleihe.setAusleihStatus(Status.ABGELEHNT);
		Artikel artikel = ausleihe.getArtikel();
		List<Ausleihe> list = artikel.getAusgeliehen();
		list.remove(ausleihe);
		artikel.setAusgeliehen(list);
		artikelRepo.save(artikel);
		ausleiheRepo.save(ausleihe);
	}

	public void zurueckGeben(Ausleihe ausleihe) {
		Artikel artikel = ausleihe.getArtikel();
		Calendar start = ausleihe.getAusleihStartdatum();

		Date date = new Date();
		int tage = getAnzahlTage(ausleihe);
		int kosten = ausleihe.getArtikel().getArtikelTarif() * tage;
		sync.ueberweisen(ausleihe.getBenutzer().getBenutzerName(), ausleihe.getArtikel().getBenutzer().getBenutzerName(), kosten);
		System.out.println(tage);

		List<Ausleihe> list = artikel.getAusgeliehen();
		int i = list.indexOf(ausleihe);
		ausleihe.setAusleihStatus(Status.ABGEGEBEN);
		list.set(i, ausleihe);
		artikel.setAusgeliehen(list);
		artikelRepo.save(artikel);
		ausleiheRepo.save(ausleihe);
	}

	private int getAnzahlTage(Ausleihe ausleihe) {
    	int ergebnis = 0;
    	Calendar start = ausleihe.getAusleihStartdatum();
		Date date=new Date(System.currentTimeMillis());
		Calendar dateCal = new GregorianCalendar();
		dateCal.setTime(date);
    	if(date.equals(start)){
    		return 1;
		}
    	if(dateCal.after(start)){
    		long milli = date.getTime() - start.getTimeInMillis();

			ergebnis = (int) TimeUnit.DAYS.convert(milli, TimeUnit.MILLISECONDS) + 1;
		}
    	return ergebnis;
	}

	public void rueckgabeAkzeptieren(Ausleihe ausleihe) {
		Artikel artikel = ausleihe.getArtikel();
		List<Ausleihe> list = artikel.getAusgeliehen();
		int i = list.indexOf(ausleihe);
		ausleihe.setAusleihStatus(Status.BEENDET);
		list.set(i, ausleihe);
		artikel.setAusgeliehen(list);
		artikelRepo.save(artikel);
		ausleiheRepo.save(ausleihe);
    }

    public List<Ausleihe> getKonflike(List<Ausleihe> liste){
        List<Ausleihe> konflikeAusleihe = new ArrayList<>();

        for( Ausleihe a: liste ) {
            if (a.getAusleihStatus()== Status.KONFLIKT){
                konflikeAusleihe.add(a);
            }
        }
        return konflikeAusleihe;
    }

    public void setzeSatusAusleihe(Ausleihe ausleihe, String name){
        ausleihe.setAusleihStatus(Status.valueOf(name));
        ausleiheRepo.save(ausleihe);
    }

	public void konfliktAusleihe(Long ausleihId) {
		Ausleihe ausleihe = getAusleiheById(ausleihId);
		Artikel artikel = ausleihe.getArtikel();
		List<Ausleihe> list = artikel.getAusgeliehen();
		int i = list.indexOf(ausleihe);
		ausleihe.setAusleihStatus(Status.KONFLIKT);
		list.set(i, ausleihe);
		artikel.setAusgeliehen(list);
		artikelRepo.save(artikel);
		ausleiheRepo.save(ausleihe);

	}
}
