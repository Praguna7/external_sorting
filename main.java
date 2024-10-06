import java.io.*;
import java.util.*;

class ExternalSort {

    public static void main(String[] args) throws IOException {
        int numOfUnsortedFiles = 3;
        int numSortIterationsPerFile = 3;
        int totalQuickSortTime = 0;
        int totalMergeSortTime = 0;
        for(int i=1; i<=numOfUnsortedFiles; i++){
            String inputFile = "unsorted_files/unsorted_numbers_" + i +".txt";
            String quickSortedFile = "sorted_files/quicksorted_output_" + i +".txt";
            String mergeSortedFile = "sorted_files/mergesorted_output_" + i +".txt";

            generateUnsortedFile(inputFile, 256); // Generate 256MB of numbers

            System.out.println("------------------------------File_"+i+"------------------------------");

            // External quick sort
            int localTotalQuickSortTime = 0;
            int localTotalMergeSortTime = 0;
            for(int j =0; j<numSortIterationsPerFile; j++){
                System.out.println("Iteration "+j+" ------------------------------------------------------");
                long startTime = System.currentTimeMillis();
                ExternalQuickSort.sort(inputFile, quickSortedFile);
                long endTime = System.currentTimeMillis();
                localTotalQuickSortTime += (endTime - startTime);
                System.out.println("    QuickSort Time: " + (endTime - startTime) + " ms");

                // External merge sort
                startTime = System.currentTimeMillis();
                ExternalMergeSort.sort(inputFile, mergeSortedFile);
                endTime = System.currentTimeMillis();
                localTotalMergeSortTime += (endTime - startTime);
                System.out.println("    MergeSort Time: " + (endTime - startTime) + " ms");
            }
            System.err.println();
            System.out.println("File_"+i+" :Average QuickSort Time: " + (localTotalQuickSortTime/numSortIterationsPerFile) + " ms");
            System.out.println("File_"+i+" :Average MergeSort Time: " + (localTotalMergeSortTime/numSortIterationsPerFile) + " ms");
            totalMergeSortTime+=localTotalMergeSortTime;
            totalQuickSortTime+=localTotalQuickSortTime;
            System.out.println("------------------------------------------------------------------");
        }
        
        System.out.println("##################################################################");
        System.out.println("Average QuickSort Time: " + (totalQuickSortTime/(numOfUnsortedFiles*numSortIterationsPerFile)) + " ms");
        System.out.println("Average MergeSort Time: " + (totalMergeSortTime/(numOfUnsortedFiles*numSortIterationsPerFile)) + " ms");
        System.out.println("##################################################################");

        
    }

    public static void generateUnsortedFile(String fileName, int targetFileSize) throws IOException {
        Random random = new Random();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        long currentFileSize = 0;
        while (currentFileSize < targetFileSize*1024*1024) {
                int randomNumber = random.nextInt(1_000_000) + 1;
                String numberString = randomNumber + "\n";
                writer.write(numberString);
                currentFileSize += numberString.getBytes().length;
            }
        writer.close();
    }

}

