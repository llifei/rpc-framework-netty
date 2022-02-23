package org.lff.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializerCode {

    KRYO(0),
    JSON(1),
    Hessian(2);

    private int code;
}
