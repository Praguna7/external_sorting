import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalQuickSort {
    private static final int BUFFER_SIZE = 16 * 1024 * 1024; // 16MB buffer 

    public static void sort(String inputFile, String outputFile) throws IOException {

        // Split input file into chunks and sort them
        List<File> sortedChunkFiles = splitAndSortChunks(inputFile);

        // Merge sorted chunks into a single  file
        mergeSortedChunks(sortedChunkFiles, outputFile);
    }

    public static List<File> splitAndSortChunks(String inputFile) throws IOException {
        List<File> sortedChunkFiles = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        List<Integer> buffer = new ArrayList<>(BUFFER_SIZE);

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.add(Integer.parseInt(line));
            if (buffer.size() == BUFFER_SIZE) {
                File sortedChunk = sortAndSaveChunk(buffer);
                sortedChunkFiles.add(sortedChunk);
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            File sortedChunk = sortAndSaveChunk(buffer);
            sortedChunkFiles.add(sortedChunk);
        }

        reader.close();
        return sortedChunkFiles;
    }

    public static File sortAndSaveChunk(List<Integer> chunk) throws IOException {
        chunk.sort(Integer::compareTo); // QuickSort internally
        File tempFile = File.createTempFile("sortedChunk", ".txt");
        tempFile.deleteOnExit(); 
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        for (int number : chunk) {
            writer.write(Integer.toString(number));
            writer.newLine();
        }

        writer.close();
        return tempFile;
    }

    public static void mergeSortedChunks(List<File> sortedChunkFiles, String outputFile) throws IOException {
        PriorityQueue<ChunkReader> minHeap = new PriorityQueue<>();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        for (File file : sortedChunkFiles) {
            ChunkReader chunkReader = new ChunkReader(file);
            if (chunkReader.hasNext()) { 
                minHeap.add(chunkReader);
            }
        }

        while (!minHeap.isEmpty()) {
            ChunkReader smallestChunk = minHeap.poll(); 
            int smallestNumber = smallestChunk.next();
            writer.write(Integer.toString(smallestNumber));
            writer.newLine();
            if (smallestChunk.hasNext()) {
                minHeap.add(smallestChunk);
            }
        }

        writer.close();
    }
}

class ChunkReader implements Comparable<ChunkReader> {
    private BufferedReader reader;
    private Integer current;

    public ChunkReader(File file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        if (line != null) {
            current = Integer.parseInt(line);
        } else {
            current = null; 
        }
    }

    public boolean hasNext() {
        return current != null;
    }

    public int next() {
        if (current == null) {
            throw new IllegalStateException("No more data to read from this chunk.");
        }

        int temp = current;
        try {
            String line = reader.readLine();
            if (line == null) {
                current = null;
            } else {
                current = Integer.parseInt(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public int peek() {
        return current;
    }

    public void close() throws IOException {
        reader.close();
    }

    @Override
    public int compareTo(ChunkReader other) {
        if (this.current == null && other.current == null) {
            return 0;
        } else if (this.current == null) {
            return 1;
        } else if (other.current == null) {
            return -1;
        }
        return Integer.compare(this.current, other.current);
    }
}
