package de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.service;

import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.dataaccess.ArtikelRepository;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.dataaccess.BenutzerRepository;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.modell.Artikel;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.modell.Ausleihe;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.modell.Benutzer;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.modell.Status;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Import( {ArtikelManager.class})
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("dev")
public class ArtikelManagerTest {

    @Autowired
    ArtikelManager artikelM;

    @Autowired
    BenutzerRepository benutzerRepo;

    @Autowired
    ArtikelRepository artikelRepo;

    @Rollback
    @Test
    public void erstelleVerleihen_EqualName() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);
        Long bId = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();
        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        artikelM.erstelleVerleihen(bId, a0);
        Assertions.assertThat(artikelM.getAllArtikel().get(0).getArtikelName()).isEqualTo("Hammer");
        assertEquals(false, artikelM.getArtikelById(a0.getArtikelId()).getZuVerkaufen());
    }

    @Rollback
    @Test
    public void bearbeiteArtikel_AndereName() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);
        Long bId = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();
        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        artikelM.erstelleVerleihen(bId, a0);
        Artikel a1 = new Artikel();
        a1.setArtikelBeschreibung("beschreibung");
        a1.setArtikelKaution(3);
        a1.setArtikelName("Hammer2");
        a1.setArtikelOrt("Werkstatt");
        a1.setArtikelTarif(1);
        artikelM.bearbeiteArtikel(artikelM.getAllArtikel().get(0).getArtikelId(), a1);
        Assertions.assertThat(artikelM.getAllArtikel().get(0).getArtikelName()).isEqualTo("Hammer2");
    }

    @Rollback
    @Test
    public void bearbeiteArtikel_AndereBeschreibung() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);
        Long bId = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();
        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        artikelM.erstelleVerleihen(bId, a0);
        Artikel a1 = new Artikel();
        a1.setArtikelBeschreibung("test");
        a1.setArtikelKaution(3);
        a1.setArtikelName("Hammer2");
        a1.setArtikelOrt("Werkstatt");
        a1.setArtikelTarif(1);
        artikelM.bearbeiteArtikel(artikelM.getAllArtikel().get(0).getArtikelId(), a1);
        Assertions.assertThat(artikelM.getAllArtikel().get(0).getArtikelBeschreibung()).isEqualTo("test");
    }

    @Rollback
    @Test
    public void loescheArtikel_nichtAusgeliehen() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);
        Long bId = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();
        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        a0.setAusgeliehen(new ArrayList<Ausleihe>());
        artikelM.erstelleVerleihen(bId, a0);
        Artikel a1 = new Artikel();
        a1.setArtikelBeschreibung("test");
        a1.setArtikelKaution(3);
        a1.setArtikelName("Hammer2");
        a1.setArtikelOrt("Werkstatt");
        a1.setArtikelTarif(1);
        a1.setAusgeliehen(new ArrayList<Ausleihe>());
        artikelM.erstelleVerleihen(bId, a1);
        assertEquals(2, artikelM.getAllArtikel().size());
        artikelM.loescheArtikel(artikelM.getAllArtikel().get(0).getArtikelId());
        assertEquals(1, artikelM.getAllArtikel().size());
    }

    @Rollback
    @Test
    public void loescheArtikel_IstAusgeliehen() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);
        Benutzer b1 = new Benutzer();
        b1.setBenutzerEmail("test2@yahoo");
        b1.setBenutzerName("test2");
        b1.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b1);
        Long b0Id = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();
        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        a0.setAusgeliehen(new ArrayList<Ausleihe>());
        artikelM.erstelleVerleihen(b0Id, a0);
        Artikel a1 = new Artikel();
        a1.setArtikelBeschreibung("test");
        a1.setArtikelKaution(3);
        a1.setArtikelName("Hammer2");
        a1.setArtikelOrt("Werkstatt");
        a1.setArtikelTarif(1);

        Ausleihe ausleihe = new Ausleihe();
        ausleihe.setAusleihStatus(Status.BESTAETIGT);
        ausleihe.setArtikel(a1);
        ausleihe.setBenutzer(b1);
        List<Ausleihe> ausleiheList = new ArrayList<Ausleihe>();
        ausleiheList.add(ausleihe);
        a1.setAusgeliehen(ausleiheList);
        artikelM.erstelleVerleihen(b0Id, a1);

        assertEquals(2, artikelM.getAllArtikel().size());
        artikelM.loescheArtikel(a1.getArtikelId());
        assertEquals(2, artikelM.getAllArtikel().size());
    }

    @Rollback
    @Test
    public void loescheArtikel_IstAbgegeben() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);
        Benutzer b1 = new Benutzer();
        b1.setBenutzerEmail("test2@yahoo");
        b1.setBenutzerName("test2");
        b1.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b1);
        Long b0Id = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();
        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        a0.setAusgeliehen(new ArrayList<Ausleihe>());
        artikelM.erstelleVerleihen(b0Id, a0);
        Artikel a1 = new Artikel();
        a1.setArtikelBeschreibung("test");
        a1.setArtikelKaution(3);
        a1.setArtikelName("Hammer2");
        a1.setArtikelOrt("Werkstatt");
        a1.setArtikelTarif(1);

        Ausleihe ausleihe = new Ausleihe();
        ausleihe.setAusleihStatus(Status.ABGEGEBEN);
        ausleihe.setArtikel(a1);
        ausleihe.setBenutzer(b1);
        List<Ausleihe> ausleiheList = new ArrayList<Ausleihe>();
        ausleiheList.add(ausleihe);
        a1.setAusgeliehen(ausleiheList);
        artikelM.erstelleVerleihen(b0Id, a1);

        assertEquals(2, artikelM.getAllArtikel().size());
        artikelM.loescheArtikel(a1.getArtikelId());
        assertEquals(1, artikelM.getAllArtikel().size());
    }


    @Rollback
    @Test
    public void findeArtikelBeiName() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);

        Benutzer b1 = new Benutzer();
        b1.setBenutzerEmail("test2@yahoo");
        b1.setBenutzerName("test2");
        b1.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b1);

        Long b0Id = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();

        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer3");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        a0.setAusgeliehen(new ArrayList<Ausleihe>());
        artikelM.erstelleVerleihen(b0Id, a0);

        Artikel a1 = new Artikel();
        a1.setArtikelBeschreibung("test");
        a1.setArtikelKaution(3);
        a1.setArtikelName("Hammer2");
        a1.setArtikelOrt("Werkstatt");
        a1.setArtikelTarif(1);
        a1.setAusgeliehen(new ArrayList<Ausleihe>());
        artikelM.erstelleVerleihen(b0Id, a1);

        Artikel a2 = new Artikel();
        a2.setArtikelBeschreibung("test");
        a2.setArtikelKaution(3);
        a2.setArtikelName("Schraubenzieher");
        a2.setArtikelOrt("Werkstatt");
        a2.setArtikelTarif(1);
        a2.setAusgeliehen(new ArrayList<Ausleihe>());
        artikelM.erstelleVerleihen(b0Id, a2);

        assertEquals(3, artikelM.getAllArtikel().size());
        assertEquals(2, artikelM.getArtikelListSortByName("Hammer").size());
        assertEquals(a1, artikelM.getArtikelListSortByName("Hammer").get(0));
    }

    @Rollback
    @Test
    public void erstelleVerkauf() {
        Benutzer b0 = new Benutzer();
        b0.setBenutzerEmail("test@yahoo");
        b0.setBenutzerName("test");
        b0.setArtikel(new ArrayList<Artikel>());
        benutzerRepo.save(b0);
        Long bId = benutzerRepo.findBenutzerByBenutzerName("test").get().getBenutzerId();
        Artikel a0 = new Artikel();
        a0.setArtikelBeschreibung("beschreibung");
        a0.setArtikelKaution(3);
        a0.setArtikelName("Hammer");
        a0.setArtikelOrt("Werkstatt");
        a0.setArtikelTarif(1);
        artikelM.erstelleVerkauf(bId, a0);
        assertEquals("Hammer", artikelM.getAllArtikel().get(0).getArtikelName());
        assertEquals(true, artikelM.getArtikelById(a0.getArtikelId()).getZuVerkaufen());
    }

    @Test
    public void umlauteErsetzen_Test(){
        Assertions.assertThat(artikelM.ersetzeUmlaute("hällo, ik benß dür Ströber Michäl"))
            .isEqualTo("haello, ik benss duer Stroeber Michael");
    }

    @Test
    public void umlauteSchonErsetzt_Test(){
        Assertions.assertThat(artikelM.ersetzeUmlaute("haello, ik benss duer Stroeber Michael"))
                .isEqualTo("haello, ik benss duer Stroeber Michael");
    }

    @Rollback
    @Test
    public void setzteArtikelOrtAttribute_Test(){
        Artikel artikel = new Artikel();
        artikel.setArtikelName("Test");
        artikel.setArtikelBeschreibung("Testbeschreibung");
        artikel.setArtikelBildUrl("http://image.jpg");
        artikel.setArtikelOrt("Düsseldorf, Karolingerstraße");

        Artikel artikel2 = new Artikel();
        artikel2.setArtikelName("Test2");
        artikel2.setArtikelBeschreibung("Testbeschreibung");
        artikel2.setArtikelBildUrl("http://image.jpg");
        artikel2.setArtikelOrt("Duesseldorf, Karolingerstrasse");

        artikel.setArtikelPreis(0);
        artikel.setZuVerkaufen(false);
        artikelM.setzteArtikelOrtAttribute(artikel, artikel.getArtikelOrt());
        artikel = artikelRepo.save(artikel);
        artikel2.setArtikelPreis(0);
        artikel2.setZuVerkaufen(false);
        artikelM.setzteArtikelOrtAttribute(artikel2, artikel2.getArtikelOrt());
        artikel = artikelRepo.save(artikel2);

        Assertions.assertThat(artikel.getArtikelOrtX()).isEqualTo(artikel2.getArtikelOrtX());
        Assertions.assertThat(artikel.getArtikelOrtY()).isEqualTo(artikel2.getArtikelOrtY());
    }
}