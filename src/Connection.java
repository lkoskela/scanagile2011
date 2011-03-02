import static java.lang.System.identityHashCode;

import java.awt.Point;

public class Connection {
	public final Box box1;
	public final Box box2;

	public Connection(Box box1, Point point1, Box box2, Point point2) {
		if (point1.x <= point2.x) {
			this.box1 = box1;
			this.box2 = box2;
		} else {
			this.box1 = box2;
			this.box2 = box1;
		}
	}

	@Override
	public int hashCode() {
		return identityHashCode(box1) + identityHashCode(box2);
	}

	@Override
	public boolean equals(Object obj) {
		if (!getClass().isInstance(obj))
			return false;
		Connection anotherConnection = (Connection) obj;
		return (this.box1 == anotherConnection.box1 && this.box2 == anotherConnection.box2)
				|| (this.box1 == anotherConnection.box2 && this.box2 == anotherConnection.box1);
	}
}
