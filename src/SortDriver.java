import java.util.Arrays;
import java.util.Random;

public class SortDriver {

    private static boolean validate(int[] input, int[] output) {
	boolean result = true;
	long start = System.currentTimeMillis();
	Arrays.sort(input);
	long end = System.currentTimeMillis();
	for (int i = 0; i < input.length; i++) {
	    if (input[i] != output[i]) {
		result = false;
		System.out.println(i + ": input[i] = " + input[i] + " output[i] = " + output[i]);
		break;
	    }
	}
	System.out.println("Arrays.sort: " + (end - start) + " msec");
	return result;
    }

    /**
     * The main function to demonstrate my sorting algorithm
     * 
     * @param args
     */
    public static void main(String[] args) {
	// Takes two arguments, one for input array size and another for memory
	// size
	if (args.length != 2) {
	    System.out.println("Usage: java SortDriver <input size (i.e., array size on the filesystem)> <memory size>");
	    return;
	}
	int inputSize = Integer.valueOf(args[0]);
	int memorySize = Integer.valueOf(args[1]);
	if (memorySize < 4) {
	    System.out.println("Memory size needs to be greater than 3.");
	}

	// Initialize an input array with random value
	int[] input = new int[inputSize];
	int[] output = null;
	Random rand = new Random();
	for (int i = 0; i < inputSize; i++) {
	    input[i] = rand.nextInt(Integer.MAX_VALUE);
	}

	// Instantiate my sorting engine
	SortingEngine engine = new SortingEngine(memorySize);

	// Read the input
	engine.readArray(input);

	// Sort and print out the result
	output = engine.sort();

	// Just in case compare the result with a Java sort library
	boolean checkResult = SortDriver.validate(input, output);
	System.out.println("Validation result >> " + checkResult);
    }
}
