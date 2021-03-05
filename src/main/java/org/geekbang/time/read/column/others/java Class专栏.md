原味出处
https://blog.csdn.net/javazejian/article/details/73413292

类加载的机制的层次结构
类加载过程
加载、链接(验证、准备、解析)、初始化
加载:通过全限定名查找字节码文件,字节码文件创建Class对象。
验证:确保Class文件的字节流中包含信息符合当前虚拟机要求
    主要包括四种验证，文件格式验证，元数据验证，字节码验证，符号引用验证。
准备:为类变量(即static修饰的字段变量)分配内存并且设置该类变量的初始值即0(如static int i=5;这里只将i初始化为0，至于5的值将在初始化时赋值)，这里不包含用final修饰的static，因为final在编译的时候就会分配了，注意这里不会为实例变量分配初始化，类变量会分配在方法区中，而实例变量是会随着对象一起分配到Java堆中。
解析：主要将常量池中的符号引用替换为直接引用的过程。符号引用就是一组符号来描述目标，可以是任何字面量，而直接引用就是直接指向目标的指针、相对偏移量或一个间接定位到目标的句柄。有类或接口的解析，字段解析，类方法解析，接口方法解析(这里涉及到字节码变量的引用，如需更详细了解，可参考《深入Java虚拟机》)。
初始化：类加载最后阶段，若该类具有超类，则对其进行初始化，执行静态初始化器和静态初始化成员变量(如前面只初始化了默认值的static变量将会在这个阶段赋值，成员变量也将被初始化)。

是不是可以简单理解
加载:class文件->Class对象
验证:验证Class文件字节流包含的信息符合JVM
准备:非final的static赋0值
解析：常量池的符号引用->直接引用。描述目标的符号变为直接指向目标的指针
初始化：初始化父类,执行静态初始化器和静态成员变量


类加载器间的关系
    启动类加载器，由C++实现，没有父类。
    拓展类加载器(ExtClassLoader)，由Java语言实现，父类加载器为null
    系统类加载器(AppClassLoader)，由Java语言实现，父类加载器为ExtClassLoader
    自定义类加载器，父类加载器肯定为AppClassLoader。
    代码示例: FileClassLoader
Launcher代码解读
    首先创建拓展类加载器
    再创建AppClassLoader并把extcl作为父加载器传递给AppClassLoader
    ExtClassLoader的构造器可以看到是调用父类构造URLClassLoader传递null作为parent
    ExtClassLoader的父类为null，而AppClassLoader的父加载器为ExtClassLoader，
    所有自定义的类加载器其父加载器只会是AppClassLoader，注意这里所指的父类并不是Java继承关系中的那种父子关系。

类与类加载器
在JVM中表示两个class对象是否为同一个类对象存在两个必要条件
    类的完整类名必须一致，包括包名。
    加载这个类的ClassLoader(指ClassLoader实例对象)必须相同。

