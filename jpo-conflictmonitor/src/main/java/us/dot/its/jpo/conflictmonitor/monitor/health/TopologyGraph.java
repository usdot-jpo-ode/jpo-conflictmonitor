package us.dot.its.jpo.conflictmonitor.monitor.health;



import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class TopologyGraph {

    final Graph<Vertex, DefaultEdge> g;



    final LinkList links;

    public TopologyGraph(final String name, final Topology topology) {
        g = new DefaultDirectedGraph<>(DefaultEdge.class);
        links = new LinkList();
        graphTopology(name, topology);
    }


    void graphTopology(final String name, final Topology topology) {
        final Vertex topologyVertex = vertexTopology(name);
        g.addVertex(topologyVertex);
        TopologyDescription description = topology.describe();
        description.globalStores().forEach(globalStore -> graphGlobalStore(topologyVertex, globalStore));
        description.subtopologies().forEach(this::graphSubtopology);
        for (Link link : links) {
            g.addEdge(link.from, link.to);
        }
    }

    public String exportDOT() {
        DOTExporter<Vertex, DefaultEdge> exporter = new DOTExporter<>(vertex -> vertex.id);
        exporter.setVertexAttributeProvider(vertex ->
                Map.of("label", DefaultAttribute.createAttribute(vertex.label),
                        "shape", DefaultAttribute.createAttribute(
                                        switch(vertex.type) {
                                            case "store" -> "cylinder";
                                            case "topic" -> "invhouse";
                                            case "subtopology" -> "octagon";
                                            default -> "box";
                                        }))
        );
        Writer writer = new StringWriter();
        exporter.exportGraph(g, writer);

        // Hack for things DOTExporter doesn't support
        String dot = writer.toString();
        var lines = new ArrayList<>(dot.lines().toList());
        lines.add(1, "  node [shape=\"box\"];");
        dot = lines.stream().collect(Collectors.joining(System.lineSeparator()));
        return dot;
    }


    void graphGlobalStore(final Vertex topologyVertex, final TopologyDescription.GlobalStore globalStore) {
        //g.addVertex(label(globalStore));
        graphTopics(topologyVertex, globalStore.source());
        graphProcessor(topologyVertex, globalStore.processor());
    }

    void graphSubtopology(final TopologyDescription.Subtopology subtopology) {
        g.addVertex(vertex(subtopology));
        subtopology.nodes().forEach(node -> {
            graphNode(vertex(subtopology), node);
        });
    }

    void graphNode(final Vertex topologyVertex, final TopologyDescription.Node node) {
        if (node instanceof TopologyDescription.Source source) {
            graphTopics(topologyVertex, source);
        } else if (node instanceof TopologyDescription.Sink sink) {
            graphTopics(topologyVertex, sink);
        } else if (node instanceof TopologyDescription.Processor processor) {
            graphProcessor(topologyVertex, processor);
        }
    }


    void graphProcessor(final Vertex topologyVertex, final TopologyDescription.Processor processor) {
        processor.stores().forEach(store -> {
            Vertex storeVertex = vertexStore(store);
            g.addVertex(storeVertex);
            links.add(topologyVertex, storeVertex);
        });
    }

    void graphTopics(final Vertex topologyVertex, final TopologyDescription.Source source) {
        Pattern pattern = source.topicPattern();
        String topicPattern = pattern != null ? pattern.pattern() : null;
        if (topicPattern != null) {
            Vertex topicVertex = vertexTopic(topicPattern);
            g.addVertex(topicVertex);
            links.add(topicVertex, topologyVertex);
        }

        Set<String> topicSet = source.topicSet();
        if (topicSet != null) {
            topicSet.forEach(topic -> {
                Vertex topicVertex = vertexTopic(topic);
                g.addVertex(topicVertex);
                links.add(topicVertex, topologyVertex);
            });
        }
    }

    void graphTopics(final Vertex topologyVertex, final TopologyDescription.Sink sink) {
        final String extractorClass = sink.topicNameExtractor() != null ? sink.topicNameExtractor().getClass().getName() : null;
        if (extractorClass != null) {
            Vertex topicVertex = vertexTopic(extractorClass);
            g.addVertex(topicVertex);
            links.add(topologyVertex, topicVertex);
        }

        final String topic = sink.topic();
        if (topic != null) {
            Vertex topicVertex = vertexTopic(topic);
            g.addVertex(topicVertex);
            links.add(topologyVertex, topicVertex);
        }
    }



    final String TOPOLOGY = "topology";
    final String SUBTOPOLOGY = "subtopology";
    final String STORE = "store";
    final String TOPIC = "topic";

    Vertex vertexTopology(String name) {

        return new Vertex(TOPOLOGY + "_" + name, TOPOLOGY, TOPOLOGY + ": " + name);
    }

    Vertex vertex(final TopologyDescription.Subtopology subtopology) {
        return new Vertex(SUBTOPOLOGY + "_" + subtopology.id(), SUBTOPOLOGY, SUBTOPOLOGY + ": " + subtopology.id());
    }

    Vertex vertexStore(final String store) {
        return new Vertex(store, STORE, store);
    }

    Vertex vertexTopic(final String topic) {
        return new Vertex(topic.replace(".", "_").replace("-", "_"), TOPIC, topic);
    }



    static class LinkList extends ArrayList<Link> {
        public void add(Vertex from, Vertex to) {
            this.add(new Link(from, to));
        }
    }

    record Link(Vertex from, Vertex to) {
    }

    record Vertex(String id, String type, String label) {
    }

}
