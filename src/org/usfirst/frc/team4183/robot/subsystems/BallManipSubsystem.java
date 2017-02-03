package org.usfirst.frc.team4183.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.CANTalon;
import org.usfirst.frc.team4183.robot.RobotMap;
import org.usfirst.frc.team4183.robot.commands.BallManipSubsystem.Idle;


public class BallManipSubsystem extends Subsystem {
	
	
	
	private CANTalon topRollerMotor;		// Used for shooting AND intake (i.e., multi-speed)
	private CANTalon conveyerMotor;
	private CANTalon sweeperMotor;
	
	private final double SHOOTER_SPEED_RPM = 4200.0;	//speed of top roller when shooting
	private final double INTAKE_SPEED_RPM = 500.0;		//speed of top roller when intake
	private final double CONVEYER_SPEED_PMAX = 0.8;		//open loop control of conveyer in fraction vbus
	private final double SWEEPER_SPEED_PMAX = 0.1;		//open loop control of sweeper in fraction vbus
	
	private final double P_VALUE = 0.6;
	private final double I_VALUE = 1.2*0.001;
	private final double D_VALUE = .02*1000.0;
	private final double F_VALUE = 0.13;
	
	
	public BallManipSubsystem(){
		topRollerMotor = new CANTalon(RobotMap.BALL_SUBSYSTEM_TOP_ROLLER_MOTOR_ID);
		conveyerMotor = new CANTalon(RobotMap.BALL_SUBSYSTEM_CONVEYER_MOTOR_ID);
		sweeperMotor = new CANTalon(RobotMap.BALL_SUBSYSTEM_SWEEPER_MOTOR_ID);
		
		initializeMotorModes();
	}
	
    private void initializeMotorModes(){
    	topRollerMotor.changeControlMode(CANTalon.TalonControlMode.Speed);
    	topRollerMotor.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
    	topRollerMotor.configEncoderCodesPerRev(RobotMap.SHOOTER_ROLLER_PULSES_PER_REV);
    	
    	topRollerMotor.reverseOutput(false);
    	topRollerMotor.reverseSensor(false);
    	
    	topRollerMotor.setPID(P_VALUE, I_VALUE, D_VALUE);
    	topRollerMotor.setF(F_VALUE);
    	
    	topRollerMotor.setIZone(0);
    	topRollerMotor.setCloseLoopRampRate(0.0);
    	topRollerMotor.setAllowableClosedLoopErr(0);
    	topRollerMotor.configNominalOutputVoltage(0.0, 0.0);
    	topRollerMotor.configPeakOutputVoltage(+12.0, -12.0);
    }
	
	public void enable() {
		// Enable closed-loop motor;
		// motor won't actually turn on until set() is done
		topRollerMotor.enableControl();
	}
	
	public void disable() {
		setTopRollerOff();
		setConveyerOff();
		setSweeperOff();	
	}
		
    public void initDefaultCommand() {
        setDefaultCommand(new Idle());
    }
    
    public void setTopRollerToIntakeSpeed(){
    	topRollerMotor.set(INTAKE_SPEED_RPM);
    }
    
    public void setTopRollerToShootingSpeed(){
    	topRollerMotor.set(SHOOTER_SPEED_RPM);
    }
    
	public void setTopRollerOff(){
		// Stop closed-loop motor (immediate)
		// NOTE: Motor is disabled rather than setting speed to 0
		// to avoid using power to maintain zero speed
		topRollerMotor.disableControl();
	}
	
    public void setConveyerOn(){
    	conveyerMotor.set(CONVEYER_SPEED_PMAX);
    }
    
    public void setConveyerOff(){
    	conveyerMotor.set(0);
    }
    
    public void setSweeperOn(){
    	sweeperMotor.set(SWEEPER_SPEED_PMAX);
    }
    
    public void setSweeperOff(){
    	sweeperMotor.set(0);
    }
    
}

