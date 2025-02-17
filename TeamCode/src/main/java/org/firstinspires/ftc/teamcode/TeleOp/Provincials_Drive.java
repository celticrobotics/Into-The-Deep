package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREV2mDistance;

@TeleOp(name = "Provincials TeleOp")
public class Provincials_Drive extends LinearOpMode {
//    private final HardwareMap hardwareMap;
//    private final Telemetry telemetry;
//    private final Supplier<Boolean> opModeIsActive;
//    private final Consumer<Long> sleep;

// CONTROLS FOR 1 GAME PAD:
// Buttons: X Close Claw
//          B Open Claw
//          A Wrist for Sample Horizontal
//          Y Wrist for Sample Vertical

// Left stick and Right stick: Chassis movement

// Dpad: Up: upSlide up
//       Down: upSlide down
//       Right: sideSlide extract
//       Left: sideSlide retract

// Stick Button: Right: Elbow Up
//               Left: Elbow Down

    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;
    private Servo ClawS;
    private Servo ElbowS;

    DcMotor sideSlide;
    DcMotor upSlide;
    DcMotor Hangup;
    DcMotor Hang;

    Servo Wrist;
    Servo Elbow;
    Servo Claw;
    Servo Bucket;
    Servo clawElbow;
    SensorREV2mDistance Distance;
    RevTouchSensor Touch;


    private final ElapsedTime runtime = new ElapsedTime();

    int sideSlidePos;
    int upSlidePos;
    double setSpeed = 0.5;
    int HangPos;

    boolean failSafe = true;

    @Override
    public void runOpMode() throws InterruptedException {

        setup();

        waitForStart();

        Claw.setPosition(0.2);
        Elbow.setPosition(0.182);
        clawElbow.setPosition(0.181);
        Wrist.setPosition(0);
        upSlide.setTargetPosition(400);
        Bucket.setPosition(0);
        sideSlide.setTargetPosition(0);
        Hangup.setTargetPosition(0);
        Hang.setTargetPosition(0);
        ClawS.setPosition(0.2);
        ElbowS.setPosition(1);

        boolean hanging = false;

        boolean hanged = false;


        while (opModeIsActive()) {
            sideSlide.setPower(1);
            upSlide.setPower(1);
            Hangup.setPower(1);
            Hang.setPower(1);

            if (gamepad1.left_stick_button) {
                setSpeed = 1;
            } else if (gamepad1.right_stick_button){
                setSpeed = 0.25;
            } else {
                setSpeed = 0.5;
            }

            Move(setSpeed);

            // Control slides by tic increase
            if (gamepad1.dpad_right) {
                sideSlidePos = 600;

            } else if (gamepad1.dpad_left && !Touch.isPressed()) {
                sideSlidePos = 0;
            }
            if(Touch.isPressed())
            {
                sideSlidePos = 0;
                telemetry.addData("Pos", " = 0!");
            }

            //Claw Control
            if (gamepad1.b) {
                // Open
                Claw.setPosition(0.2);
            } else if (gamepad1.x) {
                //Closed
                Claw.setPosition(0.45);
            }

            // Wrist control
            if (gamepad1.a) {
                //Sample Horizontal
                Wrist.setPosition(0.28);
            } else {
                //Sample Vertical
                Wrist.setPosition(0);
            }

            // Elbow Control
            if (gamepad1.right_bumper && upSlidePos < 10)
            {
                //Elbow up && Slides retracted
                Elbow.setPosition(0.56);
                clawElbow.setPosition(1);
            } else if (sideSlide.getCurrentPosition() <= 10 && gamepad1.left_bumper)
            {
                //Elbow down + Slides retracted
                Elbow.setPosition(0.182);
                clawElbow.setPosition(0.181);
            }
            else if(sideSlide.getCurrentPosition() > 10 && gamepad1.left_bumper)
            {
                //Elbow down + Slides extended
                Elbow.setPosition(0.161);
                clawElbow.setPosition(0.194);
            }
            else if(sideSlide.getCurrentPosition() > 10)
            {
                // Slides extended + Elbows up
                Elbow.setPosition(0.299);
                clawElbow.setPosition(0);
            }

            if(gamepad1.y)
            {
                upSlidePos = 2000;
            }

            // Bucket Control
            //Bucket.setPosition(gamepad1.right_trigger);
            // No pressure = 0 --> down
            // Pressure = 1 --> up

            if (gamepad1.dpad_up) {
                upSlidePos = 4000;
            } else if (gamepad1.dpad_down) {
                upSlidePos = 0;
            }

            if(upSlide.getCurrentPosition() > 600)
            {
                Elbow.setPosition(0.02);
            }

            // Dedicated hang buttons for endgame

            if(gamepad1.start && failSafe)
            {
                failSafe = false;
            } else if (gamepad1.start && !failSafe){
                failSafe = true;
            }

            // Used to be gamepad1.right_stick_bumper && !failSafe
            if(!failSafe) {
                hanging = !hanging;
                upSlidePos = 700;
                Bucket.setPosition(0.5);
                Hangup.setTargetPosition(300);

            }
            else if (!hanging){
                Bucket.setPosition(0.6*(gamepad1.right_trigger));
                Hangup.setTargetPosition(0);
            }

            if(gamepad1.left_trigger > 0 && !failSafe)
            {
                HangPos = 16000;
                hanged = true;
            }
            else if(hanged){
                HangPos = 3000;
            }
            else {
                HangPos = 0;
            }

            if(gamepad2.a){
                //down
                ElbowS.setPosition(1);
            }
            else if(gamepad2.y){
                //scoring
                ElbowS.setPosition(0.5);
            }
            if(gamepad2.x){
                //open
                ClawS.setPosition(0.2);
            }
            else if(gamepad2.b){
                //closed
                ClawS.setPosition(0.5);
            }

            if(gamepad2.dpad_up)
            {
                upSlidePos = 1900;
            }
            else if(gamepad2.dpad_down)
            {
                upSlidePos = 1300;
            }

            //0.17 Elbow down
            // 0.70 Elbow up

                // Distance sensor test code:

//            if(Distance > 10 && gamepad1.start)
//            {
//                BL.setPower(-0.2);
//                BR.setPower(-0.2);
//            }
//            else if(Distance < 10 && gamepad1.start)
//            {
//                BL.setPower(0.2);
//                BR.setPower(0.2);
//            }
//            else if(Distance == 10 && gamepad1.start)
//            {
//                BL.setPower(0);
//                BR.setPower(0);
//                upSlide.setTargetPosition(1900);
//
//            }

                //Display telemetry
                getTelemetry();

            // Slide Constraints --> SIDE SLIDE MUST BE BELOW 1900 FOR COMP (Horizontal expansion limit)
            upSlidePos = Range.clip(upSlidePos, 0, 4000);
            upSlide.setTargetPosition(upSlidePos);
            sideSlidePos = Math.max(0, Math.min(550, sideSlidePos));

            HangPos = Range.clip(HangPos, hanged ? 1000 : 0, 16000);
            Hang.setTargetPosition(HangPos);

            sideSlide.setTargetPosition(sideSlidePos);

            // Slides run to pos
            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Hang run to pos
            Hangup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Hang.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        }

    }

