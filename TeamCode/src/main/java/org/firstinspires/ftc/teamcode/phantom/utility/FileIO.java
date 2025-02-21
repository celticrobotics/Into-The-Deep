package org.firstinspires.ftc.teamcode.phantom.utility;

import android.os.Environment;
import androidx.annotation.Nullable;
import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public final class FileIO {
    /**
     * The Mode of the file to be opened in
     */
    public enum Mode {
        READ, WRITE
    }

    /**
     * Create a file writer
     *
     * @param file File path of the file, this is relative to a unknown directory, but is still a file
     */
    public FileIO(String file, Mode mode, Telemetry telemetry, Consumer<Long> sleep) throws IOException {
        file_path = String.format("%s/%s", MACRO_DIRECTORY, file);

        this.telemetry = telemetry;
        this.sleep = sleep;
        this.path = new File(file_path);

        make_parent_directory();

        switch (mode) {
            case WRITE: {
                cull_file();
                writer = new FileWriter(path, true);

                break;
            }
            case READ: {
                reader = new BufferedReader(new FileReader(path));

                break;
            }

            default: throw new RuntimeException();
        }
    }

    /**
     * Write line to a file
     *
     * @param line Line to write
     */
    public void append(String line) throws IOException {
        writer.append(line);
        writer.append('\n');
    }

    /**
     * Read a line from a file, it will move down the file by a line until there are no lines left to read
     *
     * @return Current line, or null if there are no lines left to read
     */
    public @Nullable String read_line() throws IOException {
        return reader.readLine();
    }

    /**
     * Flush the file's contents
     */
    public void flush() throws IOException {
        writer.flush();
    }

    /**
     * Get a list of all files in the current directory
     *
     * @return Array of all file names in the current directory
     */
    public static String[] get_all_file_names() {
        final File directory = new File(MACRO_DIRECTORY);

        // check if the directory exists, if it doesn't throw an error
        if (!directory.exists()) {
            throw new RuntimeException("No macro's have ever been recorded!");
        } else if (!directory.isDirectory()) {
            throw new RuntimeException(String.format("Delete the file \"%s\" in your Control Hub", directory.getAbsolutePath()));
        }

        // get a list of all the files in the directory
        return Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                .filter(File::isFile)
                .map(File::getName)
                .toArray(String[]::new);
    }

    /**
     * Create the parent directory for the file
     */
    private void make_parent_directory() {
        final File parent_directory = path.getParentFile();

        if (parent_directory == null || parent_directory.exists()) {
            return;
        }

        // make the current directory
        if (!parent_directory.mkdirs()) {
            throw new RuntimeException("Failed to create directory: " + parent_directory);
        }
    }

    /**
     * Remove a file's contents and create the file if it doesn't exist
     */
    private void cull_file() throws IOException {
        // this truncates the file due to the properties of FileOutputStream
        new FileOutputStream(file_path).close();
    }

    private static final String MACRO_DIRECTORY = String.format("%s/FIRST/macros", Environment.getExternalStorageDirectory().getAbsolutePath());
    private final File path;
    private final String file_path;
    private FileWriter writer;
    private BufferedReader reader;

    // used only when debugging
    private final Telemetry telemetry;
    private final Consumer<Long> sleep;
}