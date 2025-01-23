package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class Motor_Setup {

    private Servo Thing1;
    private Servo Elbow;
    private Servo Thing2;

    private DcMotor sideSlide;
    private DcMotor upSlide;

    private DcMotor FL;
    private DcMotor BL;
    private DcMotor FR;
    private DcMotor BR;

    public Motor_Setup()
    {
        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

//        BL.setDirection(DcMotorSimple.Direction.REVERSE);
//        FL.setDirection(DcMotorSimple.Direction.REVERSE);

        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sideSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setDirection(DcMotorSimple.Direction.FORWARD);
        sideSlide.setDirection(DcMotorSimple.Direction.FORWARD);
    }





}
