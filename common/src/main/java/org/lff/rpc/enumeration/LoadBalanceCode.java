package org.lff.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoadBalanceCode {

    RANDOM(0),
    ROUNDBIN(1);

    private int code;
}
