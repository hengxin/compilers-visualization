package hmb.utils.tools;


import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.dfa.DFAState;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static AtnStateMsg makeATNState(ATNConfig config, Map<ATNState, ATNState> mapper) {
        return AtnStateMsg.newBuilder()
                .setAtnStateNumber(mapper.getOrDefault(config.state, config.state).stateNumber)
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
