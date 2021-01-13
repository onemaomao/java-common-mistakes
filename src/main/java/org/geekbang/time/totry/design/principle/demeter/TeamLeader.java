package org.geekbang.time.totry.design.principle.demeter;

import java.util.ArrayList;
import java.util.List;


public class TeamLeader {

    public void commandCheckNumber(Employee employee){
        employee.checkNumberOfCourses();
    }
}
