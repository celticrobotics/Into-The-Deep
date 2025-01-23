package org.firstinspires.ftc.teamcode.Hudson;

import android.os.Environment;
import android.util.Pair;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@TeleOp(name="Movement AND Writing Test")
public class MovingWritingTest extends LinearOpMode {
    DcMotorEx FL, FR, BL, BR;
    final static String file_name = "macro.csv";

    /**
     * Encode a bunch of numbers with colon delimiters and a single character prefix
     *
     * @param prefix The prefix given to the encoding, usually denoted for the type of encoding
     * @param items  The numbers to encode
     * @return The encoded string
     */
    String encode(char prefix, Number... items) {
        final String joined_items = Arrays.stream(items)
                .map(Number::toString)
                .collect(Collectors.joining(":"));

        return String.format(Locale.CANADA, "%c%s", prefix, joined_items);
    }

    /**
     * Decode an encoded string from {@link #encode}
     *
     * @param encoded_string The encoded string
     * @return The original parameters
     */
    static Pair<Character, Double[]> decode(String encoded_string) {
        Double[] result = Arrays.stream(encoded_string.substring(1).split(":"))
                .map(Double::parseDouble)
                .toArray(Double[]::new);

        return new Pair<>(encoded_string.charAt(0), result);
    }

    /**
     * Move the wheels with gamepad1
     * <br><br>
     * THE LEFT JOYSTICK CONTROLS OF GAMEPAD ONE ALLOW THE ROBOT TO STRAFE AND BE PROPELLED
     * THE RIGHT JOYSTICK CONTROLS OF GAMEPAD ONE ALLOW THE ROBOT TO TURN
     *
     * @return The powers given to each motor in the configuration {FL, FR, BL, BR}
     */
    double[] move_wheels() {
        final double normal = 0.5;    // normal speed
        final double threshold = 1.1; // point where turbo mode gets activated

        // enable boosting when you hard press the motor wheel
        final boolean boost = Math.abs(gamepad1.left_stick_x) >= threshold || Math.abs(gamepad1.left_stick_y) >= threshold;

        // power with turbo mode
        final double power = boost ? 1 : normal;

        //                      turbo   up and down                           left and right             rotation                   limit power to -1, 1
        final double FL_power = power * Range.clip((-gamepad1.left_stick_y) + ( gamepad1.left_stick_x) + ( gamepad1.right_stick_x), -1, 1);
        final double FR_power = power * Range.clip((-gamepad1.left_stick_y) + (-gamepad1.left_stick_x) + (-gamepad1.right_stick_x), -1, 1);
        final double BL_power = power * Range.clip((-gamepad1.left_stick_y) + (-gamepad1.left_stick_x) + ( gamepad1.right_stick_x), -1, 1);
        final double BR_power = power * Range.clip((-gamepad1.left_stick_y) + ( gamepad1.left_stick_x) + (-gamepad1.right_stick_x), -1, 1);

        FL.setPower(FL_power);
        FR.setPower(FR_power);
        BL.setPower(BL_power);
        BR.setPower(BR_power);

        return new double[]{FL_power, FR_power, BL_power, BR_power};
    }

    void write_line_to_file(String line, File file) {
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(line + '\n');
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    static File get_file() {
        return new File(String.format(Locale.CANADA, "%s/FIRST/data/%s", Environment.getExternalStorageDirectory().getAbsolutePath(), file_name));
    }

    void create_files_directory(File file) {
        final String directory = Objects.requireNonNull(file.getParent());
        final File directory_file = new File(directory);

        if (directory_file.exists()) {
            return;
        }

        if (!directory_file.mkdirs()) {
            throw new RuntimeException("Failed to make: " + directory);
        }
    }

    @Override
    public void runOpMode() {
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

        create_files_directory(get_file());


        try (FileWriter writer = new FileWriter(get_file())) {} catch (IOException exception) { throw new RuntimeException(exception); }

        waitForStart();

        long delta_time = 0;

        while (opModeIsActive()) {
            // start timer
            long start = System.currentTimeMillis();

            // move everything
            final double[] wheel_positions = move_wheels();

            final String wheel_encoded = encode('w', wheel_positions[0], wheel_positions[1], wheel_positions[2], wheel_positions[3]);
            //

            sleep(15);

            final String time_encoded = encode('t', delta_time);

            final String total = String.format("%s, %s", wheel_encoded, time_encoded);

            write_line_to_file(total, get_file());

            delta_time = System.currentTimeMillis() - start;
        }
    }
}