package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp(name = "Intake Test")
public class Intake_Test extends LinearOpMode {

    Servo LeftC;
    Servo RightC;

    Servo Elbow;

    @Override
    public void runOpMode() throws InterruptedException {

        LeftC = hardwareMap.get(Servo.class, "LeftC");
        RightC = hardwareMap.get(Servo.class, "RightC");

        Elbow = hardwareMap.get(Servo.class, "Elbow");

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                LeftC.setPosition(0);
                RightC.setPosition(1);
            }
            else if (gamepad1.y) {
                LeftC.setPosition(1);
                RightC.setPosition(0);
            }
            else
            {
                LeftC.setPosition(0.5);
                RightC.setPosition(0.5);
            }


        }
    }

}
