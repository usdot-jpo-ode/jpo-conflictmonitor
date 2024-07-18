//package us.dot.its.jpo.conflictmonitor.monitor.algorithms;
//
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.kstream.KStream;
//
///**
// * General interface for an algorithm implemented as a StreamsBuilder that
// * builds a subtopology of a streams topology and plugs into a larger algorithm.
// *
// * @param <TIn> - Input type, may be either a StreamsBuilder to create an unconnected
// *            subtopology, or KStream<>, etc. to construct part of a subtopology.
// * @param <TOut> - Output type (KStream<> etc)
// */
//public interface StreamsBuilderAlgorithm<TIn, TOut> {
//    TOut buildTopology(TIn builder);
//}
//
//
