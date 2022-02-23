package org.lff.rpc.transport;

import org.lff.rpc.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
