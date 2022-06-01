package hmb.antlr4.trans;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.Transition;

public record Triple(ATNState first,
                     Transition transition,
                     ATNState second) {
}
