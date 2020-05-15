import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;

@SuppressWarnings("ConstantConditions")
public class Server {

  static String data = null;

  public static void main(String[] args) {

    new Thread(() -> {
      try {
        start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();

    Scanner scanner = new Scanner(System.in);
    while (true) {
      data = scanner.nextLine();
    }

  }

  static void start() throws Exception {
    Selector selector = Selector.open();
    ServerSocketChannel serverChannel = null;

    while (true) {
      int selected = selector.select(1000);

      if (selected > 0) {
        for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
          SelectionKey sk = iterator.next();
          SelectableChannel channel = sk.channel();

          try {
            if (sk.isAcceptable()) {
              accept(selector, (ServerSocketChannel) channel);
            }

            if (sk.isWritable()) {
              write((SocketChannel) channel);
            }

            if (sk.isReadable()) {
              read((SocketChannel) channel);
            }
            iterator.remove();
          } catch (IOException e) {
            if (channel instanceof ServerSocketChannel) {
              serverChannel = null;
              System.out.println("IOException - Server");
            } else {
              System.out.println("IOException - Client (" + ((SocketChannel) channel).getRemoteAddress() + ")");
            }
            channel.close();
            e.printStackTrace();
          }
        }
      } else {
        if (serverChannel != null && serverChannel.isOpen()) {
          System.out.println("opened");
        } else {
          if (serverChannel != null) {
            serverChannel.close();
          }
          serverChannel = ServerSocketChannel.open();
          serverChannel.bind(new InetSocketAddress(2181));
          serverChannel.configureBlocking(false);
          serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
      }

      Thread.sleep(1000);
    }
  }

  static void accept(Selector selector, ServerSocketChannel channel) throws IOException {
    SocketChannel client = channel.accept();
    System.out.println("accept (" + client.getRemoteAddress() + ")");
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
  }


  static void write(SocketChannel channel) throws IOException {
    if (data != null) {
      System.out.println("write (" + channel.getRemoteAddress() + ")" + " = " + data);
      data += "\n";
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put(data.getBytes());
      buffer.flip();
      channel.write(buffer);
      data = null;
    }
  }


  static void read(SocketChannel channel) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    int read;
    while ((read = channel.read(buffer)) > 0) {
      buffer.clear();
      stream.write(buffer.array());
    }
    if (read < 0) {
      System.out.println("read (" + channel.getRemoteAddress() + ") is minus!");
      channel.close();
    } else {
      System.out.println("read (" + channel.getRemoteAddress() + ")" + " = " + new String(stream.toByteArray()));
    }
  }

}
