package sepm.dsa.service.path;

import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PathServiceDijkstraJGraphImpl<E extends PathEdge> implements PathService<E> {
    /**
     * Calculates the shortest path from startNode to any endPoint.
     *
     * @param nodes     List of all nodes.
     * @param edges     List of edges.
     * @param startNode Starting node of the search.
     * @param endNodes  List of end nodes. If to end points are equally distant to the start node, it's not defined
     *                  which one will be chosen.
     * @return List of edges. May be empty if the starting node is in the list of endNodes.
     * @throws java.lang.IllegalArgumentException    if one of the argument is null or if one of the edge's source or target is not found in the list of nodes.
     * @throws sepm.dsa.service.path.NoPathException if no path was found.
     */
    @Override
    public List<E> findShortestPath(List nodes, List edges, PathNode startNode, Collection endNodes) throws NoPathException {
        if (nodes == null || edges == null || startNode == null || endNodes == null) {
            throw new IllegalArgumentException("Expected no null parameter");
        }

        Iterator<ProxyWeightedEdge<E>> proxiedEdges = edges.stream().map(e -> new ProxyWeightedEdge<>((E) e)).iterator();

        Graph<PathNode, ProxyWeightedEdge<E>> graph = initializeGraph(nodes, proxiedEdges);
        List<ProxyWeightedEdge<E>> resultList = getShortestPath(startNode, endNodes, graph);

        if (resultList == null) {
            throw new NoPathException();
        } else {
            List<E> returnList = new ArrayList<>(resultList.size());
            resultList.stream().forEach(e -> returnList.add(e.getEdge()));
            return returnList;
        }
    }

    /**
     * Returns the shortest path for the given parameters
     *
     * @param startNode the starting node.
     * @param endNodes  the ending nodes.
     * @param graph     the graph on which the path is searched for.
     * @return a list of the edges from A to B or null if no result could be found.
     */
    private List<ProxyWeightedEdge<E>> getShortestPath(PathNode startNode, Collection<PathNode> endNodes, Graph<PathNode, ProxyWeightedEdge<E>> graph) {
        int costs = Integer.MAX_VALUE;
        List<ProxyWeightedEdge<E>> resultList = null;

        for (PathNode endNode : endNodes) {
            List<ProxyWeightedEdge<E>> pathBetween = DijkstraShortestPath.findPathBetween(graph, startNode, endNode);

            if (pathBetween == null) {
                continue;
            }

            int pathCosts = pathBetween.stream().mapToInt(PathEdge::getPathCosts).sum();
            if (pathCosts < costs) {
                costs = pathCosts;
                resultList = pathBetween;
            }
        }
        return resultList;
    }

    /**
     * Initializes graph with nodes and edges
     *
     * @param nodes the graph's nodes
     * @param edges the graph's edges
     * @return the initialized graph
     */
    private Graph<PathNode, ProxyWeightedEdge<E>> initializeGraph(List<PathNode> nodes, Iterator<ProxyWeightedEdge<E>> edges) {
        SimpleWeightedGraph<PathNode, ProxyWeightedEdge<E>> graph = new SimpleWeightedGraph<>((sourceVertex, targetVertex) -> null);

        nodes.forEach(graph::addVertex);
        edges.forEachRemaining(e -> graph.addEdge(e.getStart(), e.getEnd(), e));

        return graph;
    }

    /**
     * Proxy class to combine the abilities of PathEdge and DefaultWeightedEdge
     * <p/>
     * Sadly JGraph does not provide eny interfaces for edges but requires to extend
     * from their own implementation :(
     */
    private class ProxyWeightedEdge<EDGE extends PathEdge> extends DefaultWeightedEdge implements PathEdge {
        private static final long serialVersionUID = 3713399967102132422L;
        private EDGE edge;

        private ProxyWeightedEdge(EDGE edge) {
            super();
            this.edge = edge;
        }

        public EDGE getEdge() {
            return edge;
        }

        @Override
        public double getWeight() {
            return (double) edge.getPathCosts();
        }

        @Override
        public int getPathCosts() {
            return edge.getPathCosts();
        }

        @Override
        public PathNode getStart() {
            return edge.getStart();
        }

        @Override
        public PathNode getEnd() {
            return edge.getEnd();
        }
    }
}
