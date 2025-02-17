package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Sensor;

// Fully functional
// Each button controls all individual usages
// Colour Sensor integration

@TeleOp(name = "Arm Test")
public class Arm_Test extends LinearOpMode {


    DcMotor Arm;
    int ArmPos;


    @Override
    public void runOpMode() throws InterruptedException {

        //HardwareMap
        Arm = hardwareMap.get(DcMotor.class,"sideSlide");


        //Declare motor position variables




        // Motor Configuration

        //Motor_Config();

        Arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



        waitForStart();





        while(opModeIsActive()) {

            if(gamepad1.a){
                Arm.setPower(0.5);
            }
            else if(gamepad1.y){
                Arm.setPower(-0.5);
            }
            else{
                Arm.setPower(0);
            }

            //Arm.setTargetPosition(ArmPos);
            //Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);



            telemetry.addData("Arm", Arm.getCurrentPosition());
            telemetry.update();

            //Servo Test code
//            tgt = -this.gamepad1.left_stick_y;
//            Thing2.setPosition(tgt);





            while (Arm.isBusy() && opModeIsActive()) {
                idle();
            }
        }


    }

//    public void Motor_Config()
//    {
//        Arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Arm.setDirection(DcMotorSimple.Direction.FORWARD);
//    }




}
