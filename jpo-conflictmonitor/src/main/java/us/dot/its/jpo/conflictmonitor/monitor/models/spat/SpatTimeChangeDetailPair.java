package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

public class SpatTimeChangeDetailPair {
    private SpatTimeChangeDetail first;
    private SpatTimeChangeDetail second;

    public SpatTimeChangeDetailPair(SpatTimeChangeDetail first, SpatTimeChangeDetail second) {
        this.first = first;
        this.second = second;
    }

    public SpatTimeChangeDetail getFirst() {
        return first;
    }
    public void setFirst(SpatTimeChangeDetail first) {
        this.first = first;
    }
    public SpatTimeChangeDetail getSecond() {
        return second;
    }
    public void setSecond(SpatTimeChangeDetail second) {
        this.second = second;
    }

    
}
