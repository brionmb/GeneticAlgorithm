import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author BrionMB This is a Generational Genetic Algorithm
 */
public class GA {
	public String[] generationA;
	public String[] generationB;
	public String[] topChromosomes;
	public int[] fitness;
	public int populationSize;
	public int generations;
	public int chromosomeSize;
	ArrayList<Integer> choosingList;
	Random rand = new Random(170);

	/**
	 * Constructor for the Genetic Algorithm
	 * 
	 * @param popSize
	 *             how many chromosomes in each generation
	 * @param gen
	 *             how many generations to run the algorithm for
	 * @param chromSize
	 *             the length of the chromosome
	 */
	public GA(int popSize, int gen, int chromSize) {
		populationSize = popSize;
		generations = gen;
		chromosomeSize = chromSize;
		generationA = new String[populationSize];
		topChromosomes = new String[4];
		fitness = new int[populationSize];

	}

	/**
	 * Saves the population to a file on my desktop
	 */
	public void savePop() {
		try {
			File file = new File("/Users/BrionMB/Documents/Comp_Sci/GATEST/newGen");
			
			if (file.createNewFile()) {
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				for (int i = 0; i < generationA.length; i++) {
					String z = "individual: ";
					z = z + generationA[i];
					bw.write(z);
				}
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Finds the index of the chromosome with the best fitness
	 * 
	 * @return best the index where the best chromosome can be found
	 */
	public int getBestIndex() {
		int best = 0;
		for (int i = 1; i < generationA.length; i++) {
			if (fitness[best] < fitness[i])
				best = i;
		}
		return best;
	}

	/**
	 * Gets the average fitness of the current population
	 * 
	 * @return  the average fitness
	 */
	public double getAverage() {
		int total = 0;
		for (int i = 0; i < generationA.length; i++)
			total = total + fitness[i];
		return (total / fitness.length);

	}

	/**
	 * Returns the array which holds the fitness of the whole population
	 * 
	 * @return the fitnesses
	 */
	public int[] getFit() {
		return fitness;
	}

	/**
	 * Returns an array of the current generation of chromosomes
	 * 
	 * @return generationA the current generation
	 */
	public String[] getGen() {
		return generationA;
	}

	/**
	 * Starts the training of the genetic algorithm
	 */
	public void startSelection() {
		fillGeneration();
		// if you want to change it so it runs for a set number of generations instead of until one reaches all 1's, uncomment this and 
		// comment out the while statement
		// for(int i=0; i<generations; i++)
		// {
		while (fitness[getBestIndex()] != 64) {
			calculateFitness();
			orderArray();
			makeMax();
			breed();

			generationA[0] = topChromosomes[0];
			generationA[1] = topChromosomes[1];
			generationA[2] = topChromosomes[2];
			generationA[3] = topChromosomes[3];
			
			double avg = getAverage();
			int best = getBestIndex();
			
			System.out.println("average");
			System.out.println(avg);
			System.out.println("best: ");
			System.out.println(fitness[best]);
		}
		// }
	}

	/**
	 * Fills the current population with randomly generated chromosomes.
	 * Chromosomes contain only 1's and 0's
	 */
	public void fillGeneration() {
		for (int i = 0; i < populationSize; i++) {
			String newChromosome = "";
			for (int p = 0; p < chromosomeSize; p++) {
				newChromosome += rand.nextInt(2);
			}
			generationA[i] = newChromosome;
		}
	}

	/**
	 * Calculates the fitness of the population
	 */
	public void calculateFitness() {
		for (int i = 0; i < populationSize; i++) {
			//add to the value for ever 1 in the chromosome
			int val = 0;
			for (int p = 0; p < chromosomeSize; p++) {
				if (generationA[i].substring(p, p + 1).equals("1"))
					val++;
			}
			//assign fitness to a parallel array with matching index
			fitness[i] = val;

		}
	}

	/**
	 * Creates a list of the current population where ones with higher fitnesses
	 * will be added multiple times according to their fitness level. This will
	 * allow us to randomly search this list later on and have a higher
	 * probability of finding a "fitter" chromosome for breeding.
	 * 
	 */
	public void orderArray() {
		choosingList = new ArrayList<Integer>();
		for (int i = 0; i < populationSize; i++) {
			for (int fitlvl = fitness[i]; fitlvl > 0; fitlvl--) {
				//add it as many times as its fitness score
				choosingList.add(i);

			}
		}

	}

	/**
	 * Finds the top 4 chromosomes with the highest fitness and saves them.
	 */
	public void makeMax() {
		int max1 = -50;
		int max2 = -50;
		int max3 = -50;
		int max4 = -50;
		for (int i = 0; i < populationSize; i++) {
			if (fitness[i] > max1) {
				max4 = max3;
				max3 = max2;
				max2 = max1;
				max1 = fitness[i];

			} else if (fitness[i] > max2) {
				max4 = max3;
				max3 = max2;
				max2 = fitness[i];
			} else if (fitness[i] > max3) {
				max4 = max3;
				max3 = fitness[i];
			} else if (fitness[i] > max4)
				max4 = fitness[i];

		}
		
		topChromosomes[0] = generationA[max1];
		topChromosomes[1] = generationA[max2];
		topChromosomes[2] = generationA[max3];
		topChromosomes[3] = generationA[max4];
	}

	/**
	 * Runs through the current population and 'breeds' a new one. Chromosomes
	 * with higher fitness are given a higher chance of breeding However every
	 * chromosome is given a chance. Children are created by picking a crossover
	 * point in the parents and taking "the left" from one And "the right" from
	 * another. A second child is made with the reverse. Crossover points are a
	 * randomly selected index relating to the chromosome's length.
	 */
	public void breed() {
		//empty generation to store new children
		generationB = new String[populationSize];
		int size = choosingList.size();
		
		for (int p = 0; p < 50; p++) {
			//randomly selects 2 chromosomes from the weighted list
			int index1 = choosingList.get(rand.nextInt(size));
			int index2 = choosingList.get(rand.nextInt(size));
			//if the 2 indexes are the same, it chooses again
			if (index2 == index1) {
				while (index2 == index1) {
					index2 = choosingList.get(rand.nextInt(size));
				}
			}

			String chrom1 = generationA[index1];
			String chrom2 = generationA[index2];
			int crossPoint = rand.nextInt(chromosomeSize);
			String baby1 = "";
			String baby2 = "";
			baby1 = chrom1.substring(0, crossPoint) + chrom2.substring(crossPoint);
			baby2 = chrom2.substring(0, crossPoint) + chrom1.substring(crossPoint);

			//gives every spot on the baby's chromosome a chance for mutation
			baby1 = mutate(baby1);
			baby2 = mutate(baby2);
			
			//adds the children to the new generation
			generationB[p] = baby1;
			generationB[99 - p] = baby2;
		}
			//replaces the old generation with the new
			generationA = generationB.clone();
	}

	/**
	 * Puts a chromosome through random mutation. each bit has a chance to be
	 * swapped (from 1 to 0, or 0 to 1)
	 * 
	 * @param chrom
	 *            the chromsome to mutate
	 * @return String the mutated chromosome
	 */
	public String mutate(String chrom) {
		//goes through every bit of the chromosome and applies a 1 in 351 chance for mutation
		for (int i = 0; i < chrom.length(); i++) {
			int rate = rand.nextInt(351);
			
			if (rate == 1) {
				
				//mutation is a switching of the bits.
				if (chrom.substring(i, i + 1).equals("1")) {
					if (i + 1 != chrom.length()) {
						chrom = chrom.substring(0, i) + "0"
								+ chrom.substring(i + 1);
					} else {
						chrom = chrom.substring(0, i) + "0";
					}
				} else {
					if (i + 1 != chrom.length()) {
						chrom = chrom.substring(0, i) + "1"
								+ chrom.substring(i + 1);
					} else {
						chrom = chrom.substring(0, i) + "1";
					}
				}

			}

		}
		
		return chrom;
	}

	public static void main(String[] args) {
		GA myGA = new GA(100, 1000000, 64);
		myGA.startSelection();
		for (int i = 0; i < 100; i++)
			System.out.println(myGA.getFit()[i]);
		// myGA.savePop();
	}

}
