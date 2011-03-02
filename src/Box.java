import java.util.HashSet;
import java.util.Set;

public class Box {
	private Set<Box> connections = new HashSet<Box>();
	private final int width;
	private final int height;

	public Box(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void connectTo(Box anotherBox) {
		new ConnectionOperation(this, anotherBox);
	}

	private class ConnectionOperation {
		public ConnectionOperation(Box box1, Box box2) {
			box1.addConnection(box2);
			box2.addConnection(box1);
		}
	}

	private void addConnection(Box anotherBox) {
		connections.add(anotherBox);
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public Set<Box> connections() {
		return new HashSet<Box>(connections);
	}

	public boolean isConnectedTo(Box anotherBox) {
		return connections().contains(anotherBox);
	}
}
