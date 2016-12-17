package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;
import main.PaymentInfo;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by faahmed on 9/23/16.
 */
public class AddPaymentInfoController implements Initializable {

    @FXML
    TextField name;
    @FXML
    TextField email;
    @FXML
    TextField tel;
    @FXML
    TextField address;
    @FXML
    TextField zip;
    @FXML
    TextField city;
    @FXML
    TextField state;
    @FXML
    TextField country;
    @FXML
    TextField cardType;
    @FXML
    TextField number;
    @FXML
    TextField expMonth;
    @FXML
    TextField expYear;
    @FXML
    TextField cvv;

    static MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addChangeListener(name    );
        addChangeListener(email   );
        addChangeListener(tel     );
        addChangeListener(address );
        addChangeListener(zip     );
        addChangeListener(city    );
        addChangeListener(state   );
        addChangeListener(country );
        addChangeListener(cardType);
        addChangeListener(number  );
        addChangeListener(expMonth);
        addChangeListener(expYear );
        addChangeListener(cvv     );
    }

    public void confirmPaymentInfo() {
        String[] fields = new String[PaymentInfo.NUMBER_OF_FIELDS];
        int i = 0;
        fields[i++] = name.getText();
        fields[i++] = email.getText();
        fields[i++] = tel.getText();
        fields[i++] = address.getText();
        fields[i++] = zip.getText();
        fields[i++] = city.getText();
        fields[i++] = state.getText();
        fields[i++] = country.getText();
        fields[i++] = cardType.getText();
        fields[i++] = number.getText();
        fields[i++] = expMonth.getText();
        fields[i++] = expYear.getText();
        fields[i++] = cvv.getText();

        if (checkFields(fields)) return;

        writeProfileToFile(fields);

        reloadPaymentInfoDropdown();
    }

    private void reloadPaymentInfoDropdown() {
        try {
            mainController.loadSavedPaymentInfos();
        } catch (Exception e) {
            e.printStackTrace();
            MainController.reportError("Could not reload saved payment info.");
        }
    }

    private void writeProfileToFile(String[] fields) {
        try {
            FileUtils.writeStringToFile(new File("profiles"), getProfileString(fields), true);
        } catch (IOException e) {
            MainController.reportError("Could not save your profile.");
        }
    }

    private boolean checkFields(String[] fields) {
        try {
            new PaymentInfo(fields);
        } catch (IllegalArgumentException e) {
            reportInvalidFields();
            return true;
        }
        return false;
    }

    private String getProfileString(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PaymentInfo.NUMBER_OF_FIELDS; i++) {
            try {
                sb.append(fields[i]).append('\n');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void reportInvalidFields() {
        validate(name    );
        validate(email   );
        validate(tel     );
        validate(address );
        validate(zip     );
        validate(city    );
        validate(state   );
        validate(country );
        validate(cardType);
        validate(number  );
        validate(expMonth);
        validate(expYear );
        validate(cvv     );
    }

    private void validate(TextField tf) {
        ObservableList<String> styleClass = tf.getStyleClass();
        if (tf.getText().trim().length()==0) {
            if (! styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            styleClass.removeAll(Collections.singleton("error"));
        }
    }

    public void addChangeListener(TextField tf) {
        tf.textProperty().addListener((e) -> validate(tf));
    }
}
