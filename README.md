package com.flightbooking.controller;

import com.flightbooking.MainApp;
import com.flightbooking.dao.PrenotazioneDAO;
import com.flightbooking.dao.UtenteDAO;
import com.flightbooking.dao.VoloDAO;
import com.flightbooking.model.Prenotazione;
import com.flightbooking.model.Utente;
import com.flightbooking.model.Volo;
import com.flightbooking.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller per il pannello amministrativo.
 * Permette la gestione completa di voli, prenotazioni e utenti.
 */
public class AdminDashboardController implements Initializable {

    // ─── Statistiche ────────────────────────────────────────────────────────
    @FXML private Label lblTotVoli;
    @FXML private Label lblTotPrenotazioni;
    @FXML private Label lblTotUtenti;
    @FXML private Label lblIncassoTotale;

    // ─── Tabella Voli ────────────────────────────────────────────────────────
    @FXML private TableView<Volo> tabellaVoli;
    @FXML private TableColumn<Volo, String> colVNumero;
    @FXML private TableColumn<Volo, String> colVCompagnia;
    @FXML private TableColumn<Volo, String> colVRotta;
    @FXML private TableColumn<Volo, String> colVOrario;
    @FXML private TableColumn<Volo, String> colVPosti;
    @FXML private TableColumn<Volo, String> colVPrezzo;
    @FXML private TableColumn<Volo, String> colVStato;
    @FXML private TableColumn<Volo, String> colVAzioni;

    // ─── Tabella Prenotazioni ────────────────────────────────────────────────
    @FXML private TableView<Prenotazione> tabellaPrenotazioni;
    @FXML private TableColumn<Prenotazione, String> colPCodice;
    @FXML private TableColumn<Prenotazione, String> colPUtente;
    @FXML private TableColumn<Prenotazione, String> colPVolo;
    @FXML private TableColumn<Prenotazione, String> colPClasse;
    @FXML private TableColumn<Prenotazione, String> colPPrezzo;
    @FXML private TableColumn<Prenotazione, String> colPStato;
    @FXML private TableColumn<Prenotazione, String> colPAzioni;

    // ─── Tabella Utenti ─────────────────────────────────────────────────────
    @FXML private TableView<Utente> tabellaUtenti;
    @FXML private TableColumn<Utente, String> colUNome;
    @FXML private TableColumn<Utente, String> colUEmail;
    @FXML private TableColumn<Utente, String> colUTelefono;
    @FXML private TableColumn<Utente, String> colURuolo;

    // ─── Sezioni ────────────────────────────────────────────────────────────
    @FXML private javafx.scene.layout.VBox sezioneDashboard;
    @FXML private javafx.scene.layout.VBox sezioneVoli;
    @FXML private javafx.scene.layout.VBox sezionePrenotazioni;
    @FXML private javafx.scene.layout.VBox sezioneUtenti;

