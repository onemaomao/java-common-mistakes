package org.geekbang.time.totry.functional.scala.basic

/**
 * 字符串
 */
object StringDemo {
  //创建字符串
  //val greeting = "Hello World!"
  var greeting:String = "Hello World!";



  def main(args: Array[String]) {
    //println( greeting )

    //创建字符串
/*
    val buf = new StringBuilder;
    buf += 'a'
    buf ++= "bcdef"
    println( "buf is : " + buf.toString );
 */
    //字符串长度
/*
    val palindrome: String = "www.google.com"
    val len: Int = palindrome.length
    println("String Length is : " + len)
*/

    //字符串连接
/*
    val str1 = "谷歌："
    val str2 = "www.google.com"
    val str3 = "谷歌的 Slogan 为："
    val str4 = "geek now！"
    println(str1 + str2)
    println(str3.concat(str4))
*/

    //格式化字符串

    var floatVar = 12.456
    var intVar = 2000
    var stringVar = "极客!"
    var fs = printf("浮点型变量为 " +
      "%f, 整型变量为 %d, 字符串为 " +
      " %s", floatVar, intVar, stringVar)
    println(fs)

    //String 方法 一堆
  }
}
