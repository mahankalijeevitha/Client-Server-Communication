import java.net.*;
import java.io.*;
import java.util.*;

/* Group- Raghava Adarsh Mandarapu, Jeevitha Mahankali*/

public class AdServer extends Thread
{
	static final String HTML_START = "<html>" + "<title>HTTP Server in java</title>" + "<body>";
	static final String HTML_END = "</body>" + "</html>";
	public String temp_Content,file_Name;
	Socket connected_Client = null;
	BufferedReader in_From_Client = null;
	BufferedReader read_From_Client = null;
	//DataInputStream in_From_Client = null;
	DataOutputStream out_To_Client = null;
	DataInputStream data_input_stream = null;
	public static final int BUFFER_SIZE = 200;	                    
	public String content = "";	
	public AdServer(Socket client) {
		connected_Client = client;
		
	}	
	public void run()
	{
		try
		{											
			System.out.println( "The Client " + connected_Client.getInetAddress() + ":" + connected_Client.getPort() + " is connected");
			read_From_Client = new BufferedReader(new InputStreamReader(connected_Client.getInputStream()));
			in_From_Client = new BufferedReader(new InputStreamReader (connected_Client.getInputStream()));
			data_input_stream = new DataInputStream(connected_Client.getInputStream());
			out_To_Client = new DataOutputStream(connected_Client.getOutputStream());			
			String request_String = in_From_Client.readLine();
			String header_Line = request_String;
			StringTokenizer token = new StringTokenizer(header_Line);
			String http_Method = token.nextToken();
			String http_Query_String = token.nextToken();
			String[] temp = http_Query_String.split("/");   //Getting only the fileName by eliminating "/"
		    file_Name = temp[1];
			StringBuffer response_Buffer = new StringBuffer();
			response_Buffer.append("<b> This is the HTTP Server Home Page.... </b><BR>");
			response_Buffer.append("The HTTP Client request is ....<BR>");			
			if (http_Method.equals("GET")) {
				if (http_Query_String.equals("/")) {
					// The default home page
					send_Response(200, response_Buffer.toString(), false);
				} else {
					//This is interpreted as a file name
					String file_Name = http_Query_String.replaceFirst("/", "");
					//file_Name = URLDecoder.decode(fileName);
					if (new File(file_Name).isFile()){
						send_Response(200, file_Name, true);
					}
					else {
						send_Response(404, "<b>The Requested resource not found ...." + "Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>", false);
					}
				}
			}
			else if(http_Method.equals("PUT")){
			                String msg_length = token.nextToken();
			                msg_length = token.nextToken();
			                File i_file = new File(file_Name);
			                if (!i_file.exists())
							{
			    	               i_file.createNewFile();
			                }
			                FileWriter i_file_writer = new FileWriter(i_file.getName());
			                BufferedWriter bw = new BufferedWriter(i_file_writer);
							
			                String l = read_From_Client.readLine();
							bw.write(l);			                
			                bw.close();
			                System.out.println("File saved");
			                send_Response(200,"File was saved at server side",false);
		            
			}
		}catch(SocketTimeoutException s){
			System.out.println("Socket timed out!");			
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Caught Exception");			
		}
	}
	public void send_Response (int status_Code, String response_String, boolean isFile) throws Exception {
		String status_Line = null;
		String server_details = "Server: Java HTTPServer";
		String content_Length_Line = null;
		String file_Name = null;
		String content_Type_Line = "Content-Type: text/HTML" + "\r\n";
		FileInputStream file_input_stream = null;
		if (status_Code == 200)
			status_Line = "HTTP/1.1 200 OK" + "\r\n";
		else
			status_Line = "HTTP/1.1 404 Not Found" + "\r\n";
		if (isFile) {
			file_Name = response_String;
			file_input_stream = new FileInputStream(file_Name);
			content_Length_Line = "Content-Length: " + Integer.toString(file_input_stream.available()) + "\r\n";
			if (!file_Name.endsWith(".htm") && !file_Name.endsWith(".html"))
				content_Type_Line = "Content-Type: \r\n";
		}
		else {
			response_String = AdServer.HTML_START + response_String + AdServer.HTML_END;
			content_Length_Line = "Content-Length: " + response_String.length() + "\r\n";
		}
		out_To_Client.writeBytes(status_Line);
		out_To_Client.writeBytes(server_details);
		out_To_Client.writeBytes(content_Type_Line);
		out_To_Client.writeBytes(content_Length_Line);
		out_To_Client.writeBytes("Connection: close\r\n");
		out_To_Client.writeBytes("\r\n");
		if (isFile) 
			send_File(file_input_stream, out_To_Client);
		else 
			out_To_Client.writeBytes(response_String);
		out_To_Client.close();
	}
	public void send_File (FileInputStream file_input_stream, DataOutputStream out) throws Exception {
		byte[] buff = new byte[1024] ;
		int no_of_bytes_read;
		while ((no_of_bytes_read = file_input_stream.read(buff)) != -1 ) {
			out.write(buff, 0, no_of_bytes_read);
		}
		file_input_stream.close();
	}
	public static void main(String[] args) throws Exception{
		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println ("Waiting for client");
		while(true) {
			System.out.println("Waiting on port number" + serverSocket.getLocalPort() + "...");
			Socket connected = serverSocket.accept();
			(new AdServer(connected)).start();
		}		
	}
}