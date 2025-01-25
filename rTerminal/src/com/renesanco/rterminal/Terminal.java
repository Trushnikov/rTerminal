package com.renesanco.rterminal;

import static com.renesanco.rterminal.TerminalSettings.LineTerminator.Lf;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Terminal {

    private final TerminalSettings terminalSettings;
    private SerialPort sp;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private byte[] receivedBuffer0;
    private byte[] receivedBuffer1;
    private boolean isZeroBufferUsed = true;

    public Terminal(TerminalSettings settings) {
        terminalSettings = settings;
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
                    if (isZeroBufferUsed) {
                        receivedBuffer0 = sp.readBytes();
                        isZeroBufferUsed = false;
                    } else {
                        receivedBuffer1 = sp.readBytes();
                        isZeroBufferUsed = true;
                    }
                } catch (SerialPortException ex) {
                    System.out.println(ex.getMessage());
                }
                support.firePropertyChange("isBufferNotEmpty", 0, 1);
            }
        }
    }

    public byte[] getReceivedBuffer() {
        return (isZeroBufferUsed) ? receivedBuffer0 : receivedBuffer1;
    }

    public void connect() throws SerialPortException {
        sp = new SerialPort(terminalSettings.getPortName());
        sp.openPort();
        sp.setParams(terminalSettings.getPortBaud(), terminalSettings.getPortDatabits(), terminalSettings.getPortStopBits(), terminalSettings.getPortParity());
        try {
            sp.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        } catch (SerialPortException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Boolean isConnected() {
        return (sp != null) ? sp.isOpened() : false;
    }

    public void disconnect() throws SerialPortException {
        if (sp != null) {
            sp.removeEventListener();
            sp.closePort();
        }
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
