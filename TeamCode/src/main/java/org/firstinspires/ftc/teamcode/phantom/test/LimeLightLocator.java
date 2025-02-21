package org.firstinspires.ftc.teamcode.phantom.test;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.phantom.utility.Hardware;
import java.util.EnumSet;

@TeleOp(name="Limelight Locator")
public class LimeLightLocator extends LinearOpMode {
    Limelight3A lime_light;

    public void runOpMode() throws InterruptedException {
        final Hardware hardware = new Hardware(hardwareMap, EnumSet.of(Hardware.HardwareFlag.DRIVETRAIN_ONLY));

        lime_light = hardwareMap.get(Limelight3A.class, "limelight");
        lime_light.pipelineSwitch(0);
        lime_light.start();

        waitForStart();

        while (opModeIsActive()) {
            final double velocity = gamepad1.left_stick_button ? 2800 : 1400;

            hardware.FL.setVelocity(velocity * (-gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x));
            hardware.FR.setVelocity(velocity * (-gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x));
            hardware.BL.setVelocity(velocity * (-gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x));
            hardware.BR.setVelocity(velocity * (-gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x));

            final LLResult result = lime_light.getLatestResult();

            if (result == null) continue;
        }

        lime_light.stop();
    }
}