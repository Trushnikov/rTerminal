package com.renesanco.rterminal;

import java.awt.Taskbar;
import java.util.Arrays;
import javafx.application.Application;
import javafx.geometry.HPos;
//import javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javax.swing.ImageIcon;
import jssc.SerialPort;
import jssc.SerialPortList;

public class CommSettingsWindow extends Application {

    private TerminalSettings terminalSettings;
    private final int[] BAUDRATES = {1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200};
    private final int[] DATABITS = {SerialPort.DATABITS_5, SerialPort.DATABITS_6, SerialPort.DATABITS_7, SerialPort.DATABITS_8};
    private final int[] PARITIES = {SerialPort.PARITY_EVEN, SerialPort.PARITY_NONE, SerialPort.PARITY_ODD};
    private final int[] STOPBITS = {SerialPort.STOPBITS_1, SerialPort.STOPBITS_1_5, SerialPort.STOPBITS_2};

    private ChoiceBox<String> chboxPortName = new ChoiceBox();
    private ChoiceBox<Integer> chboxPortBaud = new ChoiceBox();
    private ChoiceBox<Integer> chboxPortDatabits = new ChoiceBox();
    private ChoiceBox<String> chboxPortParity = new ChoiceBox();
    private ChoiceBox<String> chboxPortStopbits = new ChoiceBox();

    private MainWindow parentWindow;
    private Stage thisStage;

    public CommSettingsWindow(TerminalSettings settings, MainWindow window) {
        terminalSettings = settings;
        parentWindow = window;
    }

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

        /* serial port settings area */
        TitledPane paneSerialPortSettings = new TitledPane();
        paneSerialPortSettings.setCollapsible(false);
        paneSerialPortSettings.setPadding(new Insets(3));
        paneSerialPortSettings.setMinWidth(322);
        paneSerialPortSettings.setText("Serial Port Settings");

        GridPane paneSerialPortSettingsInternalPane = new GridPane();
        paneSerialPortSettingsInternalPane.setMinWidth(316);
        paneSerialPortSettingsInternalPane.setHgap(10);
        paneSerialPortSettingsInternalPane.setVgap(5);
        
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHalignment(HPos.LEFT);
        paneSerialPortSettingsInternalPane.getColumnConstraints().add(col0);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.RIGHT);
        paneSerialPortSettingsInternalPane.getColumnConstraints().add(col1);        

        Label lblPortName = new Label("Port");
        lblPortName.setMinWidth(80);
        paneSerialPortSettingsInternalPane.add(lblPortName, 0, 0);

        chboxPortName.setMinWidth(200);
        chboxPortName.setPrefWidth(chboxPortName.getMinWidth());
        String[] ports = SerialPortList.getPortNames();
        chboxPortName.getItems().addAll(Arrays.asList(ports));
        chboxPortName.setValue(terminalSettings.getPortName());
        paneSerialPortSettingsInternalPane.add(chboxPortName, 1, 0);

        Label lblPortBaud = new Label("Baudrate");
        paneSerialPortSettingsInternalPane.add(lblPortBaud, 0, 1);

        chboxPortBaud.setMinWidth(200);
        chboxPortBaud.setPrefWidth(chboxPortBaud.getMinWidth());
        for (Integer baud : BAUDRATES) {
            chboxPortBaud.getItems().add(baud);
        }
        chboxPortBaud.setValue(terminalSettings.getPortBaud());
        paneSerialPortSettingsInternalPane.add(chboxPortBaud, 1, 1);

        Label lblPortDatabits = new Label("Databits");
        paneSerialPortSettingsInternalPane.add(lblPortDatabits, 0, 2);

        chboxPortDatabits.setMinWidth(200);
        chboxPortDatabits.setPrefWidth(chboxPortDatabits.getMinWidth());
        for (int databits : DATABITS) {
            chboxPortDatabits.getItems().add(databits);
        }
        chboxPortDatabits.setValue(terminalSettings.getPortDatabits());
        paneSerialPortSettingsInternalPane.add(chboxPortDatabits, 1, 2);

        Label lblPortParity = new Label("Parity");
        paneSerialPortSettingsInternalPane.add(lblPortParity, 0, 3);

        chboxPortParity.setMinWidth(200);
        chboxPortParity.setPrefWidth(chboxPortParity.getMinWidth());
        for (int parity : PARITIES) {
            chboxPortParity.getItems().add(TerminalSettings.getPortParityUserName(parity));
        }
        chboxPortParity.setValue(TerminalSettings.getPortParityUserName(terminalSettings.getPortParity()));
        paneSerialPortSettingsInternalPane.add(chboxPortParity, 1, 3);

        Label lblPortStopbits = new Label("Stopbits");
        paneSerialPortSettingsInternalPane.add(lblPortStopbits, 0, 4);

        chboxPortStopbits.setMinWidth(200);
        chboxPortStopbits.setPrefWidth(chboxPortStopbits.getMinWidth());
        for (int stopbits : STOPBITS) {
            chboxPortStopbits.getItems().add(TerminalSettings.getPortStopBitsUserName(stopbits));
        }
        chboxPortStopbits.setValue(TerminalSettings.getPortStopBitsUserName(terminalSettings.getPortStopBits()));
        paneSerialPortSettingsInternalPane.add(chboxPortStopbits, 1, 4);

        paneSerialPortSettings.setContent(paneSerialPortSettingsInternalPane);

        /* TCP settings area */
