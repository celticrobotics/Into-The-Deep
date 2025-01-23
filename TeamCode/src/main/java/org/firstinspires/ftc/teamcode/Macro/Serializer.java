package org.firstinspires.ftc.teamcode.Macro;

import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public final class Serializer {
    /**
     * Encode a prefix and an array of numbers into an array of bytes
     *
     * @param prefix Random character prefix, can be whatever ASCII character you want
     * @param numbers Array of numbers
     *
     * @return A buffer of encoded bytes that contain the parameters, can then be decoded in {@link #decode_to_numbers}
     */
    public static byte[] encode_numbers(final char prefix, final double[] numbers) {
        // the size of this buffer is one character prefix and the numbers array length multiplied by the sizeof a double
        final byte[] buffer = new byte[Character.BYTES + (numbers.length * Double.BYTES)];

        // first we save the prefix's bytes to the buffer, this is done using bit shifting
        buffer[0] = (byte) (prefix >> 8);
        buffer[1] = (byte) (prefix);

        // now we save the numbers, but we use a traditional for loop due to offsets
        for (int i = 0; i < numbers.length; ++i) {
            // get the bytes of a double as a long
            final long number_byte_representation = Double.doubleToLongBits(numbers[i]);

            // write these bytes to a buffer, we write from the low bits to the high bits
            for (int j = 0; j < 8; ++j) {
                buffer[(2 + i * 8) + j] = (byte)((number_byte_representation >> (j * 8)) & 0xFF);
            }
        }

        return buffer;
    }

    /**
     * Decode the bytes from {@link #encode_numbers}
     *
     * @param number_buffer The buffer returned from {@link #encode_numbers}
     *
     * @return A pair containing the prefix character and the array of doubles
     */
    public static Pair<Character, double[]> decode_to_numbers(final byte[] number_buffer) {
        // get character
        final char prefix = (char)((number_buffer[0] << 8) | (number_buffer[1] & 0xFF));

        // the numbers array is going to be the length of
        final double[] numbers = new double[(number_buffer.length - 2) / 8];

        // write these numbers back into the array
        for (int i = 0; i < numbers.length; ++i) {
            long number_byte_representation = 0;

            // build up the bytes iteratively
            for (int j = 0; j < 8; ++j) {
                number_byte_representation |= (number_buffer[(2 + i * 8) + j] & 0xFFL) << (j * 8);
            }

            numbers[i] = Double.longBitsToDouble(number_byte_representation);
        }

        return new Pair<>(prefix, numbers);
    }

    // second edition of our header, the magic number is hexadecimal 69
    private static final byte[] MAGIC_NUMBERS = new byte[]{ 0x69, 0x02 };

    /**
     * Serialize multiple buffers into a macro file, assumes the buffers are from {@link #encode_numbers}
     *
     * @param macro_name Name of the macro
     * @param buffers Buffers to encode, each buffer's size needs to be smaller than {@link Byte#MAX_VALUE}
     */
    public static void serialize(final String macro_name, final byte[][] buffers) throws InterruptedException {
        final FileIO file = get_file(macro_name);

        // write a magic number to our file for basic version control
        file.append(MAGIC_NUMBERS);

        for (final byte[] buffer : buffers) {
            final int length = buffer.length;

            if (length > Byte.MAX_VALUE) {
                throw new RuntimeException(String.format(Locale.getDefault(), "Byte buffer length %d too big", length));
            }

            file.append((byte) length);  // write the size of the doubles
            file.append(buffer);  // write the buffer
        }
    }

    /**
     * Deserialize file made from {@link #serialize}
     *
     * @param macro_name The name given for {@link #serialize}
     *
     * @return The buffers from {@link #serialize}
     */
    public static byte[][] deserialize(String macro_name) throws InterruptedException {
        final FileIO file = get_file(macro_name);

        final ArrayList<byte[]> buffers = new ArrayList<>();

        file.skip(MAGIC_NUMBERS.length);  // skip over these bytes

        while (true) {
            final int size = file.read();  // get the size of the next buffer

            // check if we reached the end of the file
            if (size == -1) { break; }

            // append the size to the buffer
            buffers.add(file.read(size));
        }

        return buffers.toArray(new byte[buffers.size()][]);
    }

    /**
     * Create a file, used in {@link #serialize} and {@link #deserialize}
     *
     * @param macro_name The macro name of the file
     *
     * @return The file
     */
    private static FileIO get_file(String macro_name) throws InterruptedException {
        final FileIO file;

        try {
            file = new FileIO(macro_name + ".smp");  // stands for "Silverfish Macro Program"
        } catch (IOException exception) {
            throw new InterruptedException(exception.getMessage());
        }

        return file;
    }
}
