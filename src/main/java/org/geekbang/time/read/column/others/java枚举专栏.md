原文出处
https://blog.csdn.net/javazejian/article/details/71333103

枚举实现原理
    通过javap xx.class可以看到枚举
    final class Day extends java.lang.Enum<xx>
    staic values()和 static valueOf()
    还有一段static代码块
    以上都是编译器自动生成的
Enum抽象类常见方法
    见java doc
    ordinal()方法:枚举变量在枚举类中声明的顺序，下标从0开始
    Enum类内部会有一个构造函数，该构造函数只能有编译器调用，我们是无法手动操作的
编译器生成的Values方法与ValueOf方法
    values()方法和valueOf(String name)方法是编译器生成的static方法
    编译器生成的valueOf方法最终还是调用了Enum类的valueOf方法

枚举与Class对象
    当枚举实例向上转型为Enum类型后，values()方法将会失效，也就无法一次性获取所有枚举实例变量，但是由于Class对象的存在，即使不使用values()方法，还是有可能一次获取到所有枚举实例变量的，在Class对象中存在如下方法
    Class对象中有isEnum(), getEnumConstants()两个方法可以使用

枚举的进阶用法
    向enum类添加方法与自定义构造函数
        私有构造,防止被外部调用
关于覆盖enum类方法
    父类Enum中的定义的方法只有toString方法没有使用final修饰，因此只能覆盖toString方法
enum类中定义抽象方法
    见示例代码
enum类与接口
    由于Java单继承的原因，enum类并不能再继承其它类，但并不妨碍它实现接口，因此enum类同样是可以实现多接口
    Thinking in Java的示例
枚举与switch
    见示例代码 EnumDemo4
枚举与单例模式
    饿汉式,懒汉式,双重检查锁,静态内部类,枚举
    除了枚举之外,它们都有两个共同的缺点：
        序列化可能会破坏单例模式，比较每次反序列化一个序列化的对象实例时都会创建一个新的实例
            解决方案:    
            //反序列时直接返回当前INSTANCE
            private Object readResolve() {     
                return INSTANCE;     
            }    
        可以使用反射强行调用私有构造器,解决方式可以修改构造器，让它在创建第二个实例的时候抛异常
    枚举序列化是由jvm保证的，每一个枚举类型和定义的枚举变量在JVM中都是唯一的，
    在枚举类型的序列化和反序列化上，Java做了特殊的规定：在序列化时Java仅仅是将枚举对象的name属性输出到结果中，反序列化的时候则是通过java.lang.Enum的valueOf方法来根据名字查找枚举对象。
    同时，编译器是不允许任何对这种序列化机制的定制的并禁用了writeObject、readObject、readObjectNoData、writeReplace和readResolve等方法，从而保证了枚举实例的唯一性，
    不妨再次看看Enum类的valueOf方法以及writeObject等相关方法
    java.lang.reflect.Constructor.newInstance中也可以看到确实无法使用反射创建枚举实例

EnumMap
EnumMap基本用法
    EnumMap是专门为枚举类型量身定做的Map实现，虽然使用其它的Map（如HashMap）也能完成相同的功能，但是使用EnumMap会更加高效，它只能接收同一枚举类型的实例作为键值且不能为null，由于枚举类型实例的数量相对固定并且有限，所以EnumMap使用数组来存放与枚举类型对应的值，毕竟数组是一段连续的内存空间，根据程序局部性原理，效率会相当高。
    三种构造
        与HashMap的主要不同在于构造方法需要传递类型参数和EnumMap保证Key顺序与枚举中的顺序一致，但请记住Key不能为null。
EnumMap实现原理剖析
    内部有两个数组，长度相同，一个表示所有可能的键(枚举值)，一个表示对应的值，不允许keynull，但允许value为null，键都有一个对应的索引，根据索引直接访问和操作其键数组和值数组，由于操作都是数组，因此效率很高。

EnumSet
与枚举类型一起使用的专用 Set 集合，EnumSet 中所有元素都必须是枚举类型
EnumSet用法
    创建EnumSet并不能使用new关键字，因为它是个抽象类，而应该使用其提供的静态工厂方法，EnumSet的静态工厂方法比较多
        noneOf ,allOf ,range ,complementOf ,of
    什么时候使用EnumSet比较恰当的，事实上当需要进行位域运算，就可以使用EnumSet提到位域
EnumSet实现原理剖析
    理解位向量
    EnumSet原理







