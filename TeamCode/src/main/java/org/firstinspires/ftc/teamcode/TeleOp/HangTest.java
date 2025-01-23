package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Disabled
@TeleOp(name = "HangTest")
public class HangTest extends LinearOpMode {
    @Override

    public void runOpMode() throws InterruptedException {

        DcMotor Hang;
        DcMotor Hangup;
        DcMotor upSlide;

        int HangPos;
        int HangupPos;

        Hang = hardwareMap.get(DcMotor.class, "Hang");
        Hangup = hardwareMap.get(DcMotor.class, "Hangup");
        upSlide = hardwareMap.get(DcMotor.class, "upSlide");
        //Slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //Slide.setDirection(DcMotorSimple.Direction.REVERSE);
        Hang.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Hang.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Hang.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Hang.setDirection(DcMotorSimple.Direction.REVERSE);

        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        Hangup.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Hangup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Hangup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Hangup.setDirection(DcMotorSimple.Direction.REVERSE);

        HangPos = Hang.getCurrentPosition();
        HangupPos = Hang.getCurrentPosition();


        waitForStart();

        while (opModeIsActive()) {
//            SlidePos = Slide.getCurrentPosition();
            Hang.setPower(1);
            Hangup.setPower(1);
            upSlide.setPower(1);

            upSlide.setTargetPosition(1000);
            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            if (gamepad1.a) {
                HangPos += 150;
            } else if (gamepad1.y) {
                HangPos -= 150;
            }

            if (gamepad1.x) {
                HangupPos += 10;
            } else if (gamepad1.b) {
                HangupPos -= 10;
            }

            // Hangup Position at top = 600

            HangPos = Range.clip(HangPos, 0, 16000);
            Hang.setTargetPosition(HangPos);
            Hangup.setTargetPosition(HangupPos);
            Hang.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Hangup.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            telemetry.addData("Position Hang: ", Hang.getCurrentPosition());
            telemetry.addData("Position Hangup: ", Hangup.getCurrentPosition());
            //telemetry.addData("Current", Hang.getCurrent(CurrentUnit.AMPS));
            telemetry.update();

        }
    }
}
