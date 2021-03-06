package Controllers;

import DBManager.DBManager;
import Form.Form;
import Initialization.Main;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import static Form.StringParsing.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * Created by erickmoo on 4/29/17.
 */
public class applicationController extends UIController {

    private DBManager dbManager = new DBManager();
    private Form form = new Form();
    private int count = 0;

    @FXML
    private ComboBox source_combobox, alcohol_type_combobox;
    @FXML
    private TextField repID, permitNO, serialNO, brandName, fancifulName,
            alcoholContent, formula, phoneNo, email, applicantStreet, applicantCity,
            applicantState, applicantZip, applicantCountry, otherStreet, otherCity,
            otherState, otherZip, otherCountry, option_2_text, option_3_text, option_4_text,
            signature, grapeVarietals, wineAppellation, vintageYear, phLevel;
    @FXML
    private Button nextButton, backButton, browseButton;
    @FXML
    private CheckBox sameAsApplicantBox, option_1_checkbox, option_2_checkbox,
            option_3_checkbox, option_4_checkbox;
    @FXML
    private ImageView label_image;
    @FXML
    private TextArea extraLabelInfo;
    public Label otherCityLabel;
    public Label otherStateLabel;
    public Label otherZipcodeLabel;
    public Label otherCountryLabel;
    public Label otherStreetLabel;
    public Label errorLabel, repIDError, permitError, serialError;

    void start(Main main, int count, Form form) {
        this.main = main;
        setCount(count);
        setForm(form);
        if(count == 0) {
            source_combobox.setItems(FXCollections.observableArrayList("Imported", "Domestic"));
            alcohol_type_combobox.setItems(FXCollections.observableArrayList("Malt Beverages", "Wine", "Distilled Spirits"));
        }
    }

    private void setCount(int count) {
        this.count = count;
    }

    private void setForm(Form form) {
        this.form = form;
    }

    @FXML
    public void changePageBack() throws IOException{
        boolean temp;
        System.out.println("Count before: " + count);
        switch (count) {
            case 0: break;/*temp = storePage1();
                    if (temp) {
                        break;
                    } else {
                        errorLabel.setText("Please enter required fields");
                        return;
                    }*/
            case 1: break;/*temp = storePage2();
                if (temp) {
                    break;
                } else {
                    errorLabel.setText("Please enter required fields");
                    return;
                }*/
            case 2: storePage3(); break;
            case 3: storePage4(); break;
            case 4: if(form.getalcohol_type().equals("Wine")) {storeWinePage();} break;
        }
        Stage stage;
        Button button = backButton;
        count--;
        System.out.println("Count after: " + count);
        stage=(Stage) button.getScene().getWindow();
        String pageName = "";
        switch (count) {
            case -1: pageName = "mainPage.fxml"; break;
            case 0: pageName = "applicationPage1.fxml"; break;
            case 1: pageName = "applicationPage2.fxml"; break;
            case 2: pageName = "applicationPage3.fxml"; break;
            case 3: pageName = "applicationPage4.fxml"; break;
            case 4:
                if(form.getalcohol_type().equals("Wine")) {pageName = "applicationPageWine.fxml";}
                else {pageName = "printableVersion.fxml";}
                break;
        }
        System.out.println("Change page to: " + pageName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/" + pageName));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        if(count < 0) {
            mainPageController controller = loader.getController();
            controller.start(this.main);
            return;
        }
        applicationController controller = loader.getController();
        controller.start(this.main, count, form);
        System.out.println(loader.getLocation());
        switch (count) {
            case 0: controller.createPage1(); break;
            case 1: controller.createPage2(); break;
            case 2: controller.createPage3(); break;
            case 3: controller.createPage4(); break;
            case 4: if(form.getalcohol_type().equals("Wine")) {controller.createWinePage();} break;
        }
    }

