package org.firstinspires.ftc.teamcode.phantom;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.phantom.utility.Hardware;
import java.util.EnumSet;

@TeleOp(name="Driving/Reset")
public class Driving extends LinearOpMode {
    public void runOpMode() {
        final Hardware hardware = new Hardware(hardwareMap, EnumSet.of(Hardware.HardwareFlag.DRIVETRAIN_ONLY));

        waitForStart();

        while (opModeIsActive()) {
            final double velocity = gamepad1.left_stick_button ? 2800 : 1400;

            hardware.FL.setVelocity(velocity * (-gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x));
            hardware.FR.setVelocity(velocity * (-gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x));
            hardware.BL.setVelocity(velocity * (-gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x));
            hardware.BR.setVelocity(velocity * (-gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x));
        }
    }
}