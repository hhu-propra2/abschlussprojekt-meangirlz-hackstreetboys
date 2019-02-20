package de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.service;


import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.dto.AccountDto;
import de.hhu.abschlussprojektmeangirlzhackstreetboys.eliteverleih.dto.ReservationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PropayManager {

    final String url = "http://localhost:8888/";
    RestTemplate rt = new RestTemplate();

    public AccountDto getAccount(String benutzername) {

        ResponseEntity<AccountDto> result = rt.getForEntity(url + "account/" + benutzername, AccountDto.class);
        AccountDto acc = result.getBody();
        return acc;
    }

    public void guthabenAufladen(String benutzername, int anzahl) {

        ResponseEntity<AccountDto> result = rt.postForEntity(url
            + "account/"
            + benutzername
            + "?amount="
            + anzahl, null, AccountDto.class);
        AccountDto acc = result.getBody();

    }

    public boolean ueberweisen(String vonBenutzername, String zuBenutzername, int anzahl) {

        String urlueberweisenUrl = url
            + "account/"
            + vonBenutzername
            + "/transfer/"
            + zuBenutzername
            + "?amount="
            + anzahl;
        try {
            rt.postForEntity(urlueberweisenUrl, null, AccountDto.class);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public ReservationDto kautionReserviern(String vonBenutzername, String zuBenutzername, int anzahl) {

        String kautionUrl = url + "reservation/reserve/" + vonBenutzername + "/" + zuBenutzername + "?amount=" + anzahl;
        try {
            ResponseEntity<ReservationDto> result = rt.postForEntity(kautionUrl, null, ReservationDto.class);
            ReservationDto res = result.getBody();
            return res;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public boolean kautionEinziehen(String benutzername, int reservationsid) {

        String kautionUrl = url + "reservation/punish/" + benutzername + "?reservationId=" + reservationsid;
        return kautionManager(kautionUrl);
    }

    public boolean kautionFreigeben(String benutzername, int reservationsid) {

        String kautionUrl = url + "reservation/release/" + benutzername + "?reservationId=" + reservationsid;
        return kautionManager(kautionUrl);
    }

    private boolean kautionManager(String kautionUrl) {
        try {
            ResponseEntity<AccountDto> result = rt.postForEntity(kautionUrl, null, AccountDto.class);
            AccountDto acc = result.getBody();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
