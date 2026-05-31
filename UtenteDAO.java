package com.flightbooking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modello che rappresenta un volo disponibile nel sistema.
 * Contiene tutte le informazioni di partenza, arrivo, prezzi e disponibilità.
 */
public class Volo {

    public enum Stato { PROGRAMMATO, IN_RITARDO, CANCELLATO, PARTITO, ATTERRATO }

    private int id;
    private String numeroVolo;
    private String compagnia;
    private String aeroportoPartenza;
    private String codicePartenza;
    private String aeroportoArrivo;
    private String codiceArrivo;
    private LocalDateTime dataOraPartenza;
    private LocalDateTime dataOraArrivo;
    private int postiTotali;
    private int postiDisponibili;
    private double prezzoEconomy;
    private double prezzoBusiness;
    private double prezzoFirstClass;
    private Stato stato;
    private String gateImbarco;

    public Volo() {
        this.stato = Stato.PROGRAMMATO;
    }

    // ─── Metodi di utilità ───────────────────────────────────────────────────

    public String getDurataVolo() {
        if (dataOraPartenza == null || dataOraArrivo == null) return "--";
        long minuti = java.time.Duration.between(dataOraPartenza, dataOraArrivo).toMinutes();
        return String.format("%dh %02dm", minuti / 60, minuti % 60);
    }

    public String getDataPartenzaFormatted() {
        if (dataOraPartenza == null) return "--";
        return dataOraPartenza.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDataArrivoFormatted() {
        if (dataOraArrivo == null) return "--";
        return dataOraArrivo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public boolean haPostiDisponibili() {
        return postiDisponibili > 0;
    }

    public String getStatoLabel() {
        return switch (stato) {
            case PROGRAMMATO -> "🟢 Programmato";
            case IN_RITARDO  -> "🟡 In Ritardo";
            case CANCELLATO  -> "🔴 Cancellato";
            case PARTITO     -> "🔵 Partito";
            case ATTERRATO   -> "✅ Atterrato";
        };
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroVolo() { return numeroVolo; }
    public void setNumeroVolo(String numeroVolo) { this.numeroVolo = numeroVolo; }

    public String getCompagnia() { return compagnia; }
    public void setCompagnia(String compagnia) { this.compagnia = compagnia; }

    public String getAeroportoPartenza() { return aeroportoPartenza; }
    public void setAeroportoPartenza(String ap) { this.aeroportoPartenza = ap; }

    public String getCodicePartenza() { return codicePartenza; }
    public void setCodicePartenza(String cp) { this.codicePartenza = cp; }

    public String getAeroportoArrivo() { return aeroportoArrivo; }
    public void setAeroportoArrivo(String aa) { this.aeroportoArrivo = aa; }

    public String getCodiceArrivo() { return codiceArrivo; }
    public void setCodiceArrivo(String ca) { this.codiceArrivo = ca; }

    public LocalDateTime getDataOraPartenza() { return dataOraPartenza; }
    public void setDataOraPartenza(LocalDateTime d) { this.dataOraPartenza = d; }

    public LocalDateTime getDataOraArrivo() { return dataOraArrivo; }
    public void setDataOraArrivo(LocalDateTime d) { this.dataOraArrivo = d; }

    public int getPostiTotali() { return postiTotali; }
    public void setPostiTotali(int pt) { this.postiTotali = pt; }

    public int getPostiDisponibili() { return postiDisponibili; }
    public void setPostiDisponibili(int pd) { this.postiDisponibili = pd; }

    public double getPrezzoEconomy() { return prezzoEconomy; }
    public void setPrezzoEconomy(double p) { this.prezzoEconomy = p; }

    public double getPrezzoBusiness() { return prezzoBusiness; }
    public void setPrezzoBusiness(double p) { this.prezzoBusiness = p; }

    public double getPrezzoFirstClass() { return prezzoFirstClass; }
    public void setPrezzoFirstClass(double p) { this.prezzoFirstClass = p; }

    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }

    public String getGateImbarco() { return gateImbarco; }
    public void setGateImbarco(String gate) { this.gateImbarco = gate; }

    @Override
    public String toString() {
        return numeroVolo + " | " + codicePartenza + " → " + codiceArrivo
               + " | " + getDataPartenzaFormatted();
    }
}