    @FXML
    public void changePageNext() throws IOException{
        boolean temp;
        System.out.println("Count before: " + count);
        if (!isValid()) {
            System.out.println("There is an error");
            displayValidityError();
            removeValidityError();
            return;
        }
        switch (count) {
            case 0: temp = storePage1();
                if (temp) {
                    break;
                } else {
                    errorLabel.setText("Please enter required fields");
                    return;
                }
            case 1: temp = storePage2();
                if (temp) {
                    break;
                } else {
                    errorLabel.setText("Please enter required fields");
                    return;
                }
            case 2:
                if(!option_1_checkbox.isSelected() && !option_2_checkbox.isSelected() &&
                        !option_3_checkbox.isSelected() && !option_4_checkbox.isSelected()) {
                    errorLabel.setText("No option selected");
                    errorLabel.setVisible(true);
                    return;
                }
                if(!((option_1_checkbox.isSelected() && !option_2_checkbox.isSelected() &&
                        !option_3_checkbox.isSelected() && !option_4_checkbox.isSelected()) ||
                        (option_2_checkbox.isSelected() && !option_1_checkbox.isSelected() &&
                                !option_3_checkbox.isSelected() && !option_4_checkbox.isSelected()) ||
                        option_3_checkbox.isSelected() && !option_2_checkbox.isSelected() &&
                                !option_1_checkbox.isSelected() && !option_4_checkbox.isSelected() ||
                        option_4_checkbox.isSelected() && !option_2_checkbox.isSelected() &&
                                !option_3_checkbox.isSelected() && !option_1_checkbox.isSelected())) {
                    errorLabel.setText("Multiple options selected");
                    errorLabel.setVisible(true);
                    return;
                }
                storePage3();
                break;
            case 3: storePage4(); break;
            case 4: if(form.getalcohol_type().equals("Wine")) {storeWinePage();} break;
        }
        Stage stage;
        Button button = nextButton;
        count++;
        System.out.println("Count after: " + count);
        stage=(Stage) button.getScene().getWindow();
        String pageName = "";
        switch (count) {
            case -1: pageName = "mainPage.fxml"; break;
            case 0: pageName = "applicationPage1.fxml"; break;
            case 1: pageName = "applicationPage2.fxml"; break;
            case 2: pageName = "applicationPage3.fxml"; break;
            case 3: pageName = "applicationPage4.fxml"; break;
            case 4:
                if(form.getalcohol_type() == null) {
                    pageName = "applicationPage1.fxml";
                    count = 0;
                }
                else if(form.getalcohol_type().equals("Wine")) {pageName = "applicationPageWine.fxml";}
                else {pageName = "printableVersion.fxml";}
                    break;
            case 5: pageName = "printableVersion.fxml"; break;
        }
        System.out.println("Change page to: " + pageName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/" + pageName));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        if(count < 0) {
            mainPageController controller = loader.getController();
            controller.start(this.main);
            return;
        }
        if(pageName.equals("printableVersion.fxml")) {
            Form newForm = submitForm(form);
            printableVersionController controller = loader.getController();
            controller.start(this.main, newForm);
            return;
        }
        applicationController controller = loader.getController();
        controller.start(this.main, count, form);
        switch (count) {
            case 0: controller.createPage1(); break;
            case 1: controller.createPage2(); break;
            case 2: controller.createPage3(); break;
            case 3:
                controller.createPage4();
                if(!form.getalcohol_type().equals("Wine")) {controller.nextButton.setText("Submit");}
                break;
            case 4: if(form.getalcohol_type().equals("Wine")) {controller.createWinePage();} break;
        }
    }

    //Sets the textfields for page 1 based on what the user previously entered
    public void createPage1() {
        if (form.getSource() != null) {
            if (form.getSource().equals("Imported")) {
                source_combobox.setValue("Imported");
            } else {
                source_combobox.setValue("Domestic");
            }
        }
        if (form.getalcohol_type() != null) {
            if (form.getalcohol_type().equals("Wine")) {
                alcohol_type_combobox.setValue("Wine");
            } else if (form.getalcohol_type().equals("Malt Beverages")) {
                alcohol_type_combobox.setValue("Malt Beverages");
            } else {
                alcohol_type_combobox.setValue("Distilled Spirits");
            }
        }
        setTextFields(form.getrep_id(), repID);
        setTextFields(form.getpermit_no(), permitNO);
        setTextFields(form.getserial_no(), serialNO);
        setTextFields(form.getbrand_name(), brandName);
        setTextFields(form.getfanciful_name(), fancifulName);
        setTextFields(Double.toString(form.getalcohol_content()), alcoholContent);
        setTextFields(form.getformula(), formula);
    }

