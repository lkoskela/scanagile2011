import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.commons.logging.*;

public class LogFileTransformer {

    private static final Log log = LogFactory
            .getLog(LogFileTransformer.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private StringWriter writer;

    private boolean hasWrittenFinishedTag;

    private long lastTimestamp;

    private String sessionId;

    private String userId;

    private String presentationId;

    private String started;

    private String finished;

    public synchronized void transform(File src, File dest)
            throws IOException {
        transform(new FileReader(src), new FileWriter(dest));
        log.debug("Transformed " + src.getName() + " to "
                + dest.getName());
    }

    public synchronized void transform(Reader src, Writer dest)
            throws IOException {
        reset();
        processLines(src);
        aggregateResultTo(dest);
    }

    private String aggregateToString() {
        StringBuffer s = new StringBuffer();
        s.append("session-id###").append(sessionId).append("\n");
        s.append("presentation-id###").append(presentationId).append(
                "\n");
        s.append("user-id###").append(userId).append("\n");
        s.append("started###").append(started).append("\n");
        s.append(writer.toString());
        s.append("finished###").append(this.finished).append("\n");
        return s.toString();
    }

    private void aggregateResultTo(Writer writer) throws IOException {
        writer.write(aggregateToString());
        writer.flush();
        writer.close();
    }

    private void processLines(Reader src) throws IOException {
        BufferedReader reader = new BufferedReader(src);
        String line = null;
        while ((line = reader.readLine()) != null) {
            handleLine(line);
        }
        reader.close();
    }

    private void reset() {
        writer = new StringWriter();
        hasWrittenFinishedTag = false;
        lastTimestamp = 0;
    }

    private void handleLine(String line) {
        String timestamp = line.substring(1, 20);
        String data = line.substring(22);
        handleLine(timestamp, data);
    }

    private void handleLine(String timestamp, String data) {
        try {
            long timeInMillis = dateFormat.parse(timestamp).getTime();
            handleLine(timeInMillis, data);
        } catch (ParseException e) {
            log.error(e);
        }
    }

    private void handleLine(long timestamp, String data) {
        if (lastTimestamp != 0) {
            // finish up the previous slide's duration
            writer.write("" + ((timestamp - lastTimestamp) / 1000));
            writer.write("\n");
            lastTimestamp = 0;
        }
        if (hasWrittenFinishedTag) {
            // ignore any lines that come after STOPPED or KILLED
            return;
        }
        if (data.equals("LAUNCHED")) {
            started = dateFormat.format(new Date(timestamp));
        } else if (data.startsWith("session-id###")) {
            sessionId = data.substring("session-id###".length());
        } else if (data.startsWith("user-id###")) {
            userId = data.substring("user-id###".length());
        } else if (data.startsWith("presentation-id###")) {
            presentationId = data.substring("presentation-id###"
                    .length());
        } else if (data.equals("STOPPED") || data.equals("KILLED")) {
            if (!hasWrittenFinishedTag) {
                finished = dateFormat.format(new Date(timestamp));
                hasWrittenFinishedTag = true;
            }
        } else {
            // just write the slide name and let the next call complete the
            // duration
            writer.write(data);
            writer.write("###");
            lastTimestamp = timestamp;
        }
        writer.flush();
    }

}
