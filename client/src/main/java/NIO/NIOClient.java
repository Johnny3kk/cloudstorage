package NIO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NIOClient extends Application {
    static final int PORT = 8189;
    static final String ADDRESS = "192.168.0.1";
    private ByteBuffer buffer = ByteBuffer.allocate(256);
    private Selector selector;
    String PathDir = "C:\\Users\\3p-r-\\IdeaProjects\\geekbrains-cloud-storage\\client\\src\\main\\resources\\clientPath";
    private Path path;
    public NIOClient() throws HeadlessException, IOException {

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);
        channel.connect(new InetSocketAddress(ADDRESS, PORT));


        JTextField txt = new JTextField();
        JButton send = new JButton("send");
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(txt);
        panel.add(send);
        send.addActionListener(action -> {
            String fileName = txt.getText();
            try {
                if (!(path = Paths.get(PathDir + "\\" + fileName)).isAbsolute()) {
                    txt.setText("FILE WITH NAME " + fileName + " NOT EXISTS IN DIRECTORY");
                } else {
                    RandomAccessFile srcFile = new RandomAccessFile(fileName, "rw");
                    FileChannel srcChannel = srcFile.getChannel();
                    long position = 0;
                    long size = srcChannel.size();

                    srcChannel.transferTo(position, size, channel);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Storage");
        primaryStage.setScene(new Scene(root, 350, 600));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        new NIOClient();
        launch(args);
    }
}
