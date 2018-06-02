package gmsis.diagrep.components;

import gmsis.diagrep.DiagRepModule;
import gmsis.models.User;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class HeaderPane extends Pane {

    private DoubleProperty headerWidthProperty;
    private DoubleProperty offsetXProperty;
    private HeaderCallback callback;

    public HeaderPane(HeaderCallback headerCallback, int numHeaders, int numVisibleHeaders) {
        getStyleClass().add("header");

        headerWidthProperty = new SimpleDoubleProperty();
        headerWidthProperty.set(widthProperty().get());

        offsetXProperty = new SimpleDoubleProperty();
        offsetXProperty.set(0);

        this.callback = headerCallback;

        getChildren().clear();

        for (int i = 0; i < numHeaders; i++) {
            getChildren().add(createHeaderSection(i, numVisibleHeaders));
        }

    }

    private Node createHeaderSection(int position, int numVisibleHeaders) {
        VBox header = new VBox();
        header.setId("header-"+position);
        header.getStyleClass().add("header-cell");

        Text headerDayLabel = new Text();
        headerDayLabel.setText(callback.getHeaderText(position));
        headerDayLabel.getStyleClass().add("header-title");

        Text headerSubLabel = new Text();
        headerSubLabel.setText(callback.getHeaderSubText(position));

        header.getChildren().addAll(headerDayLabel, headerSubLabel);

        header.prefWidthProperty().bind(headerWidthProperty.divide(numVisibleHeaders));
        header.layoutXProperty().bind(headerWidthProperty.divide(numVisibleHeaders).multiply(position).add(offsetXProperty));

        return header;
    }

    public DoubleProperty headerWidthProperty() {
        return headerWidthProperty;
    }

    public DoubleProperty offsetXProperty() {
        return offsetXProperty;
    }

    public static HeaderPane createDateHeader(final LocalDate startDate, final List<DiagRepModule.Holiday> holidays, String dateFormatString) {
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateFormatString);

        return new HeaderPane(new HeaderCallback() {
            @Override
            public String getHeaderText(int headerNum) {
                return dateFormat.format(startDate.plusDays(headerNum));
            }

            @Override
            public String getHeaderSubText(int headerNum) {
                if(holidays == null) return "";

                Optional<DiagRepModule.Holiday> todaysHoliday = holidays.stream()
                        .filter(holiday -> startDate.plusDays(headerNum).equals(holiday.getDate()))
                        .findFirst();

                return todaysHoliday.isPresent() ? todaysHoliday.get().getName() : "";
            }
        }, 7, 7);
    }

    public static HeaderPane createMechanicHeader(final LocalDate date, final List<User> mechanics, String dateFormatString) {
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateFormatString);

        return new HeaderPane(new HeaderCallback() {
            @Override
            public String getHeaderText(int headerNum) {
                return dateFormat.format(date);
            }

            @Override
            public String getHeaderSubText(int headerNum) {
                User mechanic = mechanics.get(headerNum);

                return mechanic.getFirstname() + " " + mechanic.getSurname();
            }
        }, mechanics.size(), 7);
    }

    public interface HeaderCallback {
        String getHeaderText(int headerNum);

        String getHeaderSubText(int headerNum);
    }
}
