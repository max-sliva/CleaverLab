package com.example

import java.math.BigInteger
import java.security.MessageDigest

fun main(){
//    var temp: String = "1 500"
//    var num = 0
//    temp.forEachIndexed { i, t ->
//        if (t !in '0'..'9') num = i
//    }
//    println("num = $num")

//    println("${temp.indexOf(' ')}")
//    val temp1 = temp.removeRange(temp.indexOf(' '), temp.indexOf(' ')+1)
//    println("temp = $temp1")
    print("Input string: ")
    val pass = readLine()
    println()
    val passMD5 = pass?.md5()
    println("in md5: $passMD5")
//    print("Input string: ")
//    val pass2 = readLine()
//    if (pass2?.md5().equals(passMD5)) {
//        println("Ok")
//    }
//    else println("Wrong")
//    println()


}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}