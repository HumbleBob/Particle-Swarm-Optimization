Authors: Chris MacDonald, Simon Moushabeck, Jack Truskowski, Robert Choi
April 2015

NOTES:
Our program in its present iteration is designed to work in Netbeans.  Open the program by selecting “Open project” and navigating to our project folder.

Running the project will print the global best of each particle swarm iteration, concluding with the final global best and the median global best of all iterations.

Navigating to “File > Project Properties (PSOLab) > Run” will allow you to manipulate the arguments controlling the particle swarm generation.  The parseArgs function provides the details of how arguments should be inputted.  

In summary, the first argument is the neighborhood topology.  Input:
“gl” for global,
“ri” for ring,
“vn” for von Neumann,
and “ra” for random.

The second argument is the number of particles in the swarm.

The third argument is the number of iterations the program will run.

The fourth argument is the evaluation function.  Input:
“rok” for Rosenbrock,
“ack” for Ackley,
and “ras” for Rastrigin.

The fifth argument is the number of dimensions.
