
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public class GHRepoMiner {

    private static String BASE_URL = "";

    private static HttpLoader httpLoader;


    public static void main(String[] args) {
        httpLoader = new HttpLoader();
        LocalDateTime toDateTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime fromDateTime = toDateTime.minusDays(1);
        List<URL> repoUrls = httpLoader.getRepoUrls(fromDateTime, toDateTime);
        writeUrlsToFile(repoUrls, fromDateTime, toDateTime);
    }

    private static void writeUrlsToFile(List<URL> urls, LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            FileWriter fileWriter = new FileWriter("repo_urls.txt");
            fileWriter.write("Range from " + String.valueOf(fromDate) + " to " + String.valueOf(toDate) + "\n");
            for (URL url : urls) {
                fileWriter.write(String.valueOf(url) + "\n");
            }
            fileWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