    //Is called when the user presses back or next to save the values in the textfields of page 1
    public boolean storePage1() {
        boolean temp = true;
        form.setSource((String)source_combobox.getValue());
        form.setalcohol_type((String)alcohol_type_combobox.getValue());
        form.setrep_id(repID.getText());
        form.setpermit_no(permitNO.getText());
        form.setserial_no(serialNO.getText());
        form.setbrand_name(brandName.getText());
        form.setfanciful_name(fancifulName.getText());
        if (alcoholContent.getText() != null) {
            form.setalcohol_content(Double.parseDouble(alcoholContent.getText()));
        }
        form.setFormula(formula.getText());
        if (brandName.getText().equals("") || source_combobox.getValue() == null || alcohol_type_combobox.getValue() == null) {
            temp = false;
        }

        return temp;
    }

    public void createPage2() {
        setTextFields(form.getphone_no(), phoneNo);
        setTextFields(form.getEmail(), email);
        setTextFields(form.getapplicant_street(), applicantStreet);
        setTextFields(form.getapplicant_city(), applicantCity);
        setTextFields(form.getapplicant_state(), applicantState);
        setTextFields(form.getapplicant_zip(), applicantZip);
        setTextFields(form.getapplicant_country(), applicantCountry);
        if(form.getmailing_address() != null && !form.getmailing_address().isEmpty()) {
            sameAsApplicantBox.setSelected(true);
            showSecondAddress();
            String[] otherAddress = form.getmailing_address().split(" ");
            if (otherAddress[0] != null) {
                setTextFields(otherAddress[0], otherStreet);
            }
            if (otherAddress[1] != null) {
                setTextFields(otherAddress[1], otherCity);
            }
            if (otherAddress[2] != null) {
                setTextFields(otherAddress[2], otherState);
            }
            if (otherAddress[3] != null) {
                setTextFields(otherAddress[3], otherZip);
            }
            if (otherAddress[4] != null) {
                setTextFields(otherAddress[4], otherCountry);
            }
        }
    }

    public boolean storePage2() {
        boolean temp = true;
        form.setphone_no(phoneNo.getText());
        form.setEmail(email.getText());
        form.setapplicant_street(applicantStreet.getText());
        form.setapplicant_city(applicantCity.getText());
        form.setapplicant_state(applicantState.getText());
        form.setapplicant_zip(applicantZip.getText());
        form.setapplicant_country(applicantCountry.getText());

        if (!sameAsApplicantBox.isSelected()) {
            form.setmailing_address("");
        } else {
            form.setmailing_address(otherStreet.getText() + " " + otherCity.getText() + " " + otherState.getText()
                    + " " + otherZip.getText() + " " + otherCountry.getText());
        }

        if (applicantStreet.getText().equals("") || applicantState.getText().equals("") ||
                applicantCity.getText().equals("") || applicantZip.getText().equals("") ||
                applicantCountry.getText().equals("")) {
            temp = false;
        }

        return temp;
    }

