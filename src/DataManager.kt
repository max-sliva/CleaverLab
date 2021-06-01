package com.example

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

fun main(){
    val dM = DataManager(PathToData("devices","deviceData.json"), PathToData("users", "userData.json"))
    var usersJSON = dM.fromFileToJSON("users")
    val devicesJSON = dM.fromFileToJSON("devices")
    var userArray = JSONArray(" ${usersJSON!!["user"]}")
    println("userArray = ${userArray}")
    var jo =  JSONObject()
    jo.put("login", "user")
    jo.put("fio", "Some user")
    jo.put("email", "qw@mail.ru")
    jo.put("pass", "111".md5())
    jo.put("status", "User")
    jo.put("online", false)
    dM.addUser(jo, "users")
    usersJSON = dM.fromFileToJSON("users")
    userArray = JSONArray(" ${usersJSON!!["user"]}")
    println("userArray = ${userArray}")

/*
    "pass": "",
    "devices": [],
    "online": false,
    "login": "user",
    "fio": "Some User",
    "status": "User"
*/
}

data class PathToData(val dataName: String, val dataPath: String)

class DataManager { //класс для загрузки данных из json-файлов и записи данных в них
    private var pathsToFiles = HashMap<String, String>() //хеш-мап с названием данных и файлом с json-объектом

    constructor(vararg paths: PathToData){ //конструктор с переменным числом параметров
        for (path in paths){
            pathsToFiles.put(path.dataName, path.dataPath)
        }
    }

    fun fromFileToJSON(dataName: String): JSONObject { //метод для чтения из файла в json-объект
        val str =  File(pathsToFiles.get(dataName)).readText(Charsets.UTF_8)
       // println("file data = $str")
        return JSONObject(str)
    }

    fun fromJSONtoFile(jo: JSONObject, dataName: String){ //метод для записи из json в файл
        File(pathsToFiles.get(dataName)).writeText(jo.toString())
    }

    fun addUser(userData: JSONObject, dataName: String){
        var usersJSON = fromFileToJSON(dataName)
        val userArray = JSONArray(" ${usersJSON!!["user"]}")
        userArray.put(userData)
        usersJSON.put("user", userArray)
        fromJSONtoFile(usersJSON, dataName)
    }

    fun changeUser(login: String, userData: JSONObject, dataName: String){
        var usersJSON = fromFileToJSON(dataName)
        val userArray = JSONArray(" ${usersJSON!!["user"]}")
//        userArray.forEach {
//            if (it["login"] == login ) {
//
//            }
//        }
        val findLogin = userArray.filter {
            val jsonArObj = JSONObject("$it")
            jsonArObj["login"] == login
        }
        val index = userArray.indexOf(findLogin[0])
//        println("findLogin = ${findLogin[0]}")
//        println("old user index = $index")
        println("userArray old = ")
        userArray.forEach {
            println(it)
        }
        userArray.put(3, userData)
        println("userArray new = ")
        userArray.forEach {
            println(it)
        }
        usersJSON.put("user", userArray)
//        println("userJSON = $usersJSON")
        fromJSONtoFile(usersJSON, dataName)
    }

    fun deleteUSer(login: String, dataName: String){

    }
}