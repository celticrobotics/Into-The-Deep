package org.firstinspires.ftc.teamcode.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

@Disabled
@Autonomous(name = "Auto test")
public class Autonomous_TestV1 extends LinearOpMode {
//    @Override public void runOpMode() {
//        encoders enc = new encoders(hardwareMap, () -> {
//            telemetry.addLine("something is hapeong idk");
//            telemetry.update();
//            return opModeIsActive(); }, this::idle);
//
//        waitForStart();
//
//        enc.move(2000, 2000, 2000, 2000, 0.6);
//    }


    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    DcMotor sideSlide;
    DcMotor upSlide;

    Servo Thing1;
    Servo Elbow;
    Servo Thing2;

    private ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_REV = 537.7;
    static final double CIRCUMFERENCE_CM = 9.6;

    static final double COUNTS_PER_CM = COUNTS_PER_REV / CIRCUMFERENCE_CM;

    static final double power = 0.20;

    @Override
    public void runOpMode() {
        setup();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("EncoderLeft: ", String.valueOf(leftFront.getCurrentPosition()), "Encoder Right: ", rightFront.getCurrentPosition());
        telemetry.update();

        waitForStart();

        fwd(0.5, 200, 5);
        turn(0.5, 90, 5);
        strafe(-.5, 200, 6);
    }

    private void move(double speed, double[] distance, double timeout) {

        int leftBackT;
        int rightBackT;
        int leftFrontT;
        int rightFrontT;

        leftFrontT = leftFront.getCurrentPosition() + (int) (distance[0] * COUNTS_PER_CM);
        rightFrontT = rightFront.getCurrentPosition() + (int) (distance[1] * COUNTS_PER_CM);
        leftBackT = leftBack.getCurrentPosition() + (int) (distance[2] * COUNTS_PER_CM);
        rightBackT = rightBack.getCurrentPosition() + (int) (distance[3] * COUNTS_PER_CM);

        leftBack.setTargetPosition(leftBackT);
        rightBack.setTargetPosition(rightBackT);
        leftFront.setTargetPosition(leftFrontT);
        rightFront.setTargetPosition(rightFrontT);

        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        leftBack.setPower(Math.abs(speed));
        rightBack.setPower(Math.abs(speed));
        leftFront.setPower(Math.abs(speed));
        rightFront.setPower(Math.abs(speed));


        while (opModeIsActive() && (leftFront.isBusy() || rightFront.isBusy()) && (runtime.seconds() < timeout)) {

//                telemetry.addData("LeftDrive ", (leftFront.getCurrentPosition() / CIRCUMFERENCE_CM));
//                telemetry.addData("RightDrive ", (rightFront.getCurrentPosition() / CIRCUMFERENCE_CM));

            telemetry.addData("LeftDrive ", leftFront.getCurrentPosition());
            telemetry.addData("RightDrive ", rightFront.getCurrentPosition());
            telemetry.addData("LeftDrive Target Pos", leftFrontT);
            telemetry.addData("RightDrive Target Pos", rightFrontT);

            telemetry.update();

        }


        leftBack.setPower(0);
        rightBack.setPower(0);
        leftFront.setPower(0);
        rightFront.setPower(0);

        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        sleep(50);

    }

    private void fwd(double speed, double distance, double timeout) {
        move(speed, new double[]{distance, distance, distance, distance}, timeout);
    }

    private void turn(double speed, double dgr, double timeout) {
        final double real_degrees_idk_bruh_dont_ask_me_skibidi = dgr / -6.42857142857; //360/56 --> (7 dgr = 45 degree) * 8 to reach 360

        move(speed, new double[]{
                -real_degrees_idk_bruh_dont_ask_me_skibidi,
                 real_degrees_idk_bruh_dont_ask_me_skibidi,
                -real_degrees_idk_bruh_dont_ask_me_skibidi,
                 real_degrees_idk_bruh_dont_ask_me_skibidi},
            timeout);

    }

    private void strafe(double speed, double distance, double timeout) {
        move(speed, new double[]{distance, -distance, -distance, distance}, timeout);
    }

    private void setup() {
        leftFront = hardwareMap.get(DcMotor.class, "FL");
        rightFront = hardwareMap.get(DcMotor.class, "FR");
        leftBack = hardwareMap.get(DcMotor.class, "BL");
        rightBack = hardwareMap.get(DcMotor.class, "BR");

        sideSlide = hardwareMap.get(DcMotor.class, "sideSlide");
        upSlide = hardwareMap.get(DcMotor.class, "upSlide");

        Thing1 = hardwareMap.get(Servo.class, "Thing1");
        Elbow = hardwareMap.get(Servo.class, "Elbow");
        Thing2 = hardwareMap.get(Servo.class, "Thing2");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sideSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        upSlide.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    void bring_block_into_up_bucket() {
        // bring side slide into robot
//        sideSlide.setTargetPosition(-2100);
//        sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        sideSlide.setPower(1);

        Thing1.setPosition(1);


        while (sideSlide.isBusy() && opModeIsActive()) {
            idle();
        }
    }

    void raise_Slides() {
        upSlide.setTargetPosition(3000);
        upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        upSlide.setPower(1);

        while (upSlide.isBusy() && opModeIsActive()) {
            idle();
        }


    }

//    void intake(double timeout) {
//        while(timeout Thing1.setPosition(0);
//
//        while (upSlide.isBusy() && opModeIsActive()) {
//            idle();
//        }
//    }
}