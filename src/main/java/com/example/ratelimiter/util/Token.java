package com.example.ratelimiter.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
* The Class is used to define the structure of Token. This is used for the TokenBucket and LeakyBucket implementations
* */
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class Token {

    private String UUID;
    private LocalDateTime time;

    public Token( String UUID, LocalDateTime time ){
        this.UUID = UUID;
        this.time = time;
    }

    public String getUUID() {
        return UUID;
    }
}
