package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 * <p>
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

    private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());
    private int lineCount = 0;

    public FileNumberingFilterWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        str = str.substring(off, off + len);

        // if first line
        if (lineCount == 0)
            str = ++lineCount + "\t" + str;

        int index = -1;
        while ((index = str.indexOf("\n", index + 1)) != -1) {
            lineCount++;
            str = str.substring(0, index + 1) + lineCount + "\t" + ((str.length() > index + 2) ? str.substring(index + 2) : "");
        }
        System.out.print(str);
        out.write(str);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        write(new String(cbuf), off, len);
    }

    @Override
    public void write(int c) throws IOException {
        write(String.valueOf((char) c), 0, 1);
    }

}
