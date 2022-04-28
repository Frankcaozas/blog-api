package com.frank.blog.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {

    private boolean success;

    private int code;

    private String msg;

    private Object data;

    public static Result success(Object da){
        return new Result(true,200,"success",da);
    }

    public static Result fail(int code, String msg){
        return new Result(false,code,"fail",msg);
    }
}
