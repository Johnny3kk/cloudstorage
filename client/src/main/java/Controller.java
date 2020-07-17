import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Controller implements Initializable, EventHandler<WindowEvent> {

    private static final String CLIENT_STORAGE = "client_storage/";
    private List<String> serverFilelist;
    private static final Logger log = Logger.getLogger(String.valueOf(Controller.class));

    @Override
    public void handle(WindowEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                if (!Files.exists(Paths.get(CLIENT_STORAGE))) {
                    Files.createDirectory(Paths.get(CLIENT_STORAGE));
                    log.info("Create dir " + CLIENT_STORAGE);
                }
                //fx stuff
                filesList.getItems().clear();
                Files.list(Paths.get(CLIENT_STORAGE)).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    //fx stuff
                    filesList.getItems().clear();
                    Files.list(Paths.get(CLIENT_STORAGE)).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void setList(List<String> list) {
        this.serverFilelist = list;
    }

    public void refresh() {
        if (serverFilelist != null) {
            serverFilelist.getItems().clear();
            for (String o: serverFilelist) {
                filesServerList.getItems().add(o);
            }
        }
    }
}
