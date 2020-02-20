package com.unitbv.utilitypackage;

import com.google.gson.Gson;
import com.unitbv.data.EventData;
import com.unitbv.data.InvitationData;
import com.unitbv.data.NotificationData;
import com.unitbv.data.UserData;
import com.unitbv.entity.EventEntity;
import com.unitbv.entity.InvitationEntity;
import com.unitbv.entity.NotificationEntity;
import com.unitbv.entity.UserEntity;
import com.unitbv.services.EventService;
import com.unitbv.services.InvitationService;
import com.unitbv.services.NotificationService;
import com.unitbv.services.UserService;
import com.unitbv.services.impl.EventServiceImpl;
import com.unitbv.services.impl.InvitationServiceImpl;
import com.unitbv.services.impl.NotificationServiceImpl;
import com.unitbv.services.impl.UserServiceImpl;
import com.unitbv.utilitypackage.request.*;
import com.unitbv.utilitypackage.response.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class CustomerUtilitiesServer implements Runnable {
    private ServerSocket ss;
    private UserService userService;
    private EventService eventService;
    private NotificationService notificationService;
    private InvitationService invitationService;

    public CustomerUtilitiesServer(int port) throws IOException {
        ss = new ServerSocket(port);
        ss.setSoTimeout(250);
        userService = new UserServiceImpl();
        eventService = new EventServiceImpl();
        notificationService = new NotificationServiceImpl();
        invitationService = new InvitationServiceImpl();

    }

    public void accept() throws IOException {
        System.out
                .println("Accepting connections on port " + ss.getLocalPort());
        while (!Thread.interrupted()) {
            try (Socket socket = ss.accept()) {

                BufferedWriter bufferedOutputWriter = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader bufferedInputReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), "UTF-8"));

                //comanda trimisa de catre client
                String inputCommand = bufferedInputReader.readLine();
                //datele din client
                String data_server = bufferedInputReader.readLine();

                //switch cu comanda trimisa de catre client
                switch (inputCommand) {
                    case "login":
                        Gson gson = new Gson();
                        LoginRequest clientRequest = gson.fromJson(data_server, LoginRequest.class);
                        LoginResponse clientResponse = new LoginResponse();

                        String userName = clientRequest.getUsername();
                        String pass = clientRequest.getPassword();
                        UserEntity user = userService.findByUserName(userName);
                        if (Objects.nonNull(user)) {
                            if (user.getPassword().equalsIgnoreCase(pass)) {
                                clientResponse.setCode("200");
                                clientResponse.setMessage("Login was successfully");
                            } else {
                                clientResponse.setCode("500");
                                clientResponse.setMessage("Incorrect password");
                            }
                        } else {
                            clientResponse.setCode("500");
                            clientResponse.setMessage("Username not found");
                        }

                        String text = gson.toJson(clientResponse);
                        bufferedOutputWriter.write(text);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "register":
                        Gson gsonRegister = new Gson();
                        RegisterRequest clientRegisterRequest = gsonRegister.fromJson(data_server, RegisterRequest.class);
                        RegisterResponse clientRegisterResponse = new RegisterResponse();


                        UserEntity userEntity = new UserEntity();
                        userEntity.setUserName(clientRegisterRequest.getUserName());
                        userEntity.setFirstName(clientRegisterRequest.getFirstName());
                        userEntity.setLastName(clientRegisterRequest.getLastName());
                        userEntity.setPassword(clientRegisterRequest.getPassword());
                        userEntity.setEmail(clientRegisterRequest.getEmail());
                        userEntity.setAvailability(clientRegisterRequest.isAvailability());
                        userService.persist(userEntity);

                        if (Objects.nonNull(userEntity)) {
                            clientRegisterResponse.setCode("200");
                            clientRegisterResponse.setMessage("Register was successfully");

                        } else {
                            clientRegisterResponse.setCode("500");
                            clientRegisterResponse.setMessage("Registration failed");
                        }

                        String registerText = gsonRegister.toJson(clientRegisterResponse);
                        bufferedOutputWriter.write(registerText);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "addEvent":
                        Gson gsonAddEvent = new Gson();
                        CreateEventRequest clientAddEventRequest = gsonAddEvent.fromJson(data_server, CreateEventRequest.class);
                        CreateEventResponse clientAddEventResponse = new CreateEventResponse();

                        EventEntity eventEntity = new EventEntity();
                        eventEntity.setEventName(clientAddEventRequest.getEventName());
                        eventEntity.setDate(clientAddEventRequest.getEventDate());
                        eventEntity.setLocation(clientAddEventRequest.getLocation());
                        eventEntity.setNumberOfSeats(clientAddEventRequest.getNumberOfSeats());

                        eventService.persist(eventEntity);

                        if (Objects.nonNull(eventEntity)) {
                            clientAddEventResponse.setCode("200");
                            clientAddEventResponse.setMessage("Event added successfully");

                        } else {
                            clientAddEventResponse.setCode("500");
                            clientAddEventResponse.setMessage("Event was not save, please fill all inputs");
                        }

                        String addEventText = gsonAddEvent.toJson(clientAddEventResponse);
                        bufferedOutputWriter.write(addEventText);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "showEvents":
                        Gson gsonShowEvents = new Gson();

                        ShowEventsResponse showEventsResponse = new ShowEventsResponse();
                        List<EventData> eventDataList = new ArrayList<>();
                        UserEntity userEntity1 = userService.findByUserName(data_server);
                        if (!eventService.findAll().isEmpty()) {
                            if (!data_server.equalsIgnoreCase("admin")) {
                                for (EventEntity event : eventService.findAllByUser(userEntity1)) {
                                    EventData eventData = new EventData();
                                    eventData.setEventName(event.getEventName());
                                    eventData.setDate(event.getDate());
                                    eventData.setLocation(event.getLocation());
                                    eventData.setNumberOfSeats(event.getNumberOfSeats());
                                    eventDataList.add(eventData);
                                }
                            } else {
                                for (EventEntity event : eventService.findAll()) {
                                    EventData eventData = new EventData();
                                    eventData.setEventName(event.getEventName());
                                    eventData.setDate(event.getDate());
                                    eventData.setLocation(event.getLocation());
                                    eventData.setNumberOfSeats(event.getNumberOfSeats());
                                    eventDataList.add(eventData);
                                }
                            }
                            showEventsResponse.setEventDataList(eventDataList);
                        }
                        String showEvents = gsonShowEvents.toJson(showEventsResponse);
                        bufferedOutputWriter.write(showEvents);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "showNotifications":
                        Gson gsonShowNotifications = new Gson();
                        NotificationRequest notificationRequest = gsonShowNotifications.fromJson(data_server, NotificationRequest.class);
                        String usernameForNotifications = notificationRequest.getUserName();
                        UserEntity userForNotifications = userService.findByUserName(usernameForNotifications);
                        ShowNotificationsResponse showNotificationsResponse = new ShowNotificationsResponse();
                        List<NotificationData> notificationDataList = new ArrayList<>();
                        if (!notificationService.findAllByUser(userForNotifications).isEmpty()) {
                            for (NotificationEntity notificationEntity : notificationService.findAllByUser(userForNotifications)) {
                                NotificationData notificationData = new NotificationData();
                                notificationData.setMessage(notificationEntity.getMessage());
                                notificationDataList.add(notificationData);
                            }
                            showNotificationsResponse.setNotificationDataList(notificationDataList);
                        }
                        String showNotifications = gsonShowNotifications.toJson(showNotificationsResponse);
                        bufferedOutputWriter.write(showNotifications);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "setAvailability":
                        Gson gsonAvailability = new Gson();
                        AvailabilityRequest availabilityRequest = gsonAvailability.fromJson(data_server, AvailabilityRequest.class);
                        String usernameForAvailability = availabilityRequest.getUserName();
                        UserEntity userForAvailability = userService.findByUserName(usernameForAvailability);
                        AvailabilityResponse availabilityResponse = new AvailabilityResponse();
                        userForAvailability.setAvailability(availabilityRequest.isAvailability());
                        userService.update(userForAvailability);
                        availabilityResponse.setAvailability(userForAvailability.getAvailability());
                        String availability = gsonAvailability.toJson(availabilityResponse);
                        bufferedOutputWriter.write(availability);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "getUsers":
                        Gson gsonGetUsers = new Gson();

                        UserListResponse userListResponse = new UserListResponse();
                        List<UserData> userDataList = new ArrayList<>();
                        List<UserEntity> userEntities = userService.findAll();
                        if (!userService.findAll().isEmpty()) {
                            for (UserEntity userModel : userService.findAll()) {
                                if (Boolean.TRUE.equals(userModel.getAvailability())) {
                                    UserData userData = new UserData();
                                    userData.setId(userModel.getId());
                                    userData.setFirstName(userModel.getFirstName());
                                    userData.setLastName(userModel.getLastName());
                                    userDataList.add(userData);
                                }
                            }
                            userListResponse.setUserDataList(userDataList);
                        }
                        String showUsers = gsonGetUsers.toJson(userListResponse);
                        bufferedOutputWriter.write(showUsers);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "sendInvitation":
                        Gson gsonSendInvitation = new Gson();
                        InputStream propertyFile = new FileInputStream("C:\\facultate\\EventAppServer\\src\\main\\resources\\config.properties");
                        Properties prop = new Properties();
                        prop.load(propertyFile);

                        SendInvitationRequest sendInvitationRequest = gsonSendInvitation.fromJson(data_server, SendInvitationRequest.class);
                        SendInvitationResponse sendInvitationResponse = new SendInvitationResponse();
                        if (!sendInvitationRequest.getListOfUsers().isEmpty()) {
                            for (String invitedUser : sendInvitationRequest.getListOfUsers()) {
                                InvitationEntity invitation = new InvitationEntity();
                                NotificationEntity notificationEntity = new NotificationEntity();
                                File file = new File("C:\\facultate\\EventAppServer\\src\\main\\resources\\invitations\\InvitationFor" + invitedUser + sendInvitationRequest.getEvent() + ".txt");
                                if (!invitedUser.isEmpty()) {
                                    UserEntity userInvited = userService.findByUserName(invitedUser);
                                    if (Objects.nonNull(userInvited)) {
                                        StringBuilder sb = generateSecretCode(prop);
                                        invitation.setMessage("Test message");
                                        invitation.setUser(userInvited);
                                        invitation.setEvent(eventService.findEventByName(sendInvitationRequest.getEvent()));
                                        FileWriter fileWriter = null;
                                        fileWriter = new FileWriter(file);
                                        fileWriter.write("Use your secret code to accept your invitation. Your secret code is: " + sb.toString());
                                        fileWriter.close();
                                        invitation.setAttachment(file);
                                        invitation.setSecretCode(sb.toString());
                                        invitationService.persist(invitation);
                                        notificationEntity.setUser(userInvited);
                                        notificationEntity.setMessage("You received an invitation, please check invitations tab");
                                        notificationService.persist(notificationEntity);
                                        sendInvitationResponse.setCode("200");
                                        sendInvitationResponse.setMessage("The invitation was sent successfully");
                                    } else {
                                        sendInvitationResponse.setMessage("There was an error trying to send the invitation");
                                        sendInvitationResponse.setCode("400");
                                    }
                                }
                            }
                        }

                        String invitation = gsonSendInvitation.toJson(sendInvitationResponse);
                        bufferedOutputWriter.write(invitation);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();

                        break;
                    case "showInvitations":
                        Gson gsonShowInvitations = new Gson();
                        UserInvitationRequest userInvitationRequest = gsonShowInvitations.fromJson(data_server, UserInvitationRequest.class);
                        String usernameForInvitations = userInvitationRequest.getUser();
                        UserEntity userForInvitations = userService.findByUserName(usernameForInvitations);
                        UserInvitationsResponse userInvitationsResponse = new UserInvitationsResponse();
                        List<InvitationData> invitationData = new ArrayList<>();
                        if (!invitationService.findAllByUser(userForInvitations).isEmpty()) {
                            for (InvitationEntity invitationEntity : invitationService.findAllByUser(userForInvitations)) {
                                InvitationData invitationData1 = new InvitationData();
                                invitationData1.setMessage(invitationEntity.getMessage());
                                invitationData1.setFile(invitationEntity.getAttachment());
                                invitationData1.setEvent(invitationEntity.getEvent().getEventName());
                                invitationData.add(invitationData1);
                            }
                            userInvitationsResponse.setInvitations(invitationData);
                        }
                        String showInvitation = gsonShowInvitations.toJson(userInvitationsResponse);
                        bufferedOutputWriter.write(showInvitation);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    case "acceptInvitation":
                        Gson gsonAcceptInvitations = new Gson();
                        AcceptInvitationRequest acceptInvitationRequest = gsonAcceptInvitations.fromJson(data_server, AcceptInvitationRequest.class);
                        String usernameForAccept = acceptInvitationRequest.getUser();
                        UserEntity userForAccept = userService.findByUserName(usernameForAccept);
                        AcceptInvitationResponse acceptInvitationResponse = new AcceptInvitationResponse();
                        if (!invitationService.findAllByUser(userForAccept).isEmpty()) {
                            for (InvitationEntity invitationEntity : invitationService.findAllByUser(userForAccept)) {
                                if (acceptInvitationRequest.getSecretCode().isEmpty()) {
                                    acceptInvitationResponse.setCode("500");
                                    acceptInvitationResponse.setMessage("The secret code cannot be empty");
                                } else if (invitationEntity.getSecretCode().equalsIgnoreCase(acceptInvitationRequest.getSecretCode())) {
                                    if (userForAccept.getEvents().contains(invitationEntity.getEvent())) {
                                        acceptInvitationResponse.setCode("500");
                                        acceptInvitationResponse.setMessage("You already accepted this invitation");
                                    } else if (invitationEntity.getEvent().getNumberOfSeats() > 0) {
                                        List<EventEntity> events = new ArrayList<>();
                                        if (Objects.nonNull(userForAccept.getEvents())) {
                                            events.addAll(userForAccept.getEvents());
                                        }
                                        events.add(invitationEntity.getEvent());
                                        userForAccept.setEvents(events);
                                        userService.update(userForAccept);
                                        invitationEntity.getEvent().setNumberOfSeats(invitationEntity.getEvent().getNumberOfSeats() - 1);
                                        eventService.update(invitationEntity.getEvent());
                                        NotificationEntity notificationEntity = new NotificationEntity();
                                        notificationEntity.setUser(userForAccept);
                                        notificationEntity.setMessage("You have accepted the invitation for " + invitationEntity.getEvent().getEventName() + " event");
                                        notificationService.persist(notificationEntity);
                                        acceptInvitationResponse.setCode("200");
                                        acceptInvitationResponse.setMessage("You accepted the invitation" + invitationEntity.getEvent().getEventName());
                                    } else {
                                        acceptInvitationResponse.setCode("500");
                                        acceptInvitationResponse.setMessage("No more seats are available " + invitationEntity.getEvent().getEventName() + " , we are sorry.");
                                    }
                                } else {
                                    acceptInvitationResponse.setCode("500");
                                    acceptInvitationResponse.setMessage("The secret code doesn't match with the code from your invitation");
                                }
                            }
                        }


                        String acceptInvitation = gsonAcceptInvitations.toJson(acceptInvitationResponse);
                        bufferedOutputWriter.write(acceptInvitation);
                        bufferedOutputWriter.newLine();
                        bufferedOutputWriter.flush();
                        break;
                    default:
                        break;
                }
            } catch (SocketTimeoutException ste) {
                // timeout every .25 seconds to see if interrupted
            }

        }
        System.out.println("Done accepting");
    }

    private StringBuilder generateSecretCode(Properties prop) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        int secretCodeLength = Integer.parseInt(prop.getProperty("secretCodeLength"));

        StringBuilder sb = new StringBuilder(secretCodeLength);
        for (int i = 0; i < secretCodeLength; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (alphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb
                    .append(alphaNumericString
                            .charAt(index));
        }
        return sb;
    }

    @Override
    public void run() {
        try {
            accept();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
