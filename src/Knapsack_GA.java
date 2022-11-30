import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * ID: 20190356 NAME: Omar Atef Mohamed
 * ID: 20190353 NAME: Omar Khaled Mohy
 * Group: S1
 */
public class Knapsack_GA {
    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader(String fname) throws FileNotFoundException {
            br = new BufferedReader(new FileReader(fname));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }

        String nextLine() throws IOException {
            st = new StringTokenizer(br.readLine());
            String str = "";
            try {
                if (st.hasMoreTokens()) {
                    str = st.nextToken("\n");
                } else {
                    str = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }

    static class Item {
        int weight;
        int value;

        public Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Item{" + "weight=" + weight + ", value=" + value + '}';
        }
    }

    static class Chromosome {
        ArrayList<Integer> genes;
        int fitness;
        int totalWeight;

        public Chromosome() {
            this.fitness = 0;
            this.totalWeight = 0;
            this.genes = new ArrayList<>();
        }

        public Chromosome(ArrayList<Integer> genes, int fitness, int totalWeight) {
            this.genes = genes;
            this.fitness = fitness;
            this.totalWeight = totalWeight;
        }

        public void calcFitness(Item[] items) {
            this.fitness = 0;
            this.totalWeight = 0;
            for (int i = 0; i < this.genes.size(); i++) {
                if (this.genes.get(i) == 1) {
                    this.fitness += items[i].value;
                    this.totalWeight += items[i].weight;
                }
            }
        }

        @Override
        public String toString() {
            return "Chromosome{" + "genes=" + genes + ", fitness=" + fitness + ", totalWeight=" + totalWeight + '}';
        }
    }

    public int get01Random() {
        return (int) Math.floor(Math.random() * 2);
    }

    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public double getRandom01Range() {
        Random random = new Random();
        return random.nextDouble();
    }

    final int MAX_GENERATIONS = 10;
    final double CROSSOVER_PROB = 0.6;
    final double MUTATION_PROB = 0.05;
    int POPULATION_SIZE;
    int ITEM_SIZE;
    ArrayList<Chromosome> population_chromosomes;
    Item[] items;
    int MAX_WEIGHT;

    public void start() throws FileNotFoundException {
        FastReader scanner = new FastReader("input.txt");
        int testCases = scanner.nextInt();


        int i = 0;
        while (i < testCases) {

            this.population_chromosomes = new ArrayList<>();
            items = null;
            ITEM_SIZE = 0;
            POPULATION_SIZE = 0;
            MAX_WEIGHT = 0;

            System.out.println("\n\n\nTEST CASE " + (i + 1));
            GA(scanner);
            i++;
        }

    }

    public void initializePopulation(Item[] items) {
        while (this.population_chromosomes.size() < this.POPULATION_SIZE) {
            Chromosome chromosome = new Chromosome();
            for (int i = 0; i < this.ITEM_SIZE; i++) {
                chromosome.genes.add(get01Random());
            }
            chromosome.calcFitness(items);
            this.population_chromosomes.add(chromosome);
        }
    }

    public Object[] rankSelection() {
        Collections.sort(population_chromosomes, (a, b) -> b.fitness - a.fitness);
        ArrayList<Integer> prefix = new ArrayList<>();
        int cumulative_fitness = 0;
        prefix.add(0);
        for (Chromosome c : this.population_chromosomes) {
            cumulative_fitness += c.fitness;
            prefix.add(cumulative_fitness);
        }

        return new Object[]{prefix, cumulative_fitness};
    }

    public void GA(FastReader scanner) {

        this.POPULATION_SIZE = getRandomNumber(20, 100);
        this.MAX_WEIGHT = scanner.nextInt();
        this.ITEM_SIZE = scanner.nextInt();
        this.items = new Item[this.ITEM_SIZE];
        for (int i = 0; i < this.ITEM_SIZE; i++) {
            Item item = new Item(scanner.nextInt(), scanner.nextInt());
            items[i] = item;
        }

        ///////////////////// Initial Population ///////////////////
        initializePopulation(items);
        //System.out.println(population_chromosomes);

        ///////////////////// Next Generations ///////////////////
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {


            System.out.println("**************** Generation Number " + generation +" OF " +MAX_GENERATIONS+" Generations ******************");
            //System.out.println(this.population_chromosomes);
            ///////////////////// Selection ///////////////////
            Object[] selection = rankSelection();
            ArrayList<Integer> prefix = (ArrayList<Integer>) selection[0];
            int cumulative_fitness = (int) selection[1];
            //System.out.println(prefix);

            ArrayList<Chromosome> offsprings = new ArrayList<>();
            int offspring_1_index = -1;
            int offspring_2_index = -1;

            for (int i = 0; i < 2; i++) {
                int random_parent = getRandomNumber(0, cumulative_fitness - 1);
                for (int j = 0; j < prefix.size(); j++) {
                    if (!(random_parent >= prefix.get(j))) {
                        offsprings.add(this.population_chromosomes.get(j - 1));
                        if (offspring_1_index == -1) {
                            offspring_1_index = j - 1;
                        } else {
                            offspring_2_index = j - 1;
                        }
                        break;
                    }
                }
            }

            // System.out.println("Offsprings before c/m: " + offsprings);
            // System.out.println("Off1Loc:= " + offspring_1_index + " Off2Loc:= " + offspring_2_index);

            ////////////// Crossover ///////////////////
            System.out.println("\n=================Cross Over=================");
            int cross_over_point = getRandomNumber(1, ITEM_SIZE - 1);
            double probability_crossover = getRandom01Range();

            if (probability_crossover < CROSSOVER_PROB) {
                System.out.println("Before Cross Over "+offsprings);
                System.out.println("CROSSOVER OCCURRED from point" + cross_over_point);

                for (int i = 0; i < ITEM_SIZE; i++) {
                    if (i >= cross_over_point) {
                        int temp = offsprings.get(0).genes.get(i);
                        offsprings.get(0).genes.set(i, offsprings.get(1).genes.get(i));
                        offsprings.get(1).genes.set(i, temp);
                    }
                }
                System.out.println("After Cross Over "+ offsprings);
            } else {
                System.out.println("NO CROSSOVER");
            }

            ///////////// Mutation //////////


            System.out.println("\n================ Mutation ===================");
            for (int j = 0; j < offsprings.size(); j++) {
                for (int i = 0; i < ITEM_SIZE; i++) {
                    double probability_mutation = getRandom01Range() / 10;
                    int mutation_point = i;
                    if (probability_mutation < MUTATION_PROB) {
                        System.out.println("MUTATION OCCURRED at index " + mutation_point+" for offspring number "+(j+1));

                        int offspring_1_mutation_point = offsprings.get(0).genes.get(mutation_point);

                        if (offspring_1_mutation_point == 1) {
                            offsprings.get(j).genes.set(mutation_point, 0);
                        } else {
                            offsprings.get(j).genes.set(mutation_point, 1);
                        }

                    } else {
                        System.out.println("NO MUTATION");
                    }
                }
            }
            offsprings.get(0).calcFitness(items);
            offsprings.get(1).calcFitness(items);
            System.out.println("Offsprings after crossover/mutation: " + offsprings);

            this.population_chromosomes.set(offspring_1_index, offsprings.get(0));
            this.population_chromosomes.set(offspring_2_index, offsprings.get(1));

            //////// New Generation ///////

            System.out.println("**************** New Generation ******************");
            //System.out.println(this.population_chromosomes);


        }
        Collections.sort(population_chromosomes, (a, b) -> b.fitness - a.fitness);
        // System.out.println(population_chromosomes);
        for (Chromosome chromosome : population_chromosomes) {
            if (chromosome.totalWeight <= MAX_WEIGHT) {
                System.out.println("\n\n**************************************************************");
                System.out.println("BEST ANSWER is: " + chromosome);
                System.out.println("**************************************************************");
                break;
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Knapsack_GA knapsack_ga = new Knapsack_GA();
        knapsack_ga.start();
    }
}