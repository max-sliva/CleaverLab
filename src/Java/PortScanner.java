package Java;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PortScanner {
    private HashMap<String, jssc.SerialPort> portArdus = new HashMap<String, SerialPort>();
    private ArrayList<String> portNames;
    private HashMap<String, SerialPort> portsHashMap;
    private String portInfoJSON = "[]";

    public ArrayList<String> getPortNames(){
        return portNames;
    } //получение названий портов

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
        portNames.forEach ( it -> {
            try {
                setListnerForArdu(it);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
//            Thread.sleep(2000)
        });
        ThreadForPortsUpdate threadForPortsUpdate = new ThreadForPortsUpdate(portNames);
        threadForPortsUpdate.start();
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
        AtomicBoolean first = new AtomicBoolean(true);
        AtomicReference<String> str = new AtomicReference<>("");
        tempPort.addEventListener( event ->{
            if (event.isRXCHAR()) {
                try {
                    String temp = tempPort.readString();
                    if (temp!= null) {
                        if (temp.contains("\n")) str.get().concat(temp);
                        else str.get().concat(temp.trim());
//                        else str += temp.trim { it < ' ' && it !='\n'}
                        if (str.get().contains("end devList")) {
                            System.out.println("str = "+str);

                        }
                        str.set("");
                        if (first.get()) {
                            tempPort.writeString("1");
                            first.set(false);
                        }

                    } else System.out.println("received null from "+port);
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public String[] getArdus() {
        return (String[]) portArdus.keySet().toArray();
    }

}
