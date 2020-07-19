import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NettyNetwork {

    private static NettyNetwork currentInstance = new NettyNetwork();

    private Channel currentChannel;

    public static NettyNetwork getInstance() {
        return currentInstance;
    }

    private NettyNetwork() {}

    public void start(Controller controller) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress("localhost", 8180));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    currentChannel = socketChannel;
                    socketChannel.pipeline().addLast(new ObjectDecoder(120 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                    new ObjectEncoder(), new ClientMainHandler(controller));
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getServerFilesList() {
        currentChannel.writeAndFlush(new FileListRequest());
    }

    public void getFile(String fileName) {
        currentChannel.writeAndFlush(new FileRequest(fileName));
    }

    public void sendFile(String path) throws IOException {
        currentChannel.writeAndFlush(new ClientFileRequest(path));
    }

    public void closeConnection() {
        currentChannel.close();
    }
}
