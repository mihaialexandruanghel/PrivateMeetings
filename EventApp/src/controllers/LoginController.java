package controllers;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import requests.LoginRequest;
import response.LoginResponse;
import utils.SocketClientCallable;
import utils.UserInfo;

public class LoginController {

  @FXML private Label lblErrors;

  @FXML private TextField txtUsername;

  @FXML private TextField txtPassword;

  @FXML private Button btnSignin;

  @FXML private Button btnSignup;

  @FXML
  public void handleButtonAction(MouseEvent event) throws IOException {

    if (event.getSource() == btnSignin) {
      // login here
      if (logIn().equals("Success")) {
        try {

          // add you loading or delays - ;-)
          Node node = (Node) event.getSource();
          Stage stage = (Stage) node.getScene().getWindow();
          // stage.setMaximized(true);
          stage.close();
          FXMLLoader loader;
          Parent root;
          if (txtUsername.getText().equalsIgnoreCase("admin")){
            loader = new FXMLLoader(getClass().getResource("/fxml/Organizer.fxml"));
            root = loader.load();
          }else {
            loader = new FXMLLoader(getClass().getResource("/fxml/User.fxml"));
            root = loader.load();
            UserController userController = loader.getController();
            userController.setWelcomeLabel(txtUsername.getText());
          }
          stage.setScene(new Scene(root));
          stage.show();

        } catch (IOException ex) {
          System.err.println(ex.getMessage());
        }
      }
    }
  }

  @FXML
  public void registerButton(MouseEvent event) {
    if (event.getSource() == btnSignup) {
      // login here
      try {

        // add you loading or delays - 😉
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        // stage.setMaximized(true);
        stage.close();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Register.fxml")));
        stage.setScene(scene);
        stage.show();

      } catch (IOException ex) {
        System.err.println(ex.getMessage());
      }
    }
  }

  // we gonna use string to check for status
  private String logIn() throws IOException {
    String status = "Success";
    String username = txtUsername.getText();
    String password = txtPassword.getText();
    LoginRequest user = new LoginRequest();
    user.setUsername(username);
    user.setPassword(password);

    if (username.isEmpty() || password.isEmpty()) {
      setLblError(Color.TOMATO, "Empty credentials");
      status = "Error";
    } else {
      try {
        ExecutorService es = Executors.newCachedThreadPool();
        Gson gson = new Gson();
        // creare json cu datale user-ului de login
        String text = gson.toJson(user);
        // trimitere date catre server, pe langa host si ip se trimite comanda "login" si json-ul
        SocketClientCallable commandWithSocket =
            new SocketClientCallable("127.0.0.1", 9001, "login", text);
        Future<String> response = es.submit(commandWithSocket);
        // raspunsul primit de la server
        LoginResponse serverResponse = gson.fromJson(response.get(), LoginResponse.class);
        if (!"200".equalsIgnoreCase(serverResponse.getCode())) {
          setLblError(Color.TOMATO, serverResponse.getMessage());
          status = "Error";
        } else {
          UserInfo.setCurrentUserId(user.getUsername());
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
