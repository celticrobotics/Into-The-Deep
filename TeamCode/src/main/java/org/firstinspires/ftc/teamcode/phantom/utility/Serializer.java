package org.firstinspires.ftc.teamcode.phantom.utility;

import android.util.Pair;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class Serializer {
    /**
     * The file extension to the macro
     */
    public static final String MACRO_EXTENSION = "smp";  // stands for "Small Macro Program"

    /**
     * Encode a single number
     *
     * @param prefix Random character prefix, can be whatever ASCII character you want
     * @param number A number
     */
    public static String encode_number(final char prefix, final double number) {
        return String.format(Locale.ROOT, "%c%f", prefix, number);
    }

    /**
     * Encode a prefix and an array of numbers into an array of bytes
     *
     * @param prefix Random character prefix, can be whatever ASCII character you want
     * @param numbers Array of numbers
     *
     * @return A semi-readable string that contain the parameters, can then be decoded in {@link #decode_to_numbers}
     */
    public static String encode_numbers(final char prefix, final double[] numbers) {
        final String joined_items = Arrays.stream(numbers)
                .mapToObj(Double::toString)
                .collect(Collectors.joining(":"));

        return String.format("%c%s", prefix, joined_items);
    }

    /**
     * Decode the bytes from {@link #encode_numbers}
     *
     * @param number_buffer The buffer returned from {@link #encode_numbers}
     *
     * @return A pair containing the prefix character and the array of doubles
     */
    public static Pair<Character, double[]> decode_to_numbers(final String number_buffer) {
        final double[] result = Arrays.stream(number_buffer.substring(1).split(":"))
                .mapToDouble(Double::parseDouble)
                .toArray();

        return new Pair<>(number_buffer.charAt(0), result);
    }

    /**
     * Serialize multiple buffers into a macro file, assumes the buffers are from {@link #encode_numbers}
     *
     * @param macro_name Name of the macro
     * @param buffers Buffers to serialize
     * @param hardware Hardware of the macro
     * @param telemetry Telemetry object in {@link LinearOpMode}
     * @param sleep Sleep function in {@link LinearOpMode}
     */
    public static void serialize(
            final String macro_name,
            final String[] buffers,
            final Hardware hardware,
            final Telemetry telemetry,
            final Consumer<Long> sleep
    ) {
        try {
            final FileIO file = get_file(macro_name, FileIO.Mode.WRITE, telemetry, sleep);

            // add hardware
            file.append(String.format("# hardware: %s", hardware.flags_to_string()));

            boolean tuning = true;

            int i = 0;
            for (final String buffer : buffers) {
                if (buffer == null) {
                    continue;
                }

                file.append(buffer);
            }

            file.flush();
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * Deserialize file made from {@link #serialize}
     *
     * @param macro_name The name given for {@link #serialize}
     * @param map Hardware map in {@link LinearOpMode}
     * @param telemetry Telemetry object in {@link LinearOpMode}
     * @param opmode_in_init A function that checks if the {@link LinearOpMode}'s
     *                       start butting is not pressed, but the INIT button is pressed
     * @param sleep Sleep function in {@link LinearOpMode}
     *
     * @return The buffers from {@link #serialize} and a valid hardware map
     */
    public static Pair<ArrayList<List<Pair<Character, double[]>>>, Hardware> deserialize(
            final String macro_name,
            final HardwareMap map,
            final Telemetry telemetry,
            final Supplier<Boolean> opmode_in_init,
            final Consumer<Long> sleep
    ) {
        final ArrayList<List<Pair<Character, double[]>>> buffers = new ArrayList<>();
        Hardware hardware = null;

        try {
            final FileIO file = get_file(macro_name, FileIO.Mode.READ, telemetry, sleep);

            for (String line = file.read_line(); line != null; line = file.read_line()) {
                if (!opmode_in_init.get()) {
                    throw new RuntimeException(
                            "You pressed the stop button while the macro is being read.\n" +
                            "This is highly dangerous due to the high likelihood of the\n" +
                            "file corrupting while the OpMode is reading and the macro not\n" +
                            "working anymore!");
                }

                // this allows for comments
                if (line.trim().startsWith("#")) {
                    final String comment = line.trim().substring(1).trim();

                    if (!comment.contains("hardware")) {
                        continue;
                    }

                    final String hardware_enabled = comment.split("hardware: ")[1];

                    hardware = Hardware.string_to_hardware(map, hardware_enabled);

                    continue;
                }


                buffers.add(Arrays.stream(line.split(", "))
                        .map(Serializer::decode_to_numbers)
                        .collect(Collectors.toList())
                );
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        if (hardware == null) {
            hardware = new Hardware(map, EnumSet.of(Hardware.HardwareFlag.ALL_HARDWARE));
        }

        return new Pair<>(buffers, hardware);
    }

    /**
     * Create a file, used in {@link #serialize} and {@link #deserialize}
     *
     * @param macro_name The macro name of the file
     * @param mode the Mode of opening the file
     * @param telemetry Telemetry object in {@link LinearOpMode}
     * @param sleep Sleep function in {@link LinearOpMode}
     *
     * @return The file
     */
    private static FileIO get_file(String macro_name, FileIO.Mode mode, Telemetry telemetry, Consumer<Long> sleep) throws IOException {
        return new FileIO(String.format("%s.%s", macro_name, MACRO_EXTENSION), mode, telemetry, sleep);
    }
}