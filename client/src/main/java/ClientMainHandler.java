import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ClientMainHandler extends ChannelInboundHandlerAdapter {

    final private String CLIENT_STORAGE = "client_storage/";
    private List<String> listServerFiles;
    private Controller controller;

    public ClientMainHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) return;
        if (msg instanceof FileMessage) {
            FileMessage fm = (FileMessage) msg;
            Files.write(Paths.get(CLIENT_STORAGE + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
            controller.refreshLocalFilesList();
        }
        if (msg instanceof String) {
            String str = (String) msg;
            System.out.println(str);
        }
        if (msg instanceof FileListMessage) {
            FileListMessage fileListMessage = (FileListMessage) msg;
            listServerFiles = fileListMessage.getFileList();
            controller.setList(listServerFiles);
            controller.refresh();
        }
        if (msg instanceof FileRequest) transferFile(((FileRequest) msg).getFilename(), ctx);
    }

    private void transferFile(String filename, ChannelHandlerContext ctx) throws IOException {
        if (Files.exists(Paths.get(filename))) {
            FileMessage fm = new FileMessage(Paths.get(filename));
            ctx.writeAndFlush(fm);
        }
    }


}
