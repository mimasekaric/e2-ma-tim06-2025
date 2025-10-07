# My Hobbit Application - Upustvo za pokretanje

Ovaj dokument sadrži sve potrebne korake za postavljanje i pokretanje projekta u Android Studiju.

## Preduslovi

- **Android Studio:**
- **JDK (Java Development Kit):** Projekat je postavljen da koristi JDK 21 (ugrađeni JetBrains Runtime).
- **Android Emulator ili Fizički uređaj:** API level 26 ili noviji.

---
## Arhitektura i Skladištenje Podataka

Aplikacija koristi hibridni pristup za čuvanje podataka:

-   **Firebase Firestore (Baza u oblaku):** Koristi se za sve podatke koji zahtijevaju sinhronizaciju u realnom vremenu ili su vezani za korisnički nalog na više uređaja.
    -   Korisnički profili, autentifikacija
    -   Prijatelji, savezi i pozivnice
    -   Specijalne misije saveza i napredak članova
    -   Chat poruke

-   **Lokalna SQLite Baza:** Koristi se za podatke koji su specifični za uređaj i ne zahtijevaju stalnu sinhronizaciju.
    -   Regularni zadaci (Tasks)
    -   Regularni bossevi (Bosses)
    -   Definicije opreme (Equipment)
    -   Kategorije (Category)

---

## Korak 1: Postavljanje Firebasea

Aplikacija zahtijeva Firebase za autentifikaciju i bazu podataka.

1.  **Kreirajte Firebase projekat:**
    -   Idite na [Firebase konzolu](https://console.firebase.google.com/) i kreirajte novi projekat.
    -   Unutar projekta, dodajte novu **Android aplikaciju**. Kao `package name` unesite:
        ```        com.example.myhobitapplication
        ```

2.  **Preuzmite i kopirajte `google-services.json`:**
    -   Preuzmite `google-services.json` datoteku koju vam Firebase ponudi nakon registracije aplikacije.
    -   Kopirajte preuzetu datoteku u `app/` direktorijum unutar vašeg projekta u Android Studiju.

3.  **Aktivirajte servise u konzoli:**
    -   **Authentication:** U lijevom meniju, idite na `Authentication` -> `Sign-in method` i omogućite **Email/Password** kao metodu prijave.
    -   **Firestore:** Idite na `Firestore Database`, kliknite `Create database` i pokrenite je u **Test mode** (dozvoljava čitanje i pisanje bez striktnih pravila).

---

## Korak 2: Ažuriranje IP adrese za notifikacije

Notifikacije za pozivnice u saveze se šalju preko lokalnog servera. Morate podesiti IP adresu vašeg računara u kodu.

1.  **Saznajte svoju lokalnu IP adresu:**
    -   **Na Windowsu:** Otvorite Command Prompt (CMD) i upišite `ipconfig`. Potražite vrijednost pod "IPv4 Address".
    -   **Na macOS/Linux:** Otvorite Terminal i upišite `ifconfig | grep "inet "`.
    -   *(Obično izgleda kao `192.168.1.X` ili `192.168.0.X`)*.

2.  **Ažurirajte kod:**
    -   Otvorite datoteku: `app/src/main/java/com/example/myhobitapplication/viewModels/AllianceViewModel.java`.
    -   Unutar metoda `sendInvite` i `respondToInvite`, pronađite URL i zamijenite IP adresu:

    ```java
    // Pronađite liniju:
    .url("http://192.168.1.130:3001/api/notifications/...")

    // Zamijenite s vašom IP adresom:
    .url("http://VAŠA_LOKALNA_IP_ADRESA:3001/api/notifications/...")
    ```
    3.  **Ažurirajte `network-security-config.xml`:**
    -   Moderne verzije Androida po defaultu blokiraju neenkriptovani HTTP saobraćaj. Ova datoteka pravi izuzetak za vašu lokalnu IP adresu.
    -   Otvorite datoteku: `app/src/main/res/xml/network-security-config.xml`.
    -   Zamijenite IP adresu unutar `<domain>` taga:

    ```xml
    <!-- Pronađite liniju: -->
    <domain includeSubdomains="true">192.168.1.130</domain>

    <!-- Zamijenite je vašom IP adresom: -->
    <domain includeSubdomains="true">VAŠA_LOKALNA_IP_ADRESA</domain>
    ```

---

## Korak 3: Pokretanje aplikacije

1.  Otvorite projekat u Android Studiju.
2.  Pričekajte da Gradle završi početnu sinhronizaciju i preuzimanje svih zavisnosti.
3.  Odaberite željeni emulator ili povežite fizički uređaj.
4.  Kliknite na **Run 'app'** (zelena strelica u alatnoj traci) da biste izgradili i pokrenuli aplikaciju.
