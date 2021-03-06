import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

    final private int WIDTH = 1000;
    final private int HEIGHT = 600;


    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view1.fxml"));
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setTitle("GB-Cloud-Storage");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
