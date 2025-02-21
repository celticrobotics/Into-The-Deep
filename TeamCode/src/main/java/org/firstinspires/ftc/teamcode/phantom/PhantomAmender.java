package org.firstinspires.ftc.teamcode.phantom;

import android.util.Pair;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.phantom.utility.Hardware;
import org.firstinspires.ftc.teamcode.phantom.utility.Serializer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Disabled
@TeleOp(name="Test Phantom Writing and Editing")
public class PhantomAmender extends LinearOpMode {
    // This OpMode works by reading the buffer and replaying the macro, then saving
    // the macro back to the file. When the OpMode detects input from the gamepad,
    // it will switch over to writing mode and start allowing for the gamepad to add
    // stuff to the macro.

    private ArrayList<List<Pair<Character, double[]>>> decoded_saved_hardware_commands_buffer;
    private final String[] saved_hardware_commands_buffer = new String[5_000];
    public static final boolean DEBUG = PhantomCommander.DEBUG;
    public static final String MACRO_NAME = "$";

    public void runOpMode() {
        hardware = new Hardware(hardwareMap, EnumSet.of(Hardware.HardwareFlag.ALL_HARDWARE));
        read_macro();

        waitForStart();

        ElapsedTime timer = new ElapsedTime();

        int saved_buffer_index = 0;
        saved_hardware_commands_buffer[saved_buffer_index++] = "# read-amend";
        while (saved_buffer_index < saved_hardware_commands_buffer.length && opModeIsActive() && !recording_ended) {
            String serialized_command;

            force_stop_check();

            if (reading_mode) {  // reading
                // get the command
                final List<Pair<Character, double[]>> command_block = decoded_saved_hardware_commands_buffer.get(saved_buffer_index);

                // execute the command
                hardware.execute_command_block(command_block, timer, this::sleep);

                // reserialize the command
                serialized_command = command_block.stream()
                        .map(command -> Serializer.encode_numbers(command.first, command.second))
                        .collect(Collectors.joining(", "));

                if (!nothing_currently_pressed(gamepad1)) {
                    reading_mode = false;
                    saved_hardware_commands_buffer[saved_buffer_index++] = "# write-amend";
                }
            } else {  // writing
                final String drivetrain_command = Serializer.encode_numbers('w', move_drivetrain());
                final String slides_command = Serializer.encode_numbers('s', move_slides());
                final String arm_command = Serializer.encode_numbers('a', move_arms());

                reset_check();

                sleep(2);

                final String time_command = Serializer.encode_number('t', timer.milliseconds());
                timer.reset();

                serialized_command = String.join(", ", drivetrain_command, slides_command, arm_command, time_command);
            }

            // we update telemetry here to not have flashing values
            if (DEBUG) {
                updateTelemetry(telemetry);
            }

            // add the command back to the buffer
            saved_hardware_commands_buffer[saved_buffer_index++] = serialized_command;
        }

        Serializer.serialize(MACRO_NAME, saved_hardware_commands_buffer, hardware, telemetry, this::sleep);
    }

    /**
     * Force stop check
     */
    private void force_stop_check() {
        if (DEBUG) {
            telemetry.addLine("≡≡≡ EMERGENCY PAUSE COMMAND ≡≡≡");
            telemetry.addLine(" - Press the start button on any controller to shut down the robot");
            telemetry.addLine();
        }

        if (gamepad1.start || gamepad2.start) {
            requestOpModeStop();
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

//        decoded_saved_hardware_commands_buffer = Serializer.deserialize(MACRO_NAME, telemetry, this::opModeInInit, this::sleep);

        if (DEBUG) {
            telemetry.addLine("≡≡≡ MACRO READ SUCCESSFULLY ≡≡≡");
            updateTelemetry(telemetry);
        }
    }

    /**
     * Move drivetrain
     */
    private double[] move_drivetrain() {
        if (DEBUG) {
            telemetry.addLine("≡≡≡≡≡≡ MOVEMENT COMMANDS ≡≡≡≡≡≡");
            telemetry.addLine(" - Left joystick moves robot");
            telemetry.addLine(" - Right joystick rotates robot");
            telemetry.addLine();
        }

        final double velocity = resetting_state != 0 ? 0 :   // ground the robot when resetting
                gamepad1.left_stick_button ? 2800 : 1400; // first is turbo speed, second is normal speed

        final double FL_power = velocity * Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
        final double FR_power = velocity * Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);
        final double BL_power = velocity * Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
        final double BR_power = velocity * Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);

