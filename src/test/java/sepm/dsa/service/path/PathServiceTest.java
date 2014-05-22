package sepm.dsa.service.path;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class PathServiceTest {

	PathService pathService;

	List<PathNode> stubNodes;
	List<PathEdge> stubEdges;

	@Before
	public void setUp() throws Exception {
		pathService = new PathServiceDijkstraJGraphImpl();

		PathNodeStub node1 = new PathNodeStub();
		PathNodeStub node2 = new PathNodeStub();
		PathNodeStub node3 = new PathNodeStub();

		stubNodes = new ArrayList<>();
		stubNodes.add(node1);
		stubNodes.add(node2);
		stubNodes.add(node3);

		stubEdges = new ArrayList<>();
		stubEdges.add(new PathEdgeStub(node1, node2, 1));
		stubEdges.add(new PathEdgeStub(node1, node3, 1));
		stubEdges.add(new PathEdgeStub(node2, node3, 1));
	}

	@After
	public void tearDown() throws Exception {
		pathService = null;
		stubNodes = null;
		stubEdges = null;
	}

	/******** Illegal Argument Tests ******/

	@Test(expected = IllegalArgumentException.class)
	public void testCallWithNullNodesThrowsInvalidArgumentException() throws Exception {
		pathService.findShortestPath(null, stubEdges, new PathNodeStub(), stubNodes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCallWithNullEdgesThrowsInvalidArgumentException() throws Exception {
		pathService.findShortestPath(stubNodes, null, new PathNodeStub(), stubNodes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCallWithNullStartNodeThrowsInvalidArgumentException() throws Exception {
		pathService.findShortestPath(stubNodes, stubEdges, null, stubNodes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCallWithNullEndNodesThrowsInvalidArgumentException() throws Exception {
		pathService.findShortestPath(stubNodes, stubEdges, new PathNodeStub(), null);
	}

	/*** No Path tests ***/

	@Test(expected = NoPathException.class)
	public void testCallWithoutEndNodesThrowsNoPathException() throws Exception {
		pathService.findShortestPath(stubNodes, stubEdges, new PathNodeStub(), new ArrayList<>());
	}

	@Test(expected = NoPathException.class)
	public void testCallWithoutEdgesThrowsNoPathException() throws Exception {
		pathService.findShortestPath(stubNodes, stubEdges, new PathNodeStub(), new ArrayList<>());
	}

	/**** Success tests ****/

	@Test
	@UseDataProvider("provideStubGraphData")
	public void testCallWithTestSetupReturnsCorrectResult(TestGraph stubGraph, PathNode startNode, List<PathNode> endNodes, int expectedCosts) throws Exception {
		List<PathEdge> path = pathService.findShortestPath(
			stubGraph.getNodes(),
			stubGraph.getEdges(),
			startNode,
			endNodes
		);
		int costs = path.stream().mapToInt(PathEdge::getPathCosts).sum();
		assertEquals(expectedCosts,costs);
	}

	@DataProvider
	public static Object[][] provideStubGraphData() {
		TestGraph graph1 = new TestGraph().invoke();
		ArrayList<PathNode> endNodes1 = new ArrayList<>();
		endNodes1.add(graph1.getNode1());

		TestGraph graph2 = new TestGraph().invoke();
		ArrayList<PathNode> endNodes2 = new ArrayList<>();
		endNodes2.add(graph2.getNode2());

		TestGraph graph3 = new TestGraph().invoke();
		ArrayList<PathNode> endNodes3 = new ArrayList<>();
		endNodes3.add(graph3.getNode1());

		TestGraph graph4 = new TestGraph().invoke();
		ArrayList<PathNode> endNodes4 = new ArrayList<>();
		endNodes4.add(graph4.getNode2());
		endNodes4.add(graph4.getNode4());

		TestGraph graph5 = new TestGraph().invoke();
		ArrayList<PathNode> endNodes5 = new ArrayList<>();
		endNodes5.add(graph5.getNode4());
		endNodes5.add(graph5.getNode2());

		TestGraph graph6 = new TestGraph().invoke();
		ArrayList<PathNode> endNodes6 = new ArrayList<>();
		endNodes6.add(graph6.getNode5());

		return new Object[][] {
			// Self referential case
			{ graph1, graph1.getNode1(), endNodes1, 0}, // A -> {A}

			// Neighbour case (both directions)
			{ graph2, graph2.getNode1(), endNodes2, 5}, // A -> {B}
			{ graph3, graph3.getNode2(), endNodes3, 5}, // B -> {A}

			// Longer case
			{ graph6, graph6.getNode3(), endNodes6, 18}, // C -> {E}

			// Multiple targets
			{ graph4, graph4.getNode1(), endNodes4, 1}, // A -> {B,D}
			{ graph5, graph5.getNode1(), endNodes5, 1}, // A -> {D,B}
		};
	}

	private static class PathNodeStub implements PathNode {}

	private static class PathEdgeStub implements PathEdge {
		int pathCosts;
		PathNode source;
		PathNode target;

		private PathEdgeStub(PathNode source, PathNode target, int pathCosts) {
			this.pathCosts = pathCosts;
			this.source = source;
			this.target = target;
		}

		@Override
		public int getPathCosts() {
			return pathCosts;
		}

		@Override
		public PathNode getStart() {
			return source;
		}

		@Override
		public PathNode getEnd() {
			return target;
		}
	}

	private static class TestGraph {
		private ArrayList<PathNode> nodes;
		private ArrayList<PathEdge> edges;

		private PathNodeStub node1;
		private PathNodeStub node2;
		private PathNodeStub node3;
		private PathNodeStub node4;
		private PathNodeStub node5;

		public PathNodeStub getNode1() {
			return node1;
		}

		public PathNodeStub getNode2() {
			return node2;
		}

		public PathNodeStub getNode3() {
			return node3;
		}

		public PathNodeStub getNode4() {
			return node4;
		}

		public PathNodeStub getNode5() {
			return node5;
		}

		public ArrayList<PathNode> getNodes() {
			return nodes;
		}

		public ArrayList<PathEdge> getEdges() {
			return edges;
		}

		public TestGraph invoke() {
			node1 = new PathNodeStub();
			node2 = new PathNodeStub();
			node3 = new PathNodeStub();
			node4 = new PathNodeStub();
			node5 = new PathNodeStub();

			nodes = new ArrayList<>();
			nodes.add(node1);
			nodes.add(node2);
			nodes.add(node3);
			nodes.add(node4);
			nodes.add(node5);

			edges = new ArrayList<>();
			edges.add(new PathEdgeStub(node1, node2, 5));
			edges.add(new PathEdgeStub(node1, node4, 1));
			edges.add(new PathEdgeStub(node2, node3, 4));
			edges.add(new PathEdgeStub(node2, node4, 99));
			edges.add(new PathEdgeStub(node4, node5, 8));
			return this;
		}
	}
}