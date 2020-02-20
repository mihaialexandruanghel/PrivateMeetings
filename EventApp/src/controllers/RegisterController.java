package controllers;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import requests.RegisterRequest;
import response.RegisterResponse;
import utils.SocketClientCallable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegisterController {

    @FXML
    public TextField txtUsername;
    @FXML
    public TextField txtFirstname;
    @FXML
    public TextField txtLastname;
    @FXML
    public RadioButton availabilityButton;
    @FXML
    private Label lblErrors;
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtConfirmPassword;
    @FXML
    private Button btnRegister;

    public void registerButton(MouseEvent event) {
        if (event.getSource() == btnRegister) {
            // login here
            if (register().equals("Success")) {
                try {

                    // add you loading or delays - 😉
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    // stage.setMaximized(true);
                    stage.close();
                    Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Login.fxml")));
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
    // we gonna use string to check for status
    private String register() {
        String status = "Success";
        String userName = txtUsername.getText();
        String firstName = txtFirstname.getText();
        String lastName = txtLastname.getText();
        String email = txtEmail.getText();
        String confirmPassword = txtConfirmPassword.getText();
        Boolean availability = availabilityButton.isSelected();
        String password = txtPassword.getText();

        RegisterRequest registerUser = new RegisterRequest();
        registerUser.setUserName(userName);
        registerUser.setFirstName(firstName);
        registerUser.setPassword(password);
        registerUser.setEmail(email);
        registerUser.setLastName(lastName);
        registerUser.setAvailability(availability);

        if (firstName.isEmpty() || password.isEmpty() || email.isEmpty() || lastName.isEmpty() || confirmPassword.isEmpty() || userName.isEmpty()) {
            setLblError(Color.TOMATO, "Please fill all fields");
            status = "Error";
        } else {
            try {
                ExecutorService es = Executors.newCachedThreadPool();
                Gson gson = new Gson();
                String text = gson.toJson(registerUser);
                SocketClientCallable commandWithSocket =
                        new SocketClientCallable("127.0.0.1", 9001, "register", text);
                Future<String> response = es.submit(commandWithSocket);
                RegisterResponse serverResponse = gson.fromJson(response.get(), RegisterResponse.class);
                if (!"200".equalsIgnoreCase(serverResponse.getCode())) {
                    setLblError(Color.TOMATO, serverResponse.getMessage());
                    status = "Error";
                } else {
                    // DO NOTHING
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return status;
    }

    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
        System.out.println(text);
    }
}
