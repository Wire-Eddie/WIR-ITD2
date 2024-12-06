/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED
BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
/*
 * This OpMode executes a POV Game style Teleop for a direct drive robot
 * The code is structured as a LinearOpMode
 *
 * In this mode the left stick moves the robot FWD and back, the Right stick turns left and right.
 * It raises and lowers the arm using the Gamepad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new
name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station
OpMode list
 */
@TeleOp(name="TeLeOp", group="Robot")
//@Disabled
public class wirTeleop extends LinearOpMode {
    wirHardware robot = new wirHardware();
    ElapsedTime timer = new ElapsedTime();
    int tickPostion = 0;
    @Override
    public void runOpMode() {
        double left;
        double right;
        double drive;
        double turn;
        double max;
        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready. Press START."); //
        telemetry.update();
        robot.init(hardwareMap);
        robot.leftArm.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightArm.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftArm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        robot.rightArm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        robot.leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        robot.leftBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        robot.rightBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        robot.rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        // Wait for the game to start (driver presses START)
        waitForStart();
        timer.reset();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("TickPosition", tickPostion);
            telemetry.addData("currentRight", robot.rightArm.getCurrentPosition());
            telemetry.addData("currentLeft", robot.leftArm.getCurrentPosition());
            telemetry.update();
            // Run wheels in POV mode (note: The joystick goes negative when pushed forward, so
            negate it)
            // In this mode the Left stick moves the robot fwd and back, the Right stick turns left
            and right.
            // This way it's also easy to just drive straight, or just turn.
            drive = -gamepad1.left_stick_y;
            turn = gamepad1.right_stick_x;
            // Combine drive and turn for blended motion.
            left = drive + turn;
            right = drive - turn;
            // Normalize the values so neither exceed +/- 1.0
            max = Math.max(Math.abs(left), Math.abs(right));
            if (max > 1.0)
            {
                left /= max;
                right /= max;
            }
            if (gamepad2.dpad_up){
                if (timer.milliseconds() >= 100){
                    tickPostion-=100;
                    timer.reset();
                }
            }else if (gamepad2.dpad_down){
                if (timer.milliseconds() >= 100){
                    tickPostion+=100;
                    timer.reset();
                }
            }else if(gamepad2.y){
                tickPostion = -2100;
            }else if(gamepad2.a){
                tickPostion = 0;
            }
            armHold(1,tickPostion);
            if (gamepad2.right_trigger > 0){
                robot.claw2.setPosition(1);
            }else if (gamepad2.left_trigger > 0){
                robot.claw2.setPosition(0);
            }
            if (gamepad2.x){
                robot.elbow.setPower(1);
            }else if (gamepad2.b){
                robot.elbow.setPower(-1);
            } else {
                robot.elbow.setPower(0);
            }
            // Output the safe vales to the motor drives.
            robot.leftFront.setPower(left);
            robot.rightFront.setPower(right);
            robot.leftBack.setPower(left);
            robot.rightBack.setPower(right);
            // Send telemetry mesasage to signify robot running,
        }
    }
    public void armHold(double speed, int tickTarget) {
        robot.leftArm.setTargetPositionTolerance(10);
        robot.rightArm.setTargetPositionTolerance(10);
        robot.leftArm.setTargetPosition(-tickTarget);
        robot.rightArm.setTargetPosition(-tickTarget);
        robot.leftArm.setPower(speed);
        robot.rightArm.setPower(speed);
        robot.leftArm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        robot.rightArm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }
}
//left in 1
//right in 0