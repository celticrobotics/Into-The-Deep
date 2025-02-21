package org.firstinspires.ftc.teamcode.phantom;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.phantom.utility.Hardware;
import org.firstinspires.ftc.teamcode.phantom.utility.Keyboard;
import org.firstinspires.ftc.teamcode.phantom.utility.Serializer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;

@TeleOp(name="Phantom Moving and Writing")
public class PhantomCommander extends LinearOpMode {
    // This OpMode works by saving each motor velocities, servo positions and linear
    // slide encoder values every frame to a buffer, then at the end of the OpMode,
    // we save the buffer to a file to be read in during autonomous. The way we try
    // to make the movement replication as accurate as possible is to save the data
    // as often as possible, by optimizing the code so that the part of the game
    // loop that takes the most time is the actual hardware movement itself,
    // (usually a 10ms delay). We also use DcMotorEx.setVelocity instead of
    // DcMotor.setPower because this appears to be more consistent and uses
    // encoders.

    private final String[] saved_hardware_commands_buffer = new String[5_000];

    public static final boolean DEBUG = false;

    public void runOpMode() {
        runtime.reset();

        hardware = new Hardware(hardwareMap, EnumSet.of(Hardware.HardwareFlag.ALL_HARDWARE));

        double dt = 0;
        int saved_buffer_index = 0;
        boolean start_recording = false;

        waitForStart();

        final ElapsedTime timer = new ElapsedTime();
        runtime.reset();

        // check if we have a valid index into the buffer, the op mode is active, and if we have not finished resetting
        while (saved_buffer_index < saved_hardware_commands_buffer.length && opModeIsActive() && resetting_state != 2) {
            final double[] drivetrain_values = move_drivetrain(dt * 1000);

            start_recording = start_recording || Arrays.stream(drivetrain_values).anyMatch(velocity -> Math.abs(velocity) > 0.01);

            // move everything and encode them in place
            final String drivetrain_block = Serializer.encode_numbers('w', drivetrain_values);
            final String slides_block = Serializer.encode_numbers('s', move_slides());
            final String arm_block = Serializer.encode_numbers('a', move_arms());

            reset_check();
            scoring_mode_check();

            // we update telemetry here to not have flashing values
            updateTelemetry(telemetry);


            // Our robot requires a certain amount of time for each iteration to complete.
            // What this means is that the time this loop takes in TeleOp should be greater
            // than or equal to the time it takes the Auto to run through one loop iteration
            // so what we can do is artificially inflate the time it takes for an iteration
            // to run.
            sleep(5);

            dt = timer.milliseconds();
            timer.reset();

            // save the time
            final String time_block = Serializer.encode_number('t', dt);

            telemetry.addData("elbow s", elbow_specimen_value);
            telemetry.addData("bucket", bucket_value);

            if (start_recording) {
                saved_hardware_commands_buffer[saved_buffer_index++] = String.join(", ", drivetrain_block, slides_block, arm_block, time_block);
            }

            previous_gamepad.copy(gamepad1);
       }

       if (resetting_state == 2) {  // encode all of the data at this point to a file
           final Keyboard keyboard = new Keyboard(telemetry, () -> gamepad1, this::opModeIsActive);
           String macro_name;
           boolean has_valid_macro = false;

           macro_name = keyboard.input("Enter macro name: ");

//
//           do {
//
//               if (macro_name == null) {
//                   return;
//               }
//               else if (macro_name.length() < 3) {}
//               else if (!macro_name.chars().allMatch(c -> Character.toLowerCase(c) == 'a')) {}
//                else {
//                    has_valid_macro = true;
//               }
//
//           } while (!has_valid_macro);

           Serializer.serialize(macro_name, saved_hardware_commands_buffer, hardware, telemetry, this::sleep);
       }
    }

