package psolab;
import java.util.Random;
import java.lang.Math;
        
public class Particle {
    
    /*
     * VARIABLES
     */
    
    //arrays for particle coordinates and velocities
    public double [] currentCoords;
    public double [] pBestCoords;
    public double [] neighborhoodBestCoords;
    public static double[] gBestCoords;
    public double [] velocities;
    
    //arrays for particle values
    public double currentValue;
    public double nBest;
    public double pBest;
    public static double gBest;

    //array for particle neighborhood
    public Particle [] neighborhood;
    
    //test function number
    private int fcnNum;
    
    //constants for initial particle position and velocity ranges
    private static final double ROSEN_POSITION_MIN = 15.0;
    private static final double ROSEN_POSITION_MAX = 30.0;
    private static final double ACK_POSITION_MIN = 16.0;
    private static final double ACK_POSITION_MAX = 32.0;
    private static final double RAST_POSITION_MIN = 2.56;
    private static final double RAST_POSITION_MAX = 5.12;
    private static final double ROSEN_VELOCITY_MIN = -2.0;
    private static final double ROSEN_VELOCITY_MAX = 2.0;
    private static final double ACK_VELOCITY_MIN = -2.0;
    private static final double ACK_VELOCITY_MAX = 4.0;
    private static final double RAST_VELOCITY_MIN = -2.0;
    private static final double RAST_VELOCITY_MAX = 4.0;
    
    //constants for updating particle
    private static final double PHI_ONE=2.05;
    private static final double PHI_TWO=2.05;
    private static final double CONSTRICTION_FACTOR=0.7298;
    
    /*
     * CONSTRUCTOR
     */
    public Particle(int numDimensions, int fcnArg){
        
        //initializes arrays
        currentCoords = new double [numDimensions];
        velocities = new double [numDimensions];
        pBestCoords = new double [numDimensions];
        
        //assigns the test function number to the class variable 
        fcnNum = fcnArg;
        
        //creates random object
        Random rand = new Random();
        
        //if Rosenbrock, initialize particle coords and velocity
        if(fcnNum==0){
            for(int i=0;i<numDimensions;i++){
                currentCoords[i]= ROSEN_POSITION_MIN + rand.nextDouble() * 
                                    (ROSEN_POSITION_MAX - ROSEN_POSITION_MIN);
                velocities[i]=  ROSEN_VELOCITY_MIN + rand.nextDouble() * 
                                (ROSEN_VELOCITY_MAX - ROSEN_VELOCITY_MIN); 
            }
        }
        //if Ackley, initialize particle coords and velocity
        if(fcnNum == 1){
            for(int i=0;i<numDimensions;i++){
                currentCoords[i]= ACK_POSITION_MIN + rand.nextDouble() * 
                                    (ACK_POSITION_MAX - ACK_POSITION_MIN);
                velocities[i]=  ACK_VELOCITY_MIN + rand.nextDouble() * 
                                (ACK_VELOCITY_MAX - ACK_VELOCITY_MIN); 
            }
        }
        //if Rastrigin, initialize particle coords and velocity
        if(fcnNum == 2){
            for(int i=0;i<numDimensions;i++){
                currentCoords[i]= RAST_POSITION_MIN + rand.nextDouble() * 
                                    (RAST_POSITION_MAX - RAST_POSITION_MIN);
                velocities[i]=  RAST_VELOCITY_MIN + rand.nextDouble() * 
                                (RAST_VELOCITY_MAX - RAST_VELOCITY_MIN); 
            }
        }          
        //set personal and neighborhood best coords
        pBestCoords = currentCoords.clone();
        neighborhoodBestCoords = currentCoords.clone(); 
        //find personal best and current values
        pBest = eval();
        currentValue=eval();   
        //update the global best
        if(pBest < gBest){
            gBest = pBest;
            gBestCoords = pBestCoords.clone();
        }       
    }
    
    /*
     * EVAL -- calls the correct method to find the value of a particle at a 
     *         point in the solution space
     */
    public double eval() {

	double retValue = 0.0;
    
        if (fcnNum==0) {
	    retValue = evalRosenbrock();
        }  
	else if (fcnNum==2) {
	    retValue = evalRastrigin();
	}   
        else if (fcnNum==1) {
	    retValue = evalAckley();
	}  
	return retValue;
    }
	
