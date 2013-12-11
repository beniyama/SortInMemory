SortInMemory
============

Sort an array with a limited size of memory

## Demo

### Step 1. Build

    git clone git@github.com:ymts/SortInMemory.git
    cd SortInMemory
    ant build
    
### Step 2. Run

    cd bin
    java SortDriver 1000 5
    
This program takes two arguments: "input array size" and "memory size".

### Step 3. Confirm

The program prints out its sorting performance. In addition, it checks the validity of result by comparing with the one of Arrays.sort() method.

    Finished: 30 msec (input size:1000, memory size:5)
    Arrays.sort: 6 msec
    Validation result >> true


## Approach

Basic strategy here is divide, sort, and merge. Since limited memory space is allowed, I used following steps to sort an entire array.

1. Recursively divide the given array into small chunks, until they all fit into the memory space.
2. Once an entire chunk can be loaded into memory, use bubble sort to arrange them in order.
3. Then merge the chunk with the next one. To merge the chunks, memory is used as a 'window'. From the head of the second chunk, a value is popped and shifted with the window to find an appropriate position in the first chunk. Both chunk are in order so search cost should relatively smaller than random array merge.
4. Repeat the merge process until all chunks get together.

## Note

After measuring performance with changing memory size, I realized the performance gets worse as the size increases. Perhaps I should consider bubble sort cost as it could be the biggest bottleneck.
