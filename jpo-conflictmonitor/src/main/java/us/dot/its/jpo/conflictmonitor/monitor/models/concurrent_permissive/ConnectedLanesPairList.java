package us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive;

import java.util.ArrayList;
import java.util.Collection;

/** 
 * A variant of array list which may contain one or more connectedLanePair objects
 */
public class ConnectedLanesPairList
    extends ArrayList<ConnectedLanesPair> {

    public ConnectedLanesPairList() { super(); }

    public ConnectedLanesPairList(Collection<ConnectedLanesPair> coll) { super(coll); }
}
