package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Sensor;

// Currently copy of Teleop Test V1 (Hypothetically on the RED team)
// Base off of colour sensor code
// i.e. If Blue alliance, reject red samples, and vice versa
// Fewer controls for intake
// One button to push out the sideSlide
// One button to intake
// IF CORRECT colour is sensed, THEN transfer sample
// --> Transfer sample code method: Set Target position for SideSlide to be 0, flip elbow to Thing2
// One button to Lift the slides
// Side pads to bring up and bring down Thing2
// Up and down pads to bring up and down Slides

//TRY SOMETHING: When Slide is extended to a certain point, Elbow automatically comes out and starts trying to intake

// Hanging --> One button special up and down

@Disabled
@TeleOp(name = "TeleOp Test V2")
public class TeleOpTestV2 extends LinearOpMode {

    Servo Thing1;

    Servo Elbow;

    Servo Thing2;

    DcMotor sideSlide;

    DcMotor FL;
    DcMotor BL;
    DcMotor FR;
    DcMotor BR;
    RevColorSensorV3 CS;

    DcMotor upSlide;

    //Slides Pos Variables
    int sideSlidePos;
    double upSlidePos;


    @Override
    public void runOpMode() throws InterruptedException {

        //HardwareMap
        CS = hardwareMap.get(RevColorSensorV3.class, "CS");
        final Sensor idkman = new Sensor(CS, telemetry);
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        Thing1 = hardwareMap.get(Servo.class, "Thing1");
        Elbow = hardwareMap.get(Servo.class, "Elbow");
        Thing2 = hardwareMap.get(Servo.class, "Thing2");

        sideSlide = hardwareMap.get(DcMotor.class, "sideSlide");
        upSlide = hardwareMap.get(DcMotor.class, "upSlide");


        //Declare motor position variables

        sideSlidePos = sideSlide.getCurrentPosition();
        upSlidePos = upSlide.getCurrentPosition();


        // Motor Configuration

        Motor_Config();

        waitForStart();

        sideSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Run to bucket and drop sample
        upSlide.setPower(1);
        upSlide.setTargetPosition(250);
        Thing2.setPosition(0.17);
        sideSlide.setTargetPosition(0);
        upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        while(opModeIsActive()) {
            Move();


            //Thing1 and Elbow Button Control

            if (gamepad1.a) {
                Thing1.setPosition(0);
            } else if (gamepad1.y) {
                Thing1.setPosition(0.8);
            } else {
                Thing1.setPosition(0.5);
            }
            if (gamepad1.x) {
                Elbow.setPosition(0.95);

            } else if (gamepad1.b) {
                Elbow.setPosition(0.3);
            }

            //POSSIBLE: Transfer will happen over and over again because it continues to see red
            // OR it stops seeing red after a bit, and stops running the program

            switch (idkman.get_detected_color()) {
                case RED:
                    Transfer();
                    break;
                case BLUE:
                    Thing1.setPosition(1);
                    break;
                case YELLOW:
                    Transfer();
                    break;
                case MOIST_CYAN:

                default:

            }

            telemetry.addData("Elbow", Elbow.getPosition());
            telemetry.addData("Thing1", Thing1.getPosition());
            telemetry.addData("Thing2", Thing2.getPosition());
            telemetry.addData("SideSlide", sideSlidePos);
            telemetry.addData("UpSlide", upSlidePos);
            telemetry.addData("colour", idkman.get_detected_color());
            telemetry.update();
            telemetry.update();

            if (gamepad2.x) {
                sideSlidePos += 100;

            } else if (gamepad2.b) {
                sideSlidePos -= 100;
            }


            //Slides work for low level

            //Hanging, NOT COMPLETE YET

//            if(gamepad2.y){
//                upSlide.setTargetPosition(1500);
//                upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            } else if(gamepad2.a)
//            {
//                upSlide.setTargetPosition(0);
//                upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }

            if (gamepad1.dpad_right) {
                Thing2.setPosition(0.17);
            } else if (gamepad1.dpad_left) {
                Thing2.setPosition(1);
            }

            sideSlidePos = Math.max(Math.min(sideSlidePos, 0), -2100);
            upSlidePos = Math.max(Math.min(upSlidePos, 0), -3150);

            if (gamepad1.dpad_up) {
                upSlide.setTargetPosition(3150);
            } else if (gamepad1.dpad_down) {
                upSlide.setTargetPosition(250);
            }

            sideSlide.setTargetPosition(sideSlidePos);
            sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            sideSlide.setPower(1);

            while (upSlide.isBusy() && opModeIsActive()) {
                idle();
            }
        }


    }

    public void Motor_Config()
    {
        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    private void Move() {
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        BR.setPower(0.5 * (1 * -gamepad1.left_stick_y + 1 * (1 * gamepad1.left_stick_x - gamepad1.right_stick_x)));
        BL.setPower(0.5 * (1 * gamepad1.left_stick_y + 1 * (1 * gamepad1.left_stick_x - gamepad1.right_stick_x)));
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        FR.setPower(0.5 * (1 * -gamepad1.left_stick_y + 1 * (1 * -gamepad1.left_stick_x - gamepad1.right_stick_x)));
        FL.setPower(0.5 * (1 * gamepad1.left_stick_y + 1 * -(1 * gamepad1.left_stick_x + gamepad1.right_stick_x)));
    }

    // --> Transfer sample code method: Set Target position for SideSlide to be 0, flip elbow to Thing2

    private void Transfer() {
        //Bring slides in
        sideSlide.setTargetPosition(0);
        sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Flip elbow to Thing2 UNSURE IF 0.95 IS UP OR DOWN, if it's wrong, try 0.3
        Elbow.setPosition(0.95);

        //Turn on intake to transfer Block --> Transfer OVER or UNDER?
        Thing1.setPosition(1); // Tried over
    }

    private void Ult_Intake(){
        //If sideSlidePos is greater than a certain point, Eblow Position is set to come down
        //AUTO Intake
        if(sideSlidePos >= 1000){
            Elbow.setPosition(0.3);
            Thing1.setPosition(0);
        }

    }


}
