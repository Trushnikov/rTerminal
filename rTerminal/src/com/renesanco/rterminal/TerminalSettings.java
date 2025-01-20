package com.renesanco.rterminal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jssc.SerialPort;
import org.json.JSONException;
import org.json.JSONObject;

public final class TerminalSettings {

    public enum TerminalType {
        String,
        Binary
    }

    public enum LineTerminator {
        None,
        Cr,
        Lf,
        CrLf,
        LfCr
    }

    public static final int BINARY_BYTES_PER_LINE_MIN = 1;
    public static final int BINARY_BYTES_PER_LINE_MAX = 80;
    public static final int BINARY_BYTES_PER_LINE_DEFAULT = 16;

    public boolean isChanged = false;

    private String currentSettingsFileLocation = "";
    private String portName = "";
    private Integer portBaud = SerialPort.BAUDRATE_9600;
    private Integer portDatabits = SerialPort.DATABITS_8;
    private Integer portParity = SerialPort.PARITY_NONE;
    private Integer portStopBits = SerialPort.STOPBITS_1;
    private TerminalType terminalType = TerminalType.String;
    private Boolean displayTimestamp = false;
    private LineTerminator lineTerminator = LineTerminator.None;
    private int binaryModeBytesPerLine = BINARY_BYTES_PER_LINE_DEFAULT;

    public TerminalSettings(String settingsFileLocation) {
        currentSettingsFileLocation = settingsFileLocation;

        /* if settings file exists - read him and apply, else use default settings */
        File f = new File(settingsFileLocation);
        if (f.isFile() && f.exists()) {
            try {
                InputStream is = Files.newInputStream(Paths.get(settingsFileLocation));
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    String settingsJsonString = sb.toString();
                    JSONObject settingsJson = new JSONObject(settingsJsonString);
                    displayTimestamp = settingsJson.getBoolean("DisplayTimestamp");
                    lineTerminator = TerminalSettings.LineTerminator.valueOf(settingsJson.getString("LineTerminator"));
                    portBaud = settingsJson.getInt("PortBaud");
                    portDatabits = settingsJson.getInt("PortDatabits");
                    portName = settingsJson.getString("PortName");
                    portParity = settingsJson.getInt("PortParity");
                    portStopBits = settingsJson.getInt("PortStopBits");
                    terminalType = TerminalType.valueOf(settingsJson.getString("Type"));
                    binaryModeBytesPerLine = settingsJson.getInt("binaryModeBytesPerLine");
                }
            } catch (IOException | JSONException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            save();
        }
    }

    public void setPortName(String name) {
        portName = name;
        isChanged = true;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortBaud(int baudrate) {
        if (baudrate >= SerialPort.BAUDRATE_1200 && baudrate <= SerialPort.BAUDRATE_115200) {
            portBaud = baudrate;
            isChanged = true;
        }
    }

    public int getPortBaud() {
        return portBaud;
    }

    public void setPortDatabits(int databits) {
        if (databits >= SerialPort.DATABITS_5 && databits <= SerialPort.DATABITS_8) {
            portDatabits = databits;
            isChanged = true;
        }
    }

    public int getPortDatabits() {
        return portDatabits;
    }

    public void setPortParity(int parity) {
        if (parity == SerialPort.PARITY_EVEN || parity == SerialPort.PARITY_ODD || parity == SerialPort.PARITY_NONE) {
            portParity = parity;
            isChanged = true;
        }
    }

    public int getPortParity() {
        return portParity;
    }

    public static String getPortParityUserName(int value) {
        switch (value) {
            case SerialPort.PARITY_EVEN:
                return "Even";
            case SerialPort.PARITY_NONE:
                return "None";
            case SerialPort.PARITY_ODD:
                return "Odd";
            default:
                return "---";
        }
    }

    public static int getPortParityByUserString(String value) {
        switch (value) {
            case "Even":
                return SerialPort.PARITY_EVEN;
            case "None":
                return SerialPort.PARITY_NONE;
            case "Odd":
                return SerialPort.PARITY_ODD;
            default:
                return 0;
        }
    }

    public void setPortStopBits(int stopbits) {
        if (stopbits == SerialPort.STOPBITS_1 || stopbits == SerialPort.STOPBITS_1_5 || stopbits == SerialPort.STOPBITS_2) {
            portStopBits = stopbits;
            isChanged = true;
        }
    }

    public int getPortStopBits() {
        return portStopBits;
    }

    public static String getPortStopBitsUserName(int value) {
        switch (value) {
            case SerialPort.STOPBITS_1:
                return "1";
            case SerialPort.STOPBITS_1_5:
                return "1.5";
            case SerialPort.STOPBITS_2:
                return "2";
            default:
                return "---";
        }
    }

    public static int getPortStopBitsByUserName(String value) {
        switch (value) {
            case "1":
                return SerialPort.STOPBITS_1;
            case "1.5":
                return SerialPort.STOPBITS_1_5;
            case "2":
                return SerialPort.STOPBITS_2;
            default:
                return 0;
        }
    }

    public void setType(TerminalType type) {
        terminalType = type;
        isChanged = true;
    }

    public TerminalType getType() {
        return terminalType;
    }

    public static String getTypeUserName(TerminalType value) {
        return value.name();
    }

    public void setDisplayTimestamp(boolean value) {
        displayTimestamp = value;
        isChanged = true;
    }

    public Boolean getDisplayTimestamp() {
        return displayTimestamp;
    }

    public void setLineTerminator(LineTerminator value) {
        lineTerminator = value;
        isChanged = true;
    }

    public LineTerminator getLineTerminator() {
        return lineTerminator;
    }

    public static String getLineTerminatorUserName(LineTerminator value) {
        return value.name();
    }

    public void setBinaryBytesPerLine(int value) {
        if (value >= BINARY_BYTES_PER_LINE_MIN && value <= BINARY_BYTES_PER_LINE_MAX) {
            binaryModeBytesPerLine = value;
        } else {
            binaryModeBytesPerLine = BINARY_BYTES_PER_LINE_DEFAULT;
        }
    }

    public int getBinaryBytesPerLine() {
        return binaryModeBytesPerLine;
    }

    public boolean save() {
        return saveAs(currentSettingsFileLocation);
    }

    public boolean saveAs(String settingsFileLocation) {
        if (!"".equals(settingsFileLocation)) {
            isChanged = false;
            currentSettingsFileLocation = settingsFileLocation;
            Path settingsFileLocationPath = Paths.get(settingsFileLocation);
            JSONObject settingsJson = new JSONObject();
            settingsJson.put("DisplayTimestamp", displayTimestamp);
            settingsJson.put("LineTerminator", getLineTerminatorUserName(lineTerminator));
            settingsJson.put("PortBaud", portBaud);
            settingsJson.put("PortDatabits", portDatabits);
            settingsJson.put("PortName", portName);
            settingsJson.put("PortParity", portParity);
            settingsJson.put("PortStopBits", portStopBits);
            settingsJson.put("Type", getTypeUserName(terminalType));
            settingsJson.put("binaryModeBytesPerLine", binaryModeBytesPerLine);
            settingsJson.toString();

            try {
                Files.write(settingsFileLocationPath, settingsJson.toString().getBytes());
                return true;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }
}
