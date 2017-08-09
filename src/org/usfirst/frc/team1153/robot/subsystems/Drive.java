package org.usfirst.frc.team1153.robot.subsystems;

import org.usfirst.frc.team1153.robot.RobotMap;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Drive extends Subsystem {
	private SpeedController leftBackMotor;
	private SpeedController leftFrontMotor;
	private SpeedController rightBackMotor;
	private SpeedController rightFrontMotor;
	
	private RobotDrive drive;
	
	
	public Drive() {
		leftBackMotor = new Victor(RobotMap.LEFT_BACK_MOTOR);
		leftFrontMotor = new Victor(RobotMap.LEFT_FRONT_MOTOR);
		rightBackMotor = new Victor(RobotMap.RIGHT_BACK_MOTOR);
		rightFrontMotor = new Victor(RobotMap.RIGHT_FRONT_MOTOR);

		drive = new RobotDrive(leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor);
	}
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}
	
	public void turnWithVision(double turnValue) {
		drive.arcadeDrive(0,turnValue,true);
		
	}
}