    /*
     * EVAL ROSENBROCK -- Finds the value of a particle for the Rosenbrock function
     *                    Returns a double that is the value
     */
    private double evalRosenbrock () {
        double total = 0;
        for (int i=0; i<currentCoords.length-1; i++){
            total += 100.0 * Math.pow(currentCoords[i+1] - currentCoords[i]*
                   currentCoords[i], 2.0) + Math.pow(currentCoords[i]-1.0, 2.0);
	}
	return total;
    }
	
    /*
     * EVAL RASTRIGIN -- Finds the value of a particle for the Rastrigin function
     *                   Returns a double that is the value
     */
    private double evalRastrigin () {
	double total = 0;
	for (int i=0; i<currentCoords.length; i++){
            total += currentCoords[i]*currentCoords[i] - 10.0*Math.cos(2.0*Math.PI*
                     currentCoords[i]) + 10.0;
	}
	return total;
    }
	
    /*
     * EVAL ACKLEY -- Finds the value of a particle for the Ackley function
     *                Returns a double that is the value
     */
    private double evalAckley () {
	double firstSumTotal = 0;
	double secondSumTotal = 0;
	for (int i=0; i<currentCoords.length; i++){
            firstSumTotal += (currentCoords[i]*currentCoords[i]);
            secondSumTotal += Math.cos(2.0*Math.PI*currentCoords[i]);
	}	
	return -20.0 * Math.exp(-0.2 * Math.sqrt(firstSumTotal/
                currentCoords.length)) - Math.exp(secondSumTotal/
                currentCoords.length) + 20.0 + Math.E;
    } 
        
    /*
     * UPDATE -- changes particles position and velocity, updates personal
     *           and global bests
     */
    public void update(){
        
        //arrays for particle acceleration
        double [] pBestAccel = new double [currentCoords.length];
        double [] neighborhoodBestAccel = new double [currentCoords.length];
        double [] globalBestAccel = new double [currentCoords.length];
        //creates random object
        Random rand = new Random();

        // update neighborhood best
        if(neighborhood!=null){
            Particle champ = neighborhood[0];
            //compares champ to all neighborhood values, sets it to best one
            for(int i = 1; i < neighborhood.length; i++){
		if(neighborhood[i].pBest < champ.pBest){
                    // the champ has been beat
                    champ = neighborhood[i];
		}
            }
            nBest = champ.pBest;
            neighborhoodBestCoords = champ.pBestCoords;
        }
        //compute the acceleration due to personal best
        for(int i=0;i<pBestAccel.length;i++){
            pBestAccel[i]= (pBestCoords[i]-currentCoords[i])*rand.nextDouble()*PHI_ONE;
        }
        //if a neighborhood exists (i.e. if topology is not global)
        if(neighborhood!=null){
            //compute the acceleration due to neighborhood best
            for(int j=0;j<neighborhoodBestAccel.length;j++){
                neighborhoodBestAccel[j]=(neighborhoodBestCoords[j]-currentCoords[j])*rand.nextDouble()*PHI_TWO;
            }
            //constrict the new velocity and reset the current velocity
            for(int k=0;k<velocities.length;k++){
                velocities[k]= CONSTRICTION_FACTOR*(velocities[k]+pBestAccel[k]+neighborhoodBestAccel[k]);
            }
        } 
        //if there is a global neighborhood topology
        else{
            //compute the acceleration due to global best
            for(int j=0;j<globalBestAccel.length;j++){
                globalBestAccel[j]=(gBestCoords[j]-currentCoords[j])*rand.nextDouble()*PHI_TWO;
            }
            //constrict the new velocity and reset the current velocity
            for(int k=0;k<velocities.length;k++){
                velocities[k]= CONSTRICTION_FACTOR*(velocities[k]+pBestAccel[k]+globalBestAccel[k]);
            }
        }
        //update the particle's position
        for(int l=0;l<currentCoords.length;l++){
            currentCoords[l]=currentCoords[l]+velocities[l];
        } 
        //find the value of the particle at the new position
        currentValue=eval();    
        //update the particle's personal best
        if(currentValue<pBest){
            pBest=currentValue;
            pBestCoords=currentCoords.clone();
        }
        //update the global best
        if(pBest < gBest){
            gBest = currentValue;
            gBestCoords = pBestCoords.clone();
        } 
    }
}