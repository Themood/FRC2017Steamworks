package org.usfirst.frc.team4183.robot.commands.GearHandlerSubsystem;

import org.usfirst.frc.team4183.robot.LightingControl;
import org.usfirst.frc.team4183.robot.OI;
import org.usfirst.frc.team4183.robot.Robot;
import org.usfirst.frc.team4183.robot.LightingControl.LightingObjects;
import org.usfirst.frc.team4183.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class WaitingForBalls extends Command {

    public WaitingForBalls() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.gearHandlerSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
       	Robot.lightingControl.set(LightingObjects.GEAR_SUBSYSTEM,
                LightingControl.FUNCTION_BLINK,
                LightingControl.COLOR_GREEN,
                0,			// nspace - don't care
                300);		// period_msec 
       	
    	Robot.gearHandlerSubsystem.closeGate();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {

    	Robot.gearHandlerSubsystem.spinRollerBalls();   		
   }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if(Robot.oi.btnIdle.get()) {
    		return CommandUtils.stateChange(this, new Idle());
    	}
    	if( Robot.oi.btnWaitForGear.get()) {
    		return CommandUtils.stateChange(this, new WaitingForGear());
    	}
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
