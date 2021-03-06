package de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.controller;

import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.modell.Ausleihe;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.modell.Benutzer;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.modell.Status;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class KonfliktController {

    BenutzerManager benutzerManager;

    AusleiheManager ausleiheManager;

    TransaktionManager transaktionManager;

    PropayManager propayManager;

    /**
     * Ist fuer das Testen der Klassen verantwortlich.
     * @param ausleiheManager Zugriff auf Repository.
     * @param benutzerManager Zugriff auf Repository.
     * @param propayManager Zugriff auf Propray
     */
    @Autowired
    public KonfliktController(AusleiheManager ausleiheManager,
                              BenutzerManager benutzerManager,
                              TransaktionManager transaktionManager,
                              PropayManager propayManager) {
        this.ausleiheManager = ausleiheManager;
        this.benutzerManager = benutzerManager;
        this.transaktionManager = transaktionManager;
        this.propayManager = propayManager;
    }

    /**
     * Zeigt Konfliktseite an, laedt Attribute in HTML.
     *
     * @param model   Datencontainer fuer View.
     * @param account aktueller Benutzer.
     * @return Mapping auf Konfliktloesung.
     */
    @GetMapping("/Konfliktloesung")
    public String konfliktloesungAnzeigen(Model model, Principal account) {
        List<Ausleihe> ausleihe = ausleiheManager.getAllAusleihe();
        model.addAttribute("konflikt", ausleiheManager.getKonflike(ausleihe));
        model.addAttribute("benutzer", benutzerManager.findBenutzerByName(account.getName()));
        return "Konfliktloesung";
    }

    /**
     * Bearbeitet Kaution bei einem Konflikt, loescht dann Buchung.
     *
     * @param name      ButtonName.
     * @param anfrage   zu verwaltende Anfrage.
     * @param ausleihId Id der Anfrage.
     * @return Weiterleitung auf Konfliktloesung
     */
    @PostMapping("/Konfliktloesung")
    public String konfliktLoesen(@RequestParam(value = "submitButton") String name,
                                 @ModelAttribute Ausleihe anfrage, Long ausleihId) {

        Ausleihe ausleihe = ausleiheManager.getAusleiheById(ausleihId);
        int code = 0;
        if (name.equals("Buchung Verleihender")) {
            code = propayManager.kautionEinziehen(ausleihe.getBenutzer().getBenutzerName(),
                    ausleihe.getReservationsId());
        }
        if (name.equals("Buchung Ausleihender")) {
            code = propayManager.kautionFreigeben(ausleihe.getBenutzer().getBenutzerName(),
                    ausleihe.getReservationsId());
        }
        if (code != 200) {
            return "ErrorPropaySupport";
        }
        transaktionManager.erstelleTransaktionKaution(ausleihId);
        ausleiheManager.bearbeiteAusleihe(ausleihId, Status.BEENDET);

        return "redirect:/Konfliktloesung";
    }
}
