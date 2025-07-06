package com.TwoSeaU.BaData.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeneralException extends RuntimeException{

    private final BaseException baseException;

}
