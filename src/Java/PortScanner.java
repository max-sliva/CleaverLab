package Java;

import jssc.SerialPort;

import java.util.ArrayList;
import java.util.HashMap;

public class PortScanner {
    private HashMap<String, jssc.SerialPort> portArdus = new HashMap<String, SerialPort>();
    private ArrayList<String> portNames;
    private HashMap<String, SerialPort> portsHashMap;
    private String portInfoJSON = "[]";

    public ArrayList<String> getPortNames(){
        return portNames;
    }

    public SerialPort getSerialPortByName(String str) {
       return portsHashMap.get(str);
    }

    public SerialPort getSerialPortByArdu(String str) {
        return portArdus.get(str);
    }

}
