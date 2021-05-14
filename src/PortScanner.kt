package com.example

import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import javax.swing.*


fun main(){
//    val usbScanner = PortScanner()
//    usbScanner.startUSBscanner()
//    while (true){}
    guiForScanner()
}

fun guiForScanner() {
    val myFrame = JFrame("ArduinoControl")
    myFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val usbScanner = PortScanner()
    usbScanner.startUSBscanner()
    val comPorts = JComboBox(usbScanner.getPortNames()) // создаем комбобокс с этим списком
    comPorts.selectedIndex = -1 // чтоб не было выбрано ничего в комбобоксе
    val arrowBtn = getButtonSubComponent(comPorts)
    arrowBtn?.addActionListener {
        println("arrow button pressed")
        val comArray = usbScanner.getPortNames()
        comArray.forEach {
            val model = comPorts.model as DefaultComboBoxModel
            if(model.getIndexOf(it) == -1 ) {
                comPorts.addItem(it)
            }
//            if (comPorts.model)
        }
    }

    val text = JTextField(15)
    text.text = "-info-"
    val sendButton = JButton("Send")
    sendButton.isEnabled = false
    sendButton.addActionListener{e->
        val choosenPort = comPorts.getItemAt(comPorts.selectedIndex)
        val serialPort = usbScanner.getSerialPortByName(choosenPort)
        serialPort?.writeString(text.text)
    }
    val northPanel = JPanel()
    northPanel.add(comPorts)
    northPanel.add(text)
    northPanel.add(sendButton)
    comPorts.addActionListener { arg ->
        println("index = ${comPorts.selectedIndex}")
        if (comPorts.selectedIndex != -1) sendButton.isEnabled = true
    }
    myFrame.add(northPanel, BorderLayout.NORTH)
//    myFrame.add(text, BorderLayout.CENTER)
    myFrame.size = Dimension(500, 200)
    myFrame.setLocationRelativeTo(null)
    //myFrame.pack()
    myFrame.isVisible = true
}

private fun getButtonSubComponent(container: Container): JButton? { //для получения кнопки-стрелки в комбобоксе
    if (container is JButton) {
        return container
    } else {
        val components: Array<Component> = container.getComponents()
        for (component in components) {
            if (component is Container) {
                return getButtonSubComponent(component as Container)
            }
        }
    }
    return null
}

class PortScanner {
    private lateinit var portNames : Array<String>
    private lateinit var portsHashMap: HashMap<String, SerialPort>
    private var portInfoJSON = """[]""";

    fun getPortNames(): Array<String>{
        return portNames
    }

    fun getSerialPortByName(str: String) = portsHashMap.get(str)

    private fun convertPortInfoToJSON(str: String){ //для преобразования строки в JSON-объект
        var counter = 0
        println("Converting str = \n$str")
        val devPos = str.indexOf("Devices")
        println("devPos = $devPos")
        var arduNum = 0
        if (devPos != -1) {
            val x = str.subSequence(devPos-2, devPos).toString() //получаем номер ардуино
            arduNum = Integer.parseInt(x.trim())
            println("arduNum = $arduNum")
            val arduName = "ardu#"+arduNum
            var jsonStr = """{"type":"ardu", "name":"$arduName"},""" //сама ардуино
//            var i = devPos + "Devices:".length+1
            var i = str.indexOf("\n")
            var devStr = ""
            while (++i!=str.length){
                devStr+=str[i]
                if(str[i] == '\n' && !devStr.contains("end devList")  && !devStr.contains("Devices")) {
                    devStr=devStr.trim()
                    println("devStr = $devStr")
                    jsonStr+="""{"type":"device", "name": "${devStr+counter++}", "ardu": "$arduName"},"""
                    devStr = ""
                }
            }
            if (!portInfoJSON.contains(jsonStr)) {
                portInfoJSON=portInfoJSON.replace("]"," ")
                portInfoJSON += jsonStr
                portInfoJSON += "]"
            }
            println("portInfoJSON = \n${portInfoJSON}")
            println("getJSONfromPorts = \n${getJSONfromPorts()}")
        }
    }

