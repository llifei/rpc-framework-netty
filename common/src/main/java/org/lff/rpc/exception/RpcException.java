package org.lff.rpc.exception;

import org.lff.rpc.enumeration.RpcError;

public class RpcException extends RuntimeException{
    public RpcException(RpcError error){
        super(error.getMessage());
    }
}
