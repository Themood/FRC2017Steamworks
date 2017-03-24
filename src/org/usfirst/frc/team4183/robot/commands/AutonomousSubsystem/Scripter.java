package org.usfirst.frc.team4183.robot.commands.AutonomousSubsystem;

import org.usfirst.frc.team4183.robot.OI;
import org.usfirst.frc.team4183.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Scripter extends Command {
			
	// These values written by "MeasureGear"
	static double measuredDistance_inch;    // inch
	static double measuredYaw_deg;          // gives Robot pose in target C.S.; +val means Robot sitting CCW (viewed from top)
	
	private int pc = 0;
	private final boolean debug = false;
	private final int position;
	
	// To see the Scripter instruction set documentation, 
	// scroll down to the switch() in executeNextInstruction()
	
	// Dead reckoning numbers are assuming: 
	// positions 1 & 3 start points are 7' left & right of center line respectively,
	// position 2 start point is on center line (directly facing gear peg)
	
	private String[][] theRealScript = {
			{"", 		"BranchOnPosition Left Center Right" },  // Goto label 1,2,3 based on operator position
			{"Left", 	"DriveStraight 82.2 26" },  // Inch
			{"", 		"TurnBy -60.0" },        // Degrees, + is CCW from top (RHR Z-axis up)
			{"",		"Goto Vis" },
			{"Center",	"DriveStraight 26.0 12" },
			{"",		"Goto Vis" },
			{"Right",	"DriveStraight 82.2 26" },
			{"",		"TurnBy 60.0" },
			{"Vis", 	"EnableVisionGear" },   // S.B. ~4' from airship wall, looking straight at it
			{"", 		"MeasureGear" },		// Collect distance & yaw measures, put estimates into measuredDistance, measuredYaw
			{"", 		"YawCorrect" },     		// TurnBy -measuredYaw
			{"", 		"DistanceCorrect 21.0 12" },	// Stop short by this much
			{"", 		"MeasureGear" },
			{"", 		"YawCorrect" },
			{"", 		"DistanceCorrect 15.0 0" },	
			{"", 		"DeliverGear" },			// Spit the gear
			{"",        "BranchOnColorAndPosition BlueBoiler NoBoiler NoBoiler NoBoiler NoBoiler RedBoiler"},
			{"NoBoiler",    "DriveStraight -12.0 0"},
			{"",        "Goto End"},
			{"BlueBoiler",   "StartShooter"},
			{"",   		"DriveStraight -70.2 24"},
			{"",        "TurnBy -149.3"},
			{"",        "Goto Shoot"},
			{"RedBoiler",    "StartShooter"},
			{"",    	"DriveStraight -70.2 24"},
			{"",        "TurnBy 149.3"},
			{"",        "Goto Shoot"},
			{"Shoot",   "Shoot"},
			{"",		"Delay 4000"},  // Have to Delay to allow shoot to happen!!
			{"End", 	"End" }			// MUST finish in End state
	};
	
	
	// Test small moves to make sure MIN_DRIVEs big enough.
	// e.g. TurnBy 5, DriveStraight 3.
	// Test big moves to make sure it behaves & settles.
	// e.g. TurnBy 60, DriveStraight 48.
	// Uncomment this test script to get those moves selected by Team/Position
	private String[][] tuneScript = {
		{"",        "BranchOnColorAndPosition BlueLeft BlueCntr BlueRight RedLeft RedCntr Noop"},
		{"BlueRight",	"DriveStraight 82.2 26" },
		{"",			"TurnBy 60.0" },
		{"",			"Goto Vis"},
		{"BlueLeft",	"DriveStraight 82.2 26"},
		{"",			"TurnBy -60.0"},
		{"", 			"GotoVis"},
		{"BlueCntr", 	"DriveStraight 52 20"},
		{"", 			"Goto Vis"},
		{"RedLeft",		"TurnBy 5"},
		{"",			"TurnBy -5"},
		{"",			"End"},
		{"RedCntr",		"TurnBy 60"},
		{"",			"TurnBy -60"},
		{"",			"End"},
		{"Vis", 		"EnableVisionGear" },   // S.B. ~4' from airship wall, looking straight at it
		{"", 			"MeasureGear" },		// Collect distance & yaw measures, put estimates into measuredDistance, measuredYaw
		{"", 			"YawCorrect" },     		// TurnBy -measuredYaw
		{"", 			"DistanceCorrect 15.0 0" },	// Stop short by this much	
		{"", 			"DeliverGear" },// Spit the gear
		{"", 			"End"},
		{"Noop", 		"End" }    // MUST finish with End!
	};
	
	private String[][] tuneScriptTest = {
			{"", 			"BranchOnPosition Left Center Right" },  // Goto label 1,2,3 based on operator position
			{"Left", 		"DriveStraight 82.2 26" },  // Inch
			{"", 			"TurnBy -60.0" },        // Degrees, + is CCW from top (RHR Z-axis up)
			{"",			"Goto Vis" },
			{"Center",		"DriveStraight 52.0 20" },
			{"",			"Goto VisCenter" },
			{"Right",		"DriveStraight 82.2 26" },
			{"",			"TurnBy 60.0" },
			{"Vis", 		"EnableVisionGear" },   // S.B. ~4' from airship wall, looking straight at it
			{"", 			"MeasureGear" },		// Collect distance & yaw measures, put estimates into measuredDistance, measuredYaw
			{"", 			"YawCorrect" },     		// TurnBy -measuredYaw
			{"", 			"DistanceCorrect 15.0 0" },	
			{"", 			"DeliverGear" },			// Spit the gear
			{"",        	"BranchOnColorAndPosition BlueBoiler NoBoiler BackUpBlue BackUpRed NoBoiler RedBoiler"},
			{"NoBoiler",    "DriveStraight -12.0 0"},
			{"",        	"Goto End"},
			{"BackUpBlue",  "DriveStraight -24.0 0"},
			{"", 			"TurnBy -60.0"},
			{"",       		"DriveStraight 214.0 0"},
			{"", 			"Goto End"},
			{"BackUpRed",   "DriveStraight -24.0 0"},
			{"", 			"TurnBy 60.0"},
			{"",            "DriveStraight 214.0 0"},
			{"",			"Goto End"},
			{"BlueBoiler",  "StartShooter"},
			{"",   			"DriveStraight -70.2 24"},
			{"",        	"TurnBy -149.3"},
			{"",        	"Goto Shoot"},
			{"RedBoiler",   "StartShooter"},
			{"",    		"DriveStraight -70.2 24"},
			{"",        	"TurnBy 149.3"},
			{"",        	"Goto Shoot"},
			{"Shoot",   	"Shoot"},
			{"",			"Delay 4000"},  // Have to Delay to allow shoot to happen!!
			{"End", 		"End" }			// MUST finish in End state
	};
	
	
	/*****************************************************************
	 * 
	 * Set this variable to the script you actually want to execute!!!
	 * 
	 *****************************************************************/
	private String[][] script = tuneScriptTest;
	
	
	// position 1,2,3 are Left, Center, Right respectively
    public Scripter( int position) {
    	// No "requires" - this one stands apart - it's a Meta-State.
    	// This is start()-ed from Robot.autonomousInit().
    	this.position = position;
    }

    protected void initialize() {
    	pc = 0;
    }

    protected void execute() {
    	
    	// When Auto subsystem is Idle, we give it something to do!
    	if( "Idle".equals(Robot.autonomousSubsystem.getCurrentCommand().getName()))
    	    executeNextInstruction();   	
    }

    protected boolean isFinished() {
        return false;
    }
 
    
    private void executeNextInstruction() {
    	
    	if( pc >= script.length) {
    		System.err.println( "Scripter.execute: pc is out of bounds (did you forget End in script?)");
    		return;
    	}
    	
      	String instruction = script[pc++][1];
      	
      	if(debug)
      		System.out.format( "Scripter.execute %s\n", instruction);
      		
    	String[] tokens = instruction.split(" +");
    	switch( tokens[0]) {
    	
    	// These are the legal Instruction Opcodes
    	// For each case in switch, a following comment documents Opcode's parameters if any
    	
    	case "Goto":  // label
    		doGoto(tokens[1]);
    		break;

    	case "Delay":  // msecs
    		delay( Long.parseLong(tokens[1]));
    		break;
    		
    	case "BranchOnPosition":  // Left Center Right (refers to robot position relative to airship centerline)
    		branchOnPosition( tokens[1], tokens[2], tokens[3]);
    		break;
    		
    	case "BranchOnColorAndPosition": // blueLeft, blueCntr, blueRight, redleft, ...
    		branchOnColorAndPosition(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]);
    		break;
    		
    	case "TurnBy": // yaw (degrees, + is CCW from top)
    		turnBy( Double.parseDouble(tokens[1]));
    		break;
    		
    	case "DriveStraight":  // distance (inches)
    		driveStraight( Double.parseDouble(tokens[1]), Double.parseDouble((tokens[2])));
    		break;
    	
    	case "EnableVisionGear":
    		enableVisionGear();
    		break;
    		
    	case "MeasureGear":  // (Sets measuredYaw and measuredDistance from Vision samples)
    		measureGear();
    		break;

    	case "YawCorrect":  // (Turns by -measuredYaw)
    		yawCorrect();
    		break;
    		
    	case "DistanceCorrect":  // drives forward measuredDistance - param)
    		distanceCorrect( Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
    		break;
    			
    	case "DeliverGear":  // (Spits the gear)
    		deliverGear();
    		break;
    		
    	case "StartShooter":
    		startShooter();
    		break;
    		
    	case "Shoot":
    		shoot();
    		break;
    	
    	case "End":  // (Stops all, does not exit - must be last instruction)
    		endState();
    		break;
    		
    	default:
    		throw new IllegalArgumentException( 
    			String.format("Scripter: unknown instruction: %s\n", instruction));
    	}    	
    }
    
    private void doGoto( String label) {
    	if(debug)
    		System.out.format( "Scripter.doGoto %s\n", label);
    	
    	for( int i = 0 ; i < script.length ; i++)
    		if( script[i][0].equals(label)) {
    			pc = i;
    			return;
    		}
    	
    	throw new IllegalArgumentException(
    		String.format("Scripter.doGoto: Label %s not found\n", label));
    }

    private void delay( long msecs) {
    	if(debug)
    		System.out.format("Scripter.delay %d\n", msecs);
    	(new Delay( msecs)).start();
    }
 
    private void branchOnPosition( String lbl1, String lbl2, String lbl3) {  	
    	if(debug)
    		System.out.format( "Scripter.branchOnLocation %s %s %s\n", lbl1, lbl2, lbl3);
    	
    	switch( position) {
    	case 1:
    		doGoto( lbl1);
    		break;
    	case 2:
    		doGoto( lbl2);
    		break;
    	case 3:
    		doGoto( lbl3);
    		break;
    	default:
    		throw new IllegalArgumentException(
    			String.format( "Scripter.branchOnLocation: unknown location %d\n", position));
    	}
    }
    
    private void branchOnColorAndPosition(String lblB1, String lblB2, String lblB3, 
    		String lblR1, String lblR2, String lblR3) {
    	if(debug)
    		System.out.format("Scripter.branchOnColorAndPosition %s %s %s %s %s %s\n", 
    				lblB1, lblB2, lblB3, lblR1, lblR2, lblR3);
    	
    	if(Robot.visionSubsystem.isBlueAlliance() && position == 1) {
    		doGoto(lblB1);
    	}
    	else if(Robot.visionSubsystem.isBlueAlliance() && position == 2){
    		doGoto(lblB2);
    	}
    	else if(Robot.visionSubsystem.isBlueAlliance() && position == 3){
    		doGoto(lblB3);
    	}
    	else if(Robot.visionSubsystem.isRedAlliance() && position == 1){
    		doGoto(lblR1);
    	}
    	else if(Robot.visionSubsystem.isRedAlliance() && position == 2){
    		doGoto(lblR2);
    	}
    	else if(Robot.visionSubsystem.isRedAlliance() && position == 3){
    		doGoto(lblR3);
    	}
    	else throw new IllegalArgumentException("Scripter.branchOnColorAndPosition");
    }
     
    private void turnBy( double yaw) {
    	if(debug)
    		System.out.format("Scripter.turnBy %f\n", yaw);
    	(new TurnBy( yaw)).start();
    }
    
    private void driveStraight( double dist, double hardstop) {
    	if(debug)
    		System.out.format( "Scripter.driveStraight %f\n", dist);
    	(new DriveStraight( dist, hardstop)).start();
    }

    private void enableVisionGear() {
    	if(debug)
    		System.out.println("Scripter.enableVisionGear");
    	Robot.visionSubsystem.setGearMode();
    }
    
    private void measureGear() {
    	if(debug)
    		System.out.println("Scripter.measureGear");
    	(new MeasureGear()).start();
    }
   
    private void yawCorrect() {
    	if(debug)
    		System.out.format("Scripter.yawCorrect %f\n", measuredYaw_deg);
    	(new TurnBy( -measuredYaw_deg)).start();
    }
    
    private void distanceCorrect( double dRemain,double hardstop) {
    	if(debug)
    		System.out.format( "Scripter.distanceCorrect %f\n", measuredDistance_inch - dRemain);
    	(new DriveStraight( measuredDistance_inch - dRemain,hardstop)).start();
    }
    
    private void deliverGear() {
    	if(debug)
    		System.out.println("Scripter.deliverGear");
    	OI.btnSpitGearA.hit();
    	OI.btnSpitGearB.hit();
    }
    
    private void endState() {
    	if(debug)
    		System.out.println("Scripter.endState");
    	(new End()).start();
    }  
    
    private void startShooter() {
    	OI.btnShooterStart.hit();
    }
    
    private void shoot() {
    	OI.btnShoot.hit(3000);
    }
}
