package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A processing time period with begin and end timestamps
 */
public class ProcessingTimePeriod extends Event{
    
    private ZonedDateTime beginTimestamp;
    private ZonedDateTime endTimestamp;


    /**
     * @return The timestamp at the beginning of the processing period in epoch milliseconds
     */
    public ZonedDateTime getBeginTimestamp() {
        return this.beginTimestamp;
    }

    /**
     * @param beginTimestamp Epoch milliseconds
     */
    public void setBeginTimestamp(ZonedDateTime beginTimestamp) {
        this.beginTimestamp = beginTimestamp;
    }

    /**
     * @return The timestamp at the end of the processing period in epoch milliseconds
     */
    public ZonedDateTime getEndTimestamp() {
        return this.endTimestamp;
    }

    /**
     * @param endTimestamp Epoch milliseconds
     */
    public void setEndTimestamp(ZonedDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }



    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ProcessingTimePeriod)) {
            return false;
        }
        ProcessingTimePeriod processingTimePeriod = (ProcessingTimePeriod) o;
        return Objects.equals(beginTimestamp, processingTimePeriod.beginTimestamp) && Objects.equals(endTimestamp, processingTimePeriod.endTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginTimestamp, endTimestamp);
    }


    @Override
    public String toString() {
        return "{" +
            " beginTimestamp='" + getBeginTimestamp() + "'" +
            ", endTimestamp='" + getEndTimestamp() + "'" +
            "}";
    }
    

}
