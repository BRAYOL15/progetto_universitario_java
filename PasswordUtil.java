module com.flightbooking {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    // Apre i package ai moduli JavaFX per riflessione (necessario per FXML/SceneBuilder)
    opens com.flightbooking to javafx.fxml;
    opens com.flightbooking.controller to javafx.fxml;
    opens com.flightbooking.model to javafx.base, javafx.fxml;
    exports com.flightbooking;
    exports com.flightbooking.controller;
    exports com.flightbooking.model;
    exports com.flightbooking.dao;
    exports com.flightbooking.service;
    exports com.flightbooking.util;
}
