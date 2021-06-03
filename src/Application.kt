package com.example

import ch.qos.logback.classic.LoggerContext
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import jssc.SerialPort
import kotlinx.coroutines.channels.SendChannel
//import org.bson.Document
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File

//data class SampleSession(val name: String, val value: String)
//data class MatrixLed(val device: String, val cellNumber: Int, val cellColor1:String, val normalCurCellNumber: Int, val red: Int, val green:Int, val blue:Int)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module() {
//    startUSBscanner()
//    portsWithThread()
    var socketToClient: SendChannel<Frame>? = null
    val usbScanner = PortScanner()
    var curArdu = ""  //текущая ардуино, будет прислана с клиента
    usbScanner.startUSBscanner()
    var serialPort: SerialPort? = null
    try {
        serialPort = SerialPort(usbScanner.getPortNames()[0])
    } catch (ex: ArrayIndexOutOfBoundsException) {

    }
//    serialPort!!.openPort() //открываем порт
//    serialPort!!.setParams(
//        9600,
//        8,
//        1,
//        0
   // ) //задаем параметры порта, 9600 - скорость, такую же нужно задать для Serial.begin в Arduino

//    serialPort.writeString("{'device' : 'matrix', 'clear': 'clear' }")
    val curIP = getCurrentIp()
//    val mongoUrl = "localhost";
//    val mongoClient = MongoClient(mongoUrl, 27017)
    var loginActive = "default"
    var sendCount = 0;
    var userData : JSONObject? = null
    //для отключения логгирования mongo
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
//    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
//    rootLogger.level = Level.OFF
    var mySession: SampleSession
    var ledMatrixArray = arrayOf("000000","000000","000000","000000","000000","000000","000000","000000",
        "000000","000000","000000","000000","000000","000000","000000","000000",
        "000000","000000","000000","000000","000000","000000","000000","000000",
        "000000","000000","000000","000000","000000","000000","000000","000000",
        "000000","000000","000000","000000","000000","000000","000000","000000",
        "000000","000000","000000","000000","000000","000000","000000","000000",
        "000000","000000","000000","000000","000000","000000","000000","000000",
        "000000","000000","000000","000000","000000","000000","000000","000000")

//    val mongoDatabase = mongoClient.getDatabase("local")
//    var userCollection = mongoDatabase.getCollection("user")
//    println("From Mongo = $userCollection")
//    val docs: ArrayList<Document> = ArrayList<Document>()
//    val devices = ArrayList<Document>()
//    val usersCount = userCollection.countDocuments()
//    println("usersCount = $usersCount")
//    val deviceCollection = mongoDatabase.getCollection("device")
//    val deviceCount = deviceCollection.countDocuments()
//    println("deviceCount = $deviceCount")
    val webSockSessions = ArrayList<WebSocketSession>()
