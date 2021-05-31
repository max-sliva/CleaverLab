package com.example

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

fun main(){
    val dM = DataManager(PathToData("devices","deviceData.json"), PathToData("users", "userData.json"))
    val usersJSON = dM.fromFileToJSON("users")
    val devicesJSON = dM.fromFileToJSON("devices")
    val userArray = JSONArray(" ${usersJSON!!["user"]}")
    println("user1 = ${userArray[1]}")
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
        println("file data = $str")
        return JSONObject(str)
    }

    fun fromJSONtoFile(jo: JSONObject, dataName: String){ //метод для записи из json в файл
        File(pathsToFiles.get(dataName)).writeText(jo.toString())
    }

    fun addUser(userData: JSONObject){

    }

    fun changeUser(login: String, userData: JSONObject){

    }

}