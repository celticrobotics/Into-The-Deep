//package org.firstinspires.ftc.teamcode;
//
//import androidx.annotation.ColorInt;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.hardware.rev.RevColorSensorV3;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//@TeleOp
//public class ColourSensorExample extends LinearOpMode {
//    @Override public void runOpMode() {
//
//        waitForStart();

//        switch (idkman.get_detected_color()) {
//            case RED:
//                //move_forward();
//                break;
//            case BLUE:
//                //move_right();
//                break;
//            case YELLOW:
//            case MOIST_CYAN:
//
//            default:
//
//        }
//
//        while (opModeIsActive()) {
//            telemetry.addLine()
//                    .addData("color", idkman.get_detected_color());
//
//            telemetry.update();
//        }
//    }
//}