package org.firstinspires.ftc.teamcode.TeleOp;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Robot1Setup {
    Robot1Setup(HardwareMap map, Telemetry t, Supplier<Boolean> opModeIsActive, Consumer<Long> sleep) {
        hardwareMap = map;
        telemetry = t;
        this.opModeIsActive = opModeIsActive;
        this.sleep = sleep;
    }


    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;
    private final Supplier<Boolean> opModeIsActive;
    private final Consumer<Long> sleep;

    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    DcMotor sideSlide;
    DcMotor upSlide;

    Servo Claw;
    Servo Wrist;
    Servo Elbow;
    Servo Bucket;

    private final ElapsedTime runtime = new ElapsedTime();

    public void setup() {
        leftFront = hardwareMap.get(DcMotor.class, "FL");
        rightFront = hardwareMap.get(DcMotor.class, "FR");
        leftBack = hardwareMap.get(DcMotor.class, "BL");
        rightBack = hardwareMap.get(DcMotor.class, "BR");

        sideSlide = hardwareMap.get(DcMotor.class, "sideSlide");
        upSlide = hardwareMap.get(DcMotor.class, "upSlide");

        Claw = hardwareMap.get(Servo.class, "Thing 1");
        Wrist = hardwareMap.get(Servo.class, "Claw wrist");
        Elbow = hardwareMap.get(Servo.class, "Elbow");
        Bucket = hardwareMap.get(Servo.class, "Thing2");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        upSlide.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void getTelemetry(){
        telemetry.addData("Elbow", Elbow.getPosition());
        telemetry.addData("Wrist", Wrist.getPosition());
        telemetry.addData("Claw", Claw.getPosition());
        telemetry.addData("SideSlide", sideSlide.getCurrentPosition());
        telemetry.addData("UpSlide", upSlide.getCurrentPosition());
        telemetry.update();
    }

    public void move(double rbPower, double lbPower, double rfPower, double lfPower){
        rightBack.setPower(rbPower);
        leftBack.setPower(lbPower);
        rightFront.setPower(rfPower);
        leftFront.setPower(lfPower);
    }

    public void setElbow(boolean rotation){
        if (rotation){
            Elbow.setPosition(0.95);
        } else {
            Elbow.setPosition(0.3);
        }
    }
    public void setWrist(boolean rotation){
        if (rotation){
            Wrist.setPosition(0.5);
        } else {
            Wrist.setPosition(0.1);
        }
    }

   public void setUpSlidePos(boolean direction){
        if (direction) {
            upSlide.setTargetPosition(1500);
            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } else {
            upSlide.setTargetPosition(0);
            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }
}