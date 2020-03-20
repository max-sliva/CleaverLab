package com.example

import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.net.DatagramSocket
import java.net.InetAddress
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFrame


fun main(){
//    val e: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
//    while (e.hasMoreElements()) {
//        val n = e.nextElement() as NetworkInterface
//        val ee: Enumeration<*> = n.inetAddresses
//        while (ee.hasMoreElements()) {
//            val i = ee.nextElement() as InetAddress
//            println(i.hostAddress)
//        }
//    }
    DatagramSocket().use { socket ->
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
        val ip = socket.localAddress.hostAddress
        println(ip)
    }
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


fun setComPort(): SerialPort?{
    var serialPort: SerialPort? = null
    val portNames = SerialPortList.getPortNames() // получаем список портов
    println("Available Serial ports: ")
    var i=0
    portNames.forEach { println("${i++}: $it") }
    print("Input port number: ")
    val portIndex = readLine()!!.toInt()
    serialPort = SerialPort(portNames[portIndex])
    println("Chosen port = $serialPort")
    return serialPort
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
                serialPort!!.addEventListener({ event ->   //слушатель порта для приема сообщений от ардуино
                    if (event.isRXCHAR()) { // если есть данные для приема
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
                })
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