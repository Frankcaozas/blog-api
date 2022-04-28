package com.frank.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleBodyVo implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private String content;
}

