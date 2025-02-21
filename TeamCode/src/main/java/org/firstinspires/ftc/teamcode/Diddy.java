package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class Diddy extends LinearOpMode {
    public void runOpMode() {
        RevTouchSensor victim = hardwareMap.get(RevTouchSensor.class, "Touch");
        telemetry.setMsTransmissionInterval(10);
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("touch", victim.isPressed() ? "diddling" : "no diddling");
            telemetry.update();
        }
    }
}
