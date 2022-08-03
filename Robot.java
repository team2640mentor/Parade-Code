/*
  2022 everybot code
  written by carson graf 
  don't email me, @ me on discord
*/

/*
  This is catastrophically poorly written code for the sake of being easy to follow
  If you know what the word "refactor" means, you should refactor this code
*/

package frc.robot;

import com.revrobotics.CANSparkMax;
//import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class Robot extends TimedRobot {
  
  //Definitions for the hardware. Change this if you change what stuff you have plugged in
  CANSparkMax driveLeftA = new CANSparkMax(1, MotorType.kBrushed);
  CANSparkMax driveLeftB = new CANSparkMax(2, MotorType.kBrushed);
  CANSparkMax driveRightA = new CANSparkMax(3, MotorType.kBrushed);
  CANSparkMax driveRightB = new CANSparkMax(4, MotorType.kBrushed);
  private final DoubleSolenoid solenoid1 = new DoubleSolenoid(PneumaticsModuleType.CTREPCM,0,1);  //REVPH OR CTREPCM FIND OUT LATER
  private final DoubleSolenoid solenoid2 = new DoubleSolenoid(PneumaticsModuleType.CTREPCM,2,3);
  private final DoubleSolenoid solenoid3 = new DoubleSolenoid(PneumaticsModuleType.CTREPCM,4,5);
  Joystick driverController = new Joystick(0);
  //private boolean extended; 

  //pnuematics
  /*public void toggle() {
    if (extended) {
      solenoid1.set(Value.kReverse);
      solenoid2.set(Value.kReverse);
      solenoid3.set(Value.kReverse);
      setSpeed(0);
    } else {
      solenoid1.set(Value.kReverse);
      solenoid2.set(Value.kReverse);
      solenoid3.set(Value.kForward);
    }
    extended = !extended;
  }

*/
  /*private void setSpeed(int i) {
  }

  public void deploy() {
    solenoid1.set(Value.kForward);
    solenoid2.set(Value.kForward);
    solenoid3.set(Value.kForward);
    extended = true;
  }

  public void retract() {
    solenoid1.set(Value.kReverse);
    solenoid2.set(Value.kReverse);
    solenoid3.set(Value.kReverse);
    extended = false;
  }

  public boolean isExtended() {
    return extended;
  }
*/
  /*Varibles needed for the code*/
  boolean burstMode = false; 
  double lastBurstTime = 0;

  double autoStart = 0;
  boolean goForAuto = false;

  /*
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    //Configure motors to turn correct direction. You may have to invert some of your motors, orginally Driveleftb(true),Driverightb(false)
    //added intakeB.follow(intakeA); to synchronize intake motors
    driveLeftA.setInverted(true);   
    driveLeftA.burnFlash();
    driveLeftB.setInverted(true);
    driveLeftB.burnFlash();
    driveRightA.setInverted(false);
    driveRightA.burnFlash();
    driveRightB.setInverted(false);
    driveRightB.burnFlash();
    solenoid1.set(Value.kReverse);
    solenoid2.set(Value.kReverse);
    solenoid3.set(Value.kReverse);

    //add a thing on the dashboard to turn off auto if needed
    SmartDashboard.putBoolean("Go For Auto", false);
    goForAuto = SmartDashboard.getBoolean("Go For Auto", false);
  }

  @Override
  public void autonomousInit() {
    //get a time for auton start to do events based on time later
    autoStart = Timer.getFPGATimestamp();
    //check dashboard icon to ensure good to do auto
    goForAuto = true;//SmartDashboard.getBoolean("Go For Auto", false);
  }

  //This function is called periodically during autonomous. 
  @Override
  public void autonomousPeriodic() {
  //get time since start of auto
    double autoTimeElapsed = Timer.getFPGATimestamp() - autoStart;
    if(goForAuto){
      if(autoTimeElapsed < 1.25){
        //go forward
        driveLeftA.set(0.2);
        driveLeftB.set(0.2);
        driveRightA.set(0.2);
        driveRightB.set(0.2);
      //series of timed events making up the flow of auto
      }else if(autoTimeElapsed < 8){
        // drive backwards *slowly* 
        driveLeftA.set(-0.2);
        driveLeftB.set(-0.2);
        driveRightA.set(-0.2);
        driveRightB.set(-0.2);
      }else{
        //do nothing for the rest of auto
        driveLeftA.set(0);
        driveLeftB.set(0);
        driveRightA.set(0);
        driveRightB.set(0);
      }}
    }
  

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //Set up arcade steer
    if (driverController.getRawButton(1)) {
    new ParallelCommandGroup(
     new InstantCommand(solenoid1.toggle()),
     new InstantCommand(solenoid2.toggle()),
     new InstantCommand(solenoid3.toggle())
       ;) }
    double forward= -driverController.getRawAxis(1);
    double turn = -driverController.getRawAxis(4);
    double driveLeftPower = forward - turn;
    double driveRightPower = forward + turn;

    driveLeftA.set(driveLeftPower);
    driveLeftB.set(driveLeftPower);
    driveRightA.set(driveRightPower);
    driveRightB.set(driveRightPower);
  }

  @Override
  public void disabledInit() {
    //On disable turn off everything
    //done to solve issue with motors "remembering" previous setpoints after reenable
    driveLeftA.set(0);
    driveLeftB.set(0);
    driveRightA.set(0);
    driveRightB.set(0);
  }
    
}