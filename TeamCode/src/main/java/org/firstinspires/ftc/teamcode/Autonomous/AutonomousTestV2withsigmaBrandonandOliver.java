package org.firstinspires.ftc.teamcode.Autonomous;


import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.Locale;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AutonomousTestV2withsigmaBrandonandOliver {
    AutonomousTestV2withsigmaBrandonandOliver(HardwareMap map, Telemetry t, Supplier<Boolean> opModeIsActive, Consumer<Long> sleep) {
        hardwareMap = map;
        telemetry = t;
        this.opModeIsActive = opModeIsActive;
        this.sleep = sleep;
    }

    // Declares and initializes motors, servos, and constants
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
    //DcMotor Arm;

    Servo Wrist;
    Servo Claw;
    Servo Elbow;
    Servo Bucket;

    private final ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_REV = 537.7;

    static final double CIRCUMFERENCE_CM = 9.6;

    static final double COUNTS_PER_CM = COUNTS_PER_REV / CIRCUMFERENCE_CM;

    static final double power = 0.20;

    /**
     * Moves the robot by setting target positions for each motor and applying power.
     *
     * @param speed The power level to apply to the motors (0 to 1).
     * @param distance An array of distances (in cm) for each motor in the order [leftFront, rightFront, leftBack, rightBack].
     * @param timeout The maximum time (in seconds) to complete the movement.
     */
    private void move(double speed, double[] distance, double timeout) {

        int leftBackT;
        int rightBackT;
        int leftFrontT;
        int rightFrontT;

        // Calculate target positions based on current positions and desired distances
        leftFrontT = leftFront.getCurrentPosition() + (int) (distance[0] * COUNTS_PER_CM);
        rightFrontT = rightFront.getCurrentPosition() + (int) (distance[1] * COUNTS_PER_CM);
        leftBackT = leftBack.getCurrentPosition() + (int) (distance[2] * COUNTS_PER_CM);
        rightBackT = rightBack.getCurrentPosition() + (int) (distance[3] * COUNTS_PER_CM);

        // Set target positions for each motor
        leftBack.setTargetPosition(leftBackT);
        rightBack.setTargetPosition(rightBackT);
        leftFront.setTargetPosition(leftFrontT);
        rightFront.setTargetPosition(rightFrontT);

        // Set motors to RUN_TO_POSITION mode
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        // Apply power to the motors
        leftBack.setPower(speed);
        rightBack.setPower(speed);
        leftFront.setPower(speed);
        rightFront.setPower(speed);

        // Monitor movement progress and timeout
        while (opModeIsActive.get() && (leftFront.isBusy() || rightFront.isBusy()) && (runtime.seconds() < timeout)) {
            telemetry.addData("LeftDrive ", leftFront.getCurrentPosition());
            telemetry.addData("RightDrive ", rightFront.getCurrentPosition());
            telemetry.addData("LeftDrive Target Pos", leftFrontT);
            telemetry.addData("RightDrive Target Pos", rightFrontT);

            telemetry.update();

        }

        // Stop motors and reset mode to RUN_USING_ENCODER
        leftBack.setPower(0);
        rightBack.setPower(0);
        leftFront.setPower(0);
        rightFront.setPower(0);

        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        sleep.accept(50L);
    }

    /**
     * Moves the robot forward or backward by the specified distance.
     *
     * @param speed The power level to apply to the motors (0 to 1).
     * @param distance The distance (in cm) to move. Positive for forward, negative for backward.
     * @param timeout The maximum time (in seconds) to complete the movement.
     */
    public void fwd(double speed, double distance, double timeout) {
        // Call the move method with equal distances for all motors
        move(speed, new double[]{distance, distance, distance, distance}, timeout);
    }

    /**
     * Rotates the robot in place by a specified angle.
     *
     * @param speed The power level to apply to the motors (0 to 1).
     * @param dgr The angle (in degrees) to turn. Positive for clockwise, negative for counterclockwise.
     * @param timeout The maximum time (in seconds) to complete the turn.
     */
    public void turn(double speed, double dgr, double timeout) {
        // Calculate the encoder distance required for the turn
        final double real_degrees_idk_bruh_dont_ask_me_skibidi = dgr / -6.42857142857; //360/56 --> (7 dgr = 45 degree) * 8 to reach 360

        // Call the move method with opposite distances for left and right motors
        move(speed, new double[]{
                        -real_degrees_idk_bruh_dont_ask_me_skibidi,
                        real_degrees_idk_bruh_dont_ask_me_skibidi,
                        -real_degrees_idk_bruh_dont_ask_me_skibidi,
                        real_degrees_idk_bruh_dont_ask_me_skibidi},
                timeout);

    }

    /**
     * Moves the robot sideways (left or right) by a specified distance.
     *
     * @param speed The power level to apply to the motors (0 to 1).
     * @param distance The distance (in cm) to strafe. Positive for right, negative for left.
     * @param timeout The maximum time (in seconds) to complete the movement.
     */
    public void strafe(double speed, double distance, double timeout) {
        // Call the move method with a specific motor pattern for strafing
        move(speed, new double[]{distance, distance, -distance, -distance}, timeout);
    }

    /**
     * Initializes and configures all hardware components (motors, servos, etc...).
     * This method must be called before any other methods in the class.
     */
    public void setup() {
        // Map motors and servos to hardware configurations
        leftFront = hardwareMap.get(DcMotor.class, "FL");
        rightFront = hardwareMap.get(DcMotor.class, "FR");
        leftBack = hardwareMap.get(DcMotor.class, "BL");
        rightBack = hardwareMap.get(DcMotor.class, "BR");

        sideSlide = hardwareMap.get(DcMotor.class, "sideSlide");
        upSlide = hardwareMap.get(DcMotor.class, "upSlide");
        //Arm = hardwareMap.get(DcMotor.class, "ArmH");

        Wrist = hardwareMap.get(Servo.class, "Claw wrist");
        Claw = hardwareMap.get(Servo.class, "Thing 1");
        Elbow = hardwareMap.get(Servo.class, "Elbow");
        Bucket = hardwareMap.get(Servo.class, "Thing2");

        // Configure motor directions
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Reset encoders and set motors to RUN_USING_ENCODER mode.
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set zero power behaviour to BRAKE for precise stopping.
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        upSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //Arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sideSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set up servo directions and default positions (if needed)
        sideSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        upSlide.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void Initialization()
    {
        Claw.setPosition(0);
        Elbow.setPosition(0.02);
        Wrist.setPosition(0);
        sideSlide.setTargetPosition(0);
        upSlide.setTargetPosition(300);
        Bucket.setPosition(0);

        sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    /**
     * Moves the vertical linear slide to a specified position using encoder counts.
     *
     * @param speed The power to apply to the motor (range: 0.0 to 1.0).
     * //@param position The target position in encoder counts to move the slide to, relative to its current position.
     */
    public void moveVerticalLinearSlide(double speed, int position){

        // Set the motor power to the specified speed to start moving
        upSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        upSlide.setPower(speed);
        upSlide.setTargetPosition(position);
        upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Reset the runtime timer to measure how long the operation takes
        // Continuously monitor the motor's progress while the operation is active and within the timeout limit
        while(upSlide.isBusy())
        {
            telemetry.addData("UpslidePos", upSlide.getCurrentPosition());
            telemetry.update();
        }
    }

    /**
     * Moves the horizontal linear slide to a specified position using encoder counts.
     *
     * @param speed The power to apply to the motor (range: 0.0 to 1.0).
     * //@param position The target position in encoder counts to move the slide to, relative to its current position.
     * @param timeout The maximum time (in seconds) to allow the slide to reach the target position.
     */
    public void moveHorizontalLinearSlide(double speed, int position, int timeout){
        // Calculate the target position by adding the desired movement to the current encoder position
        int sideSlideT = sideSlide.getCurrentPosition() + position;

        // Set the motor's target position for encoder-based movement
        sideSlide.setTargetPosition(sideSlideT);

        // Configure the motor to run to the specified position
        sideSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Reset the runtime timer to measure how long the operation takes
        runtime.reset();

        // Set the motor power to the specified speed to start moving
        sideSlide.setPower(speed);

        // Continuously monitor the motor's progress while the operation is active and within the timeout limit
        while (opModeIsActive.get() && sideSlide.isBusy() && runtime.seconds() < timeout) {
            // Retrieve the current encoder position of the motor
            final int currentPosition = sideSlide.getCurrentPosition();
//
//            telemetry.addLine()
//                    .addData("target position", sideSlideT)
//                    .addData("current position", currentPosition)
//                    .addData("completion", String.format(Locale.CANADA, "%d%%", (currentPosition / sideSlideT) * 100));
        }

        // Stop the motor and set the motor to RUN_USING_ENCODER mode
        sideSlide.setPower(0);
        sideSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

   public void rotateThingyIntoBasket(int timeoutS) throws InterruptedException {
        runtime.reset();
        while(runtime.seconds() < timeoutS)
        {
            Bucket.setPosition(0.5);
        }
        Bucket.setPosition(0);
   }
//   public void scoreBlockThingy() throws InterruptedException {
//       moveVerticalLinearSlide(0.7, 4000, 5);
//       rotateThingyIntoBasket();
//       moveVerticalLinearSlide(0.7, 250, 5);
//   }
   // might not work, imma blame emmy for lack of info
//   public void intake() {
//       Thing11.setPosition(0);
//       Thing12.setPosition(1);
//   }
//   // might not work, imma blame emmy for lack of info
//   public void outtake() {
//       Thing11.setPosition(1);
//       Thing12.setPosition(0);
//   }
   public void rotateOurElbow(double position) {
       Elbow.setPosition(position);
   }



    /**
     * Operates the robot's intake mechanism for grabbing or releasing objects.
     *
   //  * @param intake The desired intake state (INTAKE, OUTTAKE, or IDLE).
     //* @param timeoutS The duration (in seconds) for which the intake mechanism should operate.
     */

    public enum ClawPosition {
        OPEN, CLOSED;
    }
    public void ClawMove(ClawPosition claw, double timeoutS){

        // MUST RESET RUNTIME SO TIMER IS IN SYNC WITH METHOD
        runtime.reset();

        // Operate the intake mechanism for the specified duration
        while(runtime.seconds() < timeoutS)
        {
        switch(claw) {
            case OPEN:
                // Set servo positions for intake
                //RightC.setPosition(1);
                Claw.setPosition(0);
                break;
            case CLOSED:
                // Set servo positions for outtake
                //RightC.setPosition(0);
                Claw.setPosition(1);
                break;
            //case IDLE:
                // Set servos to idle position
                //RightC.setPosition(0.5);
                //Claw.setPosition(0.5);
                //break;
            default:
                telemetry.addData("Intake method Error:", "Incorrect value");
                telemetry.update();
        }
        }
    }

    public enum WristPos {
        HORI, VERTI;
    }
    public void WristMove(WristPos wrist, double timeoutS){

        // MUST RESET RUNTIME SO TIMER IS IN SYNC WITH METHOD
        runtime.reset();

        // Operate the intake mechanism for the specified duration
        while(runtime.seconds() < timeoutS)
        {
            switch(wrist) {
                case HORI:
                    // Set servo positions for intake
                    //RightC.setPosition(1);
                    Wrist.setPosition(0.28);
                    break;
                case VERTI:
                    // Set servo positions for outtake
                    //RightC.setPosition(0);
                    Wrist.setPosition(0);
                    break;
                //break;
                default:
                    telemetry.addData("Wrist method Error:", "Incorrect value");
                    telemetry.update();
            }
        }
    }

    public enum ElbowPos {
        UP, DOWN;
    }
    public void ElbowMove(ElbowPos elbow, double timeoutS){

        // MUST RESET RUNTIME SO TIMER IS IN SYNC WITH METHOD
        runtime.reset();

        // Operate the intake mechanism for the specified duration
        while(runtime.seconds() < timeoutS)
        {
            switch(elbow) {
                case UP:
                    // Set servo positions for intake
                    //RightC.setPosition(1);
                    Elbow.setPosition(0.7);
                    break;
                case DOWN:
                    // Set servo positions for outtake
                    //RightC.setPosition(0);
                    Elbow.setPosition(0.02);
                    break;
                //break;
                default:
                    telemetry.addData("Wrist method Error:", "Incorrect value");
                    telemetry.update();
            }
        }
    }

    public void origin(int timeoutS){
        runtime.reset();
        while(runtime.seconds() < timeoutS){
            upSlide.setTargetPosition(300);
            upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    public void reset(){

        runtime.reset();
        Bucket.setPosition(0.4);
        upSlide.setTargetPosition(0);
        upSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        ElapsedTime freaker = new ElapsedTime();

        while (opModeIsActive.get() && freaker.seconds() < 100)
            Claw.setPosition(Math.abs(Math.sin(freaker.seconds() * .5)));


        while (upSlide.isBusy());
    }



}