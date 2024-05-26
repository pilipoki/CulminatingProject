package culminating;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.TableColumn.CellEditEvent;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private static TableView<Person> table = new TableView<>();
    final ObservableList<Person> data = FXCollections.observableArrayList();
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(new Group());
        // set title of stage
        stage.setTitle("Darie's Contacts App");
        stage.setWidth(930);
        stage.setHeight(540);

        // create title
        final Label label = new Label("Contacts");
        label.setFont(new Font("Arial", 20));

        // letting it be editable
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // add 5 columns with 5 attributes of data for a particular contact

        // set firstname
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        // sset width
        firstNameCol.setMinWidth(75);
        // set data column
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("firstName"));
        // allows the name to be edited
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setOnEditCommit(event -> {
            Person person = event.getRowValue();
            String oldFirstName = person.getFirstName();
            person.setFirstName(event.getNewValue());
            updateContactInCSV(person, oldFirstName);

        });

        // same logic as firstname
        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("lastName"));
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(event -> {
            Person person = event.getRowValue();
            String oldLastName = person.getLastName();
            person.setLastName(event.getNewValue());
            updateContactInCSV(person, oldLastName);

        });

        // same logic as firstname, included alert
        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("email"));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setOnEditCommit(event -> {
            Person person = event.getRowValue();
            String oldEmail = person.getEmail();
            // checks to see if it is a valid email when editing by using the matcher
            // it checks for characters followed by an @ followed by characters a . and more
            // chracters
            if (isValidEmail(event.getNewValue())) {
                person.setEmail(event.getNewValue());
                updateContactInCSV(person, oldEmail);
                // if the conditions are not met the user gets an error message and has to put
                // in valid information

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Not a valid email adress");
                alert.showAndWait();
            }

        });


        // same logic as firstname, but also checks to make sure it is a valid 10 digit
        // phone number
        TableColumn<Person, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setMinWidth(200);
        phoneCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("phoneNumber"));
        phoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneCol.setOnEditCommit(event -> {
            Person person = event.getRowValue();
            String oldPhoneNum = person.getPhoneNumber();
            if (isValidPhoneNumber(event.getNewValue())) {
                person.setPhoneNumber(event.getNewValue());
                updateContactInCSV(person, oldPhoneNum);

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Not a valid North American phone number");
                alert.showAndWait();
            }

        });

        // same logic as firstname, but also checks to make sure it is a valid postal
        // code
        TableColumn<Person, String> postalCol = new TableColumn<>("Postal Code");
        postalCol.setMinWidth(200);
        postalCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("postalCode"));
        postalCol.setCellFactory(TextFieldTableCell.forTableColumn());
        postalCol.setStyle("-fx-background-color: #A2A2A2;");
        postalCol.setOnEditCommit(event -> {
            Person person = event.getRowValue();
            String oldPostalCode = person.getPostalCode();
            if (isValidPostalCode(event.getNewValue())) {
                person.setPostalCode(event.getNewValue());
                updateContactInCSV(person, oldPostalCode);

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Not a valid North American postal code");
                alert.showAndWait();
            }

        });

        // takes the values for the csv and puts them into the observable list which
        // updates the tableview

        populateTableFromCSV(
                "/Users/darie/OneDrive/Documents/GitHub/CulminatingProject/src/main/java/culminating/contactList.csv");

        // add data to the table, tableview automaticly updates when data changes
        table.setItems(data);
        // add to the root.
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol, postalCol, phoneCol);

        // create the vBox to add table to screen
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        // create a box to hold text fields for when the user wants to add imput
        HBox inputBox = new HBox();
        inputBox.setSpacing(10);

        // text field allows them to click and add the info they want

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        TextField postalField = new TextField();
        postalField.setPromptText("Postal Code");

        // create an add contact button
        Button contactButton = new Button("Add Contact");
        contactButton.setMinWidth(100);

        // contact button gets all the values from the textfields and first checks the
        // email,postal code and phone number
        // if the phone number,postal code and email are valid, it will create a person
        // using the add contact method

        contactButton.setOnAction(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String phoneNum = phoneField.getText();
            String postalCode = postalField.getText();

            if (isValidEmail(email) && isValidPhoneNumber(phoneNum) && isValidPostalCode(postalCode)) {
                addContact(firstName, lastName, email, postalCode, phoneNum);
                firstNameField.clear();
                lastNameField.clear();
                emailField.clear();
                phoneField.clear();
                postalField.clear();

            } else {
                // Display an alert for invalid email,postal code or phone number format
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                if (!isValidEmail(email)) {
                    alert.setContentText("Please enter a valid email address.");
                } else if (!isValidPhoneNumber(phoneNum)) {
                    alert.setContentText("Please enter a valid 10-digit phone number.");
                } else if (!isValidPostalCode(postalCode)) {
                    alert.setContentText("Please enter a valid postal code.");
                }
                alert.showAndWait();
            }

        });

        // creates a delete button that deletes a contact when you click on it

        Button deleteButton = new Button("Delete");
        deleteButton.setMinWidth(40);
        deleteButton.setMinHeight(20);
        deleteButton.setLayoutX(850);
        deleteButton.setLayoutY(10);
        deleteButton.setOnAction(e -> {
            Person selectedPerson = table.getSelectionModel().getSelectedItem();
            if (selectedPerson != null) {
                // removes them from the table
                table.getItems().remove(selectedPerson);
                // removes them from the csv file
                removeContact(selectedPerson);
            }
        });

        // adds the textfield and addcontact button to the vbox
        inputBox.getChildren().addAll(firstNameField, lastNameField, emailField, postalField, phoneField,
                contactButton);
        vbox.getChildren().addAll(inputBox);

        ((Group) scene.getRoot()).getChildren().addAll(vbox, deleteButton);
        stage.setScene(scene);
        stage.show();
    }

    // takes the csv and breaks it into lines, each line is then broken into 5 parts
    // in an array
    // they are speparated by commas and with the 5 parts a person is added
    private void populateTableFromCSV(String contactList) {
        try (BufferedReader br = new BufferedReader(new FileReader(contactList))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Person person = new Person(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    data.add(person);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // add contact method from input
    private void addContact(String firstName, String lastName, String email, String postalCode, String phoneNum) {
        // takes the input and puts it into the obseravble list
        Person newPerson = new Person(firstName, lastName, email, postalCode, phoneNum);
        data.add(newPerson);
        // use buffered writer to append the new person into the csv by putting the
        // input separated by commas
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
                "/Users/darie/OneDrive/Documents/GitHub/CulminatingProject/src/main/java/culminating/contactList.csv",
                true)))) {
            writer.print("\n"+firstName + "," + lastName + "," + email + "," + postalCode + "," + phoneNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // remove contact method to delete from csv
    private void removeContact(Person person) {
        try {
            // turns the csv into an array list
            List<String> lines = Files.readAllLines(
                    Paths.get(
                            "/Users/darie/OneDrive/Documents/GitHub/CulminatingProject/src/main/java/culminating/contactList.csv"));
            List<String> updatedLines = new ArrayList<>();
            // breaks each line into parts based on commas and checks to see if it doesn't
            // match the deleted person
            // if it doesnt match the person goes into the list that will be re-written into
            // the csv
            // if it does match the person does no go into the updated csv and is deleted
            for (String line : lines) {
                String[] parts = line.split(",");
                if (!(parts[0].equals(person.getFirstName()) &&
                        parts[1].equals(person.getLastName()) &&
                        parts[2].equals(person.getEmail()) &&
                        parts[3].equals(person.getPostalCode()) &&
                        parts[4].equals(person.getPhoneNumber()))) {
                    updatedLines.add(line);
                }
            }
            // updates the csv excluding the deleted user
            Files.write(Paths.get(
                    "/Users/darie/OneDrive/Documents/GitHub/CulminatingProject/src/main/java/culminating/contactList.csv"),
                    updatedLines);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // updates the csv when editing a contact
    public void updateContactInCSV(Person updatedPerson, String oldData) {
        try {
            // turns the csv into an array list
            List<String> lines = Files.readAllLines(
                    Paths.get(
                            "/Users/darie/OneDrive/Documents/GitHub/CulminatingProject/src/main/java/culminating/contactList.csv"));
            List<String> updatedLines = new ArrayList<>();
            // gets the new contact info
            String updatedLine = updatedPerson.getFirstName() + "," + updatedPerson.getLastName() + ","
                    + updatedPerson.getEmail() + "," + updatedPerson.getPostalCode() + ","
                    + updatedPerson.getPhoneNumber();
            // breaks the csv into lines and parts and looks for if a part matches the
            // oldData which was passed in
            for (String line : lines) {
                String[] lineParts = line.split(",");
                // used to check if the current line was updated
                boolean updated = false;
                // if the current part matches the old data we add the updated line into the csv
                // and exclude the old line
                for (int i = 0; i < lineParts.length; i++) {
                    if (lineParts[i].equals(oldData)) {
                        updatedLines.add(updatedLine);
                        updated = true;
                        break;
                    }
                }
                // if the line is not updated add original line
                if (!updated) {
                    updatedLines.add(line);
                }
            }
            // update the csv
            Files.write(Paths.get(
                    "/Users/darie/OneDrive/Documents/GitHub/CulminatingProject/src/main/java/culminating/contactList.csv"),
                    updatedLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // checks if passed in email matches the correct email pattern
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // checks if passed in email matches the correct 10 digit North American number
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    // checks if passed in postal code matches the Canadian postal code pattern
    private boolean isValidPostalCode(String postalCode) {
        // Canadian postal code pattern: A1A 1A1 or A1A1A1
        String postalCodeRegex = "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$";
        return postalCode.matches(postalCodeRegex);
    }

    // create the Person Class to define data in an address book
    public static class Person {
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty email;
        private final SimpleStringProperty postalCode;
        private final SimpleStringProperty phoneNumber;

        private Person(String fName, String lName, String email, String postalCode, String phoneNumber) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.email = new SimpleStringProperty(email);
            this.postalCode = new SimpleStringProperty(postalCode);
            this.phoneNumber = new SimpleStringProperty(phoneNumber);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String fName) {
            email.set(fName);
        }

        public String getPostalCode() {
            return postalCode.get();
        }

        public void setPostalCode(String fName) {
            postalCode.set(fName);
        }

        public String getPhoneNumber() {
            return phoneNumber.get();
        }

        public void setPhoneNumber(String fName) {
            phoneNumber.set(fName);
        }

    }

    public static void main(String[] args) {
        launch();
    }

}