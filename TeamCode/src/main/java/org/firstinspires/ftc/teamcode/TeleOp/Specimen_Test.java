//package org.firstinspires.ftc.teamcode.TeleOp.;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import java.util.EnumSet;
//
//@TeleOp
//public class nig extends LinearOpMode {
//
//    Servo Claw;
//    Servo Elbow;
//
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//
//        Claw = hardwareMap.servo.get("Specimen Claw");
//        Elbow = hardwareMap.servo.get("Specimen Elbow");
//
////        Claw.setPosition(0.2);
////        Elbow.setPosition(1);
//
//        waitForStart();
//
//
//
//        while (opModeIsActive()) {
//            final double velocity = gamepad1.left_stick_button ? 2800 : 1400;
//
//            if(gamepad1.a){
//                Elbow.setPosition(1);
//            }
//            else if(gamepad1.y){
//                Elbow.setPosition(0.5);
//            }
//            if(gamepad1.x){
//                Claw.setPosition(0.);
//            }
//            else if(gamepad1.b){
//                Claw.setPosition(0.5);
//            }
//
//            if (gamepad1.dpad_up) {
//                hardware.US.setTargetPosition(4_000);
//            } else if (gamepad1.dpad_down) {
//                hardware.US.setTargetPosition(400);
//            }
//            else if(gamepad1.start)
//            {
//                hardware.US.setTargetPosition(1300);
//            }
//            else if(gamepad1.back)
//            {
//                hardware.US.setTargetPosition(1900);
//            }
//            hardware.SS.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//            //Claw.setPosition(gamepad1.right_trigger);
//            //Elbow.setPosition(gamepad1.left_trigger);
//
//            hardware.FL.setVelocity(velocity * (-gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x));
//        }
//
//
//    }
//
//}