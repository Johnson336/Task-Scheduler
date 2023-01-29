module wgu.c195.schedulingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;


    opens wgu.c195.schedulingsystem to javafx.fxml;
    exports wgu.c195.schedulingsystem;
    exports wgu.c195.schedulingsystem.controllers;
    exports wgu.c195.schedulingsystem.components;
    opens wgu.c195.schedulingsystem.controllers to javafx.fxml;
    opens wgu.c195.schedulingsystem.components to javafx.base;
}