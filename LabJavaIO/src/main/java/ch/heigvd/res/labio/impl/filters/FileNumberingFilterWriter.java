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
    private boolean possibleWindowsNewLine = false;

    public FileNumberingFilterWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        str = str.substring(off, off + len);

        // if first line
        if (lineCount == 0)
            str = ++lineCount + "\t" + str;


        // Detect what type of newLIne

        if (str.length() == 1) {
            if (str.equals("\r")) {
                possibleWindowsNewLine = true;
            } else if (possibleWindowsNewLine) {
                if (str.equals("\n")) {
                    str = str + ++lineCount + "\t";
                } else {
                    str = ++lineCount + "\t" + str;
                }
                possibleWindowsNewLine = false;
            } else if (str.equals("\n")) {
                str = str + ++lineCount + "\t";
            }
        }
        // Chaine de plusieurs caractères
        else {
            String newLine = str.contains("\r") && !str.contains("\r\n") ? "\r" : "\n";
            int index = -1;
            while ((index = str.indexOf(newLine, index + 1)) != -1) {
                lineCount++;
                str = str.substring(0, index + 1) + lineCount + "\t" + ((str.length() > index + 1) ? str.substring(index + 1) : "");
            }
        }

        System.out.print(str);
        out.write(str);
    }

    @Override
    public void close() throws IOException {
        if (possibleWindowsNewLine) {
            write(lineCount + "\t");
        }
        super.close();
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
