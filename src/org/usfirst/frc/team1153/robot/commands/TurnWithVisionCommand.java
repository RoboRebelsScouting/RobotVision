package org.usfirst.frc.team1153.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team1153.robot.Robot;

/**
 *
 */
public class TurnWithVisionCommand extends Command {
	private int loopCount = 0;
	double turnSpeed = 0.6;
	double rightkF = 0.57;
	double leftkF = -0.57;
	
	// kP must be negative so we turn in the correct direction
	double kP = -(0.11/160);
	
			
	public TurnWithVisionCommand() {
		
		// Use requires() here to declare subsystem dependencies
		requires(Robot.drive);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		Robot.drive.turnWithVision(0);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		
	if (loopCount % 1 == 0){
		if(Robot.number_targets == 0) {
			turnSpeed = 0;
			//Robot.drive.turnWithVision(0);
		} else if (Robot.error > 5) {
			turnSpeed = (Robot.error *kP) + leftkF;
			//Robot.drive.turnWithVision(-0.6);
		} else if (Robot.error < -5) {
			turnSpeed = (Robot.error *kP) + rightkF;
			//Robot.drive.turnWithVision(0.6);
		}	else {
			turnSpeed = 0;
		}
	}
		
		Robot.drive.turnWithVision(turnSpeed);
		loopCount++;
		SmartDashboard.putNumber("Turn Speed", turnSpeed);
		SmartDashboard.putNumber("Loop Count", loopCount);
			
			
		}
	

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
	}
}
