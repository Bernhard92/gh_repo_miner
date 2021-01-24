import exceptions.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class HttpLoader {

    private GHHttpConnection connection;

    private QueryBuilder baseQuery;

    private List<URL> searchResultPages = new LinkedList<URL>();
    private List<URL> repoUrls = new LinkedList<>();

    Random random = new Random();


    public List<URL> getRepoUrls(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        baseQuery = QueryBuilder.builder()
                .fromDateTime(fromDateTime)
                .toDateTime(toDateTime)
                .language("Java")
                .build();

        getSearchResultPageUrls(fromDateTime, toDateTime);
        System.out.println(searchResultPages);
        System.out.println(searchResultPages.size());

        for (URL url : searchResultPages) {
            getUrlsFromSearchResults(url);
        }
        System.out.println(repoUrls);
        System.out.println(repoUrls.size());
        return repoUrls;
    }


    private void getSearchResultPageUrls(LocalDateTime fromDate, LocalDateTime toDate) {
        QueryBuilder queryBuilder = baseQuery.toBuilder().fromDateTime(fromDate).toDateTime(toDate).build();

        try {
            sleep();
            connection = new GHHttpConnection(queryBuilder.getUrl());
            String response = connection.getResponse();
            Document html = Jsoup.parse(response);
            Element em = html.getElementsByAttribute("data-total-pages").first();
            int numberOfPages = Integer.parseInt(em.attr("data-total-pages"));
            System.out.println(numberOfPages);
            if (numberOfPages < 100) {
                //searchResultPages.add
                for (int i = 1; i <= numberOfPages; i++) {
                    searchResultPages.add(queryBuilder.toBuilder().page(i).build().getUrl());
                }
            } else {
                long minutes = ChronoUnit.MINUTES.between(fromDate, toDate);
                System.out.println("The search distance got halved from " + minutes + " to " + minutes / 2);
                getSearchResultPageUrls(toDate.minusMinutes(minutes / 2), toDate);
                getSearchResultPageUrls(fromDate, fromDate.plusMinutes(minutes / 2));
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
            if (e.statusCode == 429) {
                getSearchResultPageUrls(fromDate, toDate);
            }
        }
    }

    private void getUrlsFromSearchResults(URL url) {
        try {
            sleep();
            connection = new GHHttpConnection(url);
            String response = connection.getResponse();
            Document html = Jsoup.parse(response);
            Elements as = html.getElementsByAttribute("data-hydro-click-hmac");

            for (Element e : as) {
                if (e.hasClass("v-align-middle")) {
                    repoUrls.add(new URL("https://github.com" + e.attr("href")));
                    System.out.println("New url added: " + repoUrls.size());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Url not valid: " + url);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            getUrlsFromSearchResults(url);
        }
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(3600 + random.nextInt(1000));
    }


}
