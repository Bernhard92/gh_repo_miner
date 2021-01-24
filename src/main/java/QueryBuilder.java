import lombok.Builder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Builder(toBuilder = true)
public class QueryBuilder {

    private static String BASE_PATH = "https://github.com/search?l=&o=desc&s=stars&type=Repositories";
    private final String language;
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;
    private final int page;


    public URL getUrl() {
        try {
            return new URL(BASE_PATH + getPage() + getQuery());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the given date in the form YYYY-MM-DDTHH:MM:SS
     *
     * @param dateTime
     * @return date time as String
     */
    private String getFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return dateTime.format(formatter);
    }

    private String getPage() {
        return String.format("&p=%d", page);
    }

    private String getQuery() {
        String languageQuery = language != null ? "language:" + language : "";
        String dateQuery = "";
        if (fromDateTime != null && toDateTime != null) {
            dateQuery = "+created:" + getFormattedDate(fromDateTime) +
                    ".." + getFormattedDate(toDateTime);
        } else if (fromDateTime != null) {
            dateQuery = "+created:" + getFormattedDate(fromDateTime);
        } else if (toDateTime != null) {
            dateQuery = "+created:.." + getFormattedDate(toDateTime);
        }
        return "&q=" + languageQuery + dateQuery;
    }
}
