package org.geekbang.time.totry.functional.scala.basic

/**
 * 迭代器
 */
object IteratorDemo {

  def main(args: Array[String]): Unit = {
/*
    val it = Iterator("Baidu", "Google", "Github", "Taobao")
    while (it.hasNext){
      println(it.next())
    }
*/

    //查找最大与最小元素
/*
    val ita = Iterator(20,40,2,50,69, 90)
    val itb = Iterator(20,40,2,50,69, 90)
    println("最大元素是：" + ita.max )
    println("最小元素是：" + itb.min )
*/

    //获取迭代器的长度
    val ita = Iterator(20,40,2,50,69, 90)
    val itb = Iterator(20,40,2,50,69, 90)
    println("ita.size 的值: " + ita.size )
    println("itb.length 的值: " + itb.length )
  }

  //Iterator 常用方法
}
