package org.usfirst.frc.team4183.robot.commands.BallManipSubsystem;

import org.usfirst.frc.team4183.robot.LightingControl;
import org.usfirst.frc.team4183.robot.OI;
import org.usfirst.frc.team4183.robot.Robot;
import org.usfirst.frc.team4183.robot.LightingControl.LightingObjects;
import org.usfirst.frc.team4183.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class WaitingForTrigger extends Command {
	
	OI.ButtonEvent btnShooting;

    public WaitingForTrigger() {
        requires(Robot.ballManipSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
       	Robot.lightingControl.set(LightingObjects.BALL_SUBSYSTEM,
                LightingControl.FUNCTION_ON,
                LightingControl.COLOR_ORANGE,
                0,		// Don't care
                0);		// Don't care 
       	
    	btnShooting = OI.getBtnEvt(OI.btnShoot);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.ballManipSubsystem.setTopRollerToShootingSpeed();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if(OI.btnIdle.get()) {
    		return CommandUtils.stateChange(this, new Idle());
    	}
    	if(btnShooting.onPressed()) {
    		return CommandUtils.stateChange(this, new Shooting());
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