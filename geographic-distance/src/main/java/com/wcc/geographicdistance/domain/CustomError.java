package com.wcc.geographicdistance.domain;

import lombok.Data;

@Data
public class CustomError {
    private Integer errorCode;
    private String errorMessage;
}
