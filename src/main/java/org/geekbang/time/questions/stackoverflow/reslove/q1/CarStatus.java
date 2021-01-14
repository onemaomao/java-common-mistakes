package org.geekbang.time.questions.stackoverflow.reslove.q1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CarStatus {
    
    READY(1),
    WAITING(2);

    private int value;

    CarStatus(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static CarStatus getItem(int code){
        for(CarStatus item : values()){
            if(item.getValue() == code){
                return item;
            }
        }
        return null;
    }


}