package sepm.dsa.service.path;

public interface PathEdge {
	/**
	 * Returns the costs from A to B.
	 *
	 * @return path costs. Must be >= 0.
	 */
	int getPathCosts();

	PathNode getStart();

	PathNode getEnd();
}
