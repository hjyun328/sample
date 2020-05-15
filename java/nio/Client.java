import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

@SuppressWarnings("ConstantConditions")
public class Client {

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
    SocketChannel clientChannel = null;

    while (true) {
      int selected = selector.select(1000);

      if (selected > 0) {
        for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
          SelectionKey sk = iterator.next();
          SelectableChannel channel = sk.channel();

          try {
            if (sk.isConnectable()) {
              System.out.println("Connectable");
              connect(selector, clientChannel);
            }

            if (sk.isWritable()) {
              System.out.println("Writable");
              write((SocketChannel) channel);
            }

            if (sk.isReadable()) {
              System.out.println("Readable");
              read((SocketChannel) channel);
            }

            iterator.remove();
          } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
            clientChannel.close();
            clientChannel = null;
          }
        }
      } else {
        if (clientChannel != null && clientChannel.isConnected()) {
          System.out.println("connected");
        } else if (clientChannel != null && clientChannel.isConnectionPending()) {
          System.out.println("connect pending");
        } else {
          try {
            if (clientChannel != null) {
              clientChannel.close();
            }
            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);
            clientChannel.connect(new InetSocketAddress("192.168.0.234", 2181));
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
          } catch (ConnectException e) {
            System.out.println("connect - ConnectException");
            e.printStackTrace();
          }
        }
      }

      Thread.sleep(1000);
    }
  }

  static void connect(Selector selector, SocketChannel channel) throws IOException {
    System.out.println("connect");
    SelectionKey key = channel.keyFor(selector);
    if (channel.finishConnect()) {
      System.out.println("finishConnect - true");
      key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    } else {
      System.out.println("finishConnect - false");
    }
  }


  static void write(SocketChannel channel) throws IOException {
    if (data != null) {
      System.out.println("write = " + data);
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
      System.out.println("read is minus!");
      channel.close();
    } else {
      System.out.println("read = " + new String(stream.toByteArray()) + ", " + read);
    }
  }

}
