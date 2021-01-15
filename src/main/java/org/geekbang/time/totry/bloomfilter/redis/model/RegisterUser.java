package org.geekbang.time.totry.bloomfilter.redis.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@Builder
@ToString
public class RegisterUser implements Serializable {
    private Integer userId;
    private String name;
}
