package controllers;

import com.google.gson.Gson;
import data.EventData;
import data.UserData;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import requests.CreateEventRequest;
import requests.SendInvitationRequest;
import response.CreateEventResponse;
import response.SendInvitationResponse;
import response.ShowEventsResponse;
import response.UserListResponse;
import utils.SocketClientCallable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class OrganizerController {

  @FXML public TextField txtEventName;
  @FXML public DatePicker dateOfEvent;
  @FXML public TextField txtEventLocation;
  @FXML public TextField seatsNumber;
  @FXML public Button eventButton;
  @FXML public Label secondLblErrors;
  @FXML private Button btnSave;
  @FXML private Button sendInvitationButton;

  @FXML private Label lblErrors;
  @FXML TableView tblData;

  @FXML
  private void initialize() throws ExecutionException, InterruptedException {
    fetColumnList();
    fetRowList();
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

  public void eventButton(MouseEvent mouseEvent) throws ExecutionException, InterruptedException {
    fetColumnList();
    fetRowList();
  }

  public void sendInvitationButton(MouseEvent mouseEvent)
      throws ExecutionException, InterruptedException {
    ExecutorService es = Executors.newCachedThreadPool();
    SendInvitationRequest sendInvitationRequest = new SendInvitationRequest();
    List<String> users = new ArrayList<>();
    if (!selectedItems.isEmpty()) {
      for (ObservableList<String> usersSelected : selectedItems) {
        users.add(usersSelected.get(0));
      }
      sendInvitationRequest.setListOfUsers(users);
      sendInvitationRequest.setEvent(eventObjectData.get(0).toString());
    }

    Gson gson = new Gson();
    String text = gson.toJson(sendInvitationRequest);
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "sendInvitation", text);
    Future<String> response = es.submit(commandWithSocket);
    SendInvitationResponse serverResponse =
        gson.fromJson(response.get(), SendInvitationResponse.class);
    if ("200".equalsIgnoreCase(serverResponse.getCode())) {
      setSecondLblError(Color.BLUE, serverResponse.getMessage());
    } else {
      setSecondLblError(Color.TOMATO, serverResponse.getMessage());
    }
  }

  public void saveButton(MouseEvent event) {
    if (event.getSource() == btnSave) {
      if (placeEvent().equals("Success")) {
        try {

          fetRowList();

        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private String placeEvent() {
    String status = "Success";
    String eventName = txtEventName.getText();
    LocalDate eventDate = dateOfEvent.getValue();
    String eventLocation = txtEventLocation.getText();
    int numberOfSeats = Integer.parseInt(seatsNumber.getText());

    CreateEventRequest event = new CreateEventRequest();
    event.setEventName(eventName);
    event.setEventDate(eventDate);
    event.setLocation(eventLocation);
    event.setNumberOfSeats(numberOfSeats);

    if (Objects.isNull(event.getEventName())) {
      setLblError(Color.TOMATO, "Empty credentials");
      status = "Error";
    } else {
      try {
        ExecutorService es = Executors.newCachedThreadPool();
        Gson gson = new Gson();
        String text = gson.toJson(event);
        SocketClientCallable commandWithSocket =
            new SocketClientCallable("127.0.0.1", 9001, "addEvent", text);
        Future<String> response = es.submit(commandWithSocket);
        CreateEventResponse serverResponse =
            gson.fromJson(response.get(), CreateEventResponse.class);
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

  private void fetColumnList() {

    try {
      List<String> columns = columns();
      tblData.getColumns().clear();
      // SQL FOR SELECTING ALL OF CUSTOMER
      for (int i = 0; i <= columns.size(); i++) {
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

  private List<String> columns() {
    List<String> columns = new ArrayList<>();
    columns.add("NAME");
    columns.add("DATE");
    columns.add("LOCATION");
    columns.add("NUMBER OF SEATS");
    return columns;
  }

  ObservableList eventObjectData;

  private void fetRowList() throws ExecutionException, InterruptedException {
    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    List<String> columns = columns();
    Gson gson = new Gson();
    LocalDate now = LocalDate.now();
    ExecutorService es = Executors.newCachedThreadPool();
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "showEvents", "admin");
    Future<String> response = es.submit(commandWithSocket);
    ShowEventsResponse showEventsResponse = gson.fromJson(response.get(), ShowEventsResponse.class);
    tblData.getItems().clear();
    if (showEventsResponse.getEventDataList() != null) {
      for (EventData eventData : showEventsResponse.getEventDataList().stream().filter(t -> t.getDate().compareTo(now) >= 0).collect(Collectors.toList())) {
        // Iterate Row
        System.out.println(eventData.toString());
        ObservableList row = FXCollections.observableArrayList();
        for (int i = 0; i <= columns.size(); i++) {
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

        tblData.setRowFactory(
            tv -> {
              TableRow rowClicked = new TableRow();
              rowClicked.setOnMouseClicked(
                  event -> {
                    if (!rowClicked.isEmpty()
                        && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {
                      fetUserColumnList();
                      try {
                        eventObjectData = (ObservableList) rowClicked.getItem();
                        fetUserRowList(rowClicked);
                      } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                  });
              return rowClicked;
            });
      }
    }
  }

  private void fetUserColumnList() {

    try {
      List<String> columns = userColumns();

      tblData.getColumns().clear();
      for (int i = 0; i <= columns.size(); i++) {
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

  private List<String> userColumns() {
    List<String> columns = new ArrayList<>();
    columns.add("FIRST NAME");
    columns.add("LAST NAME");
    return columns;
  }

  ObservableList<ObservableList<String>> selectedItems;

  private void fetUserRowList(TableRow rowClicked) throws ExecutionException, InterruptedException {

    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    List<String> columns = userColumns();
    Gson gson = new Gson();
    ExecutorService es = Executors.newCachedThreadPool();
    SocketClientCallable commandWithSocket =
        new SocketClientCallable("127.0.0.1", 9001, "getUsers", null);
    Future<String> response = es.submit(commandWithSocket);
    UserListResponse userListResponse = gson.fromJson(response.get(), UserListResponse.class);
    tblData.getItems().clear();
    for (UserData userData : userListResponse.getUserDataList()) {
      // Iterate Row
      ObservableList row = FXCollections.observableArrayList();
      for (int i = 0; i <= columns.size(); i++) {
        if (i == 0) {
          row.add(userData.getFirstName());
        } else {
          row.add(userData.getLastName());
        }
      }
      data.add(row);
      tblData.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      tblData.setOnMousePressed(
          new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
              selectedItems = tblData.getSelectionModel().getSelectedItems();
              if (sendInvitationButton.isPressed()) {
                try {
                  sendInvitationButton(event);
                } catch (ExecutionException e) {
                  e.printStackTrace();
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            }
          });
      tblData.setItems(data);
    }
  }

  private void setLblError(Color color, String text) {
    lblErrors.setTextFill(color);
    lblErrors.setText(text);
    System.out.println(text);
  }

  private void setSecondLblError(Color color, String text) {
    secondLblErrors.setTextFill(color);
    secondLblErrors.setText(text);
    System.out.println(text);
  }
}
