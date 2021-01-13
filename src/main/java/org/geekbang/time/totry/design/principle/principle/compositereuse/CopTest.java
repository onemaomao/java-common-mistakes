package org.geekbang.time.totry.design.principle.principle.compositereuse;


public class CopTest {
    public static void main(String[] args) {
        ProductDao productDao = new ProductDao();
        productDao.setConnection(new MySQLConnection());
        productDao.addProduct();
    }
}
