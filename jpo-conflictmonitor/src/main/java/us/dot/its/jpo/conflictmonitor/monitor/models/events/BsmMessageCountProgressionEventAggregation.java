package us.dot.its.jpo.conflictmonitor.monitor.models.events;

public class BsmMessageCountProgressionEventAggregation
    extends EventAggregation<BsmMessageCountProgressionEvent>{


    public BsmMessageCountProgressionEventAggregation() {
        super("BsmMessageCountProgressionAggregation");
    }

    private String messageType;
    private String vehicleId;

    @Override
    public void update(BsmMessageCountProgressionEvent event) {
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
