package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp(name = "Qualifier Robot First Version")
public class Qualifier_Robot extends LinearOpMode {
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

    DcMotor sideSlide;
    DcMotor upSlide;

    Servo Wrist;
    Servo Elbow;
    Servo Claw;
    Servo Bucket;

    private final ElapsedTime runtime = new ElapsedTime();

    int sideSlidePos;
    int upSlidePos;
    double setSpeed = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {

        setup();

        waitForStart();

        Claw.setPosition(0);
        Elbow.setPosition(0.05);
        Wrist.setPosition(0);
        upSlide.setTargetPosition(300);
        Bucket.setPosition(0);
        sideSlide.setTargetPosition(0);

        while (opModeIsActive()) {
            sideSlide.setPower(0.5);
            upSlide.setPower(1);

            if (gamepad2.left_stick_button) {
                setSpeed = 1;
            } else {
                setSpeed = 0.5;
            }

            if(gamepad2.y)
            {
                upSlide.setTargetPosition(2000);
            }

            Move(setSpeed);

            // Control slides by tic increase
            if (gamepad1.dpad_right) {
                sideSlidePos += 10;
                sideSlide.setTargetPosition(sideSlidePos);

            } else if (gamepad1.dpad_left) {
                sideSlidePos -= 10;
                sideSlide.setTargetPosition(sideSlidePos);
            }

            //Claw Control
            if (gamepad1.x) {
                // Closed
                Claw.setPosition(1);
            } else if (gamepad1.b) {
                //Open
                Claw.setPosition(0);
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
            if (gamepad1.right_bumper) {
                //Elbow up
                Elbow.setPosition(0.7);
            } else if (gamepad1.left_bumper) {
                //Elbow down
                Elbow.setPosition(0.05);
            }

            // Bucket Control
            if (gamepad1.y) {
                //Bucket up
                Bucket.setPosition(0.5);
            } else {
                //Elbow down
                Bucket.setPosition(0);
            }

            if (gamepad2.right_bumper) {
                upSlide.setTargetPosition(4000);
            } else if (gamepad2.left_bumper) {
                upSlide.setTargetPosition(300);
            }

            if(upSlide.getCurrentPosition() > 500)
            {
                Elbow.setPosition(0.07);
            }

            // Dedicated hang buttons for endgame

            //Display telemetry
            getTelemetry();

            // Slide Constraints --> SIDE SLIDE MUST BE BELOW 1900 FOR COMP (Horizontal expansion limit)
            upSlidePos = Math.max(300, Math.min(4000, upSlidePos));
            sideSlidePos = Math.max(0, Math.min(1900, sideSlidePos));



            // Slides run to pos
            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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

        Wrist = hardwareMap.get(Servo.class, "Claw wrist");
        Elbow = hardwareMap.get(Servo.class, "Elbow");
        Claw = hardwareMap.get(Servo.class, "Thing 1");
        Bucket = hardwareMap.get(Servo.class, "Thing2");

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.FORWARD);

        upSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        sideSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sideSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        upSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //Display telemetry during opMode: All servos and both slides positions
    public void getTelemetry() {
        telemetry.addData("Elbow", Elbow.getPosition());
        telemetry.addData("Wrist", Wrist.getPosition());
        telemetry.addData("Claw", Claw.getPosition());
        telemetry.addData("SideSlide", sideSlide.getCurrentPosition());
        telemetry.addData("UpSlide", upSlide.getCurrentPosition());
        telemetry.addData("Turbo:", setSpeed);
        telemetry.update();
    }

    private void Move(double speed) {
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        BR.setPower(speed * (1 * -gamepad2.left_stick_y + 1 * (1 * -gamepad2.left_stick_x - gamepad2.right_stick_x)));
        BL.setPower(speed * (1 * -gamepad2.left_stick_y + 1 * (1 * -gamepad2.left_stick_x + gamepad2.right_stick_x)));
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        FR.setPower(speed * (1 * -gamepad2.left_stick_y + 1 * (1 * gamepad2.left_stick_x - gamepad2.right_stick_x)));
        FL.setPower(speed * (1 * -gamepad2.left_stick_y + 1 * (1 * gamepad2.left_stick_x + gamepad2.right_stick_x)));
    }

}