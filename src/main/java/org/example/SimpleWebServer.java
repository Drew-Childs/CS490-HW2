package org.example;

/****************************************************************
 SimpleWebServer.java
 This toy web server is used to illustrate security vulnerabilities. This web server only supports extremely simple HTTP GET requests.
 ****************************************************************/

import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleWebServer {

    /* Run the HTTP server on this TCP port. */
    private static final int PORT = 8080;

    /* The socket used to process incoming connections from web clients */
    private static ServerSocket dServerSocket;

    public SimpleWebServer () throws Exception {
        dServerSocket = new ServerSocket (PORT);
    }

    public void run() throws Exception {
        while (true) {
            /* wait for a connection from a client */
            Socket s = dServerSocket.accept();

            /* then process the client's request */
            processRequest(s);
        }
    }

    /* Reads the HTTP request from the client, and responds with the file the user requested or a HTTP error code. */
    public void processRequest(Socket s) throws Exception {
        /* used to read data from the client */
        BufferedReader br = new BufferedReader (new InputStreamReader (s.getInputStream()));

        /* used to write data to the client */
        OutputStreamWriter osw =  new OutputStreamWriter (s.getOutputStream());

        /* read the HTTP request from the client */
        String request = br.readLine();

        String command = null;
        String pathname = null;

        /* parse the HTTP request */
        StringTokenizer st = new StringTokenizer (request, " ");

        command = st.nextToken();
        pathname = st.nextToken();

        if (command.equals("GET")) {
            /* if the request is a GET try to respond with the file the user is requesting */
            System.out.println("Path name: "+pathname);
            serveFile (osw,pathname);
        }
        if (command.equals("PUT")) {
            System.out.println("Path name: " + pathname);
            storeFile(br, osw, pathname);
        }
        else {
            /* if the request is a NOT a GET, return an error saying this server does not implement the requested command */
            osw.write ("HTTP/1.0 501 Not Implemented\n\n");
        }

        logEntry("request_log.txt", request + "\n");

        /* close the connection to the client */
        osw.close();
    }

    public void serveFile (OutputStreamWriter osw, String pathname) throws Exception {
        FileReader fr=null;
        int c=-1;
        StringBuffer sb = new StringBuffer();

        /* remove the initial slash at the beginning of the pathname in the request */
        if (pathname.charAt(0)=='/')
            pathname=pathname.substring(1);

        /* if there was no filename specified by the client, serve the "index.html" file */
        if (pathname.equals(""))
            pathname="index.html";

        /* try to open file specified by pathname */
        try {
//    		System.out.println("Path name: "+pathname);

            // Made maximum file size 100 KB
            File fileSize = new File(pathname);
            if (fileSize.length() > 100000) {
                osw.write("HTTP/1.0 403 Forbidden\n\n");
                logEntry("error_log.txt", "Maximum download file size exceeded!\n");
                return;
            }

            fr = new FileReader (pathname);
            c = fr.read();
        }
        catch (Exception e) {
            /* if the file is not found,return the appropriate HTTP response code  */
            osw.write ("HTTP/1.0 404 Not Found\n\n");
            return;
        }

 	/* if the requested file can be successfully opened
 	   and read, then return an OK response code and
 	   send the contents of the file */
        osw.write ("HTTP/1.0 200 OK\n\n");
        while (c != -1) {
            sb.append((char)c);
            c = fr.read();
        }
        osw.write (sb.toString());
    }

    public void storeFile(BufferedReader br, OutputStreamWriter osw, String pathname) throws Exception {
        FileWriter fw = null;
        Scanner sc = new Scanner(br);
        try {
            fw = new FileWriter(pathname);
            String s = sc.nextLine();
            while(!s.isEmpty() && s != null) {
                fw.write(s+"\n");
                s = sc.nextLine();
            }
            fw.close();
            sc.close();
            osw.write("HTP/1.0 201 Created");
        } catch(Exception e) {
            osw.write("HTTP/1.0 500 Internal Server Error");
        }
    }

    public void logEntry(String filename, String record) throws IOException {
        FileWriter fw = new FileWriter(filename, true);
        fw.write((new Date()).toString()+" "+record);
        fw.close();
    }


    /* This method is called when the program is run from the command line. */
    public static void main (String argv[]) throws Exception {
        /* Create a SimpleWebServer object, and run it */
        SimpleWebServer sws = new SimpleWebServer();
        sws.run();
    }






    To clarify, below is a revised description of Problem 2.2:

    Modify SimpleWebServer.java and SimpleWebClient.java to allow the client to upload a file. When the user of the client program provides input PUT <DestinationFile> <fileToUpload>, the client program should read the content of <fileToUpload> on the client side and sends the command PUT <destination_path> and the file content to the server. The server program will save the file content to <DestinationFile> on the server and record all client requests into a log file. The sample methods for text file storage and logging are given below. You may update storeFile to deal with binary files if needed. (30 points)

    The source code of storeFile is also updated below. The previous version assumes that the file content has no empty line.

    public void storeFile(BufferedReader br, OutputStreamWriter osw, String pathname) throws Exception {

        FileWriter fw = null;

        Scanner sc = new Scanner(br);

        try {

            fw = new FileWriter(pathname);

            while (sc.hasNext()) {

                String line = sc.nextLine();

                fw.write(line+"\n");

            }

            fw.close();

            sc.close();

            osw.write("HTP/1.0 201 Created");

            System.out.println(pathname+" is saved!");

        } catch(Exception e) {

            osw.write("HTTP/1.0 500 Internal Server Error");

        }
    }
}
