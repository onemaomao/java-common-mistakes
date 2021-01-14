package org.geekbang.time.questions.stackoverflow.reslove.q1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {
    public static void main(String[] args) throws JsonProcessingException {

        //serializing trying
        Car serializingCar = new Car();
        serializingCar.setId(12);
        serializingCar.setStatus(CarStatus.WAITING);

        ObjectMapper mapper = new ObjectMapper();

        String serializingCarString = mapper.writeValueAsString(serializingCar);
        log.info("serializingCarString:{}", serializingCarString);

        //deserializing trying
        String jsonString = "{\"id\":12,\"status\":1}";
        Car deserializingCar = mapper.readValue(jsonString, Car.class);
        log.info("deserializingCar:{}", deserializingCar);
    }
}
