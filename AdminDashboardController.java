package com.flightbooking.util;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Gestore del database SQLite. Singleton pattern.
 * Gestisce connessione, creazione tabelle e dati di esempio iniziali.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:sqlite:skybook.db";
    private Connection connection;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    /**
     * Inizializza tutte le tabelle e inserisce dati di esempio.
     */
    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabella UTENTI
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS utenti (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    cognome TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    telefono TEXT,
                    data_nascita TEXT,
                    codice_fiscale TEXT,
                    ruolo TEXT DEFAULT 'UTENTE',
                    data_registrazione TEXT DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Tabella VOLI
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS voli (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero_volo TEXT UNIQUE NOT NULL,
                    compagnia TEXT NOT NULL,
                    aeroporto_partenza TEXT NOT NULL,
                    codice_partenza TEXT NOT NULL,
                    aeroporto_arrivo TEXT NOT NULL,
                    codice_arrivo TEXT NOT NULL,
                    data_ora_partenza TEXT NOT NULL,
                    data_ora_arrivo TEXT NOT NULL,
                    posti_totali INTEGER NOT NULL,
                    posti_disponibili INTEGER NOT NULL,
                    prezzo_economy REAL NOT NULL,
                    prezzo_business REAL NOT NULL,
                    prezzo_first_class REAL NOT NULL,
                    stato TEXT DEFAULT 'PROGRAMMATO',
                    gate_imbarco TEXT
                )
            """);

            // Tabella PRENOTAZIONI
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prenotazioni (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    codice_prenotazione TEXT UNIQUE NOT NULL,
                    id_utente INTEGER NOT NULL,
                    id_volo INTEGER NOT NULL,
                    classe_volo TEXT NOT NULL,
                    numero_passeggeri INTEGER NOT NULL DEFAULT 1,
                    prezzo_totale REAL NOT NULL,
                    stato TEXT DEFAULT 'IN_ATTESA',
                    data_prenotazione TEXT DEFAULT CURRENT_TIMESTAMP,
                    note_speciali TEXT,
                    bagaglio_extra INTEGER DEFAULT 0,
                    assicurazione INTEGER DEFAULT 0,
                    numero_posto TEXT,
                    FOREIGN KEY (id_utente) REFERENCES utenti(id),
                    FOREIGN KEY (id_volo) REFERENCES voli(id)
                )
            """);

            // Inserisce admin e utente demo se non esistono
            inserisciDatiDemo(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void inserisciDatiDemo(Connection conn) throws SQLException {
        // Controlla se esistono già dati
        ResultSet rs = conn.createStatement()
            .executeQuery("SELECT COUNT(*) FROM utenti");
        rs.next();
        if (rs.getInt(1) > 0) return;

        // Admin
        conn.createStatement().execute("""
            INSERT INTO utenti (nome, cognome, email, password_hash, ruolo)
            VALUES ('Admin', 'SkyBook', 'admin@skybook.it',
                    '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',
                    'ADMIN')
        """);

        // Utente demo (password: demo123)
        conn.createStatement().execute("""
            INSERT INTO utenti (nome, cognome, email, password_hash, telefono, data_nascita)
            VALUES ('Mario', 'Rossi', 'mario.rossi@email.it',
                    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
                    '+39 333 1234567', '1990-05-15')
        """);

        // Voli di esempio
        String[][] voli = {
            {"AZ101", "Alitalia ITA Airways", "Aeroporto di Roma Fiumicino", "FCO",
             "Aeroporto di Milano Malpensa", "MXP",
             "2025-06-15 08:00:00", "2025-06-15 09:15:00", "180", "120", "89.90", "249.00", "599.00", "B12"},
            {"FR2456", "Ryanair", "Aeroporto di Milano Bergamo", "BGY",
             "Aeroporto di Barcellona El Prat", "BCN",
             "2025-06-16 14:30:00", "2025-06-16 16:45:00", "189", "89", "54.99", "149.00", "0", "A5"},
            {"LH1234", "Lufthansa", "Aeroporto di Milano Malpensa", "MXP",
             "Aeroporto di Francoforte", "FRA",
             "2025-06-17 07:15:00", "2025-06-17 08:45:00", "220", "95", "119.00", "380.00", "850.00", "C22"},
            {"EK4501", "Emirates", "Aeroporto di Roma Fiumicino", "FCO",
             "Aeroporto di Dubai", "DXB",
             "2025-06-18 23:00:00", "2025-06-19 07:30:00", "350", "201", "489.00", "1299.00", "3500.00", "D14"},
            {"U24567", "easyJet", "Aeroporto di Milano Malpensa", "MXP",
             "Aeroporto di Londra Gatwick", "LGW",
             "2025-06-19 06:00:00", "2025-06-19 07:30:00", "180", "45", "79.99", "199.00", "0", "B8"},
            {"AZ302", "Alitalia ITA Airways", "Aeroporto di Napoli", "NAP",
             "Aeroporto di Palermo", "PMO",
             "2025-06-20 11:00:00", "2025-06-20 12:00:00", "140", "88", "49.90", "129.00", "0", "A3"},
            {"VY6543", "Vueling", "Aeroporto di Roma Fiumicino", "FCO",
             "Aeroporto di Parigi Orly", "ORY",
             "2025-06-21 09:45:00", "2025-06-21 12:00:00", "180", "134", "98.00", "279.00", "0", "E5"},
            {"TK1892", "Turkish Airlines", "Aeroporto di Milano Malpensa", "MXP",
             "Aeroporto di Istanbul", "IST",
             "2025-06-22 16:20:00", "2025-06-22 20:45:00", "290", "176", "219.00", "620.00", "1800.00", "F11"}
        };

        PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO voli (numero_volo, compagnia, aeroporto_partenza, codice_partenza,
                aeroporto_arrivo, codice_arrivo, data_ora_partenza, data_ora_arrivo,
                posti_totali, posti_disponibili, prezzo_economy, prezzo_business,
                prezzo_first_class, stato, gate_imbarco)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,'PROGRAMMATO',?)
        """);

        for (String[] v : voli) {
            for (int i = 0; i < v.length; i++) ps.setString(i + 1, v[i]);
            ps.executeUpdate();
        }

        System.out.println("[DB] Dati demo inseriti con successo.");
    }
}
