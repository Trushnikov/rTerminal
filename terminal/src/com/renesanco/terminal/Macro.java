package com.renesanco.terminal;

public class Macro {

    private String text = "";
    private int period = 1000;
    private boolean isPeriodic = false;

    public Macro() {
        text = AppSettings.EMPTY_MACRO;
        period = 1000;
        isPeriodic = false;
    }

    public Macro(String macroText, int macroPeriod, boolean isPeriodicMacro) {
        text = macroText;
        period = (macroPeriod < 10) ? 10 : macroPeriod;
        isPeriodic = isPeriodicMacro;
    }

    public void setText(String value) {
        text = value;
    }

    public String getText() {
        return text;
    }

    public void setPeriod(int value) {
        if (value < 10) {
            value = 10;
        }
        period = value;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriodicExecution(boolean value) {
        isPeriodic = value;
    }

    public boolean getPeriodicExecution() {
        return isPeriodic;
    }
}
