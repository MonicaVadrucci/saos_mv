# Sistema di Gestione per le Prenotazioni di un Ristorante

## Introduzione
Questo documento descrive il sistema di gestione delle prenotazioni di un ristorante. Il personale del ristorante ha la possibilità di gestire le prenotazioni e visualizzare i dettagli dei clienti. 

## Architettura

L'applicazione Ristorante è sviluppata utilizzando Spring Boot, un framework Java che semplifica lo sviluppo di applicazioni stand-alone. L'architettura dell'applicazione è basata su un modello MVC (Model-View-Controller) e utilizza OAuth2 per l'autenticazione. 

L'applicazione Ristorante utilizza OpenID Connect per autenticare gli utenti tramite Google. Questo permette agli utenti di accedere utilizzando i loro account Google esistenti, senza bisogno di creare nuovi account specifici per l'applicazione.
 OpenID Connect (OIDC) è un protocollo di autenticazione che si basa su OAuth 2.0. Fornisce un livello di autenticazione che consente alle applicazioni di verificare l'identità degli utenti basata sull'autenticazione eseguita da un server di autorizzazione

### Perché OpenID Connect?
OpenID Connect offre numerosi vantaggi:

* **Semplicità**: Fornisce un modo semplice per autenticare gli utenti senza la necessità di gestire credenziali direttamente. 

* **Sicurezza**: Utilizza il protocollo OAuth 2.0 per garantire che le interazioni siano sicure. 

* **Interoperabilità**: Supportato da numerosi provider di identità (IdP) come Google, Yahoo, Microsoft, ecc. 

* **Scalabilità**: Può essere utilizzato per autenticare milioni di utenti senza compromettere le prestazioni.

## Tecnologie 

Le tecnologie utilizzate per l'implementazione sono:

* Linguaggio: Java (JDK 17.0.11)

* Framework: Spring Boot 3.3.0

* Application server: Apache Tomcat 

* Thymeleaf

* JavaScript

* CSS

## Funzionalità

* Registrazione e login tramite Google OAuth2

* Visualizzazione del calendario per la gestione delle prenotazioni

* Gestione delle prenotazioni (creazione e visualizzazione)

* Dashboard amministrativa per la gestione delle prenotazioni


## Struttura del Progetto

Il progetto è strutturato come segue:

 * **com.example.ristorante**: Contiene il codice sorgente dell'applicazione.
   
   * **HomeController**: Gestisce le richieste HTTP e restituisce le viste.

   * **SecurityConfig**: Configura la sicurezza dell'applicazione.

   * **RistoranteApplication**: Classe principale che avvia l'applicazione Spring Boot.

 * **resources/templates**: Contiene i file HTML di Thymeleaf.
   
   * **index.html**: Pagina di benvenuto dell'applicazione.

   * **admin.html**: Pagina del dashboard amministrativo.

  * **resources/application.yml**: File di configurazione che include le proprietà per OAuth2 e altri parametri di configurazione.

  * **pom.xml**: File di configurazione di Maven che definisce le dipendenze del progetto.
 

## Configurazione

Per supportare OpenID Connect, il progetto utilizza le seguenti dipendenze nel file ***pom.xml***  :

```javascript 
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.thymeleaf.extras</groupId>
        <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    </dependency>
</dependencies> 
```

La configurazione specifica per Google come provider OpenID Connect è impostata nel file ***application.yml***: 
 
 ```javascript
 spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: 202040582617-n60rsogb0m87fldiqrgmsfo3or98tlpm.apps.googleusercontent.com
            client-secret: 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
            scope: profile, email
        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
            user-name-attribute: sub
``` 
La classe ***SecurityConfig*** configura l'autenticazione utilizzando OAuth2 e OpenID Connect: 

```javascript
package com.example.ristorante;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests -> 
                authorizeRequests
                    .requestMatchers("/", "/home").permitAll() 
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login -> 
                oauth2Login
                    .loginPage("/oauth2/authorization/google")
            )
            .logout(logout -> 
                logout
                    .logoutSuccessUrl("/") 
                    .permitAll()
            );     
        return http.build();
    }
}
```

La classe ***RistoranteApplication*** è la classe principale che avvia l'applicazione Spring Boot: 

```javascript 
package com.example.ristorante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RistoranteApplication {
    public static void main(String[] args) {
        SpringApplication.run(RistoranteApplication.class, args);
    }
}
```

***HomeController*** gestisce le richieste per le pagine principali:

```javascript
package com.example.ristorante;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal != null) {
            model.addAttribute("name", principal.getAttribute("name"));
        }
        return "admin";
    }
}
```

### File HTML con Thymeleaf

**index.html** è la pagina di benvenuto che offre l'opzione di login tramite Google o Yahoo:

```javascript
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Home</title>
    <!-- CSS omitted for brevity -->
</head>
<body>
    <div class="left-panel">
        <div class="content">
            <h1>Welcome to the Restaurant Management System</h1>
            <p>GO TO ADMIN DASHBOARD</p>
            <button onclick="window.location.href='/admin/dashboard'"><i class="fab fa-google"></i> Continue with Google</button>
            <button onclick=""><i class="fab fa-yahoo"></i> Continue with Yahoo!</button>
        </div>
    </div>
    <div class="right-panel"></div>
</body>
</html>
``` 


***admin.html*** gestisce la dashboard amministrativa con un calendario per la gestione delle prenotazioni:

```javascript
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Admin Dashboard</title>
    <!-- CSS omitted for brevity -->
</head>
<body>
    <div class="left-panel">
        <h1>Admin Dashboard</h1>
        <div id="calendar" class="calendar"></div>
        <div class="booking-details" style="display: none;">
            <h2>Prenotazioni:</h2>
            <ul id="bookingList"></ul>
        </div>
        <button onclick="window.location.href='/'"><i class="fab fa-google"></i> -> LOGOUT </button>
    </div>
    <div class="right-panel"></div>
    <!-- JavaScript omitted for brevity -->
</body>
</html>
``` 



## Funzionamento
1. **Accesso alla Home Page**: L'utente accede alla home page dell'applicazione. 

2. **Inizio del Flusso di Autenticazione**: L'utente clicca sul pulsante "Continue with Google", che avvia il flusso di autenticazione OAuth2/OpenID Connect. 

3. **Redirezione al Provider**: L'utente viene reindirizzato alla pagina di login di Google. 

4. **Autenticazione presso Google**: L'utente inserisce le proprie credenziali Google e si autentica. 

5. **Redirezione di Ritorno**: Una volta autenticato, Google redirige l'utente all'applicazione con un token di autenticazione. 

6. **Recupero delle Informazioni Utente**: L'applicazione utilizza il token per recuperare le informazioni dell'utente (come nome e email) dal provider. 

7. **Accesso alla Dashboard**: L'utente autenticato viene reindirizzato alla dashboard amministrativa dove può gestire le prenotazioni.

## Conclusione

L'uso di OpenID Connect nell'applicazione Ristorante offre rappresenta una soluzione moderna ed efficiente per gestire l'autenticazione degli utenti.  
Elimina la necessità per l'applicazione di gestire direttamente le credenziali degli utenti, affidando tale responsabilità a provider di identità consolidati e sicuri come Google.  
Inoltre,  gli utenti possono accedere all'applicazione utilizzando i loro account Google esistenti, senza dover creare nuovi account o ricordare ulteriori credenziali. Questo riduce le barriere all'accesso e migliora l'esperienza utente complessiva. 











