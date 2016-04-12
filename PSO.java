package psolab;

import java.util.Random;
import java.util.ArrayList;

public class PSO{ 

    // for random numbers
    private Random rand = new Random();
	
    // particles
    private Particle[] Particles;

    // neighborhoods
    private Particle[][] vonNeumann;
    private Particle[] ring;
    private final int RANDOM_NEIGHBORHOOD_SIZE = 5;
    private String neighborhoodType;

    // number of particles in the swarm
    private int numParticles; 

    // which one to test
    private int testFunction = 0;

    // for controlling termination 
    private int iterationNum;
    private int maxIterations;
    private int numDimensions;
    

    /*
     * CONSTRUCTOR 
     */
    public PSO(String topology,int maxParticles, int maxIters, int testFcn, int numDims) {

        //assign inputs to class variables
        numParticles=maxParticles;        
        maxIterations=maxIters;
        iterationNum=0;
        testFunction=testFcn;
        numDimensions=numDims;
        neighborhoodType=topology;
        
        // set gbest value very high so it will be replaced in the loop
	// that creates the particles
	Particle.gBest = Double.MAX_VALUE;
        
	// create particles
	Particles = new Particle[numParticles];
	for(int i = 0; i < numParticles; i++){
            // make new particle
            Particle p = new Particle(numDimensions, testFunction);
            Particles[i] = p;
	}
        //creates a neighborhood for each particle
        makeHoods(topology);
    }
    
    /*
     * MAKE HOODS -- call the function to create the appropriate neighborhood
     */
    private boolean makeHoods(String topology){
	// organize the particles into neighborhoods
        if(topology.equals("vn")){
            // make Von Neumann neighborhood (particles will be put in a 
            //different size grid depending on size of swarm)
            if(numParticles <= 12){
                makeVonNeumann(3, 4);
            }
            else if(numParticles <= 20){
		makeVonNeumann(4, 5);
            }
            else if(numParticles <= 50){
		makeVonNeumann(5, 10);
            }
	}
            else if(topology.equals("ri")){
            // make ring neighborhood
            makeRing();
	}
        else if(topology.equals("gl")){
            // tell particles to use global neighborhood
            for(int i = 0; i < Particles.length; i++){
		Particles[i].neighborhood = null;
            }
            // particles' neighborhoods are null, global information (value, 
            //coords) is static so it is shared by every particle
	}
        else if(topology.equals("ra")){
            // make random neighborhood
            makeRandom(RANDOM_NEIGHBORHOOD_SIZE);
	}
        else{
            // Something went wrong
            System.out.print("Could not make neighborhoods.");
            return false;
        }
        return true;
    }

