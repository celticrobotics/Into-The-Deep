//package org.firstinspires.ftc.teamcode.TeleOp;
//
//import com.qualcomm.hardware.rev.RevColorSensorV3;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import org.firstinspires.ftc.teamcode.Sensor;
//
//// Fully functional
//// Each button controls all individual usages
//// Colour Sensor integration
//
//@TeleOp(name = "New Robot TeleOp")
//public class TeleOp_NewRobot extends LinearOpMode {
//
//    Servo LeftC;
//    Servo RightC;
//
//    Servo Elbow;
//
//    DcMotor Slide;
//
//    DcMotor Arm;
//
//    DcMotor FL;
//    DcMotor BL;
//    DcMotor FR;
//    DcMotor BR;
////    RevColorSensorV3 CS;
//
//    //Slides Pos Variables
//    int SlidePos;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//
//        //HardwareMap
////        CS = hardwareMap.get(RevColorSensorV3.class, "CS");
////        final Sensor idkman = new Sensor(CS, telemetry);
//        FL = hardwareMap.get(DcMotor.class, "FL");
//        FR = hardwareMap.get(DcMotor.class, "FR");
//        BL = hardwareMap.get(DcMotor.class, "BL");
//        BR = hardwareMap.get(DcMotor.class, "BR");
//
//        LeftC = hardwareMap.get(Servo.class, "Thing1_1");
//        RightC = hardwareMap.get(Servo.class, "Thing1_2");
//
//        Elbow = hardwareMap.get(Servo.class, "Elbow");
//
//        Slide = hardwareMap.get(DcMotor.class, "Slide");
//        Arm = hardwareMap.get(DcMotor.class, "Arm");
//
//
//        //Declare motor position variables
//
//        Slide = Slide.getCurrentPosition();
//
//
//        // Motor Configuration
//
//        Motor_Config();
//
//        waitForStart();
//
//        sideSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        //Run to bucket and drop sample
//        upSlide.setPower(1);
//        upSlide.setTargetPosition(100);
//        Thing2.setPosition(0.17);
//        sideSlide.setTargetPosition(0);
//        Elbow.setPosition(0.91);
//        upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//
//        int ninja = 0; //Test Var
//
//
//
//        while(opModeIsActive()) {
//            Move();
//
//            sideSlidePos = Math.max(Math.min(sideSlidePos, 0), -1900);
//            upSlidePos = Math.max(Math.min(upSlidePos, 0), -3150);
//
//            //Thing1 and Elbow Button Control
//            if (gamepad1.a) {
//                Thing1_1.setPosition(0);
//                Thing1_2.setPosition(1);
//            } else if (gamepad1.y) {
//                Thing1_1.setPosition(1);
//                Thing1_2.setPosition(0);
//            } else {
//                Thing1_1.setPosition(0.5);
//                Thing1_2.setPosition(0.5);
//            }
//            if (gamepad1.x) {
//                Elbow.setPosition(0.91);
//
//            } else if (gamepad1.b) {
//                Elbow.setPosition(0.3);
//            }
//
//            if (gamepad2.x) {
//                sideSlidePos += 100;
//
//            } else if (gamepad2.b) {
//                sideSlidePos -= 100;
//            }
//
//            if(gamepad2.y){
//                upSlide.setTargetPosition(1500);
//                upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            } else if(gamepad2.a)
//            {
//                upSlide.setTargetPosition(0);
//                upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }
//
//            sideSlide.setTargetPosition(sideSlidePos);
//            sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            sideSlide.setPower(1);
//
////            switch (idkman.get_detected_color()) {
////                case RED:
////                    Thing1.setPosition(1);
////                    break;
////                case BLUE:
////                    Thing1.setPosition(0);
////                    break;
////                case YELLOW:
////                     break;
////                case MOIST_CYAN:
/////
////                default:
////
////            }
//
//            telemetry.addData("Count (dont use this)", ++ninja);
//            telemetry.addData("Elbow", Elbow.getPosition());
//            telemetry.addData("Thing1_1", Thing1_1.getPosition());
//            telemetry.addData("Thing1_2", Thing1_2.getPosition());
//            telemetry.addData("Thing2", Thing2.getPosition());
//            telemetry.addData("SideSlide", sideSlidePos);
//            telemetry.addData("UpSlide", upSlidePos);
////            telemetry.addData("colour", idkman.get_detected_color());
//            telemetry.update();
//
//
//
////            //Slides work for low level
//
//            // evil floating bit hack
//            if (gamepad1.dpad_right) {
//                Thing2.setPosition(0);
//            }
//            else if (gamepad1.dpad_left) {
//                Thing2.setPosition(0.8);
//            }
//
//            // evil floating bit hack
//            if (gamepad1.dpad_up) {
//                upSlide.setTargetPosition(4000);
//            } else if (gamepad1.dpad_down) {
//                upSlide.setTargetPosition(100);
//            }
//
//            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            upSlide.setPower(1);
//
//        }
//    }
//
//    public void Motor_Config()
//    {
//        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        upSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        upSlide.setDirection(DcMotorSimple.Direction.REVERSE);
//    }
//
//    private void Move() {
//        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
//        // We negate this value so that the topmost position corresponds to maximum forward power.
//        BR.setPower(0.5 * (1 * -gamepad1.left_stick_y + 1 * (1 * gamepad1.left_stick_x - gamepad1.right_stick_x)));
//        BL.setPower(0.5 * (1 * gamepad1.left_stick_y + 1 * (1 * gamepad1.left_stick_x - gamepad1.right_stick_x)));
//        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
//        // We negate this value so that the topmost position corresponds to maximum forward power.
//        FR.setPower(0.5 * (1 * -gamepad1.left_stick_y + 1 * (1 * -gamepad1.left_stick_x - gamepad1.right_stick_x)));
//        FL.setPower(0.5 * (1 * gamepad1.left_stick_y + 1 * -(1 * gamepad1.left_stick_x + gamepad1.right_stick_x)));
//    }
//
//
//}
