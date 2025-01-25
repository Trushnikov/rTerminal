package com.renesanco.rterminal;

import java.awt.Taskbar;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.ImageIcon;
import jssc.SerialPortException;

public class MainWindow extends Application {

    Label lblPortSettings = new Label("Disconnected");
    TextField txtTextToSend = new TextField();
    MenuItem mnuItemFirstLastOpenedFile, mnuItemSecondLastOpenedFile, mnuItemThirdLastOpenedFile;
    final ObservableList<String> sentCommandsList = FXCollections.observableArrayList();
    TableView<String> tableSentCommands = new TableView<>(sentCommandsList);
    TextArea txtData = new TextArea();
    AppSettings appSettings;
    TerminalSettings terminalSettings;
    Terminal terminal;
    Stage thisStage;
    Button btnConnect = new Button(AppSettings.CONNECT);
    Button btnSend = new Button("Send");
    Button[] macroButtons = new Button[20];
    private VBox macroPanel;

    @Override
    public void start(Stage stage) {
        thisStage = stage;

        /* app icon */
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        /* try to set icon in taskbar of MacOS */
        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(new ImageIcon(getClass().getResource("/mac_icon.png")).getImage());
        } catch (Exception ex) {
        }

        appSettings = new AppSettings();
        terminalSettings = new TerminalSettings(appSettings.getLastTerminalSettingsFileLocation());
        terminal = new Terminal(terminalSettings);
        refreshTitle();
        refreshPortSettingsLabel();

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(createMenu());
        mainPane.setCenter(createInteractivePanel());
        macroPanel = createShortcutsPanel();
        mainPane.setRight(macroPanel);
        mainPane.setBottom(createStatusPanel());

        var scene = new Scene(mainPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setIconified(false);
        stage.setMinWidth(900);
        stage.setMinHeight(700);
        stage.setOnCloseRequest((WindowEvent event) -> {
            appClose();
        });
        stage.show();
    }

    public void refreshTitle() {
        String title = appSettings.getLastTerminalSettingsFileLocation();
        title = title.substring(title.lastIndexOf("/") + 1);
        if (terminalSettings.isChanged) {
            title += "*";
        }
        thisStage.setTitle(AppSettings.APP_TITLE + " : " + title);
    }