//    var str: String = "111".md5()
//    println("str=$str")
    val server = io.ktor.server.engine.embeddedServer(Netty, port = 80) {
        install(WebSockets)
        install(Sessions) {
            cookie<SampleSession>("ROBO_COOKIE")
//            header<SampleSession>("HTTP_ROBOPORTAL")
        }

        routing {
            webSocket("/") {
                println("onConnect session = $this")
                webSockSessions.add(this)
                val thisSession = this
//                outgoing.send(Frame.Text(loginActive))
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site: $text")
                            if (loginActive == "Admin") {
                                println("!AdminMode!")
                                if (text == "NeedUsers" || text == "addUser!") { //if from site came request for users
//                                    val iter = userCollection.find()
//                                    docs.clear()
//                                    iter.into(docs);
//                                    println("users=$docs")
//                                    val userCaps = arrayListOf("login", "fio", "status", "devices", "online")
//                                    outgoing.send(Frame.Text(arrayListToJSON(docs, userCaps, "users")))
                                    var dm = DataManager(PathToData("userData", "userData.json"))
                                    val userData = dm.fromFileToJSON("userData")["user"]
//                                    val userData = fromFileToJSON("userData.json")["user"]
                                    val userJson = "{\"type\": \"users\", \"users\": $userData}"
                                    println("userJson=$userJson")
                                    outgoing.send(Frame.Text(userJson))
                                    dm = DataManager(PathToData("regData", "regData.json"))
                                    val regData = dm.fromFileToJSON("regData")["user"]
//                                    val userData = fromFileToJSON("userData.json")["user"]
                                    val regJson = "{\"type\": \"regUsers\", \"users\": $regData}"
                                    println("regJson=$regJson")
                                    outgoing.send(Frame.Text(regJson))
                                }
//                                else if (text == "NeedRegs"){
//
//                                }
                                else if (text == "NeedDevices") { //if from site came request for devices
//                                    val iter = deviceCollection.find()
//                                    devices.clear()
//                                    iter.into(devices);
//                                    println("devices=$devices")
//                                    val deviceCaps = arrayListOf("type", "name", "active")
//                                    outgoing.send(Frame.Text(arrayListToJSON(devices, deviceCaps, "devices")))
//                                    val deviceData = fromFileToJSON("deviceData.json")["device"]
//                                    println("devicesFromFile=$deviceData")
//                                    val deviceJson = """{"type": "devices", "devices": $deviceData}"""
                                    val infoFromComPorts = usbScanner.getJSONfromPorts()
                                    val deviceJson = """{"type": "devices", "devices": $infoFromComPorts}"""
//
                                    println("devicesFromComPorts = $deviceJson" )
                                    outgoing.send(Frame.Text(deviceJson))
                                    usbScanner.setUpdaterForPorts(outgoing)
                                } else {
                                    println("From Admin: $text")
                                }
                                if (text.contains("'ardu_name'")){
//                                    println("ardu_name = $text")
                                    val arduJSON = JSONObject(text)
                                    println("ardu_name = ${arduJSON["ardu_name"]}")
                                    curArdu = arduJSON["ardu_name"].toString()
//                                        curArdu = text.ardu_name
                                }
                                if (text.contains("'device'") && curArdu!=null){
                                //todo сделать передачу данных в нужный порт
                                    val serPort = usbScanner.getSerialPortByArdu(curArdu)
                                    if (serPort != null) {
                                        serPort.writeString(text)
                                    }
                                }
                            } else {
                                println("Not Admin")
                                if (text == "NeedDevices") { //if from site came request for devices
//                                    val iter = deviceCollection.find()
//                                    devices.clear()
//                                    iter.into(devices);
//                                    println("devices=$devices")
//                                    val deviceCaps = arrayListOf("type", "name", "active")
//                                    outgoing.send(Frame.Text(arrayListToJSON(devices, deviceCaps, "devices")))
                                    val dm = DataManager(PathToData("deviceData", "device.json"))
                                    val deviceData = dm.fromFileToJSON("deviceData")["device"]
                                    println("devices=$deviceData")
                                    val deviceJson = """{"type": "devices", "devices": $deviceData}"""
                                    outgoing.send(Frame.Text(deviceJson))
                                } else if (text == "Need matrix") {
                                    val arrString = matrixArrayToString(ledMatrixArray)
//                                    println("Sending matrix $arrString")
                                    outgoing.send(Frame.Text(arrString))
                                } else { //if received JSON from site
                                    println("From   user = $text")
                                    //serialPort!!.writeString(text) //send data to Arduino, !!раскомментить для работы
                                    if (text.contains(""""ledDot"""")) {
                                        val gson = Gson()
                                        val myObj = gson.fromJson(text, MatrixLed::class.java)
                                        println("cellColor = ${myObj.cellColor1}")
                                        ledMatrixArray[myObj.normalCurCellNumber] = myObj.cellColor1
                                        ledMatrixArray.forEach { print("$it ") }
                                        if (webSockSessions.size > 1)
                                            webSockSessions.forEach { socket ->
                                                if (socket != thisSession) socket.send(Frame.Text(text))
                                            }
                                    }
                                    if (text.contains("{'device' : 'matrix', 'clear': 'clear' }")) {
                                        if (webSockSessions.size > 1)
                                            webSockSessions.forEach { socket ->
                                                if (socket != thisSession) socket.send(Frame.Text("""{"device" : "matrix", "clear": "clear" }"""))
                                            }
                                    }
                                    if (text.contains("'ardu_name:'")){
                                        println("ardu_name = $text")
//                                        curArdu = text.ardu_name
                                    }
                                    if (text.contains("NeedLogins")){
                                        println("NeedLogins")
                                        val dm = DataManager(PathToData("userData", "userData.json"))
                                        val userData = dm.fromFileToJSON("userData")["user"] as JSONArray
                                        val loginData = JSONArray()
                                        userData.forEach {
                                            val user = it as JSONObject
//                                            println("it = $it")
                                            loginData.put(it["login"])
                                        }
                                        println("loginData = $loginData")
                                        val loginsJson = """{"type": "logins", "logins": $loginData}"""
//
                                        outgoing.send(Frame.Text(loginsJson))
                                        socketToClient = outgoing
                                    }
                                }

                            }
                        }
                    }
                }
            }
            //            trace { application.log.trace(it.buildText()) }
            static {
                // This marks index.html from the 'RoboPortal' folder in resources as the default file to serve.
                // This serves files from the 'RoboPortal' folder in the application resources.
                defaultResource("index.html", "RoboPortal")
                resources("RoboPortal")
            }

            post("/Login") {
                //                call.receiveParameters().forEach { s, list ->  println("$s -> $list")}
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                val pass = receivedParams["password"]
                print("login=$login ")
                println("pass=$pass pass md5 = ${pass?.md5()}")
//                bson: Bson = BSon()
//                val iter = userCollection.find()
//                docs.clear()
//                iter.into(docs);
//                val findLogin = docs.filter { it["login"] == login }
                val dm = DataManager(PathToData("userData", "userData.json"))
                userData = dm.fromFileToJSON("userData")
                val userArray = JSONArray(" ${userData!!["user"]}")
                val findLogin = userArray.filter {
                    val jsonArObj = JSONObject("$it")
                    jsonArObj["login"] == login
                }

                if (findLogin.isNotEmpty()) {
//                    println("!Match! Login = ${findLogin.first()["login"]}")
                    val jsonArObj = JSONObject("${findLogin.first()}")
                    println("!Match! Login = ${jsonArObj["login"]}")
                    if (jsonArObj["pass"] == pass?.md5()) {
//                        println("!Match! pass = ${findLogin.first()["pass"]}")
                        if (login == "Admin") {
                            call.sessions.set(SampleSession(name = "user", value = "Admin"))
//                            call.response.header("user", "Admin")
//                            val cookie = Cookie()
//                            call.response.cookies.append(cookie)
                            call.respondFile(File("resources/RoboPortal/admin3.html"))
                        } else {
                            call.respondFile(File("resources/RoboPortal/userPage.html"))
                        }
                        if (login != null) {
                            loginActive = login
                        }
                    } else {
                        println("!Wrong pass!")
                        call.respondFile(File("resources/RoboPortal/index.html"))
                    }
                } else {
                    println("!No such login!")
                    call.respondFile(File("resources/RoboPortal/index.html"))
                }
            }
            get("/Logout") {
                loginActive = "default";
                println("Logout")
                call.respondFile(File("resources/RoboPortal/index.html"))
            }
            post("/DeleteUser"){
                println("!!DeleteUser")
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                println("login from params = $login")
                val dm = DataManager(PathToData("userData", "userData.json"))
                dm.deleteUSer(login.toString(), "userData")
                call.respondFile(File("resources/RoboPortal/admin3.html"))

            }

            post("/DeleteReg"){
                println("!!DeleteReg")
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                println("login from params = $login")
                val dm = DataManager(PathToData("regData", "regData.json"))
                dm.deleteUSer(login.toString(), "regData")
                call.respondFile(File("resources/RoboPortal/admin3.html"))

            }

            post("/EditUser") {
                println("!!EditUser")
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                println("login from params = $login")
                var pass = receivedParams["pass"]
                val email = receivedParams["email"]
                var fio = receivedParams["fio"]
                println("params=$receivedParams")
                var jo =  JSONObject()
                jo.put("login", login)
                jo.put("fio", fio)
                jo.put("email", email)
                jo.put("pass", pass?.md5())
                jo.put("status", "User")
                jo.put("online", false)
                val dm = DataManager(PathToData("userData", "userData.json"))
                dm.changeUser(login.toString(), jo, "userData")
                call.respondFile(File("resources/RoboPortal/admin3.html"))
            }