    /**
     * Move drivetrain
     *
     * @param dt Delta time
     */
    private double[] move_drivetrain(final double dt) {
        if (DEBUG) {
            telemetry.addLine("≡≡≡≡≡≡ MOVEMENT COMMANDS ≡≡≡≡≡≡");
            telemetry.addLine(" - Left joystick moves robot");
            telemetry.addLine(" - Right joystick rotates robot");
            telemetry.addLine();
        }

        double power = gamepad1.left_stick_button ? 1 : gamepad1.right_stick_button ? 0.25 : 0.5;
        power *= dt;

         final double FL_direction = Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
         final double FR_direction = Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);
         final double BL_direction = Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
         final double BR_direction = Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);

        hardware.FL.setPower(power);
        hardware.FR.setPower(power);
        hardware.BL.setPower(power);
        hardware.BR.setPower(power);

        if (resetting_state == 0) {
            // first is turbo speed, second is slow speed, third is normal speed.

            FL_velocity += 30 * FL_direction;
            FR_velocity += 30 * FR_direction;
            BL_velocity += 30 * BL_direction;
            BR_velocity += 30 * BR_direction;
        }

        hardware.FL.setTargetPosition((int) Math.round(FL_velocity));
        hardware.FR.setTargetPosition((int) Math.round(FR_velocity));
        hardware.BL.setTargetPosition((int) Math.round(BL_velocity));
        hardware.BR.setTargetPosition((int) Math.round(BR_velocity));

        hardware.FL.setMode(RunMode.RUN_TO_POSITION);
        hardware.FR.setMode(RunMode.RUN_TO_POSITION);
        hardware.BL.setMode(RunMode.RUN_TO_POSITION);
        hardware.BR.setMode(RunMode.RUN_TO_POSITION);

        return new double[]{ FL_velocity, FR_velocity, BL_velocity, BR_velocity, power };
    }

    /**
     * Move the slides
     *
     * @return The individual target positions of the slides in the position { SS, US }
     */
   private double[] move_slides() {
       // disable movement commands when resetting
       if (resetting_state == 0) {
           if (gamepad1.dpad_right) {
               SS_position += 10;
           } else if (gamepad1.dpad_left) {
               SS_position -= 10;
           }

           if (gamepad1.dpad_up) {
               US_position = scoring_state == 0 ? 4_000 : 1900;
           } else if (gamepad1.y) {
               US_position = scoring_state == 0 ? 2_000 : 1000;
           } else if (gamepad1.dpad_down) {
               US_position = 0;
           }
       }

       if (hardware.SS_sensor.isPressed()) {
           hardware.SS.setPower(0.01);
           SS_position = 0;

           hardware.SS.setMode(RunMode.STOP_AND_RESET_ENCODER);
       } else {
           hardware.SS.setPower(1);
       }

       SS_position = Math.min(SS_position, 600);
       // edit the limits for resetting to allow for hard reset
       US_position = Range.clip(US_position, 0, 4000);

       hardware.SS.setTargetPosition(SS_position);
       hardware.US.setTargetPosition(US_position);
       hardware.SS.setMode(RunMode.RUN_TO_POSITION);
       hardware.US.setMode(RunMode.RUN_TO_POSITION);

       return new double[]{ SS_position, US_position };
   }

    /**
     * Move the arms
     *
     * @return The servo positions in the configuration {bucket, claw, elbow, wrist}
     */
    private double[] move_arms() {
        if (resetting_state == 0) {
            if (scoring_state == 0) {
                if (gamepad1.right_trigger > 0.5) {
                    bucket_value = 0.6;
                } else if (gamepad1.right_trigger < 0.2) {
                    bucket_value = 0.1;
                }

                if (gamepad1.x) {  // closed
                    claw_value = 0.45;
                } else if (gamepad1.b) {  // open
                    claw_value = 0.1;
                }

                if (gamepad1.right_bumper) {  // up
                    elbow_value = 0.559;
                    claw_elbow_value = 0.899;
                } else if (gamepad1.left_bumper && SS_position <= 10) {  // down
                    elbow_value = 0.145;
                    claw_elbow_value = 0.17;
                } else if (gamepad1.left_bumper && SS_position > 10) {
                    elbow_value = 0.151;
                    claw_elbow_value = 0.17;
                } else if (SS_position > 10) {
                    elbow_value = 0.299;
                    claw_elbow_value = 0;
                }

                if (gamepad1.a) {  // horizontal
                    wrist_value = 0.28;
                } else {  // vertical
                    wrist_value = 0;
                }
            } else if (scoring_state == 1) {
                if (gamepad1.x) {  // open
                    claw_specimen_value = 0.05;
                } else if (gamepad1.b) {  // closed
                    claw_specimen_value = 0.5;
                }

                if (gamepad1.a) {  // down
                    elbow_specimen_value = 1;
                } else if (gamepad1.y) {  // scoring
                    elbow_specimen_value = 0.5;
                }
            } else if (scoring_state == 2) {
                claw_specimen_value = claw_value = Math.sin((2 * Math.PI) / 5000 * runtime.milliseconds());
            }
        }

        hardware.bucket.setPosition(bucket_value);
        hardware.claw.setPosition(claw_value);
        hardware.claw_elbow.setPosition(claw_elbow_value);
        hardware.elbow.setPosition(elbow_value);
        hardware.wrist.setPosition(wrist_value);
        hardware.claw_specimen.setPosition(claw_specimen_value);
        hardware.elbow_specimen.setPosition(elbow_specimen_value);

        return new double[]{ bucket_value, claw_value, claw_elbow_value, elbow_value, wrist_value, claw_specimen_value, elbow_specimen_value };
    }

    /**
     * Reset robot for TeleOp
     */
    private void reset_check() {
        if (DEBUG) {
            telemetry.addLine("≡≡≡≡≡≡≡≡ RESET COMMAND ≡≡≡≡≡≡≡≡");
            telemetry.addLine("Press the back button in the middle of the gamepad to reset the robot");
            telemetry.addData("Robot state", resetting_state);
        }

        if (gamepad1.back && resetting_state == 0) {
            resetting_state = 2;
        }
    }

    /**
     * Change the scoring mode of the robot
     */
    private void scoring_mode_check() {
        if (gamepad1.start && previous_gamepad.start) {
            scoring_state = (scoring_state + 1) % 3;
        }

        telemetry.addLine(String.format(Locale.ROOT, "Current mode: %d", scoring_state));
    }

    // things for looping
    private Hardware hardware;
    private final Gamepad previous_gamepad = new Gamepad();
    private final ElapsedTime runtime = new ElapsedTime();

    // positions
    private double FL_velocity, FR_velocity, BL_velocity, BR_velocity;
    private int SS_position, US_position;
    private double bucket_value = 0.1, claw_value, claw_elbow_value = 0.17, elbow_value = 0.151, wrist_value;
    private double claw_specimen_value = 0.2, elbow_specimen_value = 1;

    // state machines
    private int resetting_state = 0;
    private int scoring_state = 0;
}