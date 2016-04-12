package psolab;

public class Main{
	
	/*
	 * VARIABLES
	 */
	private static String topology;
	private static int numParticles;
	private static int maxIterations;
	private static int testFunction;
	private static int numDimensions;

	/*
	 * For parsing the command line arguments
	 */
	private static void parseArgs(String [] args){
		
		//Check to make sure correct number of arguments are supplied
		if(args.length!=5){
			System.out.println("\njava Main topology numParticles maxIterations function numDimensions");
			System.out.println("    topology = Neighborhood topology to be tested:");
			System.out.println("          gl = global");
			System.out.println("          ri = ring");
			System.out.println("          vn = von Neumann");
			System.out.println("          ra = random");
			System.out.println("    numParticles = number of particles in the swarm");
			System.out.println("    maxIterations = max number of iterations to run");
			System.out.println("    testFunction = evaluation function:");
			System.out.println("          rok = Rosenbrock");
			System.out.println("          ack = Ackley");
			System.out.println("          ras = Rastrigin");
			System.out.println("    numDimensions = the number of dimensions");
			System.exit(1);
		}
		//Put the arguments in class variables
		if(	!args[0].equals("gl") &&
			!args[0].equals("ri") &&
			!args[0].equals("vn") &&
			!args[0].equals("ra") 
		  ){
			//Something's wrong
			System.out.println("INVALID TOPOLOGY...");
			System.exit(1);
		}

		topology = args[0];
		numParticles = Integer.parseInt(args[1]);
		maxIterations = Integer.parseInt(args[2]);
		
		//Translate the function from a String to an int
		if(args[3].equals("rok")){
			testFunction = 0;
		}else if (args[3].equals("ack")){
			testFunction = 1;
		}else if(args[3].equals("ras")){
			testFunction = 2;
		}else{
			//Something's wrong
			System.out.println("INVALID FUNCTION...");
			System.exit(1);
		}
		numDimensions = Integer.parseInt(args[4]);
	}
	
        /*
         * MAIN
         */
	public static void main(String[] args) {
                //get command-line args, make them variables
		parseArgs(args);
		System.out.println(topology + "\n" + numParticles + "\n" + maxIterations + "\n" + testFunction + "\n" + numDimensions);
		//create and run PSO 
                PSO pso;
                pso = new PSO(topology, numParticles, maxIterations,testFunction,numDimensions);
                pso.run();
	}
}