import java.net.*;
import java.util.Arrays;
import java.io.*;

public class Connection extends Thread {
    final int TIMEOUT = 5;
    final int MAX_REQUESTS = 100;
    Socket connection;
    File file;
    int http_code;
    int document_type;
    int requests;
    boolean timeout = true;

    public Connection(Socket that){
        this.connection = that;
        this.http_code = -1;
        this.requests = 1;
    }

    public void run(){
        
        try{
            connection.setSoTimeout(TIMEOUT * 1000);
            byte[] request = get_request();
            if(request.length > 0){
                handle_request(request);
                send_response();
                System.out.println("Response sent.");
            }
        } catch (SocketException e){
            System.out.println("Socket timed out");
        } catch (FileNotFoundException e){
            System.out.println("File not found: " + e.toString());
        }

        try{
            this.connection.shutdownOutput();
            this.connection.close();
            System.out.println("Connection closed");
        } catch (Exception e) {
            System.out.println("Exception when closing connection: " + e.toString());
        }
    }
    private void output(byte[] response){
        try{
            this.connection.getOutputStream().write(response);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    private void send_file(File file){
        try{
            FileInputStream in_file = new FileInputStream(file);
            int i = 0;
            while(i != -1){
                byte[] cache = new byte[2048];
                i = in_file.read(cache);
                output(cache);
            }
            in_file.close();
        } catch (FileNotFoundException e){
            System.out.println(e);
        } catch (Exception e){
            System.out.println(e);
        }    

    }
    private void send_response(){
        // System.out.println("HTTP CODE: " + this.http_code);
        if(this.http_code != 200) output(HTTP.response(this.http_code));
        else{
            output(HTTP.response(this.http_code, this.document_type));
            send_file(this.file);
        }
    }
    private void handle_request(byte[] request)
            throws FileNotFoundException{
            byte[] check = "GET".getBytes();

            if (Arrays.equals(HTTP.header_field(request), check)){
                byte[] document;
                File file;
                request = HTTP.next_field(request);
                document = HTTP.header_field(request);
                System.out.println(new String(document));
                if (Arrays.equals(document, "/".getBytes())) document = "/index.html".getBytes();
                System.out.println("page request:");
                System.out.println("html" + new String(document));

                request = HTTP.next_field(request);
                System.out.println(new String(HTTP.header_field(request)));
                check = "HTTP/1.1".getBytes();
                if (Arrays.equals(HTTP.header_field(request), check)){
                    file = new File("html" + new String(document));
                    if(!file.exists()) this.http_code = 404;
                    else{
                        this.file = file;
                        this.document_type = HTTP.document_type(document);
                        this.http_code = 200;
                    }
                }
            }
            else{
                this.http_code = 400;
            }
        }
     
    private byte[] get_request(){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try{
            InputStream input = this.connection.getInputStream();
            byte[] cache = new byte[4*1024];
            int i = 0;
            boolean open = true;

            // reads inputstream until "/r/n/r/n"
            while(open){
                i = input.read(cache);
                if(i != -1) buffer.write(cache, 0, i);

                if(cache[i-1] == 10) // 10 == /n
                    if(cache[i-2] == 13) // 13 == /r
                        if(cache[i-3] == 10)
                            if(cache[i-4] == 13) open = false;
            
            }
            // System.out.println(new String(buffer.toByteArray()));

        }catch(SocketTimeoutException e){
            System.out.println("Socket timed out");
        }catch(IOException e){
            System.out.println(e.toString());
        }
        return buffer.toByteArray();
    }
    
}
