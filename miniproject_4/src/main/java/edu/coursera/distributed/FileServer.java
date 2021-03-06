package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @param ncores The number of cores that are available to your
     *               multi-threaded file server. Using this argument is entirely
     *               optional. You are free to use this information to change
     *               how you create your threads, or ignore it.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs,
            final int ncores) throws IOException {
        while (true) {
            Socket currSocket = socket.accept();
            Thread thread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(currSocket.getInputStream()));
                    PCDPPath path = new PCDPPath(reader.readLine().split(" ")[1]);
                    String content = fs.readFile(path);
                    OutputStream out = currSocket.getOutputStream();
                    PrintWriter writer = new PrintWriter(out);

                    if(content != null) {
                        writer.write("HTTP/1.0 200 OK\r\n");
                        writer.write("Server: FileServer\r\n");
                        writer.write("\r\n");
                        writer.write(content + "\r\n");
                    } else {
                        writer.write("HTTP/1.0 404 Not Found\r\n");
                        writer.write("Server: FileServer\r\n");
                        writer.write("\r\n");
                    }

                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            thread.start();
        }
    }
}
