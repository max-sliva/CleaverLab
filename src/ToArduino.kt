package com.example

import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.TextArea
import java.awt.event.ActionEvent
import java.net.DatagramSocket
import java.net.InetAddress
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFrame


fun main(){
//    setComPort2()
//    val portList = getUSBports()
    portsWithThread()
//    val e: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
//    while (e.hasMoreElements()) {
//        val n = e.nextElement() as NetworkInterface
//        val ee: Enumeration<*> = n.inetAddresses
//        while (ee.hasMoreElements()) {
//            val i = ee.nextElement() as InetAddress
//            println(i.hostAddress)
//        }
//    }

//    var serialPort = setComPort()
//    serialPort!!.openPort() //открываем порт
//    serialPort!!.setParams(
//        9600,
//        8,
//        1,
//        0
//    ) //задаем параметры порта, 9600 - скорость, такую же нужно задать для Serial.begin в Arduino
//    serialPort?.writeString("0")
}


fun setComPort(par: Int=0): SerialPort?{
    var serialPort: SerialPort? = null
//    if (par!=0) {
        val portNames = SerialPortList.getPortNames() // получаем список портов
        println("Available Serial ports: ")
        var i = 0
        portNames.forEach { println("${i++}: $it") }
        print("Input port number: ")
        val portIndex = readLine()!!.toInt()
        serialPort = SerialPort(portNames[portIndex])
        println("Chosen port = $serialPort")
//    }
    return serialPort
}

fun getUSBportsCorutine(textArea: TextArea){
    var portsArray = ArrayList<SerialPort>()
    var portsStrings = ArrayList<String>()
    var portNames = SerialPortList.getPortNames() // получаем список портов
    runBlocking { // создаем корутин
        while (true) {
            portNames = SerialPortList.getPortNames()
            portNames.forEach {
                if (!portsStrings.contains(it)) {
                    portsArray.add(SerialPort(it))
                    portsStrings.add(it)
                    textArea.append("${portsArray.size - 1}: ${portsArray[portsArray.size - 1].portName}\n")
                }
                println(it)
            }
            Thread.sleep(1000)
            println("a")
//            textArea.append("a")
        }
    }

}

fun getUSBports() : ArrayList<SerialPort>{
    var portsArray = ArrayList<SerialPort>()
    val portNames = SerialPortList.getPortNames() // получаем список портов
//    println("Available Serial ports: ")
//    var i = 0
//    portNames.forEach { println("${i++}: $it") }
    portNames.forEach { portsArray.add(SerialPort(it))}
//    println("Available Serial ports 2: ")
//    var i = 0
//    portsArray.forEach{
//        println("${i++}: ${it.portName}")
//    }
    return portsArray
}

fun portsWithThread(){
    val myFrame = JFrame("ArduinoControl")
    myFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    var textArea = TextArea()

    myFrame.add(textArea, BorderLayout.CENTER)
    myFrame.size = Dimension(300,300)
    myFrame.setLocationRelativeTo(null)
    //myFrame.pack()
    myFrame.isVisible = true
    getUSBportsCorutine(textArea)
//    runBlocking { // создаем корутин
////       async { //создаем блок для асинхронного запуска вычислений, тоже корутин
//           while (true) {
//               Thread.sleep(1000)
//               textArea.append("a")
//           }
////       }
//    }
}

fun setComPort2(){
    var serialPort: SerialPort? = null
    val myFrame = JFrame("ArduinoControl")
    myFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val check = JCheckBox("LED on/off") // чекбокс, который будет отвечать за вкл/выкл светодиода на плате Arduino
    check.isEnabled = false // делаем неактивным чекбокс, пока не выберем порт для связи с Arduino

    val portNames = SerialPortList.getPortNames() // получаем список портов
    val comPorts = JComboBox(portNames) // создаем комбобокс с этим списком

    comPorts.selectedIndex = -1 // чтоб не было выбрано ничего в комбобоксе

    comPorts.addActionListener { arg: ActionEvent? ->  // слушатель выбора порта в комбобоксе
        val choosenPort = comPorts.getItemAt(comPorts.selectedIndex) // получаем название выбранного порта
        //если serialPort еще не связана с портом или текущий порт не равен выбранному в комбо-боксе
        if (serialPort == null || !serialPort!!.getPortName().contains(choosenPort)) {
            serialPort = SerialPort(choosenPort) //задаем выбранный порт
            check.isEnabled = true //активируем чек-бокс
            try { //тут секция с try...catch для работы с портом
                serialPort!!.openPort() //открываем порт
                serialPort!!.setParams(
                    9600,
                    8,
                    1,
                    0
                ) //задаем параметры порта, 9600 - скорость, такую же нужно задать для Serial.begin в Arduino
                //остальные параметры стандартные для работы с портом
                serialPort!!.addEventListener { event ->   //слушатель порта для приема сообщений от ардуино
                    if (event.isRXCHAR) { // если есть данные для приема
                        try {  //тут секция с try...catch для работы с портом
                            var str: String = serialPort!!.readString() //считываем данные из порта в строку
                            str =
                                str.trim { it <= ' ' } //убираем лишние символы (типа пробелов, которые могут быть в принятой строке)
                            println(str) //выводим принятую строку
                            if (str.contains("recv=1")) check.isSelected = true
                            if (str.contains("recv=0")) check.isSelected = false
                        } catch (ex: SerialPortException) { //для обработки возможных ошибок
                            println(ex)
                        }
                    }
                }
            } catch (e: SerialPortException) { //для обработки возможных ошибок
                e.printStackTrace()
            }
        } else println("Same port!!") //это если выбрали в списке тот же порт, что и до этого
    }

    check.addActionListener { arg0: ActionEvent? ->  //обработка нажатия на чек-бокс
        try { //тут секция с try...catch для работы с портом
            if (check.isSelected) { //если стоит галочка
                println("LED on") //то выводим текст
                serialPort?.writeString("1") // и отправляем символ "1" в порт
            } else {
                println("LED off")
                serialPort?.writeString("0") // иначе отправляем символ "0" в порт
            }
        } catch (e: SerialPortException) { //для обработки возможных ошибок
            e.printStackTrace()
        }
    }
    myFrame.add(comPorts, BorderLayout.NORTH)
    myFrame.add(check, BorderLayout.CENTER)
    myFrame.setLocationRelativeTo(null)
    myFrame.pack()
    myFrame.isVisible = true
}