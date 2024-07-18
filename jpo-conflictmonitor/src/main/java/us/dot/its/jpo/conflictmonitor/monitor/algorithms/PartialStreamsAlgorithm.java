//package us.dot.its.jpo.conflictmonitor.monitor.algorithms;
//
//import org.apache.kafka.streams.kstream.KStream;
//
///**
// * General interface for an algorithm implemented as a part of a subtopology
// * to plug into a larger topology.
// * @param <TKeyIn>
// * @param <TKeyOut>
// * @param <TValueIn>
// * @param <TValueOut>
// */
//public interface PartialStreamsAlgorithm<TKeyIn, TKeyOut, TValueIn, TValueOut>
//{
//    KStream<TKeyOut, TValueOut> buildPartialTopology(KStream<TKeyIn, TValueIn> inputStream);
//}
