package org.example;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class SimpleWebClient {
    private static final String hostName = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        try (
                Socket serverSocket = new Socket(hostName, PORT);
                PrintWriter out =
                        new PrintWriter(serverSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(serverSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            if ((userInput = stdIn.readLine()) != null) {
                String response = null;
                StringTokenizer st = new StringTokenizer (userInput, " ");

                String command = st.nextToken();
                String pathname = st.nextToken();

                if (command.equals("PUT")) {
                    String fileName = st.nextToken();

                    String fileContent = Files.readString(Paths.get(fileName));
                    userInput = command + " " + pathname + "\n" + fileContent;
                }
                out.println(userInput);
                response=in.readLine();

                if (response!=null) {
                    System.out.println("Response from Server: ");
                    System.out.println(response);
                    while ((response=in.readLine())!=null) {
                        System.out.println(response);
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +  hostName);
            System.exit(1);
        }
    }
}
