package org.firstinspires.ftc.teamcode.phantom.test;

import androidx.annotation.Nullable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.phantom.utility.TelemetryHTML;

@TeleOp(name="Naming Test")
public class NamingTest extends LinearOpMode {
    final String[][] key_commands = new String[][] {
            new String[]{ "A", "B", "C", "D", "E", "F", "G" },
            new String[]{ "H", "I", "J", "K", "L", "M", "N" },
            new String[]{ "O", "P", "Q", "R", "S", "T", "U" },
            new String[]{ "V", "W", "X", "Y", "Z", "_", "-" },
            new String[]{ "SHIFT",       "⌫", "✖", "↰" }
    };

    public void runOpMode() {
        waitForStart();

        final String input = keyboard("Enter macro name");

        if (input == null) {
            return;
        }

        telemetry.addData("Named", input);
        telemetry.update();

        while (!gamepad1.start && opModeIsActive()) { idle(); }
    }

    /**
     * Ask the user for keyboard input using the gamepad
     *
     * @param query Question to the user
     *
     * @return What the user entered, or null if the user cancelled the keyboard
     */
    public @Nullable String keyboard(final String query) {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        telemetry.clear();
        updateTelemetry(telemetry);

        final StringBuilder result = new StringBuilder();
        Gamepad previous_gamepad = new Gamepad();

        int x_key = 0, y_key = 0;
        boolean exit_loop = false;

        while (opModeIsActive()) {
            if (exit_loop) {
                break;
            }

            // telemetry
            telemetry.addLine("Use Dpad to move around, A to select, and Start to confirm\n");
            telemetry.addLine(String.format("%s: %s", query, result));

            // selection
            if (gamepad1.dpad_up && !previous_gamepad.dpad_up) {
                --y_key;
            } if (gamepad1.dpad_down && !previous_gamepad.dpad_down) {
                ++y_key;
            } if (gamepad1.dpad_left && !previous_gamepad.dpad_left) {
                --x_key;
            } if (gamepad1.dpad_right && !previous_gamepad.dpad_right) {
                ++x_key;
            }

            // save states for next loop
            previous_gamepad.copy(gamepad1);

            // loop the indexes: https://stackoverflow.com/questions/90238/whats-the-syntax-for-mod-in-java
            y_key = Math.floorMod(y_key, key_commands.length);
            x_key = Math.floorMod(x_key, key_commands[y_key].length);

            print_keyboard(x_key, y_key);

            // handle key presses
            if (gamepad1.a && !previous_gamepad.a) {
                switch (key_commands[y_key][x_key]) {
                    // delete the last character
                    case "⌫": {
                        if (result.length() != 0) {
                            result.deleteCharAt(result.length() - 1);
                        }

                        break;
                    }

                    // clear the string
                    case "✖": {
                        result.setLength(0);

                        break;
                    }

                    // escape the loop
                    case "↰": {
                        exit_loop = true;

                        break;
                    }

                    // make all characters in the array lowercase
                    case "SHIFT": {
                        for (int i = 0; i < key_commands.length; ++i) {
                            for (int j = 0; j < key_commands[i].length; ++j) {
                                key_commands[i][j] = key_commands[i][j].toLowerCase();
                            }
                        }

                        break;
                    }

                    // make all characters in the array uppercase
                    case "shift": {
                        for (int i = 0; i < key_commands.length; ++i) {
                            for (int j = 0; j < key_commands[i].length; ++j) {
                                key_commands[i][j] = key_commands[i][j].toUpperCase();
                            }
                        }

                        break;
                    }

                    // normal characters
                    default: {
                        result.append(key_commands[y_key][x_key]);
                    }
                }
            }

            if (gamepad1.start) {
                telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);
                telemetry.clear();
                updateTelemetry(telemetry);

                return result.toString();
            }

            updateTelemetry(telemetry);
        }

        telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);
        telemetry.clear();
        updateTelemetry(telemetry);

        return null;
    }

    /**
     * Print a keyboard with a character highlighted. Refer to {@link #key_commands} for indexes
     *
     * @param x_key X index of selected key
     * @param y_key Y index of selected key
     */
    private void print_keyboard(final int x_key, final int y_key) {
        for (int y = 0; y < key_commands.length; y++) {
            final StringBuilder command = new StringBuilder("<font face=\"monospace\">");

            for (int x = 0; x < key_commands[y].length; x++) {
                command.append(String.format("%s ",
                        (x == x_key && y == y_key) ?
                                TelemetryHTML.color("#08A045", "#FFFFFF", key_commands[y][x]) :
                                key_commands[y][x]
                ));
            }

            command.append("</font>");
            telemetry.addLine(command.toString());
        }
    }
}