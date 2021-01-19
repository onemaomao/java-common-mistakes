package org.geekbang.time.totry.functional.scala.basic

/**
 * basic scala test
 */
object HelloWorld {


  def main(args: Array[String]) {
    println("Hello, world!") // 输出 Hello World

    println("-----------")
    val myVar = 10
    val myVal = "Hello, Scala!"
    val xmax, ymax = 100  // xmax, ymax都声明为100
    val pa = (40,"Foo")
    println(myVar)
    println(myVal)
    println(xmax,ymax)
    println(pa)

    println("-----------")
      //算术运算符
//    val a = 10
//    val b = 20
//    val c = 25
//    val d = 25
//    println("a + b = " + (a + b))
//    println("a - b = " + (a - b))
//    println("a * b = " + (a * b))
//    println("b / a = " + (b / a))
//    println("b % a = " + (b % a))
//    println("c % a = " + (c % a))

      //关系运算符
//    val a = 10
//    val b = 20
//    println("a == b = " + (a == b))
//    println("a != b = " + (a != b))
//    println("a > b = " + (a > b))
//    println("a < b = " + (a < b))
//    println("b >= a = " + (b >= a))
//    println("b <= a = " + (b <= a))

      //逻辑运算符
//    val a = true
//    val b = false
//    println("a && b = " + (a && b))
//    println("a || b = " + (a || b))
//    println("!(a && b) = " + !(a && b))

    //位运算符
//    val a = 60
//    /* 60 = 0011 1100 */
//    val b = 13
//    /* 13 = 0000 1101 */
//    var c = 0
//    c = a & b /* 12 = 0000 1100 */
//    println("a & b = " + c)
//    c = a | b /* 61 = 0011 1101 */
//    println("a | b = " + c)
//    c = a ^ b /* 49 = 0011 0001 */
//    println("a ^ b = " + c)
//    c = ~a /* -61 = 1100 0011 */
//    println("~a = " + c)
//    c = a << 2 /* 240 = 1111 0000 */
//    println("a << 2 = " + c)
//    c = a >> 2 /* 15 = 1111 */
//    println("a >> 2  = " + c)
//    c = a >>> 2 /* 15 = 0000 1111 */
//    println("a >>> 2 = " + c)

    //赋值运算符
//    var a: Int = 10
//    val b: Int = 20
//    var c: Int = 0
//
//    c = a + b
//    println("c = a + b  = " + c)
//
//    c += a
//    println("c += a  = " + c)
//
//    c -= a
//    println("c -= a = " + c)
//
//    c *= a
//    println("c *= a = " + c)
//
//    a = 10
//    c = 15
//    c /= a
//    println("c /= a  = " + c)
//
//    a = 10
//    c = 15
//    c %= a
//    println("c %= a  = " + c)
//
//    c <<= 2
//    println("c <<= 2  = " + c)
//
//    c >>= 2
//    println("c >>= 2  = " + c)
//
//    c >>= a
//    println("c >>= a  = " + c)
//
//    c &= a
//    println("c &= 2  = " + c)
//
//    c ^= a
//    println("c ^= a  = " + c)
//
//    c |= a
//    println("c |= a  = " + c)


    val x = 30
    val y = 10
    if (x == 30) {
      if (y == 10) println("X = 30 , Y = 10")
    }

  }
}
