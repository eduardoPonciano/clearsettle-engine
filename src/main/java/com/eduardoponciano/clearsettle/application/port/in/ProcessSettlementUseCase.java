package com.eduardoponciano.clearsettle.application.port.in;

public interface ProcessSettlementUseCase {
    void execute(SettlementCommand command);
}
