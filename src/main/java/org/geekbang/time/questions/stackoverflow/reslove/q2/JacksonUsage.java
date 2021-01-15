package org.geekbang.time.questions.stackoverflow.reslove.q2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JacksonUsage {

    public static void jsonToMapDemo() throws JsonProcessingException {
        String line = "{\"ABC\":\"5149427501\",\"DEF\":4168170001,\"GHI\":\"RC81020329801823\",\"JKL\":\"24938699\",\"MNO\":\"941580078\"}";
        ObjectMapper mapper = new ObjectMapper();
        Map readValue = mapper.readValue(line, Map.class);
        System.out.println(readValue);
        System.out.println(readValue.get("ABC"));
        System.out.println(readValue.get("JKL"));

    }




    public static void main(String[] args) throws JsonProcessingException {
        jsonToMapDemo();
    }
}
