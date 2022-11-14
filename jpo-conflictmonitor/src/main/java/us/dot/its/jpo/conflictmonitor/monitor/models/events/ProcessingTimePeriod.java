package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Objects;

/**
 * A processing time period with begin and end timestamps
 */
public class ProcessingTimePeriod {
    
    private long beginTimestamp;
    private long endTimestamp;


    /**
     * @return The timestamp at the beginning of the processing period in epoch milliseconds
     */
    public long getBeginTimestamp() {
        return this.beginTimestamp;
    }

    /**
     * @param beginTimestamp Epoch milliseconds
     */
    public void setBeginTimestamp(long beginTimestamp) {
        this.beginTimestamp = beginTimestamp;
    }

    /**
     * @return The timestamp at the end of the processing period in epoch milliseconds
     */
    public long getEndTimestamp() {
        return this.endTimestamp;
    }

    /**
     * @param endTimestamp Epoch milliseconds
     */
    public void setEndTimestamp(long endTimestamp) {
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
        return beginTimestamp == processingTimePeriod.beginTimestamp && endTimestamp == processingTimePeriod.endTimestamp;
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
