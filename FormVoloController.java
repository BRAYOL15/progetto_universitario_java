package com.flightbooking.util;

import com.flightbooking.model.Utente;

/**
 * Gestore della sessione corrente. Singleton.
 * Mantiene il riferimento all'utente autenticato durante l'uso dell'app.
 */
public class SessionManager {

    private static SessionManager instance;
    private Utente utenteCorrente;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public Utente getUtenteCorrente() { return utenteCorrente; }

    public void setUtenteCorrente(Utente utente) { this.utenteCorrente = utente; }

    public boolean isLoggato() { return utenteCorrente != null; }

    public boolean isAdmin() {
        return isLoggato() && utenteCorrente.isAdmin();
    }

    public void logout() { utenteCorrente = null; }
}
