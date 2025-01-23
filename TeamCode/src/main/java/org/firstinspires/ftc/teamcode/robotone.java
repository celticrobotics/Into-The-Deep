package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@TeleOp(name = "cool robot")
public class robotone extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeft = hardwareMap.dcMotor.get("FL");
        DcMotor frontRight = hardwareMap.dcMotor.get("FR");
        DcMotor backLeft = hardwareMap.dcMotor.get("BL");
        DcMotor backRight = hardwareMap.dcMotor.get("BR");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();
        while(opModeIsActive()) {

            int compoundFactor = Math.abs(gamepad1.left_stick_x) != 0 && Math.abs(gamepad1.left_stick_y) != 0 ? 2 : 1;
            float turningFactor = -gamepad1.left_trigger + gamepad1.right_trigger;

//            frontLeft.setPower(0.1);
//            frontRight.setPower(0.1);
//            backLeft.setPower(0.1);
//            backRight.setPower(0.1);


            //  frontLeft.setPower((gamepad1.left_stick_x + gamepad1.left_stick_y) / compoundFactor - turningFactor);
            //  frontRight.setPower((-gamepad1.left_stick_x + gamepad1.left_stick_y) / compoundFactor + turningFactor);
            //  backLeft.setPower((-gamepad1.left_stick_x + gamepad1.left_stick_y) / compoundFactor - turningFactor);
            //  backRight.setPower((gamepad1.left_stick_x + gamepad1.left_stick_y) / compoundFactor + turningFactor);

            frontLeft.setPower(gamepad1.left_stick_x);
            frontRight.setPower(-gamepad1.left_stick_x);
            backLeft.setPower(-gamepad1.left_stick_x);
            backRight.setPower(gamepad1.left_stick_x);

        }
    }
}

// +     -
// -     +