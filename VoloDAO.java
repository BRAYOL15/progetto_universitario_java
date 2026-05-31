package com.flightbooking.model;

/**
 * Modello che rappresenta un utente del sistema.
 * Contiene i dati anagrafici e le credenziali di accesso.
 */
public class Utente {

    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String passwordHash;
    private String telefono;
    private String dataNascita;
    private String codiceFiscale;
    private String ruolo; // "ADMIN" o "UTENTE"

    public Utente() {}

    public Utente(String nome, String cognome, String email,
                  String passwordHash, String telefono,
                  String dataNascita, String codiceFiscale) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
        this.codiceFiscale = codiceFiscale;
        this.ruolo = "UTENTE";
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getNomeCompleto() { return nome + " " + cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDataNascita() { return dataNascita; }
    public void setDataNascita(String dataNascita) { this.dataNascita = dataNascita; }

    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String cf) { this.codiceFiscale = cf; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    public boolean isAdmin() { return "ADMIN".equals(ruolo); }

    @Override
    public String toString() {
        return "Utente{id=" + id + ", nome='" + getNomeCompleto() + "', email='" + email + "'}";
    }
}
