package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

public interface MessageIngestAlgorithmFactory {
    MessageIngestAlgorithm getAlgorithm(String algorithmName);
}
