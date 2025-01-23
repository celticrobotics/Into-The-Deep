package org.firstinspires.ftc.teamcode.Hudson;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor.*;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Encoder Moving Test")
public class Hudson_freakCode extends LinearOpMode {
    DcMotorEx FL, FR, BL, BR;

    @Override public void runOpMode() {
        FL = (DcMotorEx) hardwareMap.dcMotor.get("FL");
        FR = (DcMotorEx) hardwareMap.dcMotor.get("FR");
        BL = (DcMotorEx) hardwareMap.dcMotor.get("BL");
        BR = (DcMotorEx) hardwareMap.dcMotor.get("BR");

        FL.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);

//        FL.setDirection(Direction.REVERSE);
        BL.setDirection(Direction.REVERSE);

        FL.setMode(RunMode.RUN_USING_ENCODER);
        FR.setMode(RunMode.RUN_USING_ENCODER);
        BL.setMode(RunMode.RUN_USING_ENCODER);
        BR.setMode(RunMode.RUN_USING_ENCODER);
        FL.setMode(RunMode.STOP_AND_RESET_ENCODER);
        FR.setMode(RunMode.STOP_AND_RESET_ENCODER);
        BL.setMode(RunMode.STOP_AND_RESET_ENCODER);
        BR.setMode(RunMode.STOP_AND_RESET_ENCODER);
        FL.setPower(0.4);
        FR.setPower(0.4);
        BL.setPower(0.4);
        BR.setPower(0.4);

        FL_target = FL.getCurrentPosition();
        FR_target = FR.getCurrentPosition();
        BL_target = BL.getCurrentPosition();
        BR_target = BR.getCurrentPosition();

        waitForStart();

        while (opModeIsActive()) {
            normal_move();
//            circle_move();

            telemetry.addLine()
                    .addData("FL current", FL.getCurrentPosition())
                    .addData("FR current", FR.getCurrentPosition())
                    .addData("BL current", BL.getCurrentPosition())
                    .addData("BR current", BR.getCurrentPosition());

            telemetry.addLine()
                    .addData("FL target", FL_target)
                    .addData("FR target", FR_target)
                    .addData("BL target", BL_target)
                    .addData("BR target", BR_target);

            telemetry.update();

            sleep(10);
        }
    }

    int FL_target, FR_target, BL_target, BR_target;
    final double target = 20;

    void normal_move() {
        final double power = 0.5;

        double FL_power = power * Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
        double FR_power = power * Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);
        double BL_power = power * Range.clip(-gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x, -1, 1);
        double BR_power = power * Range.clip(-gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x, -1, 1);

        FL_target += (int)(FL_power * target);
        FL_target += (int)(FR_power * target);
        FL_target += (int)(BL_power * target);
        FL_target += (int)(BR_power * target);

        FL.setTargetPosition(FL_target);
        FR.setTargetPosition(FR_target);
        BL.setTargetPosition(BL_target);
        BR.setTargetPosition(BR_target);

        FL.setMode(RunMode.RUN_TO_POSITION);
        FR.setMode(RunMode.RUN_TO_POSITION);
        BL.setMode(RunMode.RUN_TO_POSITION);
        BR.setMode(RunMode.RUN_TO_POSITION);
    }

    void circle_move() {
        final double x = gamepad1.left_stick_x;
        final double y = -gamepad1.left_stick_y;
        final double turn = gamepad1.right_stick_x;

        final double theta = Math.atan2(y, x);
        final double power = Math.hypot(x, y);

        final double sin = Math.sin(theta - Math.PI / 4);
        final double cos = Math.cos(theta - Math.PI / 4);
        final double max = Math.max(Math.abs(sin), Math.abs(cos));

        double FL_power = power * cos / max + turn;
        double FR_power = power * sin / max - turn;
        double BL_power = power * sin / max + turn;
        double BR_power = power * cos / max - turn;

        if ((power + Math.abs(turn)) > 1) {
            FL_power /= power + Math.abs(turn);
            FR_power /= power + Math.abs(turn);
            BL_power /= power + Math.abs(turn);
            BR_power /= power + Math.abs(turn);
        }

        FL_target += (int)(FL_power * target);
        FL_target += (int)(FR_power * target);
        FL_target += (int)(BL_power * target);
        FL_target += (int)(BR_power * target);

        FL.setTargetPosition(FL_target);
        FR.setTargetPosition(FR_target);
        BL.setTargetPosition(BL_target);
        BR.setTargetPosition(BR_target);

        FL.setMode(RunMode.RUN_TO_POSITION);
        FR.setMode(RunMode.RUN_TO_POSITION);
        BL.setMode(RunMode.RUN_TO_POSITION);
        BR.setMode(RunMode.RUN_TO_POSITION);
    }
}