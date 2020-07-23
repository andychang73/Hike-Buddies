package com.example.springsecurityjwt.util;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Status<T> {
    private int status;
    private String msg;
    private Object data;

    public Status(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public Status(int status, T data){
        this.status = status;
        this.data = data;
    }
}
