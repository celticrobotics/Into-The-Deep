package org.firstinspires.ftc.teamcode.phantom.test;

import androidx.annotation.Nullable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.phantom.utility.TelemetryHTML;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

@TeleOp(name="Selection Test")
public class SelectionTest extends LinearOpMode {

    public void runOpMode() {
        final String result = get_macro();

        telemetry.addData("Selected", result);
        telemetry.update();

        waitForStart();
    }

    /**
     * Ask the user to select a macro
     *
     * @return Selected macro, or null if the user stopped the OpMode
     */
    @Nullable String get_macro() {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);

        // test data
        final ArrayList<String> macros = new ArrayList<>(Arrays.asList(
                "a_macro", "another_macro", "the_red_macro", "smegma_macro", "not_so_smegma",
                "testing_this", "testing_that", "testing_everywhere", "freak_macro", "the_really_freaky_macro"
        ));
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
                telemetry.clear();
                telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);

                return macros.get(macro_index);
            }

            updateTelemetry(telemetry);
        }

        telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);

        return null;
    }

    /**
     * Print a the selected macro and a couple of macros around it
     *
     * @param macro_index Selected macro
     * @param macros Array of macros
     * @param macros_count Length of array of macros, should be equal to macros.size()
     */
    void print_macros(final int macro_index, final ArrayList<String> macros, final int macros_count) {
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
}