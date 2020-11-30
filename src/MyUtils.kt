package com.example

//import org.bson.Document
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.io.File
import java.net.DatagramSocket
import java.net.InetAddress
import org.json.JSONObject

fun main(){
    val path = System.getProperty("user.dir")
    println("Working Directory = $path")
    val jsonObj =  fromFileToJSON("userData.json")
//    val answer = JSONObject("""{"name":"test name", "age":25}""")
    println("user = ${jsonObj["user"]}")
    val jsonArray = JSONArray(" ${jsonObj["user"]}")
    val findLogin = jsonArray.filter {
        val jsonArObj = JSONObject("$it")
        jsonArObj["login"] == "Admin"
    }
    if (findLogin.isNotEmpty()) {
        val jsonArObj = JSONObject("${findLogin.first()}")
        println("!Match! Login = ${jsonArObj["login"]}")

    }

//    println("user0 = ${jsonArray[0]}")
//    val jsonArObj = JSONObject("${jsonArray[0]}")
//    println("user0[login] = ${jsonArObj["login"]}")
//    val deviceJSON = fromFileToJSON("deviceData.json")
//    println("device = ${deviceJSON["device"]}")
}

//fun jsonToFile(){
//
//}

fun fromFileToJSON(fileName: String): JSONObject {
    val str =  File(fileName).readText(Charsets.UTF_8)
    println("file data = $str")
    return JSONObject(str)
}

fun fromJSONtoFile(jo: JSONObject, fileName: String){
    File(fileName).writeText(jo.toString())
}

fun matrixArrayToString(matrixArray: Array<String>): String{
    var str = """{"device": "matrix", "matrixType": "all", "matrixArray": ["""
    matrixArray.forEach { str +=  """"$it","""}
    str = str.replaceRange(str.length-1, str.length, "]}")

    return  str
}

//fun arrayListToJSON(docs: ArrayList<Document>, caps: ArrayList<String>, typeStr: String): String {
//    var strJSON = """{"type": "$typeStr", "$typeStr": ["""
////    println("from function = $docs")
//    docs.forEach {
////        strJSON += """{"login": "${it["login"]}", "fio": "${it["fio"]}", "status": "${it["status"]}", "devices": "${it["devices"]}", "online": "${it["online"]}"},"""
//        strJSON += """{"""
//        var temp = it
//        caps.forEach { strJSON += """"$it": "${temp[it]}",""" }
//        strJSON = strJSON.replaceRange(strJSON.length-1, strJSON.length, "},")
////        strJSON += """},"""
//    }
//
//    strJSON = strJSON.replaceRange(strJSON.length-1, strJSON.length, "]}")
////    println("strJSON = $strJSON")
//    return strJSON
//}

fun getCurrentIp(): String {
    var ip: String = ""
    DatagramSocket().use { socket ->
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
        ip = socket.localAddress.hostAddress
        println(ip)
    }
    return ip
}

fun getMapFromJson(json: String): Map<String, Any> {
//    try {
//        JSONObject(test);
//    } catch (ex: JSONException) {
//        try {
//            JSONArray(test);
//        } catch (ex1: JSONException) {
//            return false;
//        }
//    }
//    var myGson = Gson()
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, Any>>() {}.type

    var tutorialMap: Map<String, Any> = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
    tutorialMap.forEach { println(it) }
//    myGson.
    return tutorialMap;
}