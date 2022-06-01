package hmb.antlr4.trans;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.Transition;

public final class Antlr4Utils {
    private Antlr4Utils() {
    }

    public static ATNState nextATNState(Transition transition) {
        return (transition instanceof RuleTransition ruleTransition) ?
                ruleTransition.followState : transition.target;
    }


}
