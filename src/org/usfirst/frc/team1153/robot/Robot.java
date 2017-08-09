
package org.usfirst.frc.team1153.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.usfirst.frc.team1153.robot.commands.ExampleCommand;
import org.usfirst.frc.team1153.robot.commands.TurnWithVisionCommand;
import org.usfirst.frc.team1153.robot.subsystems.Drive;
import org.usfirst.frc.team1153.robot.subsystems.ExampleSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	public static final Drive drive = new Drive();
	
	public static int number_targets = 0;
	public static int target1_x = 0;
	public static int target1_y = 0;
	public static int target1_width = 0;
	public static int target1_height = 0;
	public static int target2_x = 0;
	public static int target2_y = 0;
	public static int target2_width = 0;
	public static int target2_height = 0;
	public static int target_center = 0;
	public static int center_x = 120;
	public static int error = 0;
	public static int loopCount = 0;
 

	public SerialPort arduinoSerial;
	public String outputString = new String("no target detected");
	public String arduinoString = new String("");
	public String packetPattern = "B,([0-9.,]*),[EN]$";
	public Pattern r;

	
	
	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = new OI();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		
		// create pixySerial object
		//arduinoSerial = new SerialPort(115200,Port.kUSB,8,Parity.kNone,StopBits.kOne);
		arduinoSerial = new SerialPort(9600,Port.kMXP,8,Parity.kNone,StopBits.kOne);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {

		r = Pattern.compile(packetPattern); 
		
		autonomousCommand = chooser.getSelected();
		autonomousCommand = new TurnWithVisionCommand();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		//if (loopCount % 7 == 0){
			if (arduinoSerial.getBytesReceived() > 0) {
				arduinoString += arduinoSerial.readString();
			if (!arduinoString.equalsIgnoreCase("")) {
				// process the output
				
				arduinoString = arduinoString.trim();
				SmartDashboard.putString("Arduino Output", arduinoString);
				
				// first split on return characters
				String[] outputLines = arduinoString.trim().split("\n");

				SmartDashboard.putNumber("Number of lines of arduino data", outputLines.length);
				
				String[] outputArray;
				int c = 0;
				boolean keepLooping = true;
				int number_of_targets_check = 0;
				// loop through all of the lines, look for BEGIN/END pairs to make sure we have a full packet of data
				while ((keepLooping) && (c < outputLines.length)) {
					Matcher m = r.matcher(outputLines[c]);
					if (m.find()) {
						outputArray = m.group(1).trim().split(",");
						SmartDashboard.putString("Regex matched", m.group(1));
						SmartDashboard.putNumber("Length Of Output Array", outputArray.length);
						
						// check first field
						if (outputArray.length > 1) {
							number_of_targets_check = Integer.parseInt(outputArray[0]);
							SmartDashboard.putNumber("Number of targets", number_targets);
							
							// only process if we got a packet and if the number of targets > 0
							if (number_of_targets_check > 0) {
								// stop checking for data
								keepLooping = false;
								// process the last line
								//String[] outputArray = m.group(1).trim().split(",");
							
								// clear out the arduinoString
								arduinoString="";
								
								//String[] outputArray = arduinoString.trim().split(",");
								//String targets = outputArray[0].trim().isEmpty() ? "0" : outputArray[0];
								//number_targets = Integer.parseInt(targets);
								
								if (outputArray.length >= 5) {
									String target1x = outputArray[1].trim().isEmpty() ? "0" : outputArray[1];
									String target1y = outputArray[2].trim().isEmpty() ? "0" : outputArray[2];
								
									number_targets = number_of_targets_check;
									target1_x = Integer.parseInt(target1x);
									target1_y = Integer.parseInt(target1y);
									//target1_width = Integer.parseInt(outputArray[3]);
									//target1_height = Integer.parseInt(outputArray[4]);
									target2_x = 0;
									target2_y = 0;
									target2_width = 0;
									target2_height = 0;
									target_center = 0;
								 
									 error = center_x - target1_x;
									
									outputString = "target1: " + String.valueOf(target1_x) + "," + String.valueOf(target1_y);
									
									if ((number_targets >= 2) && (outputArray.length == 9)) {
									
										
										/*
										  for (int c = 5; c < outputArray.length; c++) {
										 
											outputString += "[" + c + "]:" + outputArray[c];
										}
										*/
										String target2x = outputArray[5].trim().isEmpty() ? "0" : outputArray[5];
										String target2y = outputArray[6].trim().isEmpty() ? "0" : outputArray[6];
										target2_x = Integer.parseInt(target2x);
										target2_y = Integer.parseInt(target2y);
										 error = center_x - ((target1_x + target2_x)/2);
					//					target2_width = Integer.parseInt(outputArray[7]);
					//					target2_height = Integer.parseInt(outputArray[8]);
										
										//// estimate distance based on average height
										//int avg_height = (int) (target1_height + target2_height)/2;
										
										//int standard_z_distance = 34;
										//int min_avg_height = 25;
										//int height_at_std_z = 35;
										//int pixy_x_center = 160;
										//// at a distance of 34", 1 inch = 4 pixels
										//int std_y_pixels = 4;
										
										//// distance in inches
										//// height = 35 at a distance of 34"
										//int z_distance = 0;
										//// ratio of actual z_distance / 34"
										//double z_distance_ratio = 0.0;
										
										//// filter - potentially misleading data
										//// depending on tuning of pixycam, it may see the vision targets as multiple small 
										//// targets
										//if (avg_height > min_avg_height) {
										//	z_distance = (standard_z_distance * height_at_std_z)/avg_height;
										//	z_distance_ratio = (double)z_distance/standard_z_distance;
											
										//	target_center = (int) (target1_x + target2_x)/2;
											
										//	double distance_from_center = (pixy_x_center - target_center)/(std_y_pixels*z_distance_ratio);
											
										//	double angle = Math.toDegrees(Math.atan2(distance_from_center,z_distance));
											
										//	outputString += " target2: " + String.valueOf(target2_x) + "," + String.valueOf(target2_y) + " center: " 
										//			+ String.valueOf(target_center);
										//	outputString += " distance: " + z_distance;
										//	outputString += " angle: " + String.format("%.2f", angle);
										//}
									}
								}
							} else {
								outputString = "no target found";
							}
						}
					}
					c++;
					if ((keepLooping) && (c == outputLines.length)) {
						// clear the arduinoString	
						arduinoString = "";
						// only determine we have no targets if we have looped through all of the lines
						// and didn't find a target
						number_targets = 0;
						error = 0;
					}
				}
				SmartDashboard.putNumber("Error", error);	
				SmartDashboard.putString("Last Pixy Output", outputString); 
			}		
				//outputString = " : " + arduinoString;
			//}
				

		}
		
		loopCount++;
		}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
