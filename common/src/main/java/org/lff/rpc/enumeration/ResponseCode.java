package org.lff.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "调用方法成功啦！"),
    FAIL(500, "调用方法失败咯~"),
    NOT_FOUND_METHOD(500, "没有找到指定的方法唉..."),
    NOT_FOUND_CLASS(500, "没有找到指定的类唉...");


    private final int code;
    private final String message;
}
