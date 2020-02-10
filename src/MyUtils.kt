package com.example

import org.bson.Document
import java.util.ArrayList

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