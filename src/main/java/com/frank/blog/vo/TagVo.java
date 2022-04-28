package com.frank.blog.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class TagVo implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String tagName;

    private String avatar;
}
