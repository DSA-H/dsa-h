package sepm.dsa.service.path;

import java.util.Collection;
import java.util.List;

public interface PathService<E extends PathEdge> {
	/**
	 * Calculates the shortest path from startNode to any endPoint.
	 *
	 * @param nodes     List of all nodes.
	 * @param edges     List of edges.
	 * @param startNode Starting node of the search.
	 * @param endNodes  List of end nodes. If to end points are equally distant to the start node, it's not defined
	 *                  which one will be chosen.
	 * @return List of edges. May be empty if the starting node is in the list of endNodes.
	 *
	 * @throws java.lang.IllegalArgumentException if one of the argument is null or if one of the edge's source or target is not found in the list of nodes.
	 * @throws sepm.dsa.service.path.NoPathException if no path was found.
	 */
	List<E> findShortestPath(List<? extends PathNode> nodes, List<E> edges, PathNode startNode, Collection<? extends PathNode> endNodes) throws NoPathException;
}
