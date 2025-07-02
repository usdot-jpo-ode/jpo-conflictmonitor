package us.dot.its.jpo.conflictmonitor.monitor.health;



import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class TopologyGraph {

    final Graph<Vertex, DefaultEdge> g;



    final LinkList links;

    public TopologyGraph(final String name, final Topology topology) {
        g = new DefaultDirectedGraph<>(DefaultEdge.class);
        links = new LinkList();
        graphTopology(name, topology);
        removeUnlinkedGlobalSubtopologies();
        for (Link link : links) {
            g.addEdge(link.from, link.to);
        }
    }

    public TopologyGraph(Map<String, Topology> topologyMap) {
        try {
            g = new DefaultDirectedGraph<>(DefaultEdge.class);
            links = new LinkList();

            // Add nodes to the graph
            for (var entry : topologyMap.entrySet()) {
                final String name = entry.getKey();
                final Topology topology = entry.getValue();
                graphTopology(name, topology);
            }

            removeUnlinkedGlobalSubtopologies();

            // Add edges to the graph
            for (Link link : links) {
                g.addEdge(link.from, link.to);
            }
        }catch (Exception ex) {
            log.error("Exception creating graph", ex);
            throw new RuntimeException(ex);
        }
    }

    private void removeUnlinkedGlobalSubtopologies() {
        // Remove global subtopology nodes that aren't linked to anything (no global stores or processors)
        ImmutableSet<Vertex> vertices = ImmutableSet.copyOf(g.vertexSet());
        for (final Vertex v : vertices) {
            if (v.isGlobal) {
                boolean hasAnyLinks = links.stream().anyMatch(link -> link.from.equals(v) || link.to.equals(v));
                if (!hasAnyLinks) {
                    g.removeVertex(v);
                }
            }
        }
    }


    void graphTopology(final String name, final Topology topology) {
        final Vertex topologyVertex = vertexTopology(name);
        g.addVertex(topologyVertex);
        TopologyDescription description = topology.describe();
        description.globalStores().forEach(globalStore -> graphGlobalStore(name, topologyVertex, globalStore));
        description.subtopologies().forEach(subtopology -> graphSubtopology(name, subtopology));
    }

    public String exportDOT() {
        DOTExporter<Vertex, DefaultEdge> exporter = getVertexDefaultEdgeDOTExporter();
        Writer writer = new StringWriter();
        exporter.exportGraph(g, writer);

        // Hack for things DOTExporter doesn't support
        String dot = writer.toString();
        var lines = new ArrayList<>(dot.lines().toList());
        lines.add(1, "  node [fontsize=10.0 fontname=Arial];");
        dot = lines.stream().collect(Collectors.joining(System.lineSeparator()));
        return dot;
    }

    private static DOTExporter<Vertex, DefaultEdge> getVertexDefaultEdgeDOTExporter() {
        DOTExporter<Vertex, DefaultEdge> exporter = new DOTExporter<>(vertex -> vertex.id);
        exporter.setVertexAttributeProvider(vertex ->
                Map.of("label", DefaultAttribute.createAttribute(vertex.label),
                        "shape", DefaultAttribute.createAttribute(
                                        switch(vertex.type) {
                                            case "store" -> "cylinder";
                                            case "subtopology" -> "octagon";
                                            default -> "box";
                                        }))
        );
        return exporter;
    }


    void graphGlobalStore(final String topologyName, final Vertex topologyVertex, final TopologyDescription.GlobalStore globalStore) {
        //g.addVertex(label(globalStore));
        graphTopics(topologyVertex, globalStore.source());
        graphProcessor(topologyName, topologyVertex, globalStore.processor());
    }

    void graphSubtopology(final String topologyName, final TopologyDescription.Subtopology subtopology) {
        g.addVertex(vertexSubtopology(topologyName, subtopology));
        subtopology.nodes().forEach(node -> {
            graphNode(topologyName, vertexSubtopology(topologyName, subtopology), node);
        });
    }

    void graphNode(final String topologyName, final Vertex topologyVertex, final TopologyDescription.Node node) {
        if (node instanceof TopologyDescription.Source source) {
            graphTopics(topologyVertex, source);
        } else if (node instanceof TopologyDescription.Sink sink) {
            graphTopics(topologyVertex, sink);
        } else if (node instanceof TopologyDescription.Processor processor) {
            graphProcessor(topologyName, topologyVertex, processor);
        }
    }


    void graphProcessor(final String topologyName, final Vertex topologyVertex, final TopologyDescription.Processor processor) {
        processor.stores().forEach(store -> {
            Vertex storeVertex = vertexStore(topologyName, store);
            g.addVertex(storeVertex);
            // Add links in both directions since processors can read and write stores
            links.add(topologyVertex, storeVertex);
            links.add(storeVertex, topologyVertex);
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


    final String SUBTOPOLOGY = "subtopology";
    final String STORE = "store";
    final String TOPIC = "topic";
    final String GLOBAL = "global";

    Vertex vertexTopology(String name) {
        return new Vertex(
                name + "_" + SUBTOPOLOGY + "_" + GLOBAL,
                SUBTOPOLOGY,
                name + " " + SUBTOPOLOGY + " " + GLOBAL,
                true);
    }

    Vertex vertexSubtopology(final String topologyName, final TopologyDescription.Subtopology subtopology) {
        return new Vertex(
                topologyName + "_" + SUBTOPOLOGY + "_" + subtopology.id(),
                SUBTOPOLOGY,
                topologyName + " " + SUBTOPOLOGY + " " + subtopology.id(),
                true);
    }

    Vertex vertexStore(final String topologyName, final String store) {
        // Prefix topology name to store id to make sure it is unique for multiple topologies
        String uniqueStoreId = topologyName + "-" + store;
        return new Vertex(normalizeId(uniqueStoreId), STORE, store);
    }

    Vertex vertexTopic(final String topic) {
        return new Vertex(normalizeId(topic), TOPIC, topic);
    }

    String normalizeId(String id) {
        return id.replace(".", "_").replace("-", "_");
    }

    static class LinkList extends ArrayList<Link> {
        public void add(Vertex from, Vertex to) {
            this.add(new Link(from, to));
        }
    }

    record Link(
            Vertex from,
            Vertex to) {}

    record Vertex(
            String id,
            String type,
            String label,
            boolean isGlobal) {
        public Vertex(final String id, final String type, final String label) {
            this(id, type, label, false);
        }
    }

}
