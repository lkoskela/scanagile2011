import static java.lang.Math.abs;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class BoxConnectorMatcher extends BaseMatcher<BufferedImage> {
	private final Box box1;
	private final Box box2;
	private final Diagram diagram;

	BoxConnectorMatcher(Diagram diagram, Box box1, Box box2) {
		this.diagram = diagram;
		this.box1 = box1;
		this.box2 = box2;
	}

	@Override
	public boolean matches(Object o) {
		BufferedImage image = (BufferedImage) o;
		Point start = findEdgePointFor(box1);
		Point end = findEdgePointFor(box2);
		return new PathAlgorithm(image).areConnected(start, end);
	}

	private Point findEdgePointFor(final Box box1) {
		Point a = diagram.positionOf(box1);
		return new Point(a.x + (box1.width() / 2), a.y - (box1.height() / 2));
	}

	@Override
	public void describeTo(Description d) {
		d.appendText("connecting line exists between " + box1 + " and " + box2);
	}
	
	public static class PathAlgorithm {

		private final BufferedImage image;
		private Set<Point> visitedPoints;
		private int lineColorRGB;

		public PathAlgorithm(BufferedImage image) {
			this.image = image;
		}

		public boolean areConnected(Point start, Point end) {
			visitedPoints = new HashSet<Point>();
			lineColorRGB = image.getRGB(start.x, start.y);
			return areSomehowConnected(start, end);
		}

		private boolean areSomehowConnected(Point start, Point end) {
			visitedPoints.add(start);
			if (areDirectlyConnected(start, end)) {
				return true;
			}
			for (Point next : unvisitedNeighboursOf(start)) {
				if (areSomehowConnected(next, end)) {
					return true;
				}
			}
			return false;
		}

		private List<Point> unvisitedNeighboursOf(Point start) {
			List<Point> neighbours = new ArrayList<Point>();
			for (int xDiff = -1; xDiff <= 1; xDiff++) {
				for (int yDiff = -1; yDiff <= 1; yDiff++) {
					Point neighbour = new Point(start.x + xDiff, start.y + yDiff);
					if (!isWithinImageBoundary(neighbour)) {
						continue;
					}
					int pixel = image.getRGB(neighbour.x, neighbour.y);
					if (pixel == lineColorRGB && !visitedPoints.contains(neighbour)) {
						neighbours.add(neighbour);
					}
				}
			}
			return neighbours;
		}

		private boolean isWithinImageBoundary(Point point) {
			if (point.x < 0 || point.y < 0) {
				return false;
			}
			if (point.x >= image.getWidth() || point.y >= image.getHeight()) {
				return false;
			}
			return true;
		}

		private boolean areDirectlyConnected(Point start, Point end) {
			int xDistance = abs(start.x - end.x);
			int yDistance = abs(start.y - end.y);
			return xDistance <= 1 && yDistance <= 1;
		}
	}
}