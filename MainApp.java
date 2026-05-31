package com.flightbooking.dao;

import com.flightbooking.model.Prenotazione;
import com.flightbooking.model.Volo;
import com.flightbooking.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO per la gestione delle prenotazioni nel database.
 */
public class PrenotazioneDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final VoloDAO voloDAO = new VoloDAO();
    private final UtenteDAO utenteDAO = new UtenteDAO();
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Inserisce una nuova prenotazione e aggiorna i posti disponibili.
     */
    public boolean inserisci(Prenotazione p) {
        String sql = """
            INSERT INTO prenotazioni (codice_prenotazione, id_utente, id_volo,
                classe_volo, numero_passeggeri, prezzo_totale, stato,
                note_speciali, bagaglio_extra, assicurazione, numero_posto)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getCodicePrenotazione());
            ps.setInt(2, p.getIdUtente());
            ps.setInt(3, p.getIdVolo());
            ps.setString(4, p.getClasseVolo().name());
            ps.setInt(5, p.getNumeroPasseggeri());
            ps.setDouble(6, p.getPrezzoTotale());
            ps.setString(7, p.getStato().name());
            ps.setString(8, p.getNoteSpeciali());
            ps.setInt(9, p.isBagaglioExtra() ? 1 : 0);
            ps.setInt(10, p.isAssicurazione() ? 1 : 0);
            ps.setString(11, p.getNumeroPosto());
            ps.executeUpdate();

            // Aggiorna posti disponibili
            voloDAO.riduciPosti(p.getIdVolo(), p.getNumeroPasseggeri());
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Restituisce tutte le prenotazioni di un utente.
     */
    public List<Prenotazione> getPerUtente(int idUtente) {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, v.numero_volo, v.compagnia, v.aeroporto_partenza,
                v.codice_partenza, v.aeroporto_arrivo, v.codice_arrivo,
                v.data_ora_partenza, v.data_ora_arrivo, v.posti_disponibili,
                v.posti_totali, v.prezzo_economy, v.prezzo_business,
                v.prezzo_first_class, v.stato as volo_stato, v.gate_imbarco
            FROM prenotazioni p
            JOIN voli v ON p.id_volo = v.id
            WHERE p.id_utente = ?
            ORDER BY p.data_prenotazione DESC
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapPrenotazioneConVolo(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /**
     * Restituisce tutte le prenotazioni (admin).
     */
    public List<Prenotazione> getTutte() {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, v.numero_volo, v.compagnia, v.aeroporto_partenza,
                v.codice_partenza, v.aeroporto_arrivo, v.codice_arrivo,
                v.data_ora_partenza, v.data_ora_arrivo, v.posti_disponibili,
                v.posti_totali, v.prezzo_economy, v.prezzo_business,
                v.prezzo_first_class, v.stato as volo_stato, v.gate_imbarco
            FROM prenotazioni p
            JOIN voli v ON p.id_volo = v.id
            ORDER BY p.data_prenotazione DESC
        """;
        try (ResultSet rs = db.getConnection().createStatement().executeQuery(sql)) {
            while (rs.next()) lista.add(mapPrenotazioneConVolo(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /**
     * Cancella una prenotazione e ripristina i posti.
     */
    public boolean cancella(int idPrenotazione) {
        try {
            // Prima legge per ripristinare posti
            PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT id_volo, numero_passeggeri FROM prenotazioni WHERE id=?");
            ps.setInt(1, idPrenotazione);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idVolo = rs.getInt("id_volo");
                int nPasseggeri = rs.getInt("numero_passeggeri");
                voloDAO.ripristinaPosti(idVolo, nPasseggeri);
            }

            // Aggiorna stato a CANCELLATA
            PreparedStatement upd = db.getConnection().prepareStatement(
                "UPDATE prenotazioni SET stato='CANCELLATA' WHERE id=?");
            upd.setInt(1, idPrenotazione);
            return upd.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Aggiorna lo stato di una prenotazione.
     */
    public boolean aggiornaStato(int id, Prenotazione.Stato stato) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "UPDATE prenotazioni SET stato=? WHERE id=?")) {
            ps.setString(1, stato.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Optional<Prenotazione> getByCodice(String codice) {
        String sql = """
            SELECT p.*, v.numero_volo, v.compagnia, v.aeroporto_partenza,
                v.codice_partenza, v.aeroporto_arrivo, v.codice_arrivo,
                v.data_ora_partenza, v.data_ora_arrivo, v.posti_disponibili,
                v.posti_totali, v.prezzo_economy, v.prezzo_business,
                v.prezzo_first_class, v.stato as volo_stato, v.gate_imbarco
            FROM prenotazioni p
            JOIN voli v ON p.id_volo = v.id
            WHERE p.codice_prenotazione = ?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, codice);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapPrenotazioneConVolo(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    private Prenotazione mapPrenotazioneConVolo(ResultSet rs) throws SQLException {
        Prenotazione p = new Prenotazione();
        p.setId(rs.getInt("id"));
        p.setCodicePrenotazione(rs.getString("codice_prenotazione"));
        p.setIdUtente(rs.getInt("id_utente"));
        p.setIdVolo(rs.getInt("id_volo"));
        p.setClasseVolo(Prenotazione.ClasseVolo.valueOf(rs.getString("classe_volo")));
        p.setNumeroPasseggeri(rs.getInt("numero_passeggeri"));
        p.setPrezzoTotale(rs.getDouble("prezzo_totale"));
        p.setStato(Prenotazione.Stato.valueOf(rs.getString("stato")));
        String dataPren = rs.getString("data_prenotazione");
        if (dataPren != null) p.setDataPrenotazione(LocalDateTime.parse(dataPren, FMT));
        p.setNoteSpeciali(rs.getString("note_speciali"));
        p.setBagaglioExtra(rs.getInt("bagaglio_extra") == 1);
        p.setAssicurazione(rs.getInt("assicurazione") == 1);
        p.setNumeroPosto(rs.getString("numero_posto"));

        // Crea oggetto Volo embedded
        Volo v = new Volo();
        v.setId(rs.getInt("id_volo"));
        v.setNumeroVolo(rs.getString("numero_volo"));
        v.setCompagnia(rs.getString("compagnia"));
        v.setAeroportoPartenza(rs.getString("aeroporto_partenza"));
        v.setCodicePartenza(rs.getString("codice_partenza"));
        v.setAeroportoArrivo(rs.getString("aeroporto_arrivo"));
        v.setCodiceArrivo(rs.getString("codice_arrivo"));
        v.setDataOraPartenza(LocalDateTime.parse(rs.getString("data_ora_partenza"), FMT));
        v.setDataOraArrivo(LocalDateTime.parse(rs.getString("data_ora_arrivo"), FMT));
        v.setPostiDisponibili(rs.getInt("posti_disponibili"));
        v.setPostiTotali(rs.getInt("posti_totali"));
        v.setPrezzoEconomy(rs.getDouble("prezzo_economy"));
        v.setPrezzoBusiness(rs.getDouble("prezzo_business"));
        v.setPrezzoFirstClass(rs.getDouble("prezzo_first_class"));
        try { v.setStato(Volo.Stato.valueOf(rs.getString("volo_stato"))); }
        catch (Exception ignored) {}
        v.setGateImbarco(rs.getString("gate_imbarco"));
        p.setVolo(v);
        return p;
    }
}
