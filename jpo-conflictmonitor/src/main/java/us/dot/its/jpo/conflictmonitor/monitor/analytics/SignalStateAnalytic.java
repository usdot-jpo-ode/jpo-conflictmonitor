package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

public class SignalStateAnalytic {

    private BsmAggregator bsms;
    private SpatAggregator spats;
    private Intersection intersection;

    public BsmAggregator getBsms() {
        return bsms;
    }

    public void setBsms(BsmAggregator bsms) {
        this.bsms = bsms;
    }

    public SpatAggregator getSpats() {
        return spats;
    }

    public void setSpats(SpatAggregator spats) {
        this.spats = spats;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    public SignalStateAnalytic(BsmAggregator bsms, SpatAggregator spats, Intersection intersection){
        this.bsms = bsms;
        this.spats = spats;
        this.intersection = intersection;
    }
}
