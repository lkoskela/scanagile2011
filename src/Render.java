import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static java.lang.Math.acos;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class Render {
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private Diagram diagram;

	public Render(Diagram diagram) {
		this.diagram = diagram;
		calculateDimensions();
	}

	private int width() {
		return 1 + maxX - minX;
	}

	private int height() {
		return 1 + maxY - minY;
	}

	private void calculateDimensions() {
		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
		for (Box box : diagram.boxes()) {
			Point p = diagram.positionOf(box);
			minX = min(minX, p.x - (box.width() / 2));
			minY = min(minY, p.y - (box.height() / 2));
			maxX = max(maxX, p.x + (box.width() / 2) + (box.width() % 2));
			maxY = max(maxY, p.y + (box.height() / 2) + (box.height() % 2));
		}
	}

	public void writeTo(OutputStream output) throws IOException {
		BufferedImage image = new BufferedImage(width(), height(),
				TYPE_4BYTE_ABGR);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(BLACK);
		graphics.fillRect(0, 0, width(), height());
		graphics.setColor(WHITE);
		drawObjectsOn(graphics);
		ImageIO.write(image, "PNG", output);
	}

	private void drawObjectsOn(Graphics2D image) {
		drawBoxesOn(image);
		drawConnectionsOn(image);
	}

	private void drawBoxesOn(Graphics2D image) {
		for (Box box : diagram.boxes()) {
			image.drawRect(leftEdgeOf(box), topEdgeOf(box), box.width(),
					box.height());
		}
	}

	private void drawConnectionsOn(Graphics2D image) {
		for (Connection connection : diagram.connections()) {
			drawConnection(image, connection);
		}
	}

	private void drawConnection(Graphics2D image, Connection connection) {
		Point a = diagram.positionOf(connection.box1);
		Point b = diagram.positionOf(connection.box2);
		double angle = calculateAngleBetween(a, b);
		int relativeYFactor = (a.y <= b.y ? 1 : -1);

		int deltaAx = connection.box1.width() / 2;
		int deltaAy = (int) floor((sin(angle) * deltaAx)
				/ (sin(asRadians(180 - 90 - inDegrees(angle)))));

		int deltaBx = connection.box2.width() / 2;
		int deltaBy = (int) floor((sin(angle) * deltaBx)
				/ (sin(asRadians(180 - 90 - inDegrees(angle)))));

		image.drawLine(a.x + deltaAx, a.y + deltaAy, b.x - deltaBx, b.y
				- (deltaBy * relativeYFactor));
	}

	private double calculateAngleBetween(Point a, Point b) {
		double hypotenuse = sqrt(pow(b.x - a.x, 2) + pow(b.y - a.y, 2));
		double angle = acos((pow(b.x - a.x, 2) + pow(hypotenuse, 2) - pow(b.y
				- a.y, 2))
				/ (2 * (b.x - a.x) * hypotenuse));
		return angle;
	}

	private double inDegrees(double angleInRadians) {
		return angleInRadians * 180 / Math.PI;
	}

	private double asRadians(double angleInDegrees) {
		return angleInDegrees * Math.PI / 180;
	}

	private int leftEdgeOf(Box box) {
		Point p = diagram.positionOf(box);
		return p.x - (box.width() / 2);
	}

	private int topEdgeOf(Box box) {
		Point p = diagram.positionOf(box);
		return p.y - (box.height() / 2);
	}
}
