import java.net.*;
import java.io.*;
import java.util.*;

/* Group- Raghava Adarsh Mandarapu, Jeevitha Mahankali*/

import org.omg.Messaging.SyncScopeHelper;
public class AdClient {

	public static void main(String[] args) {
		
		String host_name = args[0];
		int port_num = Integer.parseInt(args[1]);
		String cmd_type = args[2];
		String fileName = args[3];
		try{
			Socket clientSocket = new Socket(host_name,port_num);            
            InputStream inp_stream = clientSocket.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(inp_stream));
			DataOutputStream o_Server = new DataOutputStream( clientSocket.getOutputStream() );
			//BufferedReader inFromServer = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
            OutputStream o_stream = clientSocket.getOutputStream();
            PrintWriter out = new PrintWriter(o_stream,true);  
            if (args[2].equals("GET")){             	
				out.println("GET " +  "/" + fileName + " HTTP/1.1");
				out.println(host_name);
				out.println("The connection is closed");  
				while (true) {
            	    String len = br1.readLine();
            	    if (len == null)
			break; 
			System.out.println(len);
            	}
            }
			else if (args[2].equals("PUT")){
                if (!new File(fileName).isFile()) {
                	System.out.println("Please provide a valid filename");
                	return;
                }
                FileInputStream f_input = new FileInputStream(fileName);
                byte[] buf = new byte[1024];
                int bytes_read;
                String msg = "";
    		    String tmp_msg = "";
    			int no_of_lines = 0;
    		    BufferedReader br2 = new BufferedReader(new FileReader(fileName));
    			while ((tmp_msg=br2.readLine())!= null)
				{
    		    	msg = msg+tmp_msg;
    		    	no_of_lines++;
    		    }
    			br2.close();
    			String message = "PUT /"+fileName+" HTTP/1.1 ";
    			out.println(message+Integer.toString(no_of_lines));
    			out.println("\r\n\r\n");
            		System.out.println("PUT request sent");
    			while ((bytes_read=f_input.read(buf))!=-1)
				{
                	o_Server.write(buf,0,bytes_read);
                }
    			System.out.println(fileName+" file sent");
                f_input.close(); 						
			}
            else {
				System.out.println("Incorrect command.Please choose GET or PUT");
             	return;
            }
			
            br1.close();
            inp_stream.close();	
			o_Server.close();
            out.close();
            o_stream.close();
			clientSocket.close();            
        }
		catch (UnknownHostException e){
			System.out.println("Unknown host_name");
		} 
		catch (IOException e){
			System.out.println("Not able to get I/O for the connection to host_name");
		}      
	}
}