## Modular Architecture

The suggested method for creating a modular Streams algorithm is to create an interface containing one or more methods to perform inline streams processing. The signatures of these methods depends on the specific application.  The interface then plugs into another larger interface that contains it.  

For example, suppose we have an existing streams algorithm, named `SuperStreamsAlgorithm`, that we want to add additional functionality to.  Suppose the new functionality needs to consume two existing streams and produce a KTable that will be used in the larger algorithm.

We create a hierarchy of interfaces for the new algorithm.  To the `SubStreamsAlgorithm` interface we add a method that describes the inputs and outputs, like:

```java
// Note the base sub-algoirithm extends "ConfigurableAlgorithm", 
// because this algorithm can't be independently started and stopped
public interface SubAlgorithm
    extends ConfigurableAlgorithm<SubParameters> { 
}

public interface SubStreamsAlgorithm extends SubAlgorithm {
  
  KTable<Key, OutputType> buildTopology(KStream<Key, Type1> inputStream1, KStream<Key, Type2> inputStream2);

}
```

Then we add get/set methods to the larger algorithm interface for the sub-algorithm to plug into:

```java
// Note the super-algorithm extends Algorithm which includes start/stop methods
public interface SuperAlgorithm
    extends Algorithm<SuperParameters> {

    SubAlgorithm getSubAlgorithm();
    void setSubAlgorithm(SubAlgorithm subAlgorithm);

}

public interface SuperStreamsAlgorithm
    extends SuperAlgorithm {
}
```

And within the topology that implements `SuperStreamsAlgorithm` we call `SubStreamsAlgorithm.buildTopology()` at the appropriate place, something like:

```java
public class SuperTopology implements SuperStreamsAlgorithm, BaseStreamsTopology<SuperParameters> {

    private SubStreamsTopology subStreamsTopology;

    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        // Do stuff

        KStream<Key, Type1> stream1 = builder.stream(inputTopic1);
        KStream<Key, Type2> stream2 = builder.stream(inputTopic2);

        // SubAlgorithm runs here
        KTable<Key, OutputType> table = subStreamsTopology.buildTopology(stream1, stream2);

        // Do something with the table
        table.join(// yada yada...
       
        // Do more stuff

        return builder.build();
    }


}
```






