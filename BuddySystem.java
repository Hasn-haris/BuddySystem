import java.util.*;

class MemoryBlock {
    int start, size;
    boolean isFree;

    MemoryBlock(int start, int size) {
        this.start = start;
        this.size = size;
        this.isFree = true;
    }
}

public class BuddySystem {
    private List<MemoryBlock> memory;
    private int memorySize;

    public BuddySystem(int memorySize) {
        this.memorySize = memorySize;
        memory = new ArrayList<>();
        memory.add(new MemoryBlock(0, memorySize));
    }

    // Allocate memory
    public boolean allocate(String processName, int size) {
        int requiredSize = nextPowerOfTwo(size);
        MemoryBlock bestFit = null;

        for (MemoryBlock block : memory) {
            if (block.isFree && block.size >= requiredSize) {
                if (bestFit == null || block.size < bestFit.size) {
                    bestFit = block;  // Find the smallest fitting block
                }
            }
        }
    
        if (bestFit != null) {
            while (bestFit.size > requiredSize) {
                splitBlock(bestFit);
            }
            bestFit.isFree = false;
            System.out.println("Allocated " + size + "Kb to " + processName +
                               " starting at " + bestFit.start);
            return true;
        }
    
        System.out.println("Allocation failed for " + processName + ": Not enough memory.");
        return false;
    }

    // Deallocate memory
    public void deallocate(int start) {
        for (MemoryBlock block : memory) {
            if (block.start == start && !block.isFree) {
                block.isFree = true;
                System.out.println("Deallocated memory from starting address " + start);
                mergeBlocks();
                return;
            }
        }
        System.out.println("No allocated block found at " + start);
    }

    // Get the next power of 2
    private int nextPowerOfTwo(int size) {
        int power = 1;
        while (power < size) {
            power *= 2;
        }
        return power;
    }

    // Split memory block
    private void splitBlock(MemoryBlock block) {
        int newSize = block.size / 2;
        MemoryBlock buddy = new MemoryBlock(block.start + newSize, newSize);
        memory.add(buddy);
        block.size = newSize;
        memory.sort(Comparator.comparingInt(b -> b.start));
    }

    // Merge free memory blocks
    private void mergeBlocks() {
        memory.sort(Comparator.comparingInt(b -> b.start));
        for (int i = 0; i < memory.size() - 1; i++) {
            MemoryBlock current = memory.get(i);
            MemoryBlock next = memory.get(i + 1);
    
            if (current.isFree && next.isFree && current.size == next.size &&
                current.start + current.size == next.start) {
                current.size *= 2;
                memory.remove(i + 1);  // Remove the merged block
                i--;  // Recheck the current block with the next one
            }
        }
    }

    // Print memory state
    public void printMemoryState() {
        System.out.println("\nMemory State:");
        for (MemoryBlock block : memory) {
            System.out.println("Start: " + block.start + ", Size: " + block.size + 
                               "Kb, Free: " + block.isFree);
        }
    }

    public static void main(String[] args) {
        BuddySystem system = new BuddySystem(1024);  // Initialize 1024KB memory

        // Test memory allocation
        system.allocate("A", 64);   // 64KB
        system.allocate("B", 128);   // 128KB
        system.allocate("C", 225);    // 225KB
        system.allocate("D", 256);   // 355KB

        system.printMemoryState();

        // Test memory deallocation
        system.deallocate(0);   // Deallocate first block
        system.deallocate(512); // Deallocate second block

        system.printMemoryState();
    }
}