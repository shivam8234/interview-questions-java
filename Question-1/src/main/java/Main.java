import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Main {

    public static void main(String[] args) throws IOException {
        String inputFile = "/example_input.txt";
        String outputFile = "example_output.txt";

        int result = processInputFile(inputFile);
        System.out.printf("Result: %d%n", result);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();
        bufferedWriter.close();
    }

    public static int processInputFile(String dataFile) throws IOException {
        InputStream is = Main.class.getResourceAsStream(dataFile);

        if (is == null) {
            throw new RuntimeException("can not find example_input.txt under resources");
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        int expiryLimit = Integer.parseInt(bufferedReader.readLine().trim());

        int commandsRows = Integer.parseInt(bufferedReader.readLine().trim());
        int commandsColumns = Integer.parseInt(bufferedReader.readLine().trim());

        List<List<Integer>> commands = new ArrayList<>();

        IntStream.range(0, commandsRows).forEach(i -> {
            try {
                commands.add(
                        Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                                .map(Integer::parseInt)
                                .collect(toList())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        bufferedReader.close();
        return Solution.numberOfTokens(expiryLimit, commands);
    }

}
