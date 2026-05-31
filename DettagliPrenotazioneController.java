package com.flightbooking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modello che rappresenta una prenotazione effettuata da un utente.
 * Collega un utente a un volo con dettagli su classe, prezzo e passeggeri.
 */
public class Prenotazione {

    public enum Stato { CONFERMATA, IN_ATTESA, CANCELLATA, CHECK_IN_FATTO }
    public enum ClasseVolo { ECONOMY, BUSINESS, FIRST_CLASS }

    private int id;
    private String codicePrenotazione;
    private int idUtente;
    private int idVolo;
    private Utente utente;
    private Volo volo;
    private ClasseVolo classeVolo;
    private int numeroPasseggeri;
    private double prezzoTotale;
    private Stato stato;
    private LocalDateTime dataPrenotazione;
    private String noteSpeciali;
    private boolean bagaglioExtra;
    private boolean assicurazione;
    private String numeroPosto;

    public Prenotazione() {
        this.stato = Stato.IN_ATTESA;
        this.dataPrenotazione = LocalDateTime.now();
        this.codicePrenotazione = generaCodice();
    }

    private String generaCodice() {
        return "SKY" + System.currentTimeMillis() % 100000;
    }

    // ─── Metodi di utilità ───────────────────────────────────────────────────

    public String getDataPrenotazioneFormatted() {
        if (dataPrenotazione == null) return "--";
        return dataPrenotazione.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getStatoLabel() {
        return switch (stato) {
            case CONFERMATA     -> "✅ Confermata";
            case IN_ATTESA      -> "⏳ In Attesa";
            case CANCELLATA     -> "❌ Cancellata";
            case CHECK_IN_FATTO -> "🛫 Check-in Effettuato";
        };
    }

    public String getClasseLabel() {
        return switch (classeVolo) {
            case ECONOMY    -> "Economy";
            case BUSINESS   -> "Business";
            case FIRST_CLASS -> "First Class";
        };
    }

    public double calcolaPrezzo() {
        if (volo == null) return 0;
        double prezzoBase = switch (classeVolo) {
            case ECONOMY    -> volo.getPrezzoEconomy();
            case BUSINESS   -> volo.getPrezzoBusiness();
            case FIRST_CLASS -> volo.getPrezzoFirstClass();
        };
        double totale = prezzoBase * numeroPasseggeri;
        if (bagaglioExtra) totale += 35.0 * numeroPasseggeri;
        if (assicurazione) totale += 25.0 * numeroPasseggeri;
        return totale;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodicePrenotazione() { return codicePrenotazione; }
    public void setCodicePrenotazione(String c) { this.codicePrenotazione = c; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public int getIdVolo() { return idVolo; }
    public void setIdVolo(int idVolo) { this.idVolo = idVolo; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) {
        this.utente = utente;
        if (utente != null) this.idUtente = utente.getId();
    }

    public Volo getVolo() { return volo; }
    public void setVolo(Volo volo) {
        this.volo = volo;
        if (volo != null) this.idVolo = volo.getId();
    }

    public ClasseVolo getClasseVolo() { return classeVolo; }
    public void setClasseVolo(ClasseVolo cv) { this.classeVolo = cv; }

    public int getNumeroPasseggeri() { return numeroPasseggeri; }
    public void setNumeroPasseggeri(int n) { this.numeroPasseggeri = n; }

    public double getPrezzoTotale() { return prezzoTotale; }
    public void setPrezzoTotale(double p) { this.prezzoTotale = p; }

    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }

    public LocalDateTime getDataPrenotazione() { return dataPrenotazione; }
    public void setDataPrenotazione(LocalDateTime d) { this.dataPrenotazione = d; }

    public String getNoteSpeciali() { return noteSpeciali; }
    public void setNoteSpeciali(String note) { this.noteSpeciali = note; }

    public boolean isBagaglioExtra() { return bagaglioExtra; }
    public void setBagaglioExtra(boolean b) { this.bagaglioExtra = b; }

    public boolean isAssicurazione() { return assicurazione; }
    public void setAssicurazione(boolean a) { this.assicurazione = a; }

    public String getNumeroPosto() { return numeroPosto; }
    public void setNumeroPosto(String np) { this.numeroPosto = np; }
}
