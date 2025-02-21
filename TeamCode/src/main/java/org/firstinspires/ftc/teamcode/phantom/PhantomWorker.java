package org.firstinspires.ftc.teamcode.phantom;

import android.util.Pair;
import androidx.annotation.Nullable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.phantom.utility.FileIO;
import org.firstinspires.ftc.teamcode.phantom.utility.Hardware;
import org.firstinspires.ftc.teamcode.phantom.utility.Serializer;
import org.firstinspires.ftc.teamcode.phantom.utility.TelemetryHTML;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Autonomous(name="Generic Phantom Reading")
public class PhantomWorker extends LinearOpMode {
    // This OpMode works by loading up the macro from an interactive selection
    // screen and decoding it into a readable container (converting the file to
    // character prefixes and double arrays). This data was stored sequentially each
    // frame, ie. there is no compression or rearranging of data, so it is possible
    // to read these in the order given without much thought.

    private ArrayList<List<Pair<Character, double[]>>> saved_hardware_commands_buffer;
    public static final boolean DEBUG = PhantomCommander.DEBUG;

    public void runOpMode() {
        hardware = new Hardware(hardwareMap, EnumSet.of(Hardware.HardwareFlag.DRIVETRAIN_ONLY));
        read_macro();
        telemetry.clear();
        telemetry.update();

        waitForStart();

        if (saved_hardware_commands_buffer == null) {
            return;
        }

        ElapsedTime timer = new ElapsedTime();

        for (final List<Pair<Character, double[]>> command_buffer : saved_hardware_commands_buffer) {
            if (isStopRequested() || gamepad1.start || gamepad2.start) {
                break;
            }

            hardware.execute_command_block(command_buffer, timer, this::sleep);
        }
    }

    /**
     * Read the macro
     */
    private void read_macro() {
        if (DEBUG) {
            telemetry.addLine("≡≡≡≡≡≡≡≡ READING MACRO ≡≡≡≡≡≡≡≡");
            updateTelemetry(telemetry);
        }

        final ArrayList<String> macros = Arrays.stream(FileIO.get_all_file_names())
                .map(name -> name.substring(0, name.indexOf('.')))
                .collect(Collectors.toCollection(ArrayList::new));


        // According to 11.4.1: you are not allowed to provide inputs to the robots. However this
        // rule is explicitly stated to happen during the autonomous period. However according to 11.3,
        // specifically G304, the INIT period is technically pre-match, allowing for any gamepad input.
        // Additionally, according to the Head Referee in the 2025 BC Second Lower Mainland Qualifier,
        // The period between INIT and ▶ button is not part of the match and can take an indefinite
        // amount of time.
        final String macro_name = get_macro(macros);

        if (macro_name == null) {
            return;
        }

        final Pair<ArrayList<List<Pair<Character, double[]>>>, Hardware> result = Serializer.deserialize(
                macro_name, hardwareMap, telemetry, this::opModeInInit, this::sleep
        );

        saved_hardware_commands_buffer = result.first;
        hardware = result.second;

        if (DEBUG) {
            telemetry.addData("Added data", hardware.flags_to_string());
            telemetry.addLine("≡≡≡ MACRO READ SUCCESSFULLY ≡≡≡");
            updateTelemetry(telemetry);
        }
    }

    /**
     * Ask the user to select a macro
     *
     * @return Selected macro, or null if the user stopped the OpMode
     */
    private @Nullable String get_macro(final ArrayList<String> macros) {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        telemetry.clear();
        updateTelemetry(telemetry);

        final int macros_count = macros.size();

        int macro_index = 0;
        boolean dpad_up_previously_on = false, dpad_down_previously_on = false;

        while (opModeInInit()) {
            telemetry.addLine("Select a macro");
            telemetry.addLine("Use Dpad Up/Down to search through macros and Start to confirm\n");

            // file index selection
            if (gamepad1.dpad_up && !dpad_up_previously_on) {
                --macro_index;
            }
            if (gamepad1.dpad_down && !dpad_down_previously_on) {
                ++macro_index;
            }

            // save previous inputs
            dpad_up_previously_on = gamepad1.dpad_up;
            dpad_down_previously_on = gamepad1.dpad_down;

            // limit the index to a valid number
            macro_index = Range.clip(macro_index, 0, macros.size() - 1);

            // print the available macros
            print_macros(macro_index, macros, macros_count);

            // add a index to allow for easier navigation
            telemetry.addLine(String.format(Locale.ROOT, "\n%d/%d --- %s", macro_index + 1, macros_count, macros.get(macro_index)));

            // check if the select button is pressed
            if (gamepad1.start) {
                // reset telemetry
                telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);
                telemetry.clear();
                telemetry.addData("Selected", macros.get(macro_index));
                telemetry.update();

                return macros.get(macro_index);
            }

            updateTelemetry(telemetry);
        }

        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        telemetry.clear();
        updateTelemetry(telemetry);

        return null;
    }

    /**
     * Print a the selected macro and a couple of macros around it
     *
     * @param macro_index Selected macro
     * @param macros Array of macros
     * @param macros_count Length of array of macros, should be equal to macros.size()
     */
    private void print_macros(final int macro_index, final ArrayList<String> macros, final int macros_count) {
        // print 5 items centered around the file index
        final int start = Math.max(0, macro_index - 2);
        final int end = Math.min(macros_count, macro_index + 3);  // +3 to skip macro index

        // print the items before the selected macro
        for (int i = start; i < macro_index; ++i) {
            telemetry.addLine(macros.get(i));
        }

        // print the selected item with a special prefix and styling to differentiate it from others
        telemetry.addLine(TelemetryHTML.bold(TelemetryHTML.color("#08A045", "#FFFFFF", ">>> " + macros.get(macro_index))));

        // print the items after the selected macro, we use + 1 to not include the selected macro itself
        for (int i = macro_index + 1; i < end; i++) {
            telemetry.addLine(macros.get(i));
        }
    }

    Hardware hardware;
}