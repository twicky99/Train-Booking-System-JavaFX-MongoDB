package sample;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;                                                             //json .jar file(dependancy) has to be imported (.jar file is included in the project)

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import static com.mongodb.client.model.Updates.set;

public class TrainStation extends Application {

    private static final int SEATING_CAPACITY = 42;
    private static final HashMap<Integer, Passenger> waitingRoom = new HashMap<>();        //create an empty hashamp named waitingRoom
    private static final HashMap<Integer, Passenger> tempWaitingRoom = new HashMap<>();     //create an empty hashamp named tempWaitingRoom
    private static final PassengerQueue passengerQueue = new PassengerQueue();
    private static JSONArray jsonArray = new JSONArray();
    private static List<Document> jsonList = new ArrayList<>();
    private static int currentQueueNo = 0;

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("==================================================================\n");
        System.out.println("********* Welcome to Denuwara Manike Train Boarding System *********");
        System.out.println("\n==================================================================");
        menu:
        while (true) {
            System.out.println("\nEnter \"A\" to add a passenger to the train queue");
            System.out.println("Enter \"V\" to view the train queue ");
            System.out.println("Enter \"D\" to delete passenger from the trainQueue ");
            System.out.println("Enter \"S\" to store train queue data into a plain text file");
            System.out.println("Enter \"L\" to load data back from the file into the trainQueue ");
            System.out.println("Enter \"R\" to run the simulation and produce report ");
            System.out.println("Enter \"Q\" to exit\n");

            System.out.print("Enter your option : ");
            String option = sc.next();

            switch (option) {
                case "A":
                case "a":
                    addCustomersToQueue();
                    break;
                case "V":
                case "v":
                    viewTheTrainQueue();
                    break;
                case "D":
                case "d":
                    deletePassengerFromTrainQueue();
                    break;
                case "S":
                case "s":
                    saveToDatabase();
                    break;
                case "L":
                case "l":
                    loadFromDatabase();
                    break;
                case "R":
                case "r":
                    runSimulationProcess();
                    break;
                case "Q":
                case "q":
                    System.out.println("Thanks you for booking seats in Denuwara Menike. Come again.");
                    break menu;
                default:
                    System.out.println("Invalid input....re-enter");
            }
        }
    }

    //ADD TO QUEUE
    public void addCustomersToQueue(){

        loadWaitingRoomData(); // load waiting room data

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Passenger Waiting Room");

        FlowPane flowPane = new FlowPane();
        flowPane.setOrientation(Orientation.VERTICAL);

        for (int i = 1; i <= SEATING_CAPACITY; i++) {

            Button button = new Button("Seat " + i);                        //displays the buttons as seats in the gui
            button.setId(Integer.toString(i));
            flowPane.getChildren().add(button);
            button.setPrefWidth(65);
            flowPane.setVgap(25);
            flowPane.setHgap(25);
            flowPane.setPadding(new Insets(10));
            flowPane.setStyle("-fx-background-color:#ffffff");

            button.setDisable(true); // disable the button
            if(waitingRoom.containsKey(Integer.parseInt(button.getId())) && !tempWaitingRoom.containsKey(Integer.parseInt(button.getId()))){
                button.setDisable(false);
                button.setStyle("-fx-background-color: red; -fx-text-fill: white"); // set red color for booked passengers in waiting room
            }

        }

        Button confirm = new Button("ADD TO \n QUEUE");
        confirm.setAlignment(Pos.CENTER);
        confirm.setStyle("-fx-background-color:#228B22;-fx-text-fill:white");
        confirm.setMinWidth(100);
        confirm.setDisable(false);

        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                primaryStage.close();

                int randomNumber = new Random().nextInt(6) + 1; // generate six side die number
                System.out.println("No.of passengers moved to queue : " + randomNumber);

                int count = 0;
                boolean flag = false;
                HashMap<Integer, Passenger> temp = new HashMap<>();     //creating a hashmap named temp
                for (int i = 1; i <= SEATING_CAPACITY; i++) {
                    if (waitingRoom.containsKey(i) && !tempWaitingRoom.containsKey(i)) {
                        temp.put(i, waitingRoom.get(i));
                        tempWaitingRoom.put(i, waitingRoom.get(i));
                        count++;
                        if (randomNumber == count) {
                            flag = true;
                            break;
                        }
                    }
                }

                Stage primaryStage1 = new Stage();                     //when button is clicked displays  the second stage
                primaryStage1.setTitle("Passenger Queue");

                FlowPane flowPane1 = new FlowPane();
                flowPane1.setOrientation(Orientation.VERTICAL);
                for (Map.Entry<Integer, Passenger> entry : temp.entrySet()) {
                    Button button = new Button("Seat " + entry.getKey());                        //displays the buttons as seats in the gui
                    button.setId(Integer.toString(entry.getKey()));
                    flowPane1.getChildren().add(button);
                    button.setPrefWidth(65);
                    flowPane1.setVgap(25);
                    flowPane1.setHgap(25);
                    flowPane1.setPadding(new Insets(10));
                    flowPane1.setStyle("-fx-background-color:#ffffff");
                }

                Button ok = new Button("OK");
                ok.setStyle("-fx-background-color:#228B22;-fx-text-fill:white");
                ok.setMinWidth(100);
                ok.setDisable(false);

                ok.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        primaryStage1.close();
                    }
                });

                VBox vbox1 = new VBox(ok);
                vbox1.setSpacing(25);
                vbox1.setAlignment(Pos.CENTER);
                vbox1.setPadding(new Insets(100, 100, 0, 100));
                flowPane1.getChildren().add(vbox1);

                Scene scene1 = new Scene(flowPane1, 350, 300);
                primaryStage1.setScene(scene1);
                primaryStage1.showAndWait();

            }});

        VBox vbox = new VBox(confirm);
        vbox.setSpacing(25);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(120, 0, 100, 80));
        flowPane.getChildren().add(vbox);

        Scene scene = new Scene(flowPane, 800, 350);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();

    }

    //VIEW QUEUE
    public void viewTheTrainQueue(){

        loadQueue(); // load queue

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Train Queue");

        FlowPane flowPane = new FlowPane();
        flowPane.setOrientation(Orientation.HORIZONTAL);

        int i = 1;
        boolean flag = false;
        boolean flag2;
        while (i <= SEATING_CAPACITY) {
            flag2 = false;
            String buttonText = "";
            Node node = passengerQueue.remove();
            if(node != null){
                Passenger passenger = node.key;
                flag2 = true;
                buttonText = "Queue No. " + i + "\n" + passenger.getFirstName().split("\\s+")[0] + " " + passenger.getSurname().split("\\s+")[0] + "\n" + "Seat No. " + passenger.getSeatNo();
            }else{
                if(!flag){
                    flag = true;
                    for (Map.Entry<Integer, Passenger> entry : tempWaitingRoom.entrySet()) {                //using entrySet() to get the entry's of the map
                        buttonText = "Queue No. " + i + "\n" + entry.getValue().getFirstName().split("\\s+")[0] + " " + entry.getValue().getSurname().split("\\s+")[0] + "\n" + "Seat No. " + entry.getValue().getSeatNo();
                        createButtons(buttonText, flowPane, i);
                        ++i;
                    }
                }else{
                    buttonText = "Queue No. " + i + "\n" + "Empty";
                    flag2 = true;
                }
            }

            if(flag2){
                createButtons(buttonText,flowPane,i);
                ++i;
            }
        }

        loadQueue();      //load Queue

        Button ok = new Button("OK");
        ok.setStyle("-fx-background-color:#228B22;-fx-text-fill:white");
        ok.setMinWidth(100);
        ok.setDisable(false);

        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });

        VBox vbox1 = new VBox(ok);
        vbox1.setSpacing(25);
        vbox1.setAlignment(Pos.CENTER);
        vbox1.setPadding(new Insets(0, 400, 0, 400));
        flowPane.getChildren().add(vbox1);

        Scene scene = new Scene(flowPane, 950, 550);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();

    }

    void createButtons(String buttonText,FlowPane flowPane,Integer buttonID){
        Button button1 = new Button(buttonText);                        //displays the buttons as seats in the gui
        button1.setId(Integer.toString(buttonID));
        flowPane.getChildren().add(button1);
        button1.setPrefWidth(120);
        flowPane.setVgap(25);
        flowPane.setHgap(25);
        flowPane.setPadding(new Insets(10));
        flowPane.setStyle("-fx-background-color:#000000");
    }

    //DELETE PASSENGER FROM QUEUE
    public void deletePassengerFromTrainQueue(){
        System.out.println("------------------------ Delete Passenger From Train Queue ------------------------");
        System.out.println(" Enter passenger first name ");
        Scanner in  = new Scanner(System.in);
        String name = in.nextLine();
        System.out.println("Enter passenger seat number (1-42)");
        int seatNum=0;
        try{
            String deleteSeat=in.next();
            seatNum=Integer.parseInt(deleteSeat);
            deleteFromDatabaseByPassengerFirstNameAndSeatNo(name,seatNum);
        }catch(NumberFormatException e){
            System.out.println("Please enter a valid seat number.Only integers are allowed");
        }

    }

    //STORE DATA TO DATABASE
    public void saveToDatabase(){

        addTempWaitingRoomToQueue();   //load from tempWaitingRoom to Queue

        MongoClient client = MongoClients.create();                                  // creating a Mongo client
        MongoDatabase database = client.getDatabase("Denuwara-Menike");              //creating a database
        MongoCollection collection = database.getCollection("PassengerQueue");          //creating a table

        if (jsonArray.isEmpty()) {
            System.out.println("There is no record to save in the database");
            return;
        }

        System.out.println("The data is stored in the database");
        for (int i = 0; i < jsonArray.length(); i++) {
            Document jsnObject = Document.parse(jsonArray.getJSONObject(i).toString());
            jsonList.add(jsnObject);
        }
        collection.insertMany(jsonList);     //insert the records to the collection.
        jsonArray = new JSONArray();
        jsonList = new ArrayList<>();
        client.close();

        tempWaitingRoom.clear();
    }

    //LOAD DATA FRO DATABASE
    public void loadFromDatabase(){
        MongoClient client = MongoClients.create();                                  // creating a Mongo client
        MongoDatabase database = client.getDatabase("Denuwara-Menike");              //creating a database
        MongoCollection collection = database.getCollection("PassengerQueue");          //creating a table

        final FindIterable<Document> documents = collection.find();
        System.out.println("Passenger Queue Records");

        int i = 0;
        for(Document document : documents){
            ++i;
            System.out.println("--------------------- Passenger : " + i + "---------------------");
            System.out.println("Queue No : " + document.getInteger("queueNo"));
            System.out.println("Seat No : " + document.getInteger("seat"));
            System.out.println("First Name : " + document.getString("firstName"));
            System.out.println("Surname : " + document.getString("surname"));
            System.out.println("----------------------------------------------------------------");
            System.out.println();
        }

        if(i == 0){
            new Alert(Alert.AlertType.ERROR, "There are no records to load from the database!").showAndWait();
        }
    }

    public void runSimulationProcess() {

        // creating text file
        try {
            String path="trainBoard information.txt";
            File file = new File(path);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                FileWriter fileWriter = new FileWriter("trainBoard information.txt");
                loadQueue();

                for (Map.Entry<Integer, Passenger> entry : tempWaitingRoom.entrySet()) {
                    passengerQueue.add(entry.getValue());
                }

                Random random = new Random();
                int x,y,z,delay;
                int totalDelay = 0;
                int passengerCount = 0;
                int maximumDelay=0;
                int minimumDelay=19;
                System.out.println("------------------------ Running Simulation Process ------------------------");
                Node node = passengerQueue.remove();
                int i = 0;

                VBox passengerListVBox = new VBox(10);
                ObservableList<javafx.scene.Node> vboxChildren=passengerListVBox.getChildren();

                while(node != null){
                    //making a delay by generating random number using 3 six sided die as x,y,z
                    x=random.nextInt(6)+1;
                    y=random.nextInt(6)+1;
                    z=random.nextInt(6)+1;
                    delay = x+y+z;
                    totalDelay += delay;
                    ++passengerCount;

                    if(maximumDelay < delay){
                        maximumDelay = delay;
                    }

                    if (passengerCount == 1){
                        minimumDelay = delay;
                    }
                    Passenger p = node.key;

                    // Creating a vbox consisting passenger info

                    Label label0=new Label("------------------------ Passenger " + (i+1) + " ------------------------");
                    Label label1 = new Label("Full Name\t\t\t\t: " + p.getFirstName()+" "+p.getSurname());
                    Label label2 = new Label("Seat No\t\t\t\t\t: " + p.getSeatNo());
                    Label label3 = new Label("Waiting Time in Queue\t: " + totalDelay + " seconds" );

                    //in the text file
                    fileWriter.write("------------------------ Passenger " + (i+1) + " ------------------------\r\n");
                    fileWriter.write("--- Full Name : " +p.getFirstName()+" "+p.getSurname()+"\r\n");
                    fileWriter.write("--- Seat No. : " + p.getSeatNo()+"\r\n");
                    fileWriter.write("--- Waiting Time In Queue : " + totalDelay + " seconds"+"\r\n");
                    fileWriter.write("-------------------------------------------------------------------------"+"\r\n");
                    fileWriter.write("\n");

                    label0.setTextFill(Color.web("#4b0082"));
                    label1.setFont(new Font("Arial Rounded MT Bold", 14));
                    label1.setTextFill(Color.web("#4b0082"));
                    label1.setFont(new Font("Arial Rounded MT Bold", 14));
                    label2.setTextFill(Color.web("#4b0082"));
                    label2.setFont(new Font("Arial Rounded MT Bold", 14));
                    label3.setTextFill(Color.web("#4b0082"));
                    label3.setFont(new Font("Arial Rounded MT Bold", 14));

                    VBox passengerVbox = new VBox(20,label0,label1,label2,label3);
                    passengerVbox.setSpacing(10);
                    passengerVbox.setAlignment(Pos.BASELINE_LEFT);
                    passengerVbox.setPadding(new Insets(0, 50, 0, 50));

                    vboxChildren.add(passengerVbox);

                    node = passengerQueue.remove();
                    ++i;
                }

                float averageDelay = (Math.round((totalDelay*100.0)/100.0)/(float)passengerCount);

                // in the text file
                fileWriter.write("-------------------------------- Summary --------------------------------"+"\r\n");
                fileWriter.write("--- Maximum length of queue       : " + passengerCount+"\r\n");
                fileWriter.write("--- Minimum waiting time in queue : " + minimumDelay + " seconds"+"\r\n");
                fileWriter.write("--- Maximum waiting time in queue : " + totalDelay + " seconds"+"\r\n");
                fileWriter.write("--- Average waiting time in queue : " + averageDelay + " seconds"+"\r\n");
                fileWriter.write("-------------------------------------------------------------------------"+"\r\n");
                fileWriter.close();
                //tempWaitingRoom.clear();

                MongoClient client = MongoClients.create();
                MongoDatabase database = client.getDatabase("Denuwara-Menike");
                MongoCollection<Document> collection = database.getCollection("PassengerQueue");

                Stage window = new Stage();
                window.setTitle("Report");

                ScrollPane scrollPane=new ScrollPane(passengerListVBox);
                scrollPane.setPrefSize(500,650);

                FlowPane flowPane = new FlowPane();
                Label lb1 = new Label("SUMMARY");
                lb1.setTextFill(Color.web("#f4a460"));
                lb1.setFont(new Font("Arial Rounded MT Bold", 18));

                //create a vbox containing summary
                Label label1 = new Label("Maximum length of queue       : " + passengerCount);
                Label label2 = new Label("Minimum waiting time in queue : " + minimumDelay + " seconds");
                Label label3 = new Label("Maximum waiting time in queue : " + totalDelay + " seconds");
                Label label4 = new Label("Average waiting time in queue : " + averageDelay + " seconds");

                label1.setTextFill(Color.web("#4b0082"));
                label1.setFont(new Font("Arial Rounded MT Bold", 14));
                label2.setTextFill(Color.web("#4b0082"));
                label2.setFont(new Font("Arial Rounded MT Bold", 14));
                label3.setTextFill(Color.web("#4b0082"));
                label3.setFont(new Font("Arial Rounded MT Bold", 14));
                label4.setTextFill(Color.web("#4b0082"));
                label4.setFont(new Font("Arial Rounded MT Bold", 14));

                flowPane.setOrientation(Orientation.HORIZONTAL);
                Button ok = new Button("OK");
                ok.setStyle("-fx-background-color:#228B22;-fx-text-fill:white");
                ok.setMinWidth(100);
                ok.setDisable(false);

                ok.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        window.close();
                    }
                });

                VBox vbox1 = new VBox(lb1,label1,label2,label3,label4,ok);
                vbox1.setSpacing(25);
                vbox1.setAlignment(Pos.CENTER);
                vbox1.setPadding(new Insets(0, 50, 0, 50));
                flowPane.getChildren().addAll(scrollPane,vbox1);

                try
                {
                    Thread.sleep(10000);      //makes a delay of 10seconds to display the GUI
                    Scene scene0 = new Scene(flowPane, 1000, 900);
                    window.setScene(scene0);
                    window.showAndWait();
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }

            }else{
                file.delete();
                runSimulationProcess();
            }
        } catch (IOException e) {
            System.out.println("An error occurred. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addTempWaitingRoomToQueue(){
        loadQueue();
        for (Map.Entry<Integer, Passenger> entry : tempWaitingRoom.entrySet()){     //using entrySet() to get the entry's of the map
            currentQueueNo++;
            Passenger passenger = entry.getValue();
            passenger.setSeatNo(entry.getKey());
            passenger.setQueueNo(currentQueueNo);
            passengerQueue.add(passenger);

            JSONObject jsonObject = new JSONObject();
            //add Queue No,Seat No,First Name,Surname to jsonObject
            jsonObject.put("queueNo", passenger.getQueueNo());
            jsonObject.put("seat", passenger.getSeatNo());
            jsonObject.put("firstName", passenger.getFirstName());
            jsonObject.put("surname", passenger.getSurname());
            jsonArray.put(jsonObject);

            if(currentQueueNo == SEATING_CAPACITY){
                // display info msg "Queue is FULL"
                new Alert(Alert.AlertType.ERROR, "Queue is FULL!").showAndWait();
            }
        }

    }

    boolean findSeatInQueue(int seatNo){
        boolean flag = false;
        Node node = passengerQueue.remove();
        while (node != null){
            Passenger passenger = node.key;
            if(passenger.getSeatNo() == seatNo){
                flag = true;
                break;
            }
            node = passengerQueue.remove();
        }
        loadQueue();
        return flag;
    }

    public void loadWaitingRoomData(){
        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase("Denuwara-Menike"); // create database connection
        MongoCollection<Document> collection = database.getCollection("BookingRecords"); // accessing the BookingRecords Collection
        final FindIterable<Document> documents = collection.find();
        waitingRoom.clear(); // clear waitingRoom hashmap
        loadQueue();
        for(Document document : documents){
            if(!findSeatInQueue(Integer.parseInt(document.getString("seat")))){
                Passenger passenger = new Passenger(document.getString("surname"),document.getString("first name"));
                passenger.setSeatNo(Integer.parseInt(document.getString("seat")));
                waitingRoom.put(Integer.parseInt(document.getString("seat")),passenger);
            }
        }
    }

    public void loadQueue(){
        MongoClient client = MongoClients.create();                                  // creating a Mongo client
        MongoDatabase database = client.getDatabase("Denuwara-Menike");              //accessing the database
        MongoCollection collection = database.getCollection("PassengerQueue");          //retrieving a collection

        while (passengerQueue.first != null){
            passengerQueue.remove();                                 //to remove the elements
        }

        final FindIterable<Document> documents = collection.find();   //getting the iterable object
        currentQueueNo = 0;
        for(Document document : documents){
            Passenger passenger = new Passenger(document.getString("surname"),document.getString("firstName"));
            passenger.setSeatNo(document.getInteger("seat"));
            passenger.setQueueNo(document.getInteger("queueNo"));
            passengerQueue.add(passenger);             //add passenger details to passengerQueue
            currentQueueNo++;
        }
    }

    private void deleteFromDatabaseByPassengerFirstNameAndSeatNo(String passengerName,Integer seatNumber) {

        boolean flag = false;
        for (Map.Entry<Integer, Passenger> entry : tempWaitingRoom.entrySet()){     //using entrySet() to get the entry's of the map
            if(entry.getValue().getFirstName().equals(passengerName)&&entry.getValue().getSeatNo().equals(seatNumber)){   // using getValue to get the FirstName and the Seat No. and checks whether it equals to passengerName and the seatNumber entered by the user.
                tempWaitingRoom.remove(entry.getKey());       //if those are equal remove the entry
                System.out.println("Success. The passenger " +passengerName +" - Seat No. " +seatNumber+" removed from train queue");
                flag = true;
                break;
            }
        }

        if(!flag){
            MongoClient client = MongoClients.create();                                      //creating a Mongo client
            MongoDatabase database = client.getDatabase("Denuwara-Menike");                 //accessing the database
            MongoCollection<Document> collection = database.getCollection("PassengerQueue");     //retrieving a collection
            final Iterable<Document> documents = collection.find(Filters.eq("firstName",passengerName));
            if(documents.iterator().hasNext()){    //checks next once in the iteration
                int queueNo = 0;
                final FindIterable<Document> documents1 = collection.find();    //getting the iterable object
                for(Document document : documents1){

                    if(document.getString("firstName").equals(passengerName)){    //if the first name equals the entered passengerName deletes the document
                        DeleteResult  deleteResult = collection.deleteOne(Filters.eq("firstName",passengerName));    //delete one document
                        if(deleteResult.getDeletedCount() > 0){
                            System.out.println(passengerName + " removed from the queue");
                        }else{
                            System.out.println(passengerName + " cannot remove from the queue.Try again");
                        }
                    }else{
                        queueNo++;
                        Passenger passenger = new Passenger(document.getString("surname"),document.getString("firstName"));
                        passenger.setSeatNo(document.getInteger("seat"));
                        passenger.setQueueNo(queueNo);
                        Bson query = set("queueNo", passenger.getQueueNo());
                        UpdateResult updateResult = collection.updateOne(Filters.eq("firstName",passenger.getFirstName()),query);
                    }
                }
            }else{
                new Alert(Alert.AlertType.ERROR, "There is no such person with the entered Seat No. in the queue").showAndWait();    //displays an alert box
            }
        }
    }
}

