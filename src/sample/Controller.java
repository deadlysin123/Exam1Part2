package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.net.URL;
import java.sql.*;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private JFXTextField minValue;
    @FXML
    private JFXTextField maxValue;
    @FXML
    private JFXButton runResult;
    @FXML
    private JFXButton loadData;
    @FXML
    private Label resultLabel;
    @FXML
    private JFXListView resultListView;
    @FXML
    private JFXSnackbar notificationBar;

    final String hostname = "tommy.heliohost.org";
    final String dbName = "ultimazs_mainDB";
    final String port = "3306";
    final String username = "ultimazs";
    final String password = "Ultima123";
    final String HelioHost_URL = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + username + "&password=" +password;


    //Generate random numbers between the input values and ttore the generated number into the Table NUMBERS on an online Database
    private void generateRandomNumber()
    {
        try {

            //convert String input into integer values
            int minNum = Integer.parseInt(minValue.getText());
            int maxNum = Integer.parseInt(maxValue.getText());
            int result;
            Random random = new Random();

            //call random function with the range between max and min value then display the result to the label
            result = random.nextInt(maxNum-minNum) + minNum;
            String label = ("The random number between " + minNum + " and " + maxNum + " is: " +result);
            resultLabel.setText(label);
            System.out.println(label);

            //connect to the DB and store the generated number
            Connection conn = DriverManager.getConnection(HelioHost_URL);
            String sql = "INSERT INTO NUMBERS (Number) VALUE (?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1,result);
            statement.executeUpdate();

            System.out.println("Number successfully stored!");
            statement.close();
            conn.close();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            System.out.println("Data not loaded");
            System.out.println(msg);
        }

    }

    //Load the random numbers that were stored in the database
    private void loadNumber()
    {
        try{
            Connection conn = DriverManager.getConnection(HelioHost_URL);
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT * FROM NUMBERS";
            ResultSet result = stmt.executeQuery(sqlStatement);
            ObservableList<RandomNumber> numberList = FXCollections.observableArrayList();
            while (result.next())
            {
                RandomNumber r = new RandomNumber();
                r.ranNumber = result.getInt("Number");

                numberList.add(r);
            }

            resultListView.setItems(numberList);

            System.out.println("DATA LOADED");
            stmt.close();
            conn.close();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            System.out.println("DATA NOT LOADED");
            System.out.println(msg);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //call the generate random number function when the button clicked
        runResult.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                generateRandomNumber();
            }
        });

        //Load the stored values from the database
        loadData.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadNumber();
            }
        });
    }
}