    private MenuBar createMenu() {
        /* menu creation */
        Menu mnuFile = new Menu("File");

        MenuItem mnuItemOpen = new MenuItem("Open Terminal Settings File");
        mnuItemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        mnuItemOpen.setOnAction(event -> {
            menuItemOpenHandler();
        });
        mnuFile.getItems().add(mnuItemOpen);

        MenuItem mnuItemSave = new MenuItem("Save Terminal Settings File");
        mnuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        mnuItemSave.setOnAction(event -> {
            mnuItemSaveHandler();
        });
        mnuFile.getItems().add(mnuItemSave);

        MenuItem mnuItemSaveAs = new MenuItem("Save File As");
        mnuItemSaveAs.setOnAction(event -> {
            mnuItemSaveAsHandler();
        });
        mnuFile.getItems().add(mnuItemSaveAs);

        mnuFile.getItems().add(new SeparatorMenuItem());

        mnuItemFirstLastOpenedFile = new MenuItem();
        mnuItemFirstLastOpenedFile.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN));
        mnuItemFirstLastOpenedFile.setOnAction(event -> {
            mnuItemLastOpenedFileHandler(0);
        });
        mnuFile.getItems().add(mnuItemFirstLastOpenedFile);

        mnuItemSecondLastOpenedFile = new MenuItem();
        mnuItemSecondLastOpenedFile.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN));
        mnuItemSecondLastOpenedFile.setOnAction(event -> {
            mnuItemLastOpenedFileHandler(1);
        });
        mnuFile.getItems().add(mnuItemSecondLastOpenedFile);

        mnuItemThirdLastOpenedFile = new MenuItem();
        mnuItemThirdLastOpenedFile.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.CONTROL_DOWN));
        mnuItemThirdLastOpenedFile.setOnAction(event -> {
            mnuItemLastOpenedFileHandler(2);
        });
        mnuFile.getItems().add(mnuItemThirdLastOpenedFile);

        redrawRecentFilesMenu();

        mnuFile.getItems().add(new SeparatorMenuItem());
        MenuItem mnuItemExit = new MenuItem("Exit");
        mnuItemExit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));
        mnuItemExit.setOnAction(event -> {
            appClose();
            thisStage.close();
        });
        mnuFile.getItems().add(mnuItemExit);

        Menu mnuSettings = new Menu("Settings");
        MenuItem mnuItemCommSettings = new MenuItem("Communication settings");
        mnuSettings.getItems().add(mnuItemCommSettings);
        mnuItemCommSettings.setOnAction(event -> {
            CommSettingsWindow wnd = new CommSettingsWindow(terminalSettings, this);
            wnd.start(new Stage());
        });

        MenuItem mnuItemTerminalSettings = new MenuItem("Terminal settings");
        mnuSettings.getItems().add(mnuItemTerminalSettings);
        mnuItemTerminalSettings.setOnAction(event -> {
            TerminalSettingsWindow wnd = new TerminalSettingsWindow(terminalSettings, this);
            wnd.start(new Stage());
        });

        Menu mnuHelp = new Menu("Help");
        MenuItem mnuItemAbout = new MenuItem("About");
        mnuItemAbout.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "test", ButtonType.OK);
            alert.setContentText("Support: alexei.trushnikov@gmail.com");
            alert.setHeaderText("Serial port terminal app. \n" + AppSettings.APP_VERSION);
            alert.setTitle("About");
            alert.show();
        });
        mnuHelp.getItems().add(mnuItemAbout);

        return new MenuBar(mnuFile, mnuSettings, mnuHelp);
    }

    private void redrawRecentFilesMenu() {
        mnuItemFirstLastOpenedFile.setText("1." + appSettings.getRecentTerminalSettingsFileLocation0());
        mnuItemSecondLastOpenedFile.setText("2." + appSettings.getRecentTerminalSettingsFileLocation1());
        mnuItemThirdLastOpenedFile.setText("3." + appSettings.getRecentTerminalSettingsFileLocation2());
    }

    public void refreshPortSettingsLabel() {
        String status = String.format("%s %d,%d,%s,%s",
                terminalSettings.getPortName(),
                terminalSettings.getPortBaud(),
                terminalSettings.getPortDatabits(),
                TerminalSettings.getPortParityUserName(terminalSettings.getPortParity()),
                TerminalSettings.getPortStopBitsUserName(terminalSettings.getPortStopBits()));
        lblPortSettings.setText(status);
    }

    private SplitPane createInteractivePanel() {
        SplitPane interactivePanel = new SplitPane();
        interactivePanel.setBorder(Border.EMPTY);
        interactivePanel.setPadding(new Insets(3));
        interactivePanel.setOrientation(Orientation.VERTICAL);

        tableSentCommands.setRowFactory(tv -> {
            TableRow<String> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    int selectedIndex = tableSentCommands.getSelectionModel().getSelectedIndex();
                    String msg = sentCommandsList.get(selectedIndex);
                    if (terminal.isConnected()) {
                        try {
                            terminal.send(msg);
                            addTxMessageToLog(msg);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            });
            return row;
        });
        TableColumn<String, String> col0 = new TableColumn<>("Previously entered commands");
        col0.setCellValueFactory((TableColumn.CellDataFeatures<String, String> cellData) -> new SimpleStringProperty(cellData.getValue()));
        col0.prefWidthProperty().bind(tableSentCommands.widthProperty().multiply(1.0));
        tableSentCommands.getColumns().add(col0);
        tableSentCommands.setPlaceholder(new Label(""));
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem item1 = new MenuItem("Clear list");
        item1.setOnAction((t) -> {
            sentCommandsList.clear();
        });
        contextMenu.getItems().add(item1);
        tableSentCommands.setContextMenu(contextMenu);
        tableSentCommands.setOnContextMenuRequested((t) -> {
            contextMenu.show(tableSentCommands, t.getScreenX(), t.getScreenY());
        });
        interactivePanel.getItems().add(tableSentCommands);

        /*  */
        BorderPane loPanel = new BorderPane();

        /* field for input data to send*/
        BorderPane sendPanel = new BorderPane();
        BorderPane.setMargin(txtTextToSend, new Insets(3, 3, 3, 0));
        sendPanel.setCenter(txtTextToSend);
        txtTextToSend.setOnKeyPressed((t) -> {
            if (t.getCode() == KeyCode.ENTER) {
                btnSendClickHandler();
            }
        });

        btnSend.setMinWidth(100);
        BorderPane.setMargin(btnSend, new Insets(3, 0, 3, 3));
        sendPanel.setRight(btnSend);
        btnSend.setOnMouseClicked((t) -> {
            btnSendClickHandler();
        });
        loPanel.setTop(sendPanel);

        /* rx-tx data display */
        txtData.setEditable(false);
        txtData.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                txtData.clear();
            }
        });
        ContextMenu txtDataContextMenu = new ContextMenu();
        MenuItem txtDataContextMenuItemClear = new CheckMenuItem("Clear");
        txtDataContextMenuItemClear.setOnAction((t) -> {
            txtData.clear();
        });
        txtDataContextMenu.getItems().add(txtDataContextMenuItemClear);
        txtData.setContextMenu(txtDataContextMenu);
        txtData.setOnContextMenuRequested((t) -> {
            txtDataContextMenu.show(txtData, t.getScreenX(), t.getScreenY());
        });
        BorderPane txtDataPanel = new BorderPane();
        txtDataPanel.setCenter(txtData);
        loPanel.setCenter(txtDataPanel);
        interactivePanel.getItems().add(loPanel);

        interactivePanel.setDividerPosition(0, 0.3);
        return interactivePanel;
    }

    private VBox createShortcutsPanel() {
        VBox pane = new VBox();
        pane.setMinWidth(200);
        pane.setPadding(new Insets(5));
        pane.setSpacing(5);
        for (int i = 0; i < 20; i++) {
            FlowPane f = new FlowPane();
            f.setHgap(5);
            f.setMaxWidth(190);
            Button btn = new Button("M" + Integer.toString(i));
            btn.setMinWidth(40);
            btn.setId("m" + Integer.toString(i));
            btn.setOnMouseClicked((t) -> {
                macroButtonClickHandler(btn.getId());
            });
            macroButtons[i] = btn;
            f.getChildren().add(btn);
            Label lbl = new Label(appSettings.getMacro(i).getText());
            lbl.setId("l" + Integer.toString(i));
            if (appSettings.getMacro(i).getText().equals(AppSettings.EMPTY_MACRO)) {
                lbl.setStyle("-fx-text-fill: darkgrey");
            }
            lbl.setOnMouseClicked((t) -> {
                if (t.getClickCount() == 2) {
                    macroLabelClickHandler(lbl.getId());
                }
            });
            f.getChildren().add(lbl);
            pane.getChildren().add(f);
        }
        return pane;
    }

    private void macroLabelClickHandler(String lblId) {
        int idx = Integer.parseInt(lblId.substring(1));
        MacroEditorWindow wnd = new MacroEditorWindow(this);
        wnd.start(new Stage());
        wnd.setMacroData(idx, appSettings);
    }

    private void macroButtonClickHandler(String btnId) {
        int idx = Integer.parseInt(btnId.substring(1));
        try {
            sendMessageAndSaveToLog(appSettings.getMacro(idx).getText());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void refreshMacroLabels() {
        for (Node node : macroPanel.getChildren()) {
            FlowPane fp = (FlowPane) node;
            for (Node subNode : fp.getChildren()) {
                String id = subNode.getId();
                System.out.println((id == null) ? "null" : id);
                if (id != null && id.contains("l")) {
                    Label lbl = (Label) subNode;
                    lbl.setText(appSettings.getMacro(Integer.parseInt(id.substring(1))).getText());
                    if (appSettings.getMacro(Integer.parseInt(id.substring(1))).getText().equals(AppSettings.EMPTY_MACRO)) {
                        lbl.setStyle("-fx-text-fill: darkgrey");
                    } else {
                        lbl.setStyle("-fx-text-fill: black");
                    }
                }
            }
        }
    }

    private FlowPane createStatusPanel() {
        FlowPane pane = new FlowPane(btnConnect, lblPortSettings);
        pane.setPadding(new Insets(5, 5, 5, 10));
        pane.setHgap(10);

        btnConnect.setMinWidth(100);
        btnConnect.setOnMouseClicked((MouseEvent event) -> {
            btnConnectClickHandler();
        });

        lblPortSettings.setStyle("-fx-text-fill: darkgrey");
        lblPortSettings.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                if (!terminal.isConnected()) {
                    CommSettingsWindow wnd = new CommSettingsWindow(terminalSettings, this);
                    wnd.start(new Stage());
                }
            }
        });
        return pane;
    }

    private void menuItemOpenHandler() {
        checkForModifiedSettingsAction();

        FileChooser openFileDialog = new FileChooser();
        openFileDialog.setTitle("Open terminal settings from file");
        openFileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("tsettings files", "*.tsetting"));
        File selectedFile = openFileDialog.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String selectedFileLocation = selectedFile.toString();
            terminalSettings = new TerminalSettings(selectedFileLocation);
            appSettings.setLastTerminalSettingsFileLocation(selectedFileLocation);
            redrawRecentFilesMenu();
            refreshTitle();
            refreshPortSettingsLabel();
        }
    }

    private void mnuItemSaveHandler() {
        if (terminalSettings != null) {
            terminalSettings.save();
            refreshTitle();
        }
    }

    private void mnuItemSaveAsHandler() {
        FileChooser saveFileDialog = new FileChooser();
        saveFileDialog.setTitle("Save terminal settings as..");
        saveFileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("tsettings files", "*.tsetting"));
        File selectedFile = saveFileDialog.showSaveDialog(new Stage());

        if (selectedFile != null) {
            String newFilePath = selectedFile.toString();
            terminalSettings.saveAs(newFilePath);
            appSettings.setLastTerminalSettingsFileLocation(newFilePath);
            redrawRecentFilesMenu();
            refreshTitle();
        }
    }

    private void mnuItemLastOpenedFileHandler(int index) {
        String selectedFileLocation;

        switch (index) {
            case 0:
                selectedFileLocation = appSettings.getRecentTerminalSettingsFileLocation0();
                break;
            case 1:
                selectedFileLocation = appSettings.getRecentTerminalSettingsFileLocation1();
                break;
            case 2:
                selectedFileLocation = appSettings.getRecentTerminalSettingsFileLocation2();
                break;
            default:
                selectedFileLocation = AppSettings.EMPTY_LOCATION;
        }

        if (selectedFileLocation == null ? AppSettings.EMPTY_LOCATION != null : !selectedFileLocation.equals(AppSettings.EMPTY_LOCATION)) {
            terminalSettings = new TerminalSettings(selectedFileLocation);
            appSettings.setLastTerminalSettingsFileLocation(selectedFileLocation);
            redrawRecentFilesMenu();
            refreshTitle();
        }
    }

    private void appClose() {
        checkForModifiedSettingsAction();
        try {
            terminal.disconnect();
        } catch (SerialPortException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void btnConnectClickHandler() {
        if (btnConnect.getText().equals(AppSettings.CONNECT)) {
            try {
                terminal.connect();
                if (terminal.isConnected()) {
                    btnConnect.setText(AppSettings.DISCONNECT);
                    terminal.addPropertyChangeListener((evt) -> {
                        byte[] receivedBuffer = terminal.getReceivedBuffer();
//                        addRxMessageToLog(receivedBuffer);
                        Platform.runLater(() -> {
                            addRxMessageToLog(receivedBuffer);
                        });
                    });
                }
            } catch (SerialPortException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error has occured:");
                alert.setContentText(ex.getMessage());
            }
        } else {
            try {
                btnConnect.setText(AppSettings.CONNECT);
                terminal.disconnect();
            } catch (SerialPortException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void btnSendClickHandler() {
        if (terminal.isConnected()) {
            sendMessageAndSaveToLog(txtTextToSend.getText());
        }
    }

    private void sendMessageAndSaveToLog(String msg) {
        try {
            terminal.send(msg);
            addTxMessageToLog(msg);
            if (!sentCommandsList.contains(msg)) {
                sentCommandsList.add(msg);
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error has occured");
            alert.setContentText(ex.getMessage());
            alert.show();
        }
    }

    private void checkForModifiedSettingsAction() {
        if (terminalSettings.isChanged) {
            /* current tags table is modified - possible need to save */
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
            alert.setTitle("Confirm current terminal settings changes");
            alert.setContentText("Current terminal settings have changed. Save it?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {
                terminalSettings.save();
                refreshTitle();
            }
        }
    }

    private void addTxMessageToLog(String msg) {
        String line = "";
        if (terminalSettings.getDisplayTimestamp()) {
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            line += timeStamp + " ";
        }
        if (terminalSettings.getDisplayMsgDirection()) {
            line += "> \n";
        }
        if (terminalSettings.getType() == TerminalSettings.TerminalType.String) {
            line += msg;
        } else {
            byte[] bytes = msg.getBytes();
            for (byte b : bytes) {
                line += String.format("%02X ", b);
            }
        }
        line += "\n";
        txtData.appendText(line);
    }

    private void addRxMessageToLog(byte[] msg) {
        String line = "";

        try {
            if (msg.length == 0) {
                return;
            }

            if (terminalSettings.getDisplayTimestamp()) {
                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                line += timeStamp + " ";
            }
            if (terminalSettings.getDisplayMsgDirection()) {
                line += "< \n";
            }
            if (terminalSettings.getType() == TerminalSettings.TerminalType.String) {
                for (byte b : msg) {
                    line += (char) b;
                }
            } else {
                int bytesPerLineCounter = 0;
                int bytesPerLine = terminalSettings.getBinaryBytesPerLine();
                for (byte b : msg) {
                    line += String.format("%02X ", b);
                    bytesPerLineCounter++;
                    if (bytesPerLineCounter == bytesPerLine) {
                        line += "\n";
                        bytesPerLineCounter = 0;
                    }
                }
            }
            if (!terminalSettings.getDisplayMsgDirection() && !terminalSettings.getDisplayTimestamp()) {
                if (!line.substring(line.length() - 1).equals("\n")) {
                    line += "\n";
                }
            }
            txtData.appendText(line);
        } catch (Exception ex) {
            System.out.println("com.renesanco.rterminal.MainWindow.addRxMessageToLog() error:" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
