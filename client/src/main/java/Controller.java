import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    public void handle(WindowEvent event) {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> NettyNetwork.getInstance().start(this)).start();
        refreshLocalFilesList();
    }

    @FXML
    TextField tfFileName;

    @FXML
    TextField tfFileNameServer;

    @FXML
    ListView<String> filesList;

    @FXML
    ListView<String> filesServerList;

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                if (!Files.exists(Paths.get(CLIENT_STORAGE))) {
                    Files.createDirectory(Paths.get(CLIENT_STORAGE));
                    log.info("Create dir " + CLIENT_STORAGE);
                }
                filesList.getItems().clear();
                Files.list(Paths.get(CLIENT_STORAGE)).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
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
            filesServerList.getItems().clear();
            for (String o: serverFilelist) {
                filesServerList.getItems().add(o);
            }
        }
    }

    public void pressOnDownloadBtn() throws IOException {
        if (tfFileName.getLength() > 0) {
            send(tfFileName.getText());
            tfFileName.clear();
        }
    }

    public void send(String fileName) {
        NettyNetwork.getInstance().getFile(fileName);
    }

    public void getServerFilesList() {
        filesServerList.getItems().clear();
        NettyNetwork.getInstance().getServerFilesList();
        refresh();
    }

    public void pressedServerFileList() {
        tfFileName.setText(filesServerList.getSelectionModel().selectedItemProperty().getValue());
    }

    public void pressedClientFileList() {
        tfFileNameServer.setText(filesList.getSelectionModel().selectedItemProperty().getValue());
    }

    public void sendFile() throws IOException {
        String path = CLIENT_STORAGE + tfFileNameServer.getText();
        if (Files.exists(Paths.get(path))) NettyNetwork.getInstance().sendFile(path);
    }

    private javafx.event.EventHandler<WindowEvent> closeEventHandler = event -> NettyNetwork.getInstance().closeConnection();

    public javafx.event.EventHandler<WindowEvent> getClose() {
        return closeEventHandler;
    }

    public void onClientDelete(ActionEvent actionEvent) throws IOException {
        String path = tfFileNameServer.getText();
        if (path == null) return;
        Files.delete(Paths.get(CLIENT_STORAGE).resolve(path));
        refreshLocalFilesList();
    }
}
