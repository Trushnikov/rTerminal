package com.rnsc.rterminal;

/**
 * класс макроса - последовательность, кторая отправляется единоразово по
 * нажатии кнопки
 */
public class Macro {

    /* текст макроса */
    private String text = "";
    /* период вызова макроса в мс */
    private int period = 1000;
    /* признак периодичности вызова макроса */
    private boolean isPeriodic = false;

    /* пустой макрос по умолчанию */
    public Macro() {
        text = AppSettings.EMPTY_MACRO;
        period = 1000;
        isPeriodic = false;
    }

    /* специализированный макрос */
    public Macro(String macroText, int macroPeriod, boolean isPeriodicMacro) {
        text = macroText;
        period = (macroPeriod < 10) ? 10 : macroPeriod;
        isPeriodic = isPeriodicMacro;
    }

    /**
     * установка текста макроса
     *
     * @param value непосредственно тело макроса
     */
    public void setText(String value) {
        text = value;
    }

    /**
     * @return метод возвращает текст макроса
     */
    public String getText() {
        return text;
    }

    /**
     * устанока периодичности вызова макроса
     * @param value период в мс
     */
    public void setPeriod(int value) {
        if (value < 10) {
            value = 10;
        }
        period = value;
    }

    /**
     * @return метод возвращает значение периода выхова макроса в мс
     */
    public int getPeriod() {
        return period;
    }

    /**
     * устанавливает периодическое исполнение макроса
     * @param value true для периодического исполнения, false - для разового
     */
    public void setPeriodicExecution(boolean value) {
        isPeriodic = value;
    }

    /**
     * @return возвращает true, если макрос периодического вызова
     */
    public boolean getPeriodicExecution() {
        return isPeriodic;
    }
}
