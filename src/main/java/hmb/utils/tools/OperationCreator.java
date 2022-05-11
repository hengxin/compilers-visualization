package hmb.utils.tools;


import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.dfa.DFAState;

import java.util.List;
import java.util.Map;

import static hmb.protobuf.Response.*;

public abstract class OperationCreator {

    private OperationCreator() {
    }

    public static OperationWrapper makeOperation(StartStateClosureOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.StartStateClosure)
                .setStartStateClosureOperation(operation)
                .build();
    }

    public static OperationWrapper makeOperation(AddNewDFAStateOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.AddNewDFAState)
                .setAddNewDFAStateOperation(operation)
                .build();
    }

    public static OperationWrapper makeOperation(AddNewEdgeOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.AddNewEdge)
                .setAddNewEdgeOperation(operation)
                .build();
    }

    public static OperationWrapper makeOperation(ReuseStateOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.ReuseState)
                .setReuseStateOperation(operation)
                .build();
    }

    public static OperationWrapper makeOperation(SwitchTableOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.SwitchTable)
                .setSwitchTableOperation(operation)
                .build();
    }

    public static OperationWrapper makeOperation(ConsumeTokenOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.ConsumeToken)
                .setConsumeTokenOperation(operation)
                .build();
    }

    public static OperationWrapper makeOperation(EditTreeOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.EditTree)
                .setEditTreeOperation(operation)
                .build();
    }

    public static OperationWrapper makeOperation(EndAdaptiveOperation operation) {
        return OperationWrapper.newBuilder()
                .setOperationType(OperationType.EndAdaptive)
                .setEndAdaptiveOperation(operation)
                .build();
    }

    private static AtnStateMsg makeATNState(ATNConfig config, Map<ATNState, ATNState> mapper) {
        return AtnStateMsg.newBuilder()
                .setAtnStateNumber(mapper.getOrDefault(config.state, config.state).stateNumber)
                .setContext(config.context.toString(mapper))
                .build();
    }

    public static DFAStateMsg makeDFAState(DFAState dfaState, Map<ATNState, ATNState> mapper) {
        List<AtnStateMsg> atnStateMsgList = dfaState.configs.stream()
                .map(config -> makeATNState(config, mapper))
                .toList();
        return DFAStateMsg.newBuilder()
                .addAllAtnState(atnStateMsgList)
                .setDfaStateNumber(dfaState.stateNumber)
                .build();
    }
}
