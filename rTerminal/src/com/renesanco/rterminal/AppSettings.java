package com.renesanco.rterminal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONException;
import org.json.JSONObject;

public class AppSettings {

    public static final String APP_TITLE = "Terminal";
    public static final String APP_VERSION = "v.1.0.3";
    public static final String EMPTY_LOCATION = "empty";
    public static final String EMPTY_MACRO = "<empty>";
    public static final String CONNECT = "Connect";
    public static final String DISCONNECT = "Disconnect";

    private String lastTerminalSettingsFileLocation = "";
    private String[] recentTerminalSettingsFileLocations = {EMPTY_LOCATION, EMPTY_LOCATION, EMPTY_LOCATION};
    private Macro[] macros = new Macro[20];

    public AppSettings() {
        for (int i = 0; i < macros.length; i++) {
            macros[i] = new Macro(AppSettings.EMPTY_MACRO, 0, false);
        }

        /* check is app settings file exists */
        Path settingsFilePath = GetSettingsFilePath();
        File f = new File(settingsFilePath.toString());

        if (f.exists() && !f.isDirectory()) {
            try {
                InputStream is = Files.newInputStream(settingsFilePath);
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
                    lastTerminalSettingsFileLocation = settingsJson.getString("lastTerminalSettingsFileLocation");
                    for (int i = 0; i < recentTerminalSettingsFileLocations.length; i++) {
                        recentTerminalSettingsFileLocations[i] = settingsJson.getString("recentTerminalSettingsFileLocation" + Integer.toString(i));
                    }
                    for (int i = 0; i < macros.length; i++) {
                        macros[i].setText(settingsJson.getString("m" + Integer.toString(i) + "text"));
                        macros[i].setPeriod(settingsJson.getInt("m" + Integer.toString(i) + "period"));
                        macros[i].setPeriodicExecution(settingsJson.getBoolean("m" + Integer.toString(i) + "isPeriodic"));
                    }
                }
            } catch (IOException | JSONException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            /* create new app settings file with defaults */
            lastTerminalSettingsFileLocation = Paths.get(System.getProperty("user.dir"), "terminal.tsetting").toString();
            saveAppSettings();
        }
    }

    public String getLastTerminalSettingsFileLocation() {
        return lastTerminalSettingsFileLocation;
    }

    public void setLastTerminalSettingsFileLocation(String value) {
        for (int i = 0; i < recentTerminalSettingsFileLocations.length; i++) {
            if (recentTerminalSettingsFileLocations[i] == null ? value == null : recentTerminalSettingsFileLocations[i].equals(value)) {
                recentTerminalSettingsFileLocations[i] = EMPTY_LOCATION;
            }
        }

        String[] tempLocations = new String[recentTerminalSettingsFileLocations.length + 1];
        tempLocations[0] = lastTerminalSettingsFileLocation;
        System.arraycopy(recentTerminalSettingsFileLocations, 0, tempLocations, 1, recentTerminalSettingsFileLocations.length);

        for (int i = 0; i < recentTerminalSettingsFileLocations.length; i++) {
            recentTerminalSettingsFileLocations[i] = EMPTY_LOCATION;
        }

        int nativeArrayIdx = 0;
        for (String tempLocation : tempLocations) {
            if (tempLocation == null ? EMPTY_LOCATION != null : !tempLocation.equals(EMPTY_LOCATION)) {
                recentTerminalSettingsFileLocations[nativeArrayIdx++] = tempLocation;
                if (nativeArrayIdx == recentTerminalSettingsFileLocations.length) {
                    break;
                }
            }
        }

        lastTerminalSettingsFileLocation = value;
        saveAppSettings();
    }

    private String getRecentTerminalSettingsFileLocation(int index) {
        String result;
        try {
            result = recentTerminalSettingsFileLocations[index].substring(recentTerminalSettingsFileLocations[index].lastIndexOf("/") + 1);
        } catch (Exception ex) {
            result = recentTerminalSettingsFileLocations[index];
        }
        return result;
    }

    public String getRecentTerminalSettingsFileLocation0() {
        return getRecentTerminalSettingsFileLocation(0);
    }

    public String getRecentTerminalSettingsFileLocation1() {
        return getRecentTerminalSettingsFileLocation(1);
    }

    public String getRecentTerminalSettingsFileLocation2() {
        return getRecentTerminalSettingsFileLocation(2);
    }

    public void setMacro(int index, Macro macro) {
        if (index < macros.length) {
            macros[index] = macro;
            saveAppSettings();
        }
    }

    public Macro getMacro(int index) {
        if (index < macros.length) {
            return macros[index];
        } else {
            return null;
        }
    }

    static private Path GetSettingsFilePath() {
        String startDir = System.getProperty("user.dir");
        return Paths.get(startDir, "app.setting");
    }

    private void saveAppSettings() {
        Path settingsFilePath = GetSettingsFilePath();
        JSONObject settingsJson = new JSONObject();

        settingsJson.put("lastTerminalSettingsFileLocation", lastTerminalSettingsFileLocation);
        for (int i = 0; i < recentTerminalSettingsFileLocations.length; i++) {
            settingsJson.put("recentTerminalSettingsFileLocation" + Integer.toString(i), recentTerminalSettingsFileLocations[i]);
        }
        for (int i = 0; i < macros.length; i++) {
            settingsJson.put("m" + Integer.toString(i) + "text", macros[i].getText());
            settingsJson.put("m" + Integer.toString(i) + "period", macros[i].getPeriod());
            settingsJson.put("m" + Integer.toString(i) + "isPeriodic", macros[i].getPeriodicExecution());
        }
        
        settingsJson.toString();   

        try {
            Files.write(settingsFilePath, settingsJson.toString().getBytes());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
