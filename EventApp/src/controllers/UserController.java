package controllers;

import com.google.gson.Gson;
import data.EventData;
import data.InvitationData;
import data.NotificationData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import requests.AcceptInvitationRequest;
import requests.AvailabilityRequest;
import requests.NotificationRequest;
import requests.UserInvitationRequest;
import response.*;
import utils.SocketClientCallable;
import utils.UserInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserController {

  @FXML private Button btnNotification;
  @FXML private Button btnInvitation;
  @FXML private Button btnEvent;
  @FXML private Button btnAcceptInvitation;
  @FXML private Label welcomeLabel;
  @FXML public RadioButton availabilityButton;
  @FXML TableView tblData;
  @FXML private TextField secretCodeText;
  @FXML public Label secondLblErrors;


  @FXML
  private void initialize() throws ExecutionException, InterruptedException {

    fetEventColumnList();
    fetEventRowList();

    tblData.setRowFactory(
        tv -> {
          TableRow rowClicked = new TableRow();
          rowClicked.setOnMouseClicked(
              event -> {
                if (!rowClicked.isEmpty()
                    && event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2) {

                  ObservableList invitationObjectData = (ObservableList) rowClicked.getItem();

                  Node node = (Node) event.getSource();
                  Stage stage = (Stage) node.getScene().getWindow();
                  // stage.setMaximized(true);
                  FileChooser fileChooser = new FileChooser();

                  // Set extension filter
                  FileChooser.ExtensionFilter extFilter =
                      new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                  fileChooser.getExtensionFilters().add(extFilter);

                  // Show save file dialog
                  File file = fileChooser.showSaveDialog(stage);
                  if (file != null) {
                    try {
                      FileWriter fileWriter = null;
                      BufferedReader br =
                          new BufferedReader(new FileReader((File) invitationObjectData.get(1)));

                      fileWriter = new FileWriter(file);
                      String st;
                      while ((st = br.readLine()) != null) {
                        fileWriter.write(st);
                      }
                      fileWriter.close();
                    } catch (IOException ex) {
                    }
                  }
                }
              });
          return rowClicked;
        });
  }

  public void setWelcomeLabel(String name) {
    welcomeLabel.setText("Welcome," + name);
  }

  public void logOut(MouseEvent mouseEvent) throws IOException {
    Node node = (Node) mouseEvent.getSource();
    Stage stage = (Stage) node.getScene().getWindow();
    // stage.setMaximized(true);
    stage.close();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  public void btnNotification(MouseEvent event) throws ExecutionException, InterruptedException {
    fetNotificationColumnList();
    fetNotificationRowList();
  }

  public void btnInvitation(MouseEvent event) throws ExecutionException, InterruptedException {
    fetInvitationsColumnList();
    fetInvitationRowList();
  }

  private void fetInvitationsColumnList() {

    try {
      List<String> columns = invitationsColumns();

      tblData.getColumns().clear();
      for (int i = 0; i <= columns.size() - 1; i++) {
        final int j = i;
        TableColumn col = new TableColumn(columns.get(i).toUpperCase());
        col.setCellValueFactory(
            (Callback<
                    TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                param -> new SimpleStringProperty(param.getValue().get(j).toString()));

        tblData.getColumns().addAll(col);
      }

    } catch (Exception e) {
      System.out.println("Error " + e.getMessage());
    }
  }

  private void fetInvitationRowList() throws ExecutionException, InterruptedException {
    ObservableList<ObservableList> data = FXCollections.observableArrayList();

    Gson gson = new Gson();
    String str = welcomeLabel.getText();
    String[] userName = str.split(",", 2);
    UserInvitationRequest userInvitationRequest = new UserInvitationRequest();
    userInvitationRequest.setUser(userName[1]);
    String text = gson.toJson(userInvitationRequest);
    ExecutorService es = Executors.newCachedThreadPool();
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "showInvitations", text);
    Future<String> response = es.submit(commandWithSocket);
    UserInvitationsResponse userInvitationsResponse =
        gson.fromJson(response.get(), UserInvitationsResponse.class);

    List<String> columns = invitationsColumns();
    if (!userInvitationsResponse.getInvitations().isEmpty()) {

      for (InvitationData invitationData : userInvitationsResponse.getInvitations()) {
        // Iterate Row
        ObservableList row = FXCollections.observableArrayList();
        for (int i = 0; i <= columns.size() - 1; i++) {
          if (i == 0) {
            row.add(invitationData.getMessage());
          } else if (i == 1) {
            row.add(invitationData.getFile());
          } else if (i == 2) {
            row.add(invitationData.getEvent());
          }
        }
        data.add(row);
      }
      tblData.setItems(data);
    }
  }

  private List<String> invitationsColumns() {
    List<String> columns = new ArrayList<>();
    columns.add("MESSAGE");
    columns.add("ATTACHMENT");
    columns.add("EVENT");
    return columns;
  }

  public void btnEvent(MouseEvent event) throws ExecutionException, InterruptedException {
    fetEventColumnList();
    fetEventRowList();
  }

  public void setAvailabilityButton() throws ExecutionException, InterruptedException {
    Boolean availability = availabilityButton.isSelected();
    String str = welcomeLabel.getText();
    String[] userName = str.split(",", 2);
    AvailabilityRequest availabilityRequest = new AvailabilityRequest();
    availabilityRequest.setAvailability(availability);
    availabilityRequest.setUserName(userName[1]);

    Gson gson = new Gson();
    String text = gson.toJson(availabilityRequest);
    ExecutorService es = Executors.newCachedThreadPool();
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "setAvailability", text);
    Future<String> response = es.submit(commandWithSocket);
    AvailabilityResponse availabilityResponse =
        gson.fromJson(response.get(), AvailabilityResponse.class);

    if (Boolean.TRUE.equals(availabilityResponse.isAvailability())) {
      availabilityButton.setSelected(Boolean.TRUE);
    } else {
      availabilityButton.setSelected(Boolean.FALSE);
    }
  }

  private void fetEventColumnList() {

    try {
      List<String> columns = eventColumns();

      tblData.getColumns().clear();
      for (int i = 0; i <= columns.size() - 1; i++) {
        final int j = i;
        TableColumn col = new TableColumn(columns.get(i).toUpperCase());
        col.setCellValueFactory(
            (Callback<
                    TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                param -> new SimpleStringProperty(param.getValue().get(j).toString()));

        tblData.getColumns().addAll(col);
      }

    } catch (Exception e) {
      System.out.println("Error " + e.getMessage());
    }
  }

  private List<String> eventColumns() {
    List<String> columns = new ArrayList<>();
    columns.add("NAME");
    columns.add("DATE");
    columns.add("LOCATION");
    columns.add("NUMBER OF SEATS");
    return columns;
  }

  private void fetEventRowList() throws ExecutionException, InterruptedException {
    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    String str = welcomeLabel.getText();
    String[] userName = str.split(",", 2);
    tblData.getItems().clear();
    List<String> columns = eventColumns();
    Gson gson = new Gson();
    ExecutorService es = Executors.newCachedThreadPool();
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "showEvents", UserInfo.getCurrentUserId());
    Future<String> response = es.submit(commandWithSocket);
    ShowEventsResponse showEventsResponse = gson.fromJson(response.get(), ShowEventsResponse.class);

    for (EventData eventData : showEventsResponse.getEventDataList()) {
      // Iterate Row
      System.out.println(eventData.toString());
      ObservableList row = FXCollections.observableArrayList();
      for (int i = 0; i <= columns.size() - 1; i++) {
        if (i == 0) {
          row.add(eventData.getEventName());
        } else if (i == 1) {
          row.add(eventData.getDate());
        } else if (i == 2) {
          row.add(eventData.getLocation());
        } else if (i == 3) {
          row.add(eventData.getNumberOfSeats());
        }
      }
      data.add(row);
      tblData.setItems(data);
    }
  }

  private void fetNotificationColumnList() {

    try {
      List<String> columns = notificationsColumns();
      tblData.getColumns().clear();
      for (int i = 0; i <= columns.size() - 1; i++) {
        final int j = i;
        TableColumn col = new TableColumn(columns.get(i).toUpperCase());
        col.setCellValueFactory(
            (Callback<
                    TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                param -> new SimpleStringProperty(param.getValue().get(j).toString()));

        tblData.getColumns().addAll(col);
      }

    } catch (Exception e) {
      System.out.println("Error " + e.getMessage());
    }
  }

  private List<String> notificationsColumns() {
    List<String> columns = new ArrayList<>();
    columns.add("MESSAGE");
    return columns;
  }

  private void fetNotificationRowList() throws ExecutionException, InterruptedException {

    String str = welcomeLabel.getText();
    String[] userName = str.split(",", 2);
    NotificationRequest notificationRequest = new NotificationRequest();
    notificationRequest.setUserName(userName[1]);

    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    List<String> columns = eventColumns();
    Gson gson = new Gson();
    String text = gson.toJson(notificationRequest);
    ExecutorService es = Executors.newCachedThreadPool();
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "showNotifications", text);
    Future<String> response = es.submit(commandWithSocket);
    ShowNotificationsResponse showNotificationsResponse =
        gson.fromJson(response.get(), ShowNotificationsResponse.class);

    for (NotificationData notificationData : showNotificationsResponse.getNotificationDataList()) {
      // Iterate Row
      System.out.println(notificationData.toString());
      ObservableList row = FXCollections.observableArrayList();
      for (int i = 0; i <= columns.size() - 1; i++) {
        row.add(notificationData.getMessage());
      }
      data.add(row);
      tblData.setItems(data);
    }
  }

  public void btnAcceptInvitation(MouseEvent mouseEvent)
      throws ExecutionException, InterruptedException {
    String str = welcomeLabel.getText();
    String[] userName = str.split(",", 2);
    AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
    acceptInvitationRequest.setUser(userName[1]);
    acceptInvitationRequest.setSecretCode(secretCodeText.getText());

    Gson gson = new Gson();
    String text = gson.toJson(acceptInvitationRequest);
    ExecutorService es = Executors.newCachedThreadPool();
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "acceptInvitation", text);
    Future<String> response = es.submit(commandWithSocket);
    AcceptInvitationResponse acceptInvitationResponse =
        gson.fromJson(response.get(), AcceptInvitationResponse.class);
    if ("200".equalsIgnoreCase(acceptInvitationResponse.getCode())) {
      setSecondLblError(Color.BLUE, acceptInvitationResponse.getMessage());
    } else {
      setSecondLblError(Color.TOMATO, acceptInvitationResponse.getMessage());    }
  }

  private void setSecondLblError(Color color, String text) {
    secondLblErrors.setTextFill(color);
    secondLblErrors.setText(text);
    System.out.println(text);
  }
}
