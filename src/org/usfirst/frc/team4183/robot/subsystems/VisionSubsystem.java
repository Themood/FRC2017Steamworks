package org.usfirst.frc.team4183.robot.subsystems;

import org.usfirst.frc.team4183.robot.commands.VisionSubsystem.Idle;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionSubsystem extends Subsystem
{
	// Strings to place into BucketVision NetworkTable
	public static final String GEAR_LIFT_MODE = "gearLift";
	public static final String BOILER_MODE = "Boiler";	
	public static final String FRONT_CAM = "frontCam";
	public static final String REAR_CAM = "rearCam";
	
	public static final String FRONT_CAM_MODE = "FrontCamMode";
	public static final String CURRENT_CAM = "CurrentCam";
	
	public static final String GEAR_LIFT_DATA = "GearLiftData";
	public static final String BOILER_DATA = "BoilerData";
	
	public static final String RED_ALLIANCE = "red";
	public static final String BLUE_ALLIANCE = "blue";
	
	private static final String ALLIANCE_COLOR_KEY = "allianceColor";
	private static final String ALLIANCE_LOCATION_KEY = "allianceLocation";
	
	private static String currentCam = FRONT_CAM;
	private static String currentFrontCamMode = GEAR_LIFT_MODE;
	
	public static String currentAllianceColor = "red";
	public static int currentAllianceLocation = 1;

	private static NetworkTable bvtable;
	
	private static DriverStation driverStation;
	
	class TargetData
	{
		public double confidenceFactor;
		public double distance_m;
		public double angleToCenter_deg;	// Angle to center target in FOV
		public double angleToTarget_deg;	// Angle of camera relative target center line
		
		TargetData()
		{
			confidenceFactor = 0.0;
			distance_m = 0.0;
			angleToCenter_deg = 0.0;
			angleToTarget_deg = 0.0;
		}
	}
	
	private static TargetData gearLiftData;
	private static TargetData boilerData;


	public VisionSubsystem()
	{
		// Set up defaults 
		bvtable = NetworkTable.getTable("BucketVision");
		bvtable.putString(FRONT_CAM_MODE, currentFrontCamMode);
		bvtable.putString(CURRENT_CAM, currentCam);
		
		gearLiftData = new TargetData();
		//bvtable.putValue(GEAR_LIFT_DATA, gearLiftData);
		
		boilerData = new TargetData();
		//bvtable.putValue(BOILER_DATA, boilerData);
		
		driverStation = DriverStation.getInstance();
		
	}
	
	public void setFrontCam()
	{
		currentCam = FRONT_CAM;
		bvtable.putString(CURRENT_CAM, currentCam);
	}

	public void setRearCam()
	{
		currentCam = REAR_CAM;
		bvtable.putString(CURRENT_CAM, currentCam);
	}
	
	public void setGearMode()
	{
		currentFrontCamMode = GEAR_LIFT_MODE;
		bvtable.putString(FRONT_CAM_MODE, currentFrontCamMode);
	}
	
	public boolean isGearMode()
	{
		return (currentFrontCamMode.equals(GEAR_LIFT_MODE));
	}
	
	public void setBoilerMode()
	{
		currentFrontCamMode = BOILER_MODE;
		bvtable.putString(FRONT_CAM_MODE, currentFrontCamMode);
	}

	public boolean isBoilerMode()
	{
		return (currentFrontCamMode.equals(BOILER_MODE));
	}
	
	public boolean isGearLiftPresent()
	{
		bvtable.getValue(GEAR_LIFT_DATA, gearLiftData);
		
		return (gearLiftData.confidenceFactor >= 0.5);
	}
	
	public boolean isBoilerPresent()
	{
		bvtable.getValue(BOILER_DATA, boilerData);
		
		return (boilerData.confidenceFactor >= 0.5);
	}
	
	public boolean isRedAlliance() 
	{
		return driverStation.getAlliance().equals(DriverStation.Alliance.Red);
	}
	
	public void setRedAlliance() 
	{
		if (currentAllianceColor.equals(BLUE_ALLIANCE))
		{
			currentAllianceColor = RED_ALLIANCE;
			bvtable.putString(ALLIANCE_COLOR_KEY, currentAllianceColor);
		}
	}
	
	public void setBlueAlliance() 
	{
		// Only change it if we think the color is wrong
		if (currentAllianceColor.equals(RED_ALLIANCE))
		{
			currentAllianceColor = BLUE_ALLIANCE;
			bvtable.putString(ALLIANCE_COLOR_KEY, currentAllianceColor);
		}
	}
	
	public boolean isBlueAlliance() 
	{
		return driverStation.getAlliance().equals(DriverStation.Alliance.Blue);
	}
	
	public void setAllianceNumber() 
	{
		if (currentAllianceLocation != driverStation.getLocation())
		{
			currentAllianceLocation = driverStation.getLocation();
			bvtable.putNumber(ALLIANCE_LOCATION_KEY, currentAllianceLocation);
		}
	}
	
	public int getAllianceNumber() 
	{
		return driverStation.getLocation();
	}
	
	@Override
	protected void initDefaultCommand() 
	{
		setDefaultCommand( new Idle());
	}

}