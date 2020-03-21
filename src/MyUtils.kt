package com.example

import org.bson.Document
import java.util.ArrayList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.DatagramSocket
import java.net.InetAddress

fun arrayListToJSON(docs: ArrayList<Document>, caps: ArrayList<String>, typeStr: String): String {
    var strJSON = """{"type": "$typeStr", "$typeStr": ["""
//    println("from function = $docs")
    docs.forEach {
//        strJSON += """{"login": "${it["login"]}", "fio": "${it["fio"]}", "status": "${it["status"]}", "devices": "${it["devices"]}", "online": "${it["online"]}"},"""
        strJSON += """{"""
        var temp = it
        caps.forEach { strJSON += """"$it": "${temp[it]}",""" }
        strJSON = strJSON.replaceRange(strJSON.length-1, strJSON.length, "},")
//        strJSON += """},"""
    }

    strJSON = strJSON.replaceRange(strJSON.length-1, strJSON.length, "]}")
//    println("strJSON = $strJSON")
    return strJSON
}

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