    private final VoloDAO voloDAO = new VoloDAO();
    private final PrenotazioneDAO prenotazioneDAO = new PrenotazioneDAO();
    private final UtenteDAO utenteDAO = new UtenteDAO();
    private ObservableList<Volo> listaVoli = FXCollections.observableArrayList();
    private ObservableList<Prenotazione> listaPrenotazioni = FXCollections.observableArrayList();
    private ObservableList<Utente> listaUtenti = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inizializzaTabellaVoli();
        inizializzaTabellaPrenotazioni();
        inizializzaTabellaUtenti();
        caricaDati();
        mostraSezione("dashboard");
    }

    private void caricaDati() {
        List<Volo> voli = voloDAO.getTutti();
        List<Prenotazione> prenotazioni = prenotazioneDAO.getTutte();
        List<Utente> utenti = utenteDAO.getTutti();

        listaVoli.setAll(voli);
        listaPrenotazioni.setAll(prenotazioni);
        listaUtenti.setAll(utenti);

        double incasso = prenotazioni.stream()
            .filter(p -> p.getStato() != Prenotazione.Stato.CANCELLATA)
            .mapToDouble(Prenotazione::getPrezzoTotale).sum();

        lblTotVoli.setText(String.valueOf(voli.size()));
        lblTotPrenotazioni.setText(String.valueOf(prenotazioni.size()));
        lblTotUtenti.setText(String.valueOf(utenti.size()));
        lblIncassoTotale.setText(String.format("€ %.2f", incasso));
    }

    private void inizializzaTabellaVoli() {
        colVNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroVolo()));
        colVCompagnia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCompagnia()));
        colVRotta.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCodicePartenza() + " → " + c.getValue().getCodiceArrivo()));
        colVOrario.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getDataPartenzaFormatted()));
        colVPosti.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getPostiDisponibili() + "/" + c.getValue().getPostiTotali()));
        colVPrezzo.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("€ %.2f", c.getValue().getPrezzoEconomy())));
        colVStato.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatoLabel()));

        colVAzioni.setCellFactory(col -> new TableCell<>() {
            final Button btnMod = new Button("✏ Modifica");
            final Button btnDel = new Button("🗑 Elimina");
            final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(5, btnMod, btnDel);
            {
                btnMod.getStyleClass().add("btn-modifica");
                btnDel.getStyleClass().add("btn-cancella");
                btnMod.setOnAction(e -> {
                    Volo v = getTableView().getItems().get(getIndex());
                    apriFormVolo(v);
                });
                btnDel.setOnAction(e -> {
                    Volo v = getTableView().getItems().get(getIndex());
                    eliminaVolo(v);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        tabellaVoli.setItems(listaVoli);
    }

    private void inizializzaTabellaPrenotazioni() {
        colPCodice.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodicePrenotazione()));
        colPUtente.setCellValueFactory(c -> new SimpleStringProperty(
            "Utente #" + c.getValue().getIdUtente()));
        colPVolo.setCellValueFactory(c -> {
            Volo v = c.getValue().getVolo();
            return new SimpleStringProperty(v != null ?
                v.getNumeroVolo() + " (" + v.getCodicePartenza() + "→" + v.getCodiceArrivo() + ")" : "--");
        });
        colPClasse.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasseLabel()));
        colPPrezzo.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("€ %.2f", c.getValue().getPrezzoTotale())));
        colPStato.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatoLabel()));

        colPAzioni.setCellFactory(col -> new TableCell<>() {
            final Button btnConf = new Button("✅");
            final Button btnAnn = new Button("❌");
            final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(5, btnConf, btnAnn);
            {
                btnConf.setOnAction(e -> {
                    Prenotazione p = getTableView().getItems().get(getIndex());
                    prenotazioneDAO.aggiornaStato(p.getId(), Prenotazione.Stato.CONFERMATA);
                    caricaDati();
                });
                btnAnn.setOnAction(e -> {
                    Prenotazione p = getTableView().getItems().get(getIndex());
                    prenotazioneDAO.cancella(p.getId());
                    caricaDati();
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        tabellaPrenotazioni.setItems(listaPrenotazioni);
    }

    private void inizializzaTabellaUtenti() {
        colUNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeCompleto()));
        colUEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colUTelefono.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getTelefono() != null ? c.getValue().getTelefono() : "—"));
        colURuolo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRuolo()));
        tabellaUtenti.setItems(listaUtenti);
    }

    @FXML
    private void handleNuovoVolo() {
        apriFormVolo(null);
    }

    private void apriFormVolo(Volo volo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/flightbooking/fxml/FormVolo.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(MainApp.getPrimaryStage());
            dialog.setTitle(volo == null ? "Nuovo Volo" : "Modifica Volo — " + volo.getNumeroVolo());
            Scene scene = new Scene(loader.load(), 650, 600);
            scene.getStylesheets().add(
                getClass().getResource("/com/flightbooking/css/style.css").toExternalForm());
            dialog.setScene(scene);
            FormVoloController ctrl = loader.getController();
            if (volo != null) ctrl.setVolo(volo);
            ctrl.setOnSalvato(() -> { caricaDati(); dialog.close(); });
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eliminaVolo(Volo volo) {
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Elimina Volo");
        conf.setHeaderText("Eliminare il volo " + volo.getNumeroVolo() + "?");
        conf.setContentText("Verranno eliminate anche le prenotazioni associate.");
        Optional<ButtonType> r = conf.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            voloDAO.elimina(volo.getId());
            caricaDati();
        }
    }

    @FXML private void mostraDashboard()     { mostraSezione("dashboard"); }
    @FXML private void mostraVoli()          { mostraSezione("voli"); }
    @FXML private void mostraPrenotazioni()  { mostraSezione("prenotazioni"); }
    @FXML private void mostraUtenti()        { mostraSezione("utenti"); }

    private void mostraSezione(String s) {
        sezioneDashboard.setVisible("dashboard".equals(s));   sezioneDashboard.setManaged("dashboard".equals(s));
        sezioneVoli.setVisible("voli".equals(s));              sezioneVoli.setManaged("voli".equals(s));
        sezionePrenotazioni.setVisible("prenotazioni".equals(s)); sezionePrenotazioni.setManaged("prenotazioni".equals(s));
        sezioneUtenti.setVisible("utenti".equals(s));          sezioneUtenti.setManaged("utenti".equals(s));
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        MainApp.navigateTo("LoginView.fxml", "Accesso");
    }
}
