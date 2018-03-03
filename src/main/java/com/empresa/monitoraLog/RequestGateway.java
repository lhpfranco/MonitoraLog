package com.empresa.monitoraLog;

import com.empresa.monitoraLog.domain.*;;

public interface RequestGateway {
    String echo(StackTrace request);
}
