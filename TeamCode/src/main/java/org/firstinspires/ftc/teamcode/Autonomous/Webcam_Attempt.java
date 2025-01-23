// Pipeline base taken from FTC team Reynolds Reybots 18840

package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.openftc.easyopencv.OpenCvPipeline;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;
import java.util.List;

/**
 * This program allows you to cycle through the different filters in a EasyOpenCV Pipeline.
 *
 * To use, press A on Gamepad 1 to toggle to the next filter. Please note that you will not be able
 * to cycle to the next one while in the camera stream. Return to the main interface (where you can
 * see telemetry) to cycle.
 *
 * It currently detects for a blue team prop.
 */
@TeleOp(name="OpenCV Colour Example", group="OpenCV")
public class Webcam_Attempt extends OpMode
{
    // Variable declaration
    OpenCvCamera camera;
    BlueBlobPipeline blueBlobPipeline;
    long lastButtonPress = 0;

    /**
     * Stages (filters) in this EasyOpenCV Pipeline.
     */
    enum Stage
    {
        RAW_IMAGE,
        RAW_IMAGE_TO_HSV,
        THRESHOLD,
        ERODE,
        CONTOURS_OVERLAY_ON_FRAME,
        BOUNDING_BOX
    }

    // Pipeline Stage Variable Declaration
    private static Stage stageToRenderToViewport = Stage.RAW_IMAGE;
    private final Stage[] stages = Stage.values();
    int currentStageNum = stageToRenderToViewport.ordinal();


    /**
     * Sets up Pipeline and Camera Stream
     */
    @Override
    public void init()
    {
        blueBlobPipeline = new BlueBlobPipeline();

        int cameraMonitorViewId = hardwareMap
                .appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        camera = OpenCvCameraFactory
                .getInstance()
                .createWebcam(hardwareMap.get(WebcamName.class, "Laggy"), cameraMonitorViewId);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override public void onOpened() {
                camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                camera.setPipeline(blueBlobPipeline);
            }

            @Override public void onError(int errorCode) {
                telemetry.addData("Failed to open camera due to error code", errorCode);
                telemetry.update();
            }
        });
    }

    /**
     * Infinite loop until hit run to allow cycling through stages.
     */
    @Override
    public void init_loop()
    {
        telemetry.addData("Prop location is", BlueBlobPipeline.getPropLocation());
        telemetry.addData("Current filter", BlueBlobPipeline.getCurrentStage());

        if (gamepad1.a && (System.currentTimeMillis() - lastButtonPress) > 200)
        {
            int nextStageNum = currentStageNum + 1;
            lastButtonPress = System.currentTimeMillis();

            if(nextStageNum >= stages.length)
            {
                nextStageNum = 0;
            }

            stageToRenderToViewport = stages[nextStageNum];

            currentStageNum = nextStageNum;
        }

        telemetry.update();
    }

    @Override public void loop() {}


    /**
     * EasyOpenCV Pipeline to detect Blue Team Prop.
     */
    static class BlueBlobPipeline extends OpenCvPipeline
    {
        public static int CAMERA_WIDTH = 320;

        // Declaring which is defined as left and right of screen
        public static double LEFT_X  = 0.25 * (double) CAMERA_WIDTH;
        public static double RIGHT_X = 0.75 * (double) CAMERA_WIDTH;

        // Variable to store Prop location
        public static String propLocation;

        // The more Erode passes you do, the more noise will be removed (but if you do too much, you
        // may erode your team prop. A good balance is key - Remember you don't have to remove all
        // noise. Part of the pipeline is to select the biggest blob, removing noise simply reduces
        // computational time.
        public static int ERODE_PASSES = 9;

        // Erode setup
        private static final Point CV_ANCHOR        = new Point(-1, -1);
        private static final Scalar CV_BORDER_VALUE = new Scalar(-1);
        private static final int CV_BORDER_TYPE     = Core.BORDER_CONSTANT;

        // Colour of the Bounding Rectangle
        public static volatile Scalar BOUNDING_RECTANGLE_COLOR = new Scalar(0, 255, 0);

        // Range for a Blue Team Prop
        public static Scalar LOW_HSV_RANGE_BLUE  = new Scalar(97, 100, 0);
        public static Scalar HIGH_HSV_RANGE_BLUE = new Scalar(125, 255, 255);

        // Mat object initialization
        private final Mat hsvMat    = new Mat(),
                hierarchy           = new Mat(),
                cvErodeKernel       = new Mat(),
                thresholdOutput     = new Mat(),
                erodeOutput         = new Mat(),
                contoursOutput      = new Mat();

        /**
         * Pipeline to process the frame.
         *
         * @param input from the camera.
         * @return current stage of pipeline (normally you only return the final stage)
         */
        @Override
        public Mat processFrame(Mat input)
        {
            // Convert color to HSV
            Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

            // Checks if the image is in range
            Core.inRange(hsvMat, LOW_HSV_RANGE_BLUE, HIGH_HSV_RANGE_BLUE, thresholdOutput);

            // Erode to remove noise
            Imgproc.erode(
                    thresholdOutput,
                    erodeOutput,
                    cvErodeKernel,
                    CV_ANCHOR,
                    ERODE_PASSES,
                    CV_BORDER_TYPE,
                    CV_BORDER_VALUE);

            // Finds the contours of the image
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(erodeOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            // Creates bounding rectangles along all of the detected contours
            MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
            Rect[] boundRect = new Rect[contours.size()];
            for (int i = 0; i < contours.size(); i++)
            {
                contoursPoly[i] = new MatOfPoint2f();
                Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
                boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            }

            Rect biggestBoundingBox = new Rect(0, 0, 0, 0);

            // Gets the biggest bounding box
            for (Rect rect : boundRect)
            {
                if (rect.area() > biggestBoundingBox.area())
                {
                    biggestBoundingBox = rect;
                }
            }

            if (biggestBoundingBox.area() != 0)
            { // If prop is detected
                if (biggestBoundingBox.x < LEFT_X)
                { // Check to see if the bounding box is on the left 25% of the screen
                    propLocation = "LEFT";
                } else if (biggestBoundingBox.x > RIGHT_X)
                { // Check to see if the bounding box is on the right 25% of the screen
                    propLocation = "RIGHT";
                } else
                { // If it isn't left or right and the prop is detected it must be in the center
                    propLocation = "CENTER";
                }
            } else
            { // If prop is not detected
                propLocation = "NONE";
            }

            // Select stage to return to viewport
            switch (stageToRenderToViewport)
            {
                default:
                case RAW_IMAGE:
                {
                    return input;
                }

                case RAW_IMAGE_TO_HSV:
                {
                    return hsvMat;
                }

                case THRESHOLD:
                {
                    return thresholdOutput;
                }

                case ERODE:
                {
                    return erodeOutput;
                }

                case CONTOURS_OVERLAY_ON_FRAME:
                {
                    input.copyTo(contoursOutput);
                    Imgproc.drawContours(contoursOutput, contours, -1, new Scalar(0, 255, 0), 1, 8);

                    return contoursOutput;
                }

                case BOUNDING_BOX:
                {
                    Imgproc.rectangle(input, biggestBoundingBox, BOUNDING_RECTANGLE_COLOR);
                    return input;
                }
            }
        }

        /**
         * Get location of prop.
         * @return A string with the location of prop.
         */
        public static String getPropLocation()
        {
            return propLocation;
        }

        /**
         * Name of the current stage.
         * @return string of the current stage name.
         */
        private static String getCurrentStage()
        {
            return stageToRenderToViewport.name();
        }
    }
}