    /*
     * MAKE VON NEUMANN -- makes a grid of particles from which to choose neighbors
     */
    public void makeVonNeumann(int width, int height){
	// make a 2d array of particles	
        vonNeumann = new Particle[width][height];
        int next = 0;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(next >= Particles.length){
                    //call function to tell particles which other particles in
                    //grid are their neighbors
                    informVonNeumann(vonNeumann);
                    return;
                }
                vonNeumann[i][j] = Particles[next];
                next++;
            }
        }
    }
    
    /*
     * INFORM VON NEUMANN -- tell particles who their neighbors are
     */
    public void informVonNeumann(Particle[][] vonNeumann){
        //size of 2d array from which particles pick their neighbors
        int width = vonNeumann.length;
        int height = vonNeumann[0].length;
        
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                // don't add empty spaces to neighborhood
                if(vonNeumann[i][j] == null) continue; 

                vonNeumann[i][j].neighborhood = new Particle[5];
                int J = j;
                // particle includes itself in neighborhood
                vonNeumann[i][j].neighborhood[0] = vonNeumann[i][j]; 

                // include particle to the left in neighborhood
                int I = i-1;
                if(I < 0) I += width;
                while(vonNeumann[I][J] == null){
                    I--;
                    if(I < 0) I += width;
                }
                vonNeumann[i][j].neighborhood[1] = vonNeumann[I][J];
                J = j;

                // include particle to the right in neighborhood
                I = (i+1)%width;
                while(vonNeumann[I][J] == null) I = (I+1)%width;
                vonNeumann[i][j].neighborhood[2] = vonNeumann[I][J];
                I = i;

                // include particle above in neighborhood
                J = j -1;
                if(J < 0) J += height;
                while(vonNeumann[I][J] == null){
                    J--;
                    if(J < 0) J += height;
                }
                vonNeumann[i][j].neighborhood[3] = vonNeumann[I][J];
                I = i;
                
                // include particle below in neighborhood
                J = (j+1)%height;
                while(vonNeumann[I][J] == null) J = (J+1)%height;
                vonNeumann[i][j].neighborhood[4] = vonNeumann[I][J];
            }
        }
    }
    
    /*
     * MAKE RING -- makes a 1d array from which particles are assigned neighbors
     */
    public void makeRing (){
        //makes array
        ring = new Particle[Particles.length];
        int next = 0;
	for(int i = 0; i < Particles.length; i++){
            ring[i] = Particles[next];
            next++;
	}
        //calls function that assigns neighbors to each particle in array
        informRing(ring);
    }
    
    /*
     * INFORM RING -- tells particles who their neighbors are
     */
    public void informRing(Particle[] ring){       
        //go through ring array, inform each particle that its neighbors are the 
        //two particles next to it
        int length = ring.length;
        for(int i = 0; i < length; i++){
            ring[i].neighborhood = new Particle[3];
            int I = i;
            //particle includes itself in its neighborhoods 
            ring[i].neighborhood[0] = ring[i]; 
            
            //add particle to the left to the neighborhood
            if(I < 0) I += length;
            ring[i].neighborhood[1] = ring[I];
            System.out.println("left: (" + I + ")");
            I = i;           
            I++; 
            //add particle to the right to the neighborhood
            if(I >= length) I -= length;
            ring[i].neighborhood[2] = ring[I];
            System.out.println("right: (" + I + ")");
            I = i;
        }
    }
    
    /*
     * MAKE RANDOM
     */
    public void makeRandom (int length){
        int randIndex, spot;
        //go through every particle, randomly assign it four other neighbors 
        //(total of 5 neighbors because particle includes itself)
        for(int i = 0; i < Particles.length; i++){
            Particles[i].neighborhood = new Particle[length];
            //particle includes itself in neighborhood
            Particles[i].neighborhood[0] = Particles[i];
            spot = 1;
            //particle adds 4 more random neighbors
            while(spot < length){
                randIndex = rand.nextInt(Particles.length);
                for (int j = 0; j < spot; j++){
                    //This particle is already in the neighborhood, break and grab a new one
                    if (Particles[i].neighborhood[j] == Particles[randIndex]) {
                        randIndex = -1;
                        break;
                    }
                }
                //Valid particle found. Put it in the neighborhood and continue
                if (randIndex != -1) {
                    Particles[i].neighborhood[spot] = Particles[randIndex];
                    spot++;
                }
            }
        }
    }
    
    /*
     * RUN
     */
    public boolean run(){
        double median=0;  
        //update the partices for a certain number of iterations, print results
        while(iterationNum < maxIterations) {
            //print out gbest value at each iteration
            System.out.println("iteration " + iterationNum + "  gbest value = " + Particle.gBest);
            // update all the particles
            for (int p = 0 ; p < numParticles ; p++) {
                Particles[p].update();
            }
            //record the global best at the median iteration
            if(iterationNum==25000){
                median=Particle.gBest;
            }
            // if random topology, shuffle neighbors
            if(neighborhoodType.equals("ra")){
                makeRandom(RANDOM_NEIGHBORHOOD_SIZE);                
            }
            iterationNum++;
	}
        //print out the overall global best and the median global best 
        System.out.println("gbest value = "+Particle.gBest +"\n"+ "median gbest value = " +median);
        return true; 
    }   
}