//            AddUserFromIndex
            post("/AddUserFromIndex") {                         //для получения запросов на регистрацию
//                todo сделать передачу данных на страницу админа, может сделать отдельную таблицу с запросами на регистрацию
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                var pass = receivedParams["pass"]
                val email = receivedParams["email"]
                var fio = receivedParams["fio"]
                println("params=$receivedParams")
                var jo =  JSONObject()
                jo.put("login", login)
                jo.put("fio", fio)
                jo.put("email", email)
                jo.put("pass", pass?.md5())
                jo.put("status", "User")
                jo.put("online", false)
                val dm = DataManager(PathToData("regData", "regData.json"))
                dm.addUser(jo, "regData")
                val newUserJson = """{"type": "newUser", "newUser": $jo}"""
                socketToClient?.send(Frame.Text(newUserJson))
//todo разобраться с сокетом, чтоб данные отправлять
                call.respondFile(File("resources/RoboPortal/index.html"))
            }

            post("/AddUser") {
                val userArray = JSONArray(" ${userData?.get("user")}")
                println("all users = $userArray")
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                var pass = receivedParams["pass"]
                val email = receivedParams["email"]
                var fio = receivedParams["fio"]
                println("params=$receivedParams")
                var jo =  JSONObject()
                jo.put("login", login)
                jo.put("fio", fio)
                jo.put("email", email)
                jo.put("pass", pass?.md5())
                jo.put("status", "User")
                jo.put("online", false)
//                userArray.put(jo)
                println("all users = $userArray")
//                userData?.put("user", userArray)
                println("new userData = $userData")
                val dm = DataManager(PathToData("userData", "userData.json"))
                dm.addUser(jo, "userData")
//                var insertDocument = Document()
//                receivedParams.forEach { s, list ->
//                    println("   $s   ${list[0]}")
//                    insertDocument[s] = list[0]
//                }
//                insertDocument["pass"] = pass?.md5()
//                insertDocument["status"] = "User"
//                insertDocument["devices"] = "[]"
//                insertDocument["online"] = "false"
//
//                println("Document = $insertDocument")
//                try {
//                    val findLogin = userCollection.find().filter { it["login"] == insertDocument["login"] }
//                    if (findLogin.count() == 0) userCollection.insertOne(insertDocument)
//                    else println("found same login = $findLogin")
//                } catch (e: MongoException) {
//                    e.printStackTrace()
//                }
                call.respondFile(File("resources/RoboPortal/admin3.html"))
            }

            get("/") {
                //  call.respondFile(File("resources/RoboPortal/index.html"))
//                mySession = call.sessions.get<SampleSession>()!!
//                println("Session = $mySession")
                println("Root ")
            }
        }
    }
    server.start(wait = true)
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
