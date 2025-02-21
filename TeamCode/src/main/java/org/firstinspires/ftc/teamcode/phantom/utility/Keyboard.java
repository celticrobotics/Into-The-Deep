package org.firstinspires.ftc.teamcode.phantom.utility;

import androidx.annotation.Nullable;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Keyboard {
    private final String[][] key_commands = new String[][] {
            new String[]{ "A", "B", "C", "D", "E", "F", "G" },
            new String[]{ "H", "I", "J", "K", "L", "M", "N" },
            new String[]{ "O", "P", "Q", "R", "S", "T", "U" },
            new String[]{ "V", "W", "X", "Y", "Z", "_", "-" },
            new String[]{ "SHIFT",       "⌫", "✖", "↰" },
    };

    public Keyboard(final Telemetry telemetry, final Supplier<Gamepad> gamepad, final Supplier<Boolean> active) {
        this.telemetry = telemetry;
        this.gamepad = gamepad;
        this.active = active;
    }

    /**
     * Ask the user for keyboard input using the gamepad
     *
     * @param query Question to the user
     *
     * @return What the user entered, or null if the user cancelled the keyboard
     */
    public @Nullable String input(final String query) {
        delta_timer.reset();

        double step = 0;

        initialize_telemetry();

        final StringBuilder result = new StringBuilder();

        while (active.get()) {
            // gamepad
            final Gamepad gamepad = this.gamepad.get();

            // delta time
            double dt = delta_timer.seconds();
            delta_timer.reset();

            // step is used for RGB
            step += dt * 10;

            // the string was finished
            if (gamepad.start) {
                reset_telemetry();

                return result.toString();
            }

            // give instructions
            telemetry.addLine("Use Dpad to move around, A to select, and Start to confirm\n");
            telemetry.addLine(String.format("%s%s\n", query, result));

            handle_input(gamepad);

            print_keyboard(step);

            // handle key presses
            if (gamepad.a && !previous_gamepad.a) switch (key_commands[y_key][x_key]) {
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

                // stop asking for input
                case "↰": {
                    reset_telemetry();

                    return null;
                }

                // make all characters in keyboard lowercase
                case "SHIFT": {
                    make_keys_lower();

                    break;
                }

                // make all characters in keyboard uppercase
                case "shift": {
                    make_keys_upper();

                    break;
                }

                // normal characters
                default: {
                    result.append(key_commands[y_key][x_key]);

                    break;
                }
            }

            telemetry.update();
        }

        reset_telemetry();

        return null;
    }

    /**
     * Print a keyboard with a character highlighted. Refer to {@link #key_commands} for indexes
     *
     * @param step Step
     */
    private void print_keyboard(final double step) {
        for (int y = 0; y < key_commands.length; y++) {
            final StringBuilder command = new StringBuilder("<font face=\"monospace\">");

            for (int x = 0; x < key_commands[y].length; x++) {
                command.append(String.format("%s ",
                        (x == x_key && y == y_key) ?
                                TelemetryHTML.color("#193F0E", "#FFFFFF", key_commands[y][x]) :
                                key_commands[y][x]
                ));
            }

            command.append("</font>");
            telemetry.addLine(command.toString());
        }
    }

    /**
     * Handle gamepad input
     *
     * @param gamepad Gamepad for the current iteration
     */
    private void handle_input(final Gamepad gamepad) {
        // selection
        if (gamepad.dpad_up && !previous_gamepad.dpad_up) {
            --y_key;
        } if (gamepad.dpad_down && !previous_gamepad.dpad_down) {
            ++y_key;
        } if (gamepad.dpad_left && !previous_gamepad.dpad_left) {
            --x_key;
        } if (gamepad.dpad_right && !previous_gamepad.dpad_right) {
            ++x_key;
        }

        // save states for next loop
        previous_gamepad.copy(gamepad);

        // loop the indexes: https://stackoverflow.com/questions/90238/whats-the-syntax-for-mod-in-java
        y_key = Math.floorMod(y_key, key_commands.length);
        x_key = Math.floorMod(x_key, key_commands[y_key].length);
    }

    /**
     * Initialize HTML telemetry
     */
    private void initialize_telemetry() {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        telemetry.clear();
        telemetry.setMsTransmissionInterval(0);
        telemetry.update();
    }

    /**
     * Reset telemetry to user default
     */
    private void reset_telemetry() {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);
        telemetry.clear();
        telemetry.setMsTransmissionInterval(250);
        telemetry.update();
    }

    /**
     * Make the keyboard lowercase
     */
    private void make_keys_lower() {
        for (int i = 0; i < key_commands.length; ++i) {
            for (int j = 0; j < key_commands[i].length; ++j) {
                key_commands[i][j] = key_commands[i][j].toLowerCase();
            }
        }
    }

    /**
     * Make the keyboard uppercase
     */
    private void make_keys_upper() {
        for (int i = 0; i < key_commands.length; ++i) {
            for (int j = 0; j < key_commands[i].length; ++j) {
                key_commands[i][j] = key_commands[i][j].toUpperCase();
            }
        }
    }

    /**
     * Get the RGB color based on a timer
     *
     * @param step Step for the robot
     *
     * @return RGB in HEX
     */
    private String rgb(final double step) {
        final int red = Range.clip((int) Math.round(Math.sin(0.3 * step + 0) * 127 + 128), 0, 255);
        final int grn = Range.clip((int) Math.round(Math.sin(0.3 * step + 2) * 127 + 128), 0, 255);
        final int blu = Range.clip((int) Math.round(Math.sin(0.3 * step + 4) * 127 + 128), 0, 255);

        return String.format("#%02x%02x%02x", red, grn, blu);
    }

    final Telemetry telemetry;
    final Supplier<Gamepad> gamepad;
    final Supplier<Boolean> active;

    final Gamepad previous_gamepad = new Gamepad();
    int x_key = 0, y_key = 0;
    ElapsedTime delta_timer = new ElapsedTime();
}