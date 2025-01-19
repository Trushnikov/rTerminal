package com.renesanco.terminal;

import java.awt.Taskbar;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javax.swing.ImageIcon;

public class TerminalSettingsWindow extends Application {

    private TerminalSettings terminalSettings;
    private MainWindow parentWindow;

    public TerminalSettingsWindow(TerminalSettings settings, MainWindow window) {
        terminalSettings = settings;
        parentWindow = window;
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

        /* Common settings pane */
        TitledPane commonSettingsPanel = new TitledPane();
        commonSettingsPanel.setPadding(new Insets(3));
        commonSettingsPanel.setText("Common Settings");

        GridPane commonSettingsPanelInternalPane = new GridPane();
        commonSettingsPanelInternalPane.setHgap(10);
        commonSettingsPanelInternalPane.setVgap(5);

        Label lblTerminalType = new Label("Terminal type");
        commonSettingsPanelInternalPane.add(lblTerminalType, 0, 0);

        ChoiceBox<String> chboxTerminalType = new ChoiceBox();
        chboxTerminalType.setMinWidth(100);
        for (TerminalSettings.TerminalType type : TerminalSettings.TerminalType.values()) {
            chboxTerminalType.getItems().add(type.name());
        }
        chboxTerminalType.setValue(TerminalSettings.getTypeUserName(terminalSettings.getType()));
        commonSettingsPanelInternalPane.add(chboxTerminalType, 1, 0);

        Label lblTerminator = new Label("Line terminator");
        commonSettingsPanelInternalPane.add(lblTerminator, 0, 1);

        ChoiceBox<String> chboxTerminator = new ChoiceBox();
        chboxTerminator.setMinWidth(100);
        commonSettingsPanelInternalPane.add(chboxTerminator, 1, 1);
        for (TerminalSettings.LineTerminator terminator : TerminalSettings.LineTerminator.values()) {
            chboxTerminator.getItems().add(terminator.name());
        }
        chboxTerminator.setValue(TerminalSettings.getLineTerminatorUserName(terminalSettings.getLineTerminator()));

        Label lblBytesPerLine = new Label("Bytes per lin in binary mode");
        commonSettingsPanelInternalPane.add(lblBytesPerLine, 0, 2);

        TextField txtBytesPerLine = new TextField(Integer.toString(terminalSettings.getBinaryBytesPerLine()));
        txtBytesPerLine.setMinWidth(100);
        commonSettingsPanelInternalPane.add(txtBytesPerLine, 1, 2);

        CheckBox chkDisplayTimeStamp = new CheckBox("Display timestamp");
        chkDisplayTimeStamp.setSelected(terminalSettings.getDisplayTimestamp());
        commonSettingsPanelInternalPane.add(chkDisplayTimeStamp, 0, 3);
        commonSettingsPanel.setContent(commonSettingsPanelInternalPane);

        /* buttons area */
        Button btnApply = new Button("Apply");
        btnApply.setMinWidth(75);
        btnApply.setOnMouseClicked(event -> {
            int bytesPerLine = Integer.parseInt(txtBytesPerLine.getText());
            if (bytesPerLine < TerminalSettings.BINARY_BYTES_PER_LINE_MIN || bytesPerLine > TerminalSettings.BINARY_BYTES_PER_LINE_MAX) {
                return;
            }

            terminalSettings.setType(TerminalSettings.TerminalType.valueOf(chboxTerminalType.getValue()));
            terminalSettings.setLineTerminator(TerminalSettings.LineTerminator.valueOf(chboxTerminator.getValue()));
            terminalSettings.setDisplayTimestamp(chkDisplayTimeStamp.isSelected());
            terminalSettings.setBinaryBytesPerLine(bytesPerLine);
            terminalSettings.isChanged = true;
            parentWindow.refreshTitle();
            stage.close();
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setMinWidth(75);
        btnCancel.setOnMouseClicked(event -> {
            stage.close();
        });

        FlowPane paneButtons = new FlowPane();
        paneButtons.setPadding(new Insets(5));
        paneButtons.setHgap(10);
        paneButtons.setAlignment(Pos.CENTER_RIGHT);
        paneButtons.getChildren().add(btnApply);
        paneButtons.getChildren().add(btnCancel);

        /* common composer */
        BorderPane verticalPane = new BorderPane();
        verticalPane.setCenter(commonSettingsPanel);
        verticalPane.setBottom(paneButtons);

        var scene = new Scene(verticalPane);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Terminal Settings");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
