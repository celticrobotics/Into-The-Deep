package org.firstinspires.ftc.teamcode.Macro;

import android.os.Environment;
import androidx.annotation.Nullable;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;

public final class FileIO {
    private final File path;
    private RandomAccessFile internal_file_object;

    /**
     * Create a file writer
     *
     * @param file File path of the file, this is relative to a unknown directory, but is still a file
     */
    public FileIO(String file) throws IOException {
        final String file_path = String.format(Locale.getDefault(), "%s/FIRST/data/%s",
                Environment.getExternalStorageDirectory().getAbsolutePath(), file);

        path = new File(file_path);

        make_parent_directory();
        cull_file();

        //see: https://docs.oracle.com/javase/8/docs/api/java/io/RandomAccessFile.html#RandomAccessFile-java.io.File-java.lang.String-
        // opens file to reading, writing synchronously
        try {
            internal_file_object = new RandomAccessFile(file_path, "rwd");
        }
        catch (Exception ignored) {} // we have already made sure that the file exists in cull_file()
    }

    /**
     * Write line to a file
     *
     * @param line Line to write
     */
    public void append(String line) throws IOException {
        internal_file_object.seek(internal_file_object.length());
        internal_file_object.writeUTF(line + '\n');
    }

    /**
     * Append a byte to a file
     *
     * @param number Byte to write
     */
    public void append(byte number) throws InterruptedException {
        try {
            internal_file_object.seek(internal_file_object.length());
            internal_file_object.write(number);
        } catch (IOException exception) {
            throw new InterruptedException(exception.getMessage());
        }
    }

    /**
     * Write a buffer to a file
     *
     * @param buffer Buffer to write
     */
    public void append(byte[] buffer) throws InterruptedException {
        try {
            internal_file_object.seek(internal_file_object.length());
            internal_file_object.write(buffer);
        } catch (IOException exception) {
            throw new InterruptedException(exception.getMessage());
        }
    }

    /**
     * Skip over certain parts of a file
     */
    public void skip(int length) throws InterruptedException {
        try {
            if ( internal_file_object.skipBytes(length) != length) {
                throw new RuntimeException(String.format(Locale.getDefault(), "Failed to skip %d number of bytes", length));
            }
        } catch (IOException exception) {
            throw new InterruptedException(exception.getMessage());
        }
    }

    /**
     * Read from an offset and length into a buffer
     *
     * @param offset Offset into the file
     * @param length Length of the data to get
     *
     * @return The bytes in that portion of the file
     */
    public byte[] read(int offset, int length) throws InterruptedException {
        final byte[] buffer = new byte[length];

        try {
            final long previous_position = internal_file_object.getFilePointer();

            internal_file_object.seek(offset);

            if (internal_file_object.read(buffer, 0, length) == -1) {
                throw new RuntimeException("reached end of file before filling buffer when reading bytes");
            }

            internal_file_object.seek(previous_position);
        } catch (IOException exception) {
            throw new InterruptedException(exception.getMessage());
        }

        return buffer;
    }

    /**
     * Read a certain amount of bytes into a file
     *
     * @param length Number of bytes to read
     *
     * @return The bytes found
     */
    public byte[] read(int length) throws InterruptedException{
        final byte[] buffer = new byte[length];

        try {
            if (internal_file_object.read(buffer) == -1) {
                throw new RuntimeException("reached end of file before filling buffer when reading bytes");
            }
        } catch (IOException exception) {
            throw new InterruptedException(exception.getMessage());
        }

        return buffer;
    }

    /**
     * Read a single byte from the buffer
     *
     * @return int that can be converted to byte using casting or -1 to say that there are no more bytes to read
     */
    public int read() throws InterruptedException {
        try {
            return internal_file_object.readByte();
        } catch (EOFException ignore) {
            return -1;
        } catch (IOException exception) {
            throw new InterruptedException(exception.getMessage());
        }
    }

    /**
     * Read a line from a file, it will move down the file by a line until there are no lines left to read
     *
     * @return Current line, or null if there are no lines left to read
     */
    public @Nullable String read_line() throws IOException {
        return internal_file_object.readLine();
    }

    /**
     * Close the file writer
     */
    public void close() {
        try {
            internal_file_object.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
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
        final FileWriter temp_file = new FileWriter(path.getName(), false);
        temp_file.close();
    }
}