//        TitledPane paneTCPSettings = new TitledPane();
//        paneTCPSettings.setText("TCPIP Settings");
//        paneTCPSettings.setMinWidth(paneSerialPortSettings.getMinWidth());
//        paneTCPSettings.setPrefWidth(paneTCPSettings.getMinWidth());
//        VBox paneTCPPortSettingsInternalPane = new VBox(5);
//
//        Label lblIP = new Label("Remote IP address:");
//        lblIP.setMinWidth(lblPortName.getMinWidth());
//        lblIP.setPrefWidth(lblPortName.getMinWidth());
//        TextField txtIp = new TextField(AppSettings.TcpPortIp);
//        txtIp.setMinWidth(150);
//        txtIp.setPrefWidth(txtIp.getMinWidth());
//        FlowPane paneIp = new FlowPane(lblIP, txtIp);
//        paneTCPPortSettingsInternalPane.getChildren().add(paneIp);
//
//        Label lblTcpPort = new Label("Remote port:");
//        lblTcpPort.setMinWidth(lblPortName.getMinWidth());
//        lblTcpPort.setPrefWidth(lblPortName.getMinWidth());
//        TextField txtTcpPort = new TextField(Integer.toString(AppSettings.TcpPortPort));
//        txtTcpPort.setMinWidth(150);
//        txtTcpPort.setPrefWidth(txtTcpPort.getMinWidth());
//        FlowPane paneTcpPort = new FlowPane(lblTcpPort, txtTcpPort);
//        paneTCPPortSettingsInternalPane.getChildren().add(paneTcpPort);
//        paneTCPSettings.setContent(paneTCPPortSettingsInternalPane);

        /* buttons area */
        Button btnApply = new Button("Apply");
        btnApply.setMinWidth(75);
        btnApply.setOnMouseClicked(event -> {
            btnApplyClickHandler();
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setMinWidth(75);
        btnCancel.setOnMouseClicked(event -> {
            btnCancelClickHandler();
        });

        FlowPane paneButtons = new FlowPane();
        paneButtons.setPadding(new Insets(5));
        paneButtons.setHgap(10);
        paneButtons.setAlignment(Pos.CENTER_RIGHT);
        paneButtons.getChildren().add(btnApply);
        paneButtons.getChildren().add(btnCancel);

        /* common composer */
        BorderPane verticalPane = new BorderPane();
        BorderPane.setAlignment(paneSerialPortSettings, Pos.TOP_CENTER);
        verticalPane.setCenter(paneSerialPortSettings);
        verticalPane.setBottom(paneButtons);

        var scene = new Scene(verticalPane);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(328);
        stage.setMinHeight(275);
        stage.setTitle("Communication Settings");
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    private void btnApplyClickHandler() {
        terminalSettings.setPortBaud(chboxPortBaud.getValue());
        terminalSettings.setPortDatabits(chboxPortDatabits.getValue());
        terminalSettings.setPortName(chboxPortName.getValue());
        terminalSettings.setPortParity(TerminalSettings.getPortParityByUserString(chboxPortParity.getValue()));
        terminalSettings.setPortStopBits(TerminalSettings.getPortStopBitsByUserName(chboxPortStopbits.getValue()));
        parentWindow.refreshTitle();
        parentWindow.refreshPortSettingsLabel();
            
        thisStage.close();
    }

    private void btnCancelClickHandler() {
        thisStage.close();
    }

    public static void main(String[] args) {
        launch();
    }
}
