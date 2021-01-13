package org.geekbang.time.totry.design.principle.demeter;


public class LodTest {

    public static void main(String[] args) {
        TeamLeader teamLeader = new TeamLeader();
        Employee employee = new Employee();
        teamLeader.commandCheckNumber(employee);
    }
}
