package org.geekbang.time.totry.functional.scala.basic

/**
 * 类和对象
 */
object ClassAndObject02 extends App {
  val fred = new Employee
  fred.name = "Fred"
  fred.salary = 50000
  println(fred)
}

class Person {
  var name = ""
  override def toString = getClass.getName + "[name=" + name + "]"
}
//Scala重写一个非抽象方法，必须用override修饰符。
class Employee extends Person {
  var salary = 0.0
  override def toString = super.toString + "[salary=" + salary + "]"
}

