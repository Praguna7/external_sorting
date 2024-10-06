import java.io.*;
import java.util.*;

class TournamentNode {
    int value;
    int runIndex;

    public TournamentNode(int value, int runIndex) {
        this.value = value;
        this.runIndex = runIndex;
    }
}

public class ExternalMergeSort {

    private static final int BUFFER_SIZE = 16 * 1024 * 1024; // 16MB buffer 

    public static void sort(String inputFile, String outputFile) throws IOException {

        int runSize = BUFFER_SIZE / 4; // Integer is 4 bytes
        generateRuns(inputFile, "run_", runSize);

        List<String> runFiles = new ArrayList<>();
        for (int i = 0; ; i++) {
            String runFile = "run_" + i + ".txt";
            if (new File(runFile).exists()) {
                runFiles.add(runFile);
            } else {
                break;
            }
        }

        mergeRuns(runFiles, outputFile);
    }

    public static void generateRuns(String inputFile, String runPrefix, int runSize) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        List<Integer> buffer = new ArrayList<>();
        int runCount = 0;

        while (reader.ready()) {
            buffer.clear();
            for (int i = 0; i < runSize && reader.ready(); i++) {
                buffer.add(Integer.parseInt(reader.readLine()));
            }
            Collections.sort(buffer);
            writeRunToFile(runPrefix + runCount + ".txt", buffer);
            runCount++;
        }

        reader.close();
    }

    private static void writeRunToFile(String runFile, List<Integer> buffer) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(runFile));
        for (int num : buffer) {
            writer.write(num + "\n");
        }
        writer.close();
    }

    public static void mergeRuns(List<String> runFiles, String outputFile) throws IOException {
        BufferedReader[] readers = new BufferedReader[runFiles.size()];
        TournamentNode[] tournamentTree = new TournamentNode[runFiles.size() * 2 - 1]; 

        for (int i = 0; i < runFiles.size(); i++) {
            readers[i] = new BufferedReader(new FileReader(runFiles.get(i)));
            int firstValue = Integer.parseInt(readers[i].readLine());
            tournamentTree[runFiles.size() - 1 + i] = new TournamentNode(firstValue, i); 
        }

        // Build initial tree
        buildTournamentTree(tournamentTree);

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        while (tournamentTree[0] != null) { 
            TournamentNode smallestNode = tournamentTree[0];
            writer.write(smallestNode.value + "\n");

            if (readers[smallestNode.runIndex].ready()) {
                int nextValue = Integer.parseInt(readers[smallestNode.runIndex].readLine());
                tournamentTree[runFiles.size() - 1 + smallestNode.runIndex] = new TournamentNode(nextValue, smallestNode.runIndex);
            } else {
                tournamentTree[runFiles.size() - 1 + smallestNode.runIndex] = null;
            }
            rebuildTournamentTree(tournamentTree, smallestNode.runIndex + runFiles.size() - 1);
        }

        writer.close();

        for (BufferedReader reader : readers) {
            reader.close();
        }

        deleteTempRunFiles(runFiles);
    }

    private static void buildTournamentTree(TournamentNode[] tree) {
        int size = tree.length / 2;
        for (int i = size - 1; i >= 0; i--) {
            tree[i] = getWinner(tree[2 * i + 1], tree[2 * i + 2]);
        }
    }

    private static void rebuildTournamentTree(TournamentNode[] tree, int leafIndex) {
        int parentIndex = (leafIndex - 1) / 2;
        while (parentIndex >= 0) {
            tree[parentIndex] = getWinner(tree[2 * parentIndex + 1], tree[2 * parentIndex + 2]);
            if (parentIndex == 0) break; 
            parentIndex = (parentIndex - 1) / 2;
        }
    }

    private static TournamentNode getWinner(TournamentNode left, TournamentNode right) {
        if (left == null) return right;
        if (right == null) return left;
        return (left.value <= right.value) ? left : right;
    }

    private static void deleteTempRunFiles(List<String> runFiles) {
        for (String runFile : runFiles) {
            File file = new File(runFile);
            file.delete();
        }
    }
}
