package de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.controller;

import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.dataaccess.ArtikelRepository;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.modell.Artikel;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.modell.Benutzer;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.EliteVerleih.service.BenutzerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArtikelerstellungController {

    @Autowired
    ArtikelRepository artikelRepository;

    @Autowired
    BenutzerManager benutzerManager;

    @GetMapping("/Erstellen")
    public String greetingForm(Long id, Model model) {

        if(id==null){
            return "redirect:/";
        }
        Benutzer benutzer = benutzerManager.getBenutzerById(id);
        model.addAttribute("artikel", new Artikel());
        model.addAttribute("benutzer", benutzer);
        return "Artikelerstellung";
    }

    @PostMapping("/Erstellen")
    public String personSubmitStart(Long id, @ModelAttribute Artikel artikel, Model model) {
        if(id==null) {
            return "redirect:/";
        }
        Benutzer benutzer = benutzerManager.getBenutzerById(id);
        artikel.setBenutzer(benutzer);
        benutzer.getArtikel().add(artikel);
        artikelRepository.save(artikel);
        model.addAttribute("artikel", artikelRepository.findAll());
        model.addAttribute("benutzer", benutzer);
        return "redirect:/Uebersicht?id=" + benutzer.getBenutzerId();
    }

}