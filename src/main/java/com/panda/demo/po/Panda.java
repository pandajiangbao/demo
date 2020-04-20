package com.panda.demo.po;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Panda
 * @date 4/19/2020
 */
@Document
@Data
@Builder
public class Panda {
    @Id
    private String id;
    @Indexed
    private String name;
}
