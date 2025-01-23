package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp(name = "Intake and skibidi elbow Test")
public class Intake_and_Elbow extends LinearOpMode {

    Servo LeftC;
    Servo RightC;
    Servo Elbow;
    DcMotor Arm;
    DcMotor FL;
    DcMotor BL;
    DcMotor FR;
    DcMotor BR;
    DcMotor Slide;
    int ArmPos = 0;
    int SlidePos = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        LeftC = hardwareMap.get(Servo.class, "LeftC");
        RightC = hardwareMap.get(Servo.class, "RightC");
        Elbow = hardwareMap.get(Servo.class, "Elbow");
        Arm = hardwareMap.get(DcMotor.class, "Arm");

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        Slide = hardwareMap.get(DcMotor.class, "Slide");

        Arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Motor_Config();

        Slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        Slide.setTargetPosition(0);
        Elbow.setPosition(0.6);

        while (opModeIsActive()) {

            Move();

            ArmPos = Arm.getCurrentPosition();
            SlidePos = Slide.getCurrentPosition();

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

            if(gamepad1.x){
                ArmPos += 20;
            }
            else if(gamepad1.b){
                ArmPos -= 20;
            }

            if (gamepad1.dpad_up) {
                SlidePos += 100;

            } else if (gamepad1.dpad_down) {
                SlidePos -= 100;
            }
            else
            {
                SlidePos += 0;
            }

            if(gamepad1.dpad_left)
            {
                Elbow.setPosition(0.2);
            }
            else if(gamepad1.dpad_right)
            {
                Elbow.setPosition(0.6);
            }


            telemetry.addData("ElbowPos", Elbow.getPosition());
            telemetry.addData("LeftC",LeftC.getPosition());
            telemetry.addData("RightC",RightC.getPosition());
            telemetry.addData("Arm",Arm.getCurrentPosition());
            telemetry.addData("ArmPos",ArmPos);
            telemetry.addData("Slide", Slide.getCurrentPosition());
            telemetry.update();

            ArmPos = Math.max(0, Math.min(1900, ArmPos));
            SlidePos = Math.max(0, Math.min(1000, SlidePos));
            Arm.setTargetPosition(ArmPos);
            Slide.setTargetPosition(SlidePos);
            Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Arm.setPower(1);
            Slide.setPower(0.5);

        }




    }
    private void Move() {
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        BR.setPower(0.5 * (1 * -gamepad1.left_stick_y + 1 * (1 * gamepad1.left_stick_x - gamepad1.right_stick_x)));
        BL.setPower(0.5 * (1 * -gamepad1.left_stick_y + 1 * (1 * -gamepad1.left_stick_x - gamepad1.right_stick_x)));
        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
        // We negate this value so that the topmost position corresponds to maximum forward power.
        FR.setPower(0.5 * (1 * -gamepad1.left_stick_y + 1 * (1 * -gamepad1.left_stick_x - gamepad1.right_stick_x)));
        FL.setPower(0.5 * (1 * gamepad1.left_stick_y + 1 * -(1 * gamepad1.left_stick_x + gamepad1.right_stick_x)));
    }

    public void Motor_Config()
    {
        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FL.setDirection(DcMotorSimple.Direction.REVERSE);
        BL.setDirection(DcMotorSimple.Direction.REVERSE);

        Slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Slide.setDirection(DcMotorSimple.Direction.REVERSE);

    }



}

