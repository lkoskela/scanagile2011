import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Diagram {

	private Set<Box> boxes = new HashSet<Box>();
	private Map<Box, Point> positions = new HashMap<Box, Point>();

	public void add(Box box, Point centerPosition) {
		boxes.add(box);
		positions.put(box, centerPosition);
	}

	public List<Box> boxes() {
		return new ArrayList<Box>(boxes);
	}

	public List<Connection> connections() {
		Set<Connection> connections = new HashSet<Connection>();
		for (Box box : boxes) {
			for (Box anotherBox : box.connections()) {
				connections.add(new Connection(box, positionOf(box), anotherBox, positionOf(anotherBox)));
			}
		}
		return new ArrayList<Connection>(connections);
	}

	public Point positionOf(Box box) {
		return positions.get(box);
	}
}
