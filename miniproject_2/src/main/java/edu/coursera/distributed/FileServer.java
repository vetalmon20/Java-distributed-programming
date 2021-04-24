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
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {
            Socket currSocket = socket.accept();

            Thread thread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(currSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(currSocket.getOutputStream());
                    String currLine = reader.readLine();
                    PCDPPath path = new PCDPPath(currLine.split(" ")[1]);
                    String content = fs.readFile(path);

                    if (content == null) {
                        writer.write("HTTP/1.0 404 Not Found\r\n");
                        writer.write("Server: FileServer\r\n");
                        writer.write("\r\n");
                    } else {
                        writer.write("HTTP/1.0 200 OK\r\n");
                        writer.write("Server: FileServer\r\n");
                        writer.write("\r\n");
                        writer.write(content);
                    }

                    writer.close();
                } catch (IOException ignored){}
            });

            thread.start();
        }
    }
}
