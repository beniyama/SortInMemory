import java.util.Arrays;

public final class SortingEngine {
    private int[] array;
    private int[] memory;
    private int bufferSize;

    /*
     * The memory size needs to be greater than 4. The last 2 rows are used as a
     * temporal buffer to implement the sorting algorithm, and the rest is for
     * loading an input array.
     */
    private final static int RESERVED_SPACE_SIZE = 2;

    /**
     * Constructor
     * 
     * @param memorySize
     */
    public SortingEngine(int memorySize) {
	this.memory = new int[memorySize];
	this.bufferSize = memorySize - RESERVED_SPACE_SIZE;
    }

    /**
     * Read the input array, which is supposed to be on a file system.
     * 
     * @param arrayOnTheFileSystem
     */
    public final void readArray(int[] arrayOnTheFileSystem) {
	this.array = new int[arrayOnTheFileSystem.length];
	System.arraycopy(arrayOnTheFileSystem, 0, this.array, 0, arrayOnTheFileSystem.length);
    }

    /**
     * A file system & memory interface function to 'load file system data to a
     * memory'
     * 
     * @param index
     */
    private void loadToMemory(int index) {
	int restArraySize = this.array.length - index;
	int copySize = (restArraySize < this.bufferSize) ? restArraySize : this.bufferSize;
	System.arraycopy(this.array, index, this.memory, 0, copySize);
    }

    /**
     * A file system & memory interface function to 'write back memory data to a
     * file system'
     * 
     * @param index
     */
    private void writeToDisk(int index) {
	int restArraySize = this.array.length - index;
	int copySize = (restArraySize < this.bufferSize) ? restArraySize : this.bufferSize;
	System.arraycopy(this.memory, 0, this.array, index, copySize);
    }

    /**
     * A memory interface function to 'clear all the memory space'
     */
    private void clearMemory() {
	Arrays.fill(this.memory, 0);
    }

    /**
     * Swap two values inside the memory
     * 
     * @param indexA
     * @param indexB
     */
    private void swap(int indexA, int indexB) {
	int lastIndex = this.memory.length - 1;
	this.memory[lastIndex] = this.memory[indexA];
	this.memory[indexA] = this.memory[indexB];
	this.memory[indexB] = this.memory[lastIndex];
    }

    /**
     * Bubble sort implementation to sort a chunk of data loaded in the memory
     * 
     * @param range
     */
    private void bubbleSort(int range) {
	int outerLoopLimit = (range < this.bufferSize) ? range - 1 : this.bufferSize - 1;
	for (int i = 0; i < outerLoopLimit; i++) {
	    int innerLoopLimit = outerLoopLimit - i;
	    for (int j = 0; j < innerLoopLimit; j++) {
		// If inverse order is found, then swap the values
		if (this.memory[j] > this.memory[j + 1]) {
		    this.swap(j, j + 1);
		}
	    }
	}
    }

    /**
     * Merge two chunks that are already sorted by the bubble sort
     * 
     * @param firstChunkStart
     * @param firstChunkEnd
     * @param secondChunkStart
     * @param secondChunkEnd
     */
    private void mergeChunks(int firstChunkStart, int firstChunkEnd, int secondChunkStart, int secondChunkEnd) {
	// The value in reserved index is used to check whether a value in the
	// second chunk needs to shift further or not.
	int reservedIndex = this.memory.length - RESERVED_SPACE_SIZE;
	int secondChunkSize = secondChunkEnd - secondChunkStart + 1;
	for (int i = 0; i < secondChunkSize; i++) {
	    // Pop the first (smallest) value in the second chunk and search the
	    // appropriate position in the first chunk by decrement the pointer
	    // value. The i value increments until it reaches the last element
	    // of second chunk, so eventually two chunks would be merged in
	    // order.
	    int _pointer = (secondChunkStart + i) - this.bufferSize + 1;
	    for (int pointer = _pointer; pointer >= 0; pointer--) {
		this.loadToMemory(pointer);
		// Remember the value from the second chunk, otherwise we need
		// to fully continue the loop.
		if (this.memory[reservedIndex] == 0) {
		    this.memory[reservedIndex] = this.memory[this.bufferSize - 1];
		}
		// Step-by-step sort the loaded value by bubble sort.
		this.bubbleSort(secondChunkEnd - firstChunkStart + 1);
		this.writeToDisk(pointer);
		// If the position of the value from second chunk doesn't
		// change, we can stop iteration.
		if (this.memory[0] != this.memory[reservedIndex]) {
		    this.clearMemory();
		    break;
		}
	    }
	}
    }

    /**
     * Merge sort implementation: if the given chunk is larger than memory,
     * recursively divide into small chunks. If the chunk could fit to the
     * memory, use the bubble sort and write back to the disk.
     * 
     * @param startIndex
     * @param endIndex
     */
    private void sortAndMerge(int startIndex, int endIndex) {
	int chunkSize = endIndex - startIndex + 1;
	if (chunkSize > this.bufferSize) {
	    // Divide, sort, and merge
	    int firstChunkEnd = chunkSize / 2 + startIndex;
	    int secondChunkStart = firstChunkEnd + 1;
	    this.sortAndMerge(startIndex, firstChunkEnd);
	    this.sortAndMerge(secondChunkStart, endIndex);
	    this.mergeChunks(startIndex, firstChunkEnd, secondChunkStart, endIndex);
	} else if (chunkSize > 1) {
	    // If the chunk size is enough small to fit into the memory, let's
	    // sort them.
	    this.loadToMemory(startIndex);
	    this.bubbleSort(chunkSize);
	    this.writeToDisk(startIndex);
	    this.clearMemory();
	}
    }

    /**
     * A public function to invoke recursive sort
     */
    public final int[] sort() {
	this.print();
	long start = System.currentTimeMillis();
	sortAndMerge(0, this.array.length - 1);
	long end = System.currentTimeMillis();
	this.print();
	System.out.println("Finished: " + (end - start) + " msec (input size:" + this.array.length + ", memory size:" + this.memory.length
		+ ")");
	return this.array;
    }

    /**
     * An utility function to print out the input/output array
     */
    public final void print() {
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < this.array.length; i++) {
	    buffer.append(String.format("%1$10d", this.array[i]) + "\n");
	}
	System.out.println(buffer.toString());
    }
}
