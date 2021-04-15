package com.example

import io.ktor.http.cio.websocket.*
import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.TextArea
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFrame


fun main(){
//    setComPort2()
//    val portList = getUSBports()
//    portsWithThread()
    sendDatoToCLient()
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

fun sendDatoToCLient (socket: SendChannel<Frame>? = null, deviceMap: JSONObject? = null){ //для отпарвки клиенту json-объекта с данными об ардуинах и девайсах
    println("Start coroutine for scanning ports")
    var portNames = SerialPortList.getPortNames() // получаем список портов
    var portNames2 = SerialPortList.getPortNames() // получаем список портов
    portNames.forEach {
        println(it)
    }
    runBlocking { // создаем корутин
        while (true) { //в бесконечном цикле будем раз в 2 сек сканировать порты
            portNames = SerialPortList.getPortNames() //получаем список активных портов
            if (portNames.size!=portNames2.size) {
                portNames.forEach {
                    println(it)
                }
                portNames2 = portNames
            }
            else {
                //todo сделать сравнение портов в portNames и portNames2 в цикле
            }
            Thread.sleep(2000)
        }
    }
}

fun getUSBportsCorutineWithTextArea(textArea: TextArea){ //функция с корутином для автоопределения подключенных ардуин
    var portsArray = ArrayList<SerialPort>() //массив портов
    var portsStrings = ArrayList<String>()  //массив названий портов
    var portNames = SerialPortList.getPortNames() // получаем список портов
    var flag = false
    var timePassed = 0
    runBlocking { // создаем корутин
        while (true) { //в бесконечном цикле будем раз в 2 сек сканировать порты
            portNames = SerialPortList.getPortNames() //получаем список активных портов
//            Thread.sleep(9000)
//            portNames = SerialPortList.getPortNames() //получаем список активных портов
            portNames.forEach {             //цикл по портам
                if (!portsStrings.contains(it)) { //если еще не было такого порта
                    if (!flag) { //это для плат типа Leonardo - они по 2 раза определяются
                        val tempPort = SerialPort(it)
//                        tempPort.openPort() //открываем порт
//                        tempPort.setParams(
//                            9600,
//                            8,
//                            1,
//                            0
//                        ) //задаем параметры порта, 9600 - скорость, такую же нужно задать для Serial.begin в Arduino

//                        var str: String = ""
//                        tempPort!!.addEventListener { event ->   //слушатель порта для приема сообщений от ардуино
//                            if (event.isRXCHAR) { // если есть данные для приема
//                                try {  //тут секция с try...catch для работы с портом
//                                    str += tempPort!!.readString() //считываем данные из порта в строку
//                                    str = str.trim { it <= ' ' } //убираем лишние символы (типа пробелов, которые могут быть в принятой строке)
//                                    println("str = $str") //выводим принятую строку
//
//                                } catch (ex: SerialPortException) { //для обработки возможных ошибок
//                                    println(ex)
//                                }
//                            }
//                        }
//                        var currentDate = Date()
//                        var time = currentDate.time
//                        var i = 0
//                        var x : Long = 0
//                        currentDate = Date()
//                        var time2 = currentDate.time
//                        println("time = $time")
//                        while(currentDate.time - time < 8000){
//                            currentDate = Date()
////                            time2 = currentDate.time
//                            x = currentDate.time - time2
//                            if (x>=1000)  {
//                                println("x = $x")
//                                println("i = ${i++}")
//                                time2 = currentDate.time
//                            }
//                        }
//                        println("time = ${currentDate.time - time}")

//                        if (str.contains("Device")){
                            portsArray.add(tempPort)
                            portsStrings.add(it)
                            textArea.append("${portsArray.size - 1}: ${portsArray[portsArray.size - 1].portName}\n")
//                        }
//                        tempPort.closePort()
                    }
                }
                println(it)
            }
            Thread.sleep(2000)
//            if (flag) timePassed++
//            if (timePassed>10) { //сбрасываем счетчик и флаг
//                timePassed = 0
//                flag = false
//            }
            println("a $timePassed")
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
    getUSBportsCorutineWithTextArea(textArea)
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