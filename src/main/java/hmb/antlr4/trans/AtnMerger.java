package hmb.antlr4.trans;

import org.antlr.v4.runtime.atn.*;

import java.util.*;
import java.util.function.Predicate;

public class AtnMerger {
    public static Map<ATNState, ATNState> merge(final RuleStartState ruleStartState) {
        final RuleStopState ruleStopState = ruleStartState.stopState;


        LinkedList<ATNState> atnStateList = new LinkedList<>();
        atnStateList.add(ruleStartState);

        Set<ATNState> visitedAtnStateSet = new HashSet<>();
        Set<Transition> visitedTransitionSet = new HashSet<>();

        Map<ATNState, List<Transition>> preTransitionMap = new HashMap<>();

        while (!atnStateList.isEmpty()) {
            final ATNState currentState = atnStateList.removeFirst();
            visitedAtnStateSet.add(currentState);

            for (final Transition transition : currentState.getTransitions()) {
                final ATNState nextState = Antlr4Utils.nextATNState(transition);
                if (!visitedTransitionSet.contains(transition)) {

                    {
                        preTransitionMap.putIfAbsent(nextState, new LinkedList<>());
                        // do not write "else" here
                        preTransitionMap.get(nextState).add(transition);
                    }


                    visitedTransitionSet.add(transition);
                    if (!nextState.equals(ruleStopState)) {
                        atnStateList.add(nextState);
                    }
                }
            }
        }
        visitedAtnStateSet.add(ruleStopState);

        final List<ATNState> onlyOneNext = visitedAtnStateSet.stream().filter(Predicate.not(ruleStopState::equals))
                .filter(s -> s.getTransitions().length == 1)
                .sorted(Comparator.comparingInt(s -> s.stateNumber))
                .toList();

        final List<ATNState> onlyEpsilonNext = visitedAtnStateSet.stream().filter(Predicate.not(ruleStopState::equals))
                .filter(s -> Arrays.stream(s.getTransitions()).allMatch(t -> t instanceof EpsilonTransition))
                .sorted(Comparator.comparingInt(s -> s.stateNumber))
                .toList();

        final List<ATNState> onlyOneEpsilonNext = onlyOneNext.size() < onlyEpsilonNext.size() ?
                onlyOneNext.stream()
                        .filter(s -> Arrays.stream(s.getTransitions()).allMatch(t -> t instanceof EpsilonTransition))
                        .toList() :
                onlyEpsilonNext.stream()
                        .filter(s -> s.getTransitions().length == 1)
                        .toList();


        final List<ATNState> onlyOneBefore = visitedAtnStateSet.stream().filter(Predicate.not(ruleStartState::equals)).filter(preTransitionMap::containsKey)
                .filter(s -> (long) preTransitionMap.get(s).size() == 1)
                .sorted(Comparator.comparingInt(state -> state.stateNumber))
                .toList();

        final List<ATNState> onlyEpsilonBefore = visitedAtnStateSet.stream().filter(Predicate.not(ruleStartState::equals)).filter(preTransitionMap::containsKey)
                .filter(s -> preTransitionMap.get(s).stream().allMatch(t -> t instanceof EpsilonTransition))
                .sorted(Comparator.comparingInt(state -> state.stateNumber))
                .toList();

        final List<ATNState> onlyOneEpsilonBefore = onlyOneBefore.size() < onlyEpsilonBefore.size() ?
                onlyOneBefore.stream()
                        .filter(s -> preTransitionMap.get(s).stream().allMatch(t -> t instanceof EpsilonTransition))
                        .toList() :
                onlyEpsilonBefore.stream()
                        .filter(s -> (long) preTransitionMap.get(s).size() == 1)
                        .toList();


//        System.out.println("onlyOneBefore: " + onlyOneBefore);
//        System.out.println("onlyOneNext: " + onlyOneNext);
//        System.out.println("onlyEpsilonBefore: " + onlyEpsilonBefore);
//        System.out.println("onlyEpsilonNext: " + onlyEpsilonNext);
//        System.out.println("onlyOneEpsilonBefore: " + onlyOneEpsilonBefore);
//        System.out.println("onlyOneEpsilonNext: " + onlyOneEpsilonNext);


        Map<ATNState, ATNState> result = new TreeMap<>(Comparator.comparingInt(state -> state.stateNumber));

        for (final ATNState state : onlyOneEpsilonNext) {
            if (!result.containsKey(state)) {
                LinkedList<ATNState> linkedList = new LinkedList<>();
                ATNState curState = state;
                while (true) {
                    final Transition transition = curState.transition(0);
                    final ATNState next = transition.target;
                    ATNState end = curState;
                    if (true || onlyOneEpsilonBefore.contains(next)) {
                        linkedList.add(curState);
                        end = next;
                    }
                    if (onlyOneEpsilonBefore.contains(next) && onlyOneEpsilonNext.contains(next)) {
                        curState = next;
                    } else {
                        for (ATNState s : linkedList) {
                            result.put(s, end);
                        }
                        break;
                    }
                }

            }
        }

        return result;

    }
}
