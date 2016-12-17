package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;
import main.BotThread;
import main.Order;
import main.PaymentInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.*;

// TODO: 9/25/16 do not allow improper payment infos
// TODO: 10/9/2016 Set the tab order for the UI
public class MainController implements Initializable {

    //fixed number of threads
    // TODO: 10/9/2016 Handle cases where user submits too many threads?
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(15);

    static ArrayList<PaymentInfo> paymentInfos = new ArrayList<>();

    private static final String BUTTON_STYLE_HOVER = "-fx-text-fill: red;\n " +
            "-fx-background-color: white";

    private static final String BUTTON_STYLE_PRESSED = "-fx-background-color: red;\n" +
            "-fx-text-fill: white";
    private static final String BUTTON_STYLE_DEFAULT = "-fx-background-color: black;\n" +
            "-fx-border-color: white;\n" +
            "-fx-text-fill: white;\n";

    boolean isMouseOnButton = false;

    @FXML
    Button submit;
    @FXML
    ListView listView;
    @FXML
    ComboBox savedPaymentInfos;
    @FXML
    ComboBox categories;
    @FXML
    TextField keyword;
    @FXML
    TextField color;
    @FXML
    TextField size;
    @FXML
    DatePicker datePicker;
    @FXML
    TextField timeToFire;
    @FXML
    RadioButton radioAM;
    @FXML
    RadioButton radioPM;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AddPaymentInfoController.mainController = this;
        categories.getItems().addAll(Order.Category.values());

        try {
            loadSavedPaymentInfos();
        } catch (Exception e) {
            e.printStackTrace();
            reportError(e.getLocalizedMessage());
            terminateApp();
        }
    }

    public static void reportError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred:\n " +
                error, ButtonType.OK);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.showAndWait();
    }

    public void handleSubmitButtonReleased() {
        if (isMouseOnButton) {
            submit.setStyle(BUTTON_STYLE_HOVER);
            submitNewOrder();
        } else {
            submit.setStyle(BUTTON_STYLE_DEFAULT);
        }
    }

    public Callable<Integer> botProcess(Order order) {
        return () -> {
            BotThread bot = new BotThread(order, 3);
            bot.run();
            listView.refresh();
            return 0;
        };
    }

    private void submitNewOrder() {
        Order order;
        try {
            order = new Order(getCategory(), getKeyword(), getColor(), getSize(),
                    getPaymentInfo());
        } catch (IllegalArgumentException e) {
            reportError("Failed to create new shop order: " + e.getLocalizedMessage());
            return;
        }
        scheduler.schedule(botProcess(order), getTimeToFire(), TimeUnit.MILLISECONDS);
        listView.getItems().add(order);
    }

    private void terminateApp() {
        scheduler.shutdownNow();
        Platform.exit();
        System.exit(0);
    }

    public void loadSavedPaymentInfos() throws GeneralSecurityException, IOException {

        // TODO: 10/8/2016 get the combobox to observe the list so that you only have to update the list
        savedPaymentInfos.getItems().removeAll(paymentInfos);
        paymentInfos.clear();

        final Scanner       s           = new Scanner(new File("profiles"));
        final StringBuilder sb          = new StringBuilder();
        int                 lineCounter = 0;
        String[]            fields      = new String[PaymentInfo.NUMBER_OF_FIELDS];

        while(s.hasNextLine()) {
            try {
                fields[lineCounter] = s.nextLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            lineCounter++;
            if (lineCounter == fields.length) {
                paymentInfos.add(new PaymentInfo(fields));
                sb.setLength(0);
                lineCounter =0;
            }
        }

        savedPaymentInfos.getItems().addAll(paymentInfos);
    }

    public long getTimeToFire() {
        Instant now = Instant.now();
        Instant instantToFire = datePicker.getValue().atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        String[] hoursMinutesSecondsMillis = timeToFire.getText().split(":");

        long hours = Long.parseLong(hoursMinutesSecondsMillis[0]);
        long minutes = Long.parseLong(hoursMinutesSecondsMillis[1]);
        long seconds = Long.parseLong(hoursMinutesSecondsMillis[2]);
        long millis = Long.parseLong(hoursMinutesSecondsMillis[3]);

        if (radioAM.isSelected()) {
            instantToFire = instantToFire.plus((hours == 12) ? 0L : hours, ChronoUnit.HOURS);
        }
        else {
            instantToFire = instantToFire.plus((hours == 12) ? 12L : hours + 12L, ChronoUnit.HOURS);
            instantToFire = instantToFire.plus(minutes, ChronoUnit.MINUTES);
            instantToFire = instantToFire.plus(seconds, ChronoUnit.SECONDS);
            instantToFire = instantToFire.plus(millis, ChronoUnit.MILLIS);
        }

        return Duration.between(now, instantToFire).toMillis();
    }

    public Order.Category getCategory() {
        return (Order.Category) categories.getSelectionModel().getSelectedItem();
    }

    public String getKeyword() {
        return keyword.getText();
    }

    public String getColor() {
        return color.getText();
    }

    public String getSize() {
        return size.getText();
    }

    public PaymentInfo getPaymentInfo() {
        return (PaymentInfo) savedPaymentInfos.getSelectionModel().getSelectedItem();
    }

    public void handleSubmitButtonPressed() {
        submit.setStyle(BUTTON_STYLE_PRESSED);
    }

    public void handleMouseEnterSubmitButton() {
        isMouseOnButton = true;
        submit.setStyle(BUTTON_STYLE_HOVER);
    }

    public void handleMouseExitSubmitButton() {
        isMouseOnButton = false;
        submit.setStyle(BUTTON_STYLE_DEFAULT);
    }

    public void addPaymentInfo() throws IOException {
        createWindow("paymentinfo.fxml");
    }

    public void deleteSavedPaymentInfo() {
        try {
            FileUtils.write(new File("profiles"), "");
        } catch (IOException e) {
            reportError("An error occurred while deleting the saved payment information");
        }
        savedPaymentInfos.getItems().removeAll(paymentInfos);
        paymentInfos.clear();
    }

    public void createWindow(String resourceName) throws IOException {
        // TODO: 10/6/2016 Figure out window modality and enforce proper logic flow within secondary windows.
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(
                MainController.class.getResource(resourceName));
        stage.setScene(new Scene(root));
        stage.setTitle("My modal window");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

}
