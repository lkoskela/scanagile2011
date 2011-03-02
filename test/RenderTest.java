import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static org.junit.Assert.assertThat;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

public class RenderTest {
	private Diagram diagram;

	@Test
	public void boxesAreConnectedWithALine() throws Exception {
		Box box1 = new Box(20, 20);
		Box box2 = new Box(20, 20);
		box1.connectTo(box2);

		Diagram diagram = new Diagram();
		diagram.add(box1, new Point(10, 10));
		diagram.add(box2, new Point(40, 20));

		BufferedImage image = render(diagram);

		assertThat(colorAt(image, 19, 12), is(BLACK));
		assertThat(colorAt(image, 19, 13), is(BLACK));
		assertThat(colorAt(image, 20, 13), is(WHITE));
		assertThat(colorAt(image, 21, 13), is(WHITE));
		assertThat(colorAt(image, 22, 14), is(WHITE));
		assertThat(colorAt(image, 23, 14), is(WHITE));
		assertThat(colorAt(image, 24, 15), is(WHITE));
		assertThat(colorAt(image, 25, 15), is(WHITE));
		assertThat(colorAt(image, 26, 15), is(WHITE));
		assertThat(colorAt(image, 27, 16), is(WHITE));
		assertThat(colorAt(image, 28, 16), is(WHITE));
		assertThat(colorAt(image, 29, 17), is(WHITE));
		assertThat(colorAt(image, 30, 17), is(WHITE));
		assertThat(colorAt(image, 31, 17), is(BLACK));
		assertThat(colorAt(image, 31, 18), is(BLACK));
	}

	@Test
	public void boxesAreConnectedWithALine2() throws Exception {
		Box box1 = new Box(20, 20);
		Box box2 = new Box(20, 20);
		box1.connectTo(box2);

		diagram = new Diagram();
		diagram.add(box1, new Point(10, 10));
		diagram.add(box2, new Point(40, 20));

		assertThat(render(diagram), hasConnectingLineBetween(box1, box2));
	}

	private Matcher<BufferedImage> hasConnectingLineBetween(final Box box1,
			final Box box2) {
		return new BoxConnectorMatcher(diagram, box1, box2);
	}

	private BufferedImage render(Diagram diagram) throws IOException,
			FileNotFoundException {
		Render render = new Render(diagram);
		File tempFile = File.createTempFile("render", ".png");
		System.out.println(tempFile.getAbsolutePath());
		render.writeTo(new FileOutputStream(tempFile));
		Runtime.getRuntime().exec("open " + tempFile.getAbsolutePath());
		return ImageIO.read(tempFile);
	}

	private Matcher<Integer> is(final Color color) {
		return new BaseMatcher<Integer>() {

			@Override
			public boolean matches(Object o) {
				return new Color((Integer) o).equals(color);
			}

			@Override
			public void describeTo(Description d) {
				d.appendText("pixel color is " + color);
			}
		};
	}

	private Integer colorAt(BufferedImage image, int x, int y) {
		return image.getRGB(x, y);
	}
}
