import java.io.*;

public class TempFile {

	private final File file;

	public TempFile(File file) {
		this.file = file;
	}

	public File file() {
		return file;
	}

	public boolean exists() {
		return file.exists();
	}

	public TempFile append(String content) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public String content() {
		StringBuffer content = new StringBuffer();
		try {
			Reader reader = new FileReader(file);
			int r = -1;
			while ((r = reader.read()) != -1) {
				content.append((char) r);
			}
			return content.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static TempFile withSuffix(String suffix) {
		try {
			return new TempFile(File.createTempFile("tempfile", suffix));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
