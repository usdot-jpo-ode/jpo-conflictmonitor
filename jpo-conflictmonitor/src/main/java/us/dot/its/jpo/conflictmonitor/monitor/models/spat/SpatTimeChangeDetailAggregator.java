package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SpatTimeChangeDetailAggregator {

    private final static Logger logger = LoggerFactory.getLogger(SpatTimeChangeDetailAggregator.class);

    @Getter
    @Setter
    private ArrayList<SpatTimeChangeDetail> spatTimeChangeDetails = new ArrayList<>();

    @Getter
    @Setter
    private int messageBufferSize = 2;


    // Parameterless constructor needed by Jackson
    public SpatTimeChangeDetailAggregator() { }

    public SpatTimeChangeDetailAggregator(int messageBufferSize){
        if(messageBufferSize < 2){
            messageBufferSize = 2;
            logger.warn("Supplied Message Buffer Size is < 2. Message Buffer size set to 2. Minimum Message buffer size is 2");
        }
        this.messageBufferSize = messageBufferSize;
    }

    private Comparator<SpatTimeChangeDetail> spatTimeChangeDetailsComparator = new Comparator<SpatTimeChangeDetail>() {
        @Override
        public int compare(SpatTimeChangeDetail spatTCD1, SpatTimeChangeDetail spatTCD2) {
            long t1 = spatTCD1.getTimestamp();
            long t2 = spatTCD2.getTimestamp();
            if (t2 < t1) {
                return 1;
            } else if (t2 == t1) {
                return 0;
            } else {
                return -1;
            }
        }
    };

    public void sort(){
        Collections.sort(this.spatTimeChangeDetails, spatTimeChangeDetailsComparator);
    }

    public SpatTimeChangeDetailPair add(SpatTimeChangeDetail spatTimeChangeDetail){

        int x = Collections.binarySearch(this.spatTimeChangeDetails, spatTimeChangeDetail, spatTimeChangeDetailsComparator);
        int index = 0;
        if (x < 0){
            index = -1 * (x+1);
        }else{
            index = x;
        }
        this.spatTimeChangeDetails.add(index, spatTimeChangeDetail);


        if(this.spatTimeChangeDetails.size() >= messageBufferSize){
            SpatTimeChangeDetailPair outputPair = new SpatTimeChangeDetailPair(
                this.spatTimeChangeDetails.get(0),
                this.spatTimeChangeDetails.get(1)
            );
            this.spatTimeChangeDetails.remove(0);
            // logger.info("Emitting Spat Pair First {}  {}  Second {}   {}" + outputPair.getFirst().getTimestamp(),outputPair.getFirst().getIntersectionID(), outputPair.getSecond().getTimestamp(),outputPair.getSecond().getOriginIP());
            long delta = Math.abs(outputPair.getFirst().getTimestamp() - outputPair.getSecond().getTimestamp());
            if(delta < 150){
                //logger.info("Emitting Spat Pair First {}  {}  Second {}   {}", outputPair.getFirst().getTimestamp(),outputPair.getFirst().getIntersectionID(), outputPair.getSecond().getTimestamp(), outputPair.getSecond().getIntersectionID());
                return outputPair;
            }else{
                return null;
            }
        }
        else{
            return null;
        }

    }
    





}
