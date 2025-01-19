package com.renesanco.terminal;

import java.awt.Taskbar;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javax.swing.ImageIcon;

public class MacroEditorWindow extends Application {

    private final MainWindow parentWindow;
    private Macro currentMacro = new Macro();
    private int selectedMacroIdx = 0;
    private AppSettings currentAppSettings;
    private TextField txtMacroText;
    private CheckBox chkPeriodic;
    private TextField txtMacroPeriod;
    
    public MacroEditorWindow(MainWindow parent){
        parentWindow = parent;
    }

    @Override
    public void start(Stage stage) {

        /* app icon */
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        /* try to set icon in taskbar of MacOS */
        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(new ImageIcon(getClass().getResource("/mac_icon.png")).getImage());
        } catch (Exception ex) {
        }

        GridPane macroEditorPanel = new GridPane();
        macroEditorPanel.setAlignment(Pos.CENTER);
        macroEditorPanel.setPadding(new Insets(10, 5, 10, 5));
        macroEditorPanel.setHgap(10);
        macroEditorPanel.setVgap(10);

        ColumnConstraints col0 = new ColumnConstraints();
        macroEditorPanel.getColumnConstraints().add(col0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        col1.setMaxWidth(150);
        col1.setHalignment(HPos.RIGHT);
        macroEditorPanel.getColumnConstraints().add(col1);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(100);
        col2.setMaxWidth(100);
        macroEditorPanel.getColumnConstraints().add(col2);

        Label lblMacroText = new Label("Macro text");
        macroEditorPanel.add(lblMacroText, 0, 0);

        txtMacroText = new TextField();
        txtMacroText.setPromptText("Input macro text here...");
        macroEditorPanel.add(txtMacroText, 1, 0, 2, 1);

        chkPeriodic = new CheckBox("Periodic");
        macroEditorPanel.add(chkPeriodic, 0, 1);

        Label lblMacroPeriodText = new Label("Period, ms");
        macroEditorPanel.add(lblMacroPeriodText, 1, 1);

        txtMacroPeriod = new TextField();
        macroEditorPanel.add(txtMacroPeriod, 2, 1);

        /* buttons area */
        Button btnApply = new Button("Apply");
        btnApply.setMinWidth(75);
        btnApply.setOnMouseClicked(event -> {
            currentMacro.setText(txtMacroText.getText());
            currentMacro.setPeriod(Integer.parseInt(txtMacroPeriod.getText()));
            currentMacro.setPeriodicExecution(chkPeriodic.isSelected());
            currentAppSettings.setMacro(selectedMacroIdx, currentMacro);
            parentWindow.refreshMacroLabels();
            stage.close();
        });
        macroEditorPanel.add(btnApply, 1, 2);

        Button btnCancel = new Button("Cancel");
        btnCancel.setMinWidth(75);
        btnCancel.setOnMouseClicked(event -> {
            stage.close();
        });
        macroEditorPanel.add(btnCancel, 2, 2);

        /* common composer */
        BorderPane verticalPane = new BorderPane();
        verticalPane.setCenter(macroEditorPanel);

        var scene = new Scene(verticalPane);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    public void setMacroData(int macroIdx, AppSettings appSettings) {
        selectedMacroIdx = macroIdx;
        currentAppSettings = appSettings;
        currentMacro = appSettings.getMacro(selectedMacroIdx);
        txtMacroText.setText(currentMacro.getText());
        txtMacroPeriod.setText(Integer.toString(currentMacro.getPeriod()));
        chkPeriodic.setSelected(currentMacro.getPeriodicExecution());
    }

    public static void main(String[] args) {
        launch();
    }
}
