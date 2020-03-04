package com.example

//import io.ktor.client.features.websocket.WebSockets
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.google.gson.Gson
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.MongoException
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.html.insert
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.response.header
import io.ktor.response.respondFile
import io.ktor.routing.post
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.bson.Document
import org.bson.BsonDocument
import org.slf4j.LoggerFactory
import java.io.File

data class SampleSession(val name: String, val value: String)

fun main(args: Array<String>) {
    val mongoUrl = "localhost";
    val mongoClient = MongoClient(mongoUrl, 27017)
    var loginActive = "default"
    var sendCount = 0;
    //для отключения логгирования mongo
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    var mySession: SampleSession

    val mongoDatabase = mongoClient.getDatabase("local")
    var userCollection = mongoDatabase.getCollection("user")
    println("From Mongo = $userCollection")
    val docs: ArrayList<Document> = ArrayList<Document>()
    val devices = ArrayList<Document>()
    val usersCount = userCollection.countDocuments()
    println("usersCount = $usersCount")
    val deviceCollection = mongoDatabase.getCollection("device")
    val deviceCount = deviceCollection.countDocuments()
    println("deviceCount = $deviceCount")
    var str: String = "111".md5()
    println("str=$str")
    val server = embeddedServer(Netty, port = 80) {
        install(WebSockets)
        install(Sessions) {
            cookie<SampleSession>("ROBO_COOKIE")
        }

        routing {
            webSocket("/") {
                println("onConnect")
//                outgoing.send(Frame.Text(loginActive))
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site: $text")
                            if (loginActive == "Admin") {
                                if (text == "NeedUsers" || text == "addUser!") { //if from site came request for users
                                    val iter = userCollection.find()
                                    docs.clear()
                                    iter.into(docs);
                                    println("users=$docs")
                                    val userCaps = arrayListOf("login", "fio", "status", "devices", "online")
                                    outgoing.send(Frame.Text(arrayListToJSON(docs, userCaps, "users")))
                                }
                                else if (text == "NeedDevices") { //if from site came request for devices
                                    val iter = deviceCollection.find()
                                    devices.clear()
                                    iter.into(devices);
                                    println("devices=$devices")
                                    val deviceCaps = arrayListOf("type", "name", "active")
                                    outgoing.send(Frame.Text(arrayListToJSON(devices, deviceCaps, "devices")))
                                }
                                else {
                                    println("From site: $text")
                                }
                            } else {
                                println("Not Admin")
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
                val iter = userCollection.find()
                docs.clear()
                iter.into(docs);

                val findLogin = docs.filter { it["login"] == login }
                if (findLogin.isNotEmpty()) {
                    println("!Match! Login = ${findLogin.first()["login"]}")
                    if (findLogin.first()["pass"] == pass?.md5()) {
                        println("!Match! pass = ${findLogin.first()["pass"]}")
                        if (login=="Admin") {
                            //call.sessions.set(SampleSession(name = "user", value = "Admin"))
                            call.response.header("user", "Admin")
                            call.respondFile(File("resources/RoboPortal/admin3.html"))
                        }
                        else {
                            call.respondFile(File("resources/RoboPortal/userPage.html"))
                        }
                        if (login != null) {
                            loginActive = login
                        }
                    } else {
                        println("!Wrong pass!")
                    }
                } else {
                    println("!No such login!")
                }
            }
            get("/Logout"){
                loginActive = "default";
                println("Logout")
                call.respondFile(File("resources/RoboPortal/index.html"))
            }
            post ("/AddUser"){
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                var pass = receivedParams["pass"]
                println("params=$receivedParams")
                var insertDocument = Document()
                receivedParams.forEach { s, list ->
                    println("   $s   ${list[0]}")
                    insertDocument[s] = list[0]
                }
                insertDocument["pass"] = pass?.md5()
                insertDocument["status"] = "User"
                insertDocument["devices"] = "[]"
                insertDocument["online"] = "false"

                println("Document = $insertDocument")
                try {
                    val findLogin= userCollection.find().filter{it["login"] == insertDocument["login"]}
                    if (findLogin.count()==0) userCollection.insertOne(insertDocument)
                    else println("found same login = $findLogin")
                } catch (e: MongoException) {
                    e.printStackTrace()
                }
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
