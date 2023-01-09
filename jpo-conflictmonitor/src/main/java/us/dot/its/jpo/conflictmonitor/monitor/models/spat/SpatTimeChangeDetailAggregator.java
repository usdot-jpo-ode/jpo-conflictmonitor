package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SpatTimeChangeDetailAggregator {
    private ArrayList<SpatTimeChangeDetail> spatTimeChangeDetails = new ArrayList<>();
    private int messageBufferSize;

    public SpatTimeChangeDetailAggregator(int messageBufferSize){
        if(messageBufferSize < 2){
            messageBufferSize = 2;
            System.out.println("Supplied Message Buffer Size is < 2. Message Buffer size set to 2. Minimum Message buffer size is 2");
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
            return outputPair;
        }
        else{
            return null;
        }

    }
    


    public ArrayList<SpatTimeChangeDetail> getSpatTimeChangeDetails() {
        return spatTimeChangeDetails;
    }

    public void setSpatTimeChangeDetails(ArrayList<SpatTimeChangeDetail> spatTimeChangeDetails) {
        this.spatTimeChangeDetails = spatTimeChangeDetails;
    }

    public int getMessageBufferSize() {
        return messageBufferSize;
    }


    public void setMessageBufferSize(int messageBufferSize) {
        this.messageBufferSize = messageBufferSize;
    }


}
