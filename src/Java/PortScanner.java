package Java;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.Arrays;
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

    public void printPortsArray() { //вывод списка портов
        System.out.println("Ports changed");
        if (portNames.size() == 0) System.out.println("No arduinos");
        else portNames.forEach ( it ->
            System.out.println(it)
        );
    }

    public void startUSBscanner() {
        System.out.println("Start thread for scanning ports");
        portNames = (ArrayList<String>) Arrays.asList(SerialPortList.getPortNames()); // получаем список портов
        portsHashMap = new HashMap<String, SerialPort>();
        String[] portNames2 = SerialPortList.getPortNames(); // получаем список портов, с ним будем потом сравнивать новый список
        printPortsArray();
//todo Дописать с нвоым подходом к автоопределению
    }

    public void setListnerForArdu(String port) throws SerialPortException {
        SerialPort tempPort = new SerialPort(port);
        portsHashMap.put(port, tempPort);
        System.out.println("Set listner for $port");
//    if (tempPort.isOpened) tempPort.closePort()
        tempPort.openPort(); //открываем порт
        tempPort.setParams(
                9600,
                8,
                1,
                0
        ); //задаем параметры порта, 9600 - скорость, такую же нужно задать для Serial.begin в Arduino

    }
}
