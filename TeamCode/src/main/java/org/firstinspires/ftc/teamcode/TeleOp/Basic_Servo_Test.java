package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "Basic Servo Test")
public class Basic_Servo_Test extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        //Servo Claw;
        double ServoPos;
        Servo Servo;
        //Servo Elbow;

        //Claw = hardwareMap.get(Servo.class, "Thing 1");
        Servo = hardwareMap.get(Servo.class, "Claw wrist");
        //Elbow = hardwareMap.get(Servo.class, "Elbow");

        waitForStart();

        //Claw.setPosition(0);
        //Wrist.setPosition(0.44);

        while(opModeIsActive()){

            ServoPos = -this.gamepad1.left_stick_y;
            Servo.setPosition(ServoPos);

//            if(gamepad1.x)
//            {
//                // Closed
//                Claw.setPosition(1);
//            }
//            else if(gamepad1.b)
//            {
//                //Open
//                Claw.setPosition(0);
//            }

//            if(gamepad1.a)
//            {
//                //Sample Horizontal
//                Wrist.setPosition(0);
//            }
//            else if(gamepad1.y)
//            {
//                //Sample Vertical
//                Wrist.setPosition(0.28);
//            }
//
//            if (gamepad1.dpad_right)
//            {
//                //Elbow up
//                Elbow.setPosition(0.8);
//            }
//            else if(gamepad1.dpad_left)
//            {
//                //Elbow down
//                Elbow.setPosition(0.1);
//            }

            //telemetry.addData("Elbow", Elbow.getPosition());
            //telemetry.addData("Claw Pos", Claw.getPosition());
            telemetry.addData("Servo Pos", Servo.getPosition());

            telemetry.update();

        }

    }
}
