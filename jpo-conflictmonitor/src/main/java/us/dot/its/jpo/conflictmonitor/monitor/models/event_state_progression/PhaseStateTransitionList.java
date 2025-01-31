package us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression;

import java.util.ArrayList;
import java.util.Collection;

public class PhaseStateTransitionList
    extends ArrayList<PhaseStateTransition> {

    public PhaseStateTransitionList() { super(); }

    public PhaseStateTransitionList(Collection<PhaseStateTransition> coll) {
        super(coll);
    }
}