        hardware.FL.setVelocity(FL_power);
        hardware.FR.setVelocity(FR_power);
        hardware.BL.setVelocity(BL_power);
        hardware.BR.setVelocity(BR_power);

        return new double[]{ FL_power, FR_power, BL_power, BR_power };
    }

    /**
     * Move the slides
     *
     * @return The individual target positions of the slides in the position { SS, US }
     */
    private double[] move_slides() {
        // disable movement commands when resetting
        if (resetting_state != 0) {
            if (gamepad1.dpad_right) {
                SS_position += 10;
            } else if (gamepad1.dpad_left) {
                SS_position -= 10;
            }

            if (gamepad1.dpad_up) {
                US_position = 4_000;
            } else if (gamepad1.y) {
                US_position = 2_000;
            } else if (gamepad1.dpad_down) {
                US_position = 400;
            }
        }

        SS_position = Range.clip(SS_position, 0, 1900);
        // edit the limits for resetting to allow for hard reset
        US_position = Range.clip(US_position, resetting_state != 0 ? 400 : 0, 4000);

        hardware.SS.setTargetPosition(SS_position);
        hardware.US.setTargetPosition(US_position);
        hardware.SS.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.US.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        return new double[]{ SS_position, US_position };
    }

    /**
     * Move the arms
     *
     * @return The servo positions in the configuration {bucket, claw, elbow, wrist}
     */
    private double[] move_arms() {
        if (resetting_state != 0) {
            bucket_value = 0.6 * gamepad1.right_trigger;

            if (gamepad1.x) {  // closed
                claw_value = 0.45;
            } else if (gamepad1.b) {  // open
                claw_value = 0.2;
            }

            if (gamepad1.right_bumper) {  // up
                elbow_value = 0.65;
            } else if (gamepad1.left_bumper) {  // down
                elbow_value = 0.02;
            } else if (SS_position > 500) {
                elbow_value = 0.17;
            }

            if (gamepad1.a) {  // horizontal
                wrist_value = 0.28;
            } else {  // vertical
                wrist_value = 0;
            }

            hardware.bucket.setPosition(bucket_value);
            hardware.claw.setPosition(claw_value);
            hardware.elbow.setPosition(elbow_value);
            hardware.wrist.setPosition(wrist_value);
        }

        return new double[]{ bucket_value, claw_value, elbow_value, wrist_value };
    }

    /**
     * Reset robot for TeleOp
     */
    private void reset_check() {
        if (DEBUG) {
            telemetry.addLine("≡≡≡≡≡≡≡≡ RESET COMMAND ≡≡≡≡≡≡≡≡");
            telemetry.addLine("Press the back button in the middle of the gamepad to reset the robot");
        }

        if (gamepad1.back && resetting_state == 0) {
            resetting_state = 1;
        }

        switch (resetting_state) {
            case 1: {
                // this is a physical get current position because we want the OpMode
                // to automatically stop when the reset has finished. This also does
                // not really need to be recorded by the macro
                if (hardware.US.getCurrentPosition() > 500) {
                    resetting_state = 2;
                } else {
                    US_position = 500;
                }

                break;
            }
            case 2: {
                US_position = 0;
                SS_position = 0;

                bucket_value = 0.5;

                if (hardware.US.getCurrentPosition() <= 1) {
                    recording_ended = true;
                    resetting_state = 3;
                }

                break;
            }
        }
    }

    /**
     * Check if a buffer empty
     *
     * @param gamepad Gamepad to check
     *
     * @return Whether the buffer is empty
     */
    private boolean nothing_currently_pressed(Gamepad gamepad) {
        boolean nothing_pressed = true;

        for (final Field field : Gamepad.class.getFields()) {
            // we only care about primitive types
            if (!field.getType().isPrimitive()) { continue; }

            try {
                Object value = field.get(gamepad);

                if (value instanceof Float) {  // joysticks and triggers
                    if (Math.abs(((Float) value)) > 0.1) {  // FPE and slight presses
                        nothing_pressed = false;
                    }
                } else if (value instanceof Boolean) {  // every other button
                    if ((Boolean) value) {
                        nothing_pressed = false;
                    }
                }
            } catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }

        return nothing_pressed;
    }

    private Hardware hardware;

    private int SS_position, US_position;
    private double bucket_value, claw_value, elbow_value, wrist_value;
    private boolean recording_ended = false;
    private int resetting_state = 0;
    private boolean reading_mode = true;
}