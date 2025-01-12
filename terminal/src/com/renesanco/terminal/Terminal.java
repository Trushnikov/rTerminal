package com.renesanco.terminal;

import static com.renesanco.terminal.TerminalSettings.LineTerminator.Lf;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Terminal {

    private final TerminalSettings terminalSettings;
    private final SerialPort sp;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private byte[] receivedBuffer;

    public Terminal(TerminalSettings settings) {
        terminalSettings = settings;
        sp = new SerialPort(terminalSettings.getPortName());
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    }

    private class PortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    receivedBuffer = sp.readBytes();
                } catch (SerialPortException ex) {
                    System.out.println(ex.getMessage());
                }
                support.firePropertyChange("isBufferNotEmpty", 0, 1);
            }
        }
    }

    public byte[] getReceivedBuffer() {
        return receivedBuffer;
    }

    public void connect() throws SerialPortException {
        sp.openPort();
        sp.setParams(terminalSettings.getPortBaud(), terminalSettings.getPortDatabits(), terminalSettings.getPortStopBits(), terminalSettings.getPortParity());
        try {
            sp.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        } catch (SerialPortException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Boolean isConnected() {
        return sp.isOpened();
    }

    public void disconnect() throws SerialPortException {
        sp.removeEventListener();
        sp.closePort();
    }

    public void send(String msg) throws Exception {
        switch (terminalSettings.getLineTerminator()) {
            case Cr:
                sp.writeString(msg + '\r');
                break;
            case Lf:
                sp.writeString(msg + '\n');
                break;
            case CrLf:
                sp.writeString(msg + "\r\n");
                break;
            case LfCr:
                sp.writeString(msg + "\n\r");
                break;
            case None:
                sp.writeString(msg);
                break;
        }
    }
}