    public void createPage3() {
        ArrayList<Boolean> application_type;
        ArrayList<String> application_type_text;
        if (form.getapplication_type() != null) {
            application_type = form.getapplication_type();
        } else {
            application_type = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                application_type.add(false);
            }
        }
        if (form.getapplication_type() != null) {
            application_type_text = form.getapplication_type_text();
        } else {
            application_type_text = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                application_type_text.add("hello");
            }
        }
        if (application_type.get(0)) {//choice 0
            option_1_checkbox.setSelected(true);
        } else if (application_type.get(1)) {
            option_2_text.setText(application_type_text.get(0));
            option_2_checkbox.setSelected(true);
        } else if (application_type.get(2)) {
            option_3_text.setText((application_type_text.get(1)));
            option_3_checkbox.setSelected(true);
        } else if (application_type.get(3)) {
            option_4_text.setText((application_type_text.get(2)));
            option_4_checkbox.setSelected(true);
        }
    }

    public void storePage3() {
        ArrayList<Boolean> application_type = new ArrayList<Boolean>();
        for (int i = 0; i < 5; i++) {
            application_type.add(false);
        }
        ArrayList<String> application_type_text = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            application_type_text.add("hello");
        }
        if (option_1_checkbox.isSelected()) {//choice 0
            application_type.set(0, true);
        } else if (option_2_checkbox.isSelected()) {
            application_type_text.set(0, option_2_text.getText());
            application_type.set(1, true);
        } else if (option_3_checkbox.isSelected()) {
            application_type_text.set(1, option_3_text.getText());
            application_type.set(2, true);
        } else if (option_4_checkbox.isSelected()) {
            application_type_text.set(2, option_4_text.getText());
            application_type.set(3, true);
        }
        System.out.println(application_type);
        System.out.println(application_type_text);
        form.setapplication_type(application_type);
        form.setapplication_type_text(application_type_text);
    }

    public void createPage4() {
        extraLabelInfo.setText(form.getlabel_text());
        setTextFields(form.getSignature(), signature);
        if (form.getlabel_image() != null) {
            try {
                String path = (System.getProperty("user.dir") + "/src/resources/images/" + form.getlabel_image());
                System.out.println(path);
                File file = new File(path);
                String localURL = file.toURI().toURL().toString();
                Image image = new Image(localURL);
                System.out.println("Image loaded");
                label_image.setImage(image);
                System.out.println("displaying image");
                label_image.setFitHeight(239);
                label_image.setFitWidth(286);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void storePage4() {
        form.setlabel_text(extraLabelInfo.getText());
        form.setSignature(signature.getText());
    }

    // Only display this if "Wine" was selected as the type of alcohol
    public void createWinePage() {
        setTextFields(form.getgrape_varietals(), grapeVarietals);
        setTextFields(form.getwine_appellation(), wineAppellation);
        setTextFields(form.getvintage_year(), vintageYear);
        setTextFields(Integer.toString(form.getpH_level()), phLevel);
    }

    public void storeWinePage() {
        form.setgrape_varietals(grapeVarietals.getText());
        form.setwine_appellation(wineAppellation.getText());
        form.setvintage_year(vintageYear.getText());
        form.setpH_level(Integer.parseInt(phLevel.getText()));
    }

    @FXML
    public void browseForFile() {
        FileChooser fc = new FileChooser();
        String currentDir = System.getProperty("user.dir");
//        System.out.println(currentDir);

        fc.setInitialDirectory(new File(currentDir));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files (.jpg .png)", "*.jpg", "*.png"));
        File selectedFile = fc.showOpenDialog(null);


        if (selectedFile != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                label_image.setImage(image);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid File");
        }

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

        String newFileName = selectedFile.getName().split("\\.")[0] + dateFormat.format(date) + "." + selectedFile.getName().split("\\.")[1];
        File destInSys = new File(System.getProperty("user.dir") + "/src/resources/images/" + newFileName);
        try {
            Files.copy(selectedFile.toPath(), destInSys.toPath(), StandardCopyOption.REPLACE_EXISTING, NOFOLLOW_LINKS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        form.setlabel_image(newFileName);
        try {
            System.out.println("here");
            String path = (System.getProperty("user.dir") + "/src/main/resources/Controllers/images/" + newFileName);
            File file = new File(path);
            String localURL = file.toURI().toURL().toString();
            Image image = new Image(localURL);
            System.out.println("Now here");
            System.out.println("down");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @FXML
    public void showSecondAddress() {
        System.out.println("Showing second address");
        if (sameAsApplicantBox.isSelected()) {
            otherCityLabel.setVisible(true);
            otherStateLabel.setVisible(true);
            otherStreetLabel.setVisible(true);
            otherZipcodeLabel.setVisible(true);
            otherCityLabel.setVisible(true);
            otherCountry.setVisible(true);
            otherCountryLabel.setVisible(true);
            otherZip.setVisible(true);
            otherState.setVisible(true);
            otherStreet.setVisible(true);
            otherCity.setVisible(true);
        } else {
            otherCityLabel.setVisible(false);
            otherStateLabel.setVisible(false);
            otherStreetLabel.setVisible(false);
            otherZipcodeLabel.setVisible(false);
            otherCityLabel.setVisible(false);
            otherCountry.setVisible(false);
            otherCountryLabel.setVisible(false);
            otherZip.setVisible(false);
            otherState.setVisible(false);
            otherStreet.setVisible(false);
            otherCity.setVisible(false);
        }
    }

    @FXML
    public void setTextFields(String string, TextField textfield) {
        if (string != null){
            textfield.setText(string);
        }
    }

    /**
     * Helper function for checking boxes that need validation
     * @return
     */
    private boolean isValid(){
        boolean temp = true;
        switch (count) {
            case 0:
                if (!repID.getText().equals("")) {
                    temp = (repIDValidation(repID.getText()) &&
                            permitValidation(permitNO.getText()) &&
                            serialValidation(serialNO.getText()));
                } else {
                    temp = (permitValidation(permitNO.getText()) &&
                            serialValidation(serialNO.getText()));
                }
                    break;
            case 1: temp =  (emailValidation(email.getText()) &&
                    phoneNmbrValidation(phoneNo.getText()));
                    break;
        }
        return temp;
    }

    @FXML
    private void displayValidityError(){
        String errorString = ""; //TODO: DO THIS!!!
        switch (count) {
            case 0:
                System.out.println("repID: " + repID.getText());
                if (!repID.getText().equals("")) {
                    if (!repIDValidation(repID.getText())) {
                        setRed(repID);
                        repIDError.setText("Format: #####");
                    }
                }
                if (!permitValidation(permitNO.getText())){
                    setRed(permitNO);
                    permitError.setText("Format: SS-SS-####");
                }
                if (!serialValidation(serialNO.getText())){
                    setRed(serialNO);
                    serialError.setText("Format: ##-####");
                }
                break;
            case 1: if (!emailValidation(email.getText())){setRed(email);}
                    if (!phoneNmbrValidation(phoneNo.getText())){setRed(phoneNo);}
                    break;
        }
        errorLabel.setText("Please correct mistakes highlighted in red\n");
    }

    @FXML
    private void removeValidityError(){
        switch (count) {
            case 0: if (repIDValidation(repID.getText()) || repID.getText().equals("")){removeRed(repID);}
                if (permitValidation(permitNO.getText())){removeRed(permitNO);}
                if (serialValidation(serialNO.getText())){removeRed(serialNO);}
                break;
            case 1: if (emailValidation(email.getText())){removeRed(email);}
                if (phoneNmbrValidation(phoneNo.getText())){removeRed(phoneNo);}
                break;
        }
        if ((repIDValidation(repID.getText()) || repID.getText().equals("")) && permitValidation(permitNO.getText()) && serialValidation(serialNO.getText())) {
            errorLabel.setText("");
        }
    }

    private void setRed(TextField text){
        text.setStyle("-fx-border-color:red");
    }

    private void removeRed(TextField text){
        text.setStyle("-fx-border-color:default");
    }

    private Form submitForm(Form form) {
        Form newForm = new Form(form.getrep_id(), form.getpermit_no(), form.getSource(), form.getserial_no(), form.getalcohol_type(),
                form.getbrand_name(), form.getfanciful_name(), form.getalcohol_content(), form.getapplicant_street(), form.getapplicant_city(), form.getapplicant_state(),
                form.getapplicant_zip(), form.getapplicant_country(), form.getmailing_address(), form.getformula(), form.getphone_no(),
                form.getEmail(), form.getlabel_text(), form.getlabel_image(), new Date(Calendar.getInstance().getTimeInMillis()), form.getSignature(), "Pending",
                null, main.getUser().getUid(), null, null, form.getvintage_year(), form.getpH_level(),
                form.getgrape_varietals(), form.getwine_appellation(), form.getapplication_type(), form.getapplication_type_text(), form.getapproval_comments());
        dbManager.persistForm(newForm);
        return newForm;
    }

}
