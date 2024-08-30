package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import java.util.ArrayList;
import java.util.Collection;

public class SpatTransitionList
    extends ArrayList<SpatTransition> {

    public SpatTransitionList() { super(); }

    public SpatTransitionList(Collection<SpatTransition> coll) {
        super(coll);
    }
}
