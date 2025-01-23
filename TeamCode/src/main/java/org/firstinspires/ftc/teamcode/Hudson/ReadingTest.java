package org.firstinspires.ftc.teamcode.Hudson;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

@TeleOp(name="Reading Test")
public class ReadingTest extends LinearOpMode {
    DcMotorEx FL, FR, BL, BR;

    @Override public void runOpMode() {
        FL = (DcMotorEx) hardwareMap.dcMotor.get("FL");
        FR = (DcMotorEx) hardwareMap.dcMotor.get("FR");
        BL = (DcMotorEx) hardwareMap.dcMotor.get("BL");
        BR = (DcMotorEx) hardwareMap.dcMotor.get("BR");

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        BL.setDirection(DcMotorSimple.Direction.REVERSE);
        FL.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        File file_name = MovingWritingTest.get_file();

        try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            String line = null;

            // read input line by line
            while ((line = br.readLine()) != null) {
                Arrays.stream(line.split(", "))
                        .map(MovingWritingTest::decode)
                        .forEach(power_type -> {
                            if (isStopRequested()) {
                                return;
                            }

                            final Double[] powers = power_type.second;

                            switch (power_type.first) {
                                case 'w': {
                                    FL.setPower(powers[0]);
                                    FR.setPower(powers[1]);
                                    BL.setPower(powers[2]);
                                    BR.setPower(powers[3]);

                                    break;
                                }
                                case 't': {
                                    sleep(powers[0].longValue()); // it takes about 7ms to write to file

                                    break;
                                }

                                default: throw new RuntimeException("Unknown case for power type: " + power_type.first);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}