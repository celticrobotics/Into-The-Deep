package org.firstinspires.ftc.teamcode.phantom.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.phantom.utility.Hardware;
import org.firstinspires.ftc.teamcode.phantom.utility.Serializer;
import java.util.EnumSet;

@Disabled
@TeleOp(name="Coefficient Graphing Test")
public class CoefficientGraphingTest extends LinearOpMode {
    private final String[] saved_hardware_commands_buffer = new String[5_000];
    public static final String MACRO_NAME = "coefficient";

    public void runOpMode() {
        hardware = new Hardware(hardwareMap, EnumSet.of(Hardware.HardwareFlag.DRIVETRAIN_ONLY));
        get_xi();

        waitForStart();

        ElapsedTime timer = new ElapsedTime();

        for (int saved_buffer_index = 0; saved_buffer_index < saved_hardware_commands_buffer.length && opModeIsActive(); ++saved_buffer_index) {
            // move everything and encode them in place
            final String drivetrain_command = Serializer.encode_numbers('w', move_drivetrain());

            force_stop_check();

            sleep(2);

            final String time_command = Serializer.encode_number('t', timer.milliseconds());
            timer.reset();

            // we save this to a buffer
            saved_hardware_commands_buffer[saved_buffer_index] = String.join(", ", drivetrain_command, time_command);
        }

        // encode all of the data at this point to a file
        Serializer.serialize(MACRO_NAME, saved_hardware_commands_buffer, hardware, telemetry, this::sleep);
    }

    /**
     * Move the drivetrain
     *
     * @return The individual motor velocities for the robot encoded in { FL, FR, BL, BR }
     */
    private double[] move_drivetrain() {
        double FL_power, FR_power, BL_power, BR_power;

        final double power = gamepad1.left_stick_button ? 2800 : 1400; // first is turbo speed, second is normal speed

        /* traditional movement */ {
            FL_power = power * Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
            FR_power = power * Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);
            BL_power = power * Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
            BR_power = power * Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);
        }

        hardware.FL.setVelocity(FL_power);
        hardware.FR.setVelocity(FR_power);
        hardware.BL.setVelocity(BL_power);
        hardware.BR.setVelocity(BR_power);

        return new double[]{ FL_power, FR_power, BL_power, BR_power };
    }

    /**
     * Force stop check
     */
    private void force_stop_check() {
        if (gamepad1.start || gamepad2.start) {
            requestOpModeStop();
        }
    }

    void get_xi() {
        double accuracy = 0.1;

        while (opModeInInit()) {
            telemetry.addLine("Dpad up/down to change value, left/right to change accuracy, Start to confirm");
            telemetry.addData("accuracy",  accuracy);
            telemetry.addData("xi", xi);

            if (gamepad1.dpad_up && !previous_gamepad.dpad_up) {
                xi += accuracy;
            }
            if (gamepad1.dpad_down && !previous_gamepad.dpad_down) {
                xi -= accuracy;
            }
            if (gamepad1.dpad_right && !previous_gamepad.dpad_right) {
                accuracy *= 10;
            }
            if (gamepad1.dpad_left && !previous_gamepad.dpad_left) {
                accuracy /= 10;
            }

            previous_gamepad.copy(gamepad1);

            xi = Range.clip(xi, 0, 1);

            updateTelemetry(telemetry);

            if (gamepad1.start) {
                telemetry.clear();
                telemetry.update();

                return;
            }
        }
    }
    static double xi = 1;
    private final Gamepad previous_gamepad = new Gamepad();


    private Hardware hardware;
}