    // Setup: HardwareMap, motor direction, set brake, encoder setup(Run using encoders, reset encoders)
    public void setup() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        sideSlide = hardwareMap.get(DcMotor.class, "sideSlide");
        upSlide = hardwareMap.get(DcMotor.class, "upSlide");

        Hangup = hardwareMap.get(DcMotor.class, "Hangup");
        Hang = hardwareMap.get(DcMotor.class, "Hang");

        Wrist = hardwareMap.get(Servo.class, "Claw Wrist");
        Elbow = hardwareMap.get(Servo.class, "Elbow");
        Claw = hardwareMap.get(Servo.class, "Thing 1");
        Bucket = hardwareMap.get(Servo.class, "Thing2");
        clawElbow = hardwareMap.get(Servo.class, "Claw Elbow");

        ElbowS = hardwareMap.get(Servo.class, "Specimen Elbow");
        ClawS = hardwareMap.get(Servo.class, "Specimen Claw");

        //Distance = hardwareMap.get(SensorREV2mDistance.class, "Distance");
        Touch = hardwareMap.get(RevTouchSensor.class, "Penis");

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.FORWARD);

        Hang.setDirection(DcMotorSimple.Direction.REVERSE);
        Hangup.setDirection(DcMotorSimple.Direction.REVERSE);

        upSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        sideSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Hangup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Hang.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Hangup.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Hang.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sideSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        upSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        Hangup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Hang.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    //Display telemetry during opMode: All servos and both slides positions
    public void getTelemetry() {
        telemetry.addData("Elbow", Elbow.getPosition());
        telemetry.addData("Wrist", Wrist.getPosition());
        telemetry.addData("Claw", Claw.getPosition());
        telemetry.addData("SideSlide", sideSlide.getCurrentPosition());
        telemetry.addData("UpSlide", upSlide.getCurrentPosition());
        telemetry.addData("Turbo:", setSpeed);
        telemetry.addData("Hang Prime Angle:", Hangup.getCurrentPosition());
        telemetry.addData("Hang Slides Position:", Hang.getCurrentPosition());
        telemetry.addData("Distance from wall:", Distance);
        telemetry.update();
    }

    private void Move(double speed) {
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        BR.setPower(speed * (1 * -gamepad1.left_stick_y + 1 * (1 * -gamepad1.left_stick_x - gamepad1.right_stick_x)));
        BL.setPower(speed * (1 * -gamepad1.left_stick_y + 1 * (1 * -gamepad1.left_stick_x + gamepad1.right_stick_x)));
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        FR.setPower(speed * (1 * -gamepad1.left_stick_y + 1 * (1 * gamepad1.left_stick_x - gamepad1.right_stick_x)));
        FL.setPower(speed * (1 * -gamepad1.left_stick_y + 1 * (1 * gamepad1.left_stick_x + gamepad1.right_stick_x)));
    }

}