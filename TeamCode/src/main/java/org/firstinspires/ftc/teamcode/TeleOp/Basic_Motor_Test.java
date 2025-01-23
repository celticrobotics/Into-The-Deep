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
@TeleOp(name = "Motor Test")
public class Basic_Motor_Test extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx Slide;
        int SlidePos;

        Slide = (DcMotorEx) hardwareMap.get(DcMotor.class, "Hang");
        //Slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //Slide.setDirection(DcMotorSimple.Direction.REVERSE);
        Slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Slide.setDirection(DcMotorSimple.Direction.REVERSE);

        SlidePos = Slide.getCurrentPosition();

        waitForStart();

        while(opModeIsActive())
        {
//            SlidePos = Slide.getCurrentPosition();
            Slide.setPower(1);

            if(gamepad1.a)
            {
                SlidePos += 150;
            }
            else if(gamepad1.y)
            {
                SlidePos -= 150;
            }

            SlidePos = Range.clip(SlidePos, 0, 16000);
            Slide.setTargetPosition(SlidePos);
            Slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            telemetry.addData("Position: ", Slide.getCurrentPosition());
            telemetry.addData("Current", Slide.getCurrent(CurrentUnit.AMPS));
            telemetry.update();

            sleep(16);
        }
    }
}