    fun getJSONfromPorts(): String { //для возврата json с объектами
        return "${portInfoJSON.replace(",]","]")}"
    }

    fun printPortsArray() { //вывод списка портов
        println("Ports changed")
        if (portNames.size == 0) println("No arduinos")
        else portNames.forEach {
            println(it)
        }
    }

    fun startUSBscanner() { //для старта сканера юсб
        println("Start coroutine for scanning ports")
        portNames = SerialPortList.getPortNames() // получаем список портов
        portsHashMap = HashMap<String, SerialPort>()
        var portNames2 = SerialPortList.getPortNames() // получаем список портов, с ним будем потом сравнивать новый список
//    portNames.forEach {
//        println(it)
//    }
        printPortsArray()
//    var deviceMap: JSONObject? = createDeviceMap(portNames)
        portNames.forEach {
            setListnerForArdu(it)
            Thread.sleep(2000)
            //todo сделать получение от ардуино номера, и если он = -1, то отправить на ардуино его номер с учетом текущего массива
        }
        GlobalScope.async { // создаем корутин
            println("in coroutine")
            while (true) { //в бесконечном цикле будем раз в 3 сек сканировать порты
                var num = 0;
                portNames = SerialPortList.getPortNames() //получаем список активных портов
                if (portNames.size != portNames2.size) { //если размер прошлого списка и нового разные
                    printPortsArray()
                    portNames.forEach {
                        if (it !in portNames2) {
                            println("!$it - new!")
                            setListnerForArdu(it)
                        }
                    }

                    portNames2 = portNames //приравниваем старый и новый список портов
//                var deviceMap: JSONObject? = createDeviceMap(portNames)
                } else { //если списки равны по размеру
                    num = 0; //то будем сравнивать названия портов
                    portNames.forEach {
                        val tempPort = it
                        portNames2.forEach { it2 ->
                            if (it2.equals(tempPort)) num++
                        }
                    }
                    if (num != portNames.size) {  //если кол-во одинаковых портов меньше кол-ва портов
                        num = 0;
                        println("Ports changed 2")
                        portNames.forEach {
                            if (it !in portNames2) {
                                println("!$it - new!")
                                setListnerForArdu(it)}
                        }
                        portNames2 = portNames
                        printPortsArray(portNames)
                    }
                }
                Thread.sleep(3000)
//                println("ports size = ${portNames.size}")
            }
            println("End coroutine")
        }
    }

    fun setListnerForArdu(port: String) {
        val tempPort = SerialPort(port)
        portsHashMap.set(port, tempPort)
        println("Set listner for $port")
//    if (tempPort.isOpened) tempPort.closePort()
        tempPort.openPort() //открываем порт
        tempPort.setParams(
            9600,
            8,
            1,
            0
        ) //задаем параметры порта, 9600 - скорость, такую же нужно задать для Serial.begin в Arduino
        var first = true
        var str: String = ""
        tempPort!!.addEventListener { event ->   //слушатель порта для приема сообщений от ардуино
            if (event.isRXCHAR) { // если есть данные для приема
                try {  //тут секция с try...catch для работы с портом
                    val temp = tempPort!!.readString()
                    if (temp!= null) {
//                        temp = temp.trim { it <= ' ' }
                        if (temp.contains("\n")) str += temp
                        else str += temp.trim { it < ' ' && it !='\n'} //считываем данные из порта в строку
                        str = str.trim { it < ' ' && it !='\n'} //убираем лишние символы (типа пробелов, которые могут быть в принятой строке)
                        if (str.contains("end devList")) {
                            println("str = $str")
                            convertPortInfoToJSON(str)
                            str = ""
                            if (first) {
                                tempPort.writeString("1")
                                first = false
                            }
//                            val jsonStr = createJSONfromStr(str);
                        } //выводим принятую строку
                    }
                    else println("received null from $port")
                } catch (ex: SerialPortException) { //для обработки возможных ошибок
                    println(ex)
                }
            }
        }
    }

}