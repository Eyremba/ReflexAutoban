package rip.reflex.autoban.util;

import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {

    public static void write(final FileWriter writer, final String data) {
        try {
            writer.write(data);
            writer.append("\n");

            writer.flush();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

}
