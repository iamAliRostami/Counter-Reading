package com.leon.counter_reading.utils.backup_restore;

import java.io.IOException;

import java.io.PrintWriter;
import java.io.Writer;

public class CSVWriter {

    private final PrintWriter pw;

    private final char separator;

    private final char quoteChar;

    private final char escapeChar;

    private final String lineEnd;

    /** The character used for escaping quotes. */
    public static final char DEFAULT_ESCAPE_CHARACTER = '"';

    /** The default separator to use if none is supplied to the constructor. */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    /** The quote constant to use when you wish to suppress all quoting. */
    public static final char NO_QUOTE_CHARACTER = '\u0000';

    /** The escape constant to use when you wish to suppress all escaping. */
    public static final char NO_ESCAPE_CHARACTER = '\u0000';

    /** Default line terminator uses platform encoding. */
    public static final String DEFAULT_LINE_END = "\n";

    /**
     * Constructs CSVWriter using a comma for the separator.
     *
     * @param writer
     *            the writer to an underlying CSV source.
     */
    public CSVWriter(Writer writer) {
        this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
    }

    /**
     * Constructs CSVWriter with supplied separator, quote char, escape char and line ending.
     *
     * @param writer
     *            the writer to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quoteChar
     *            the character to use for quoted elements
     * @param escapeChar
     *            the character to use for escaping quotechars or escapechars
     * @param lineEnd
     * 			  the line feed terminator to use
     */
    public CSVWriter(Writer writer, char separator, char quoteChar, char escapeChar, String lineEnd) {
        this.pw = new PrintWriter(writer);
        this.separator = separator;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
        this.lineEnd = lineEnd;
    }

    /**
     * Writes the next line to the file.
     *
     * @param nextLine
     *            a string array with each comma-separated element as a separate
     *            entry.
     */
    public void writeNext(String[] nextLine) {

        if (nextLine == null)
            return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(separator);
            }

            String nextElement = nextLine[i];
            if (nextElement == null)
                continue;
            if (quoteChar !=  NO_QUOTE_CHARACTER)
                sb.append(quoteChar);
            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);
                if (escapeChar != NO_ESCAPE_CHARACTER && nextChar == quoteChar) {
                    sb.append(escapeChar).append(nextChar);
                } else if (escapeChar != NO_ESCAPE_CHARACTER && nextChar == escapeChar) {
                    sb.append(escapeChar).append(nextChar);
                } else {
                    sb.append(nextChar);
                }
            }
            if (quoteChar != NO_QUOTE_CHARACTER)
                sb.append(quoteChar);
        }

        sb.append(lineEnd);
        pw.write(sb.toString());

    }

    /**
     * Flush underlying stream to writer.
     *
     * @throws IOException if bad things happen
     */
    public void flush() throws IOException {
        pw.flush();
    }

    /**
     * Close the underlying stream writer flushing any buffered content.
     *
     * @throws IOException if bad things happen
     *
     */
    public void close() throws IOException {
        pw.flush();
        pw.close();
    }

}