package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Move test")
public class MoveTest extends LinearOpMode {

    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;

    @Override
    public void runOpMode() throws InterruptedException {

        setup();

        waitForStart();

        while(opModeIsActive())
        {
            Move(0.5);
        }

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

    public void setup() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.FORWARD);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    }
}
