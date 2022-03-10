public class HTTP {
    public static final byte[] response_200 = (
        "HTTP/1.1 200 OK\r\n").getBytes();
    public static final byte[] response_400 = (
        "HTTP/1.1 400 Bad Request\r\n").getBytes();
    public static final byte[] response_404 = (
        "HTTP/1.1 404 Not Found\r\n").getBytes();
    public static final byte[] response_501 = (
        "HTTP/1.1 501 Not Implemented\r\n").getBytes();
    public static final byte[] response_502 = (
        "HTTP/1.1 502 Bad Gateway\r\n").getBytes();
    public static final byte[] response_505 = (
        "HTTP/1.1 505 HTTP Version Not Supported\r\n").getBytes();
    public static final byte[] content_type_css = (
        "Content-Type: text/css\r\n").getBytes();
    public static final byte[] content_type_html = (
        "Content-Type: text/html\r\n").getBytes();
    public static final byte[] connection_close = (
        "Connection: close\r\n").getBytes();
    public static final byte[] connection_keep_alive = (
        "Connection: Keep-Alive\r\n").getBytes();
    public static final byte[] keep_alive_timeout_max = (
        "Keep-Alive: timeout=5, max=100\r\n").getBytes();        
    public static final byte[] header_end = ("\r\n").getBytes();

    public static byte[] header_field(byte[] header){
        int i = 0;
        while(header[i++] != ' ') if(header[i] == '\n') break;
        byte[] value = new byte[--i];
        while(i-- > 0) value[i] = header[i]; 
        return value;
    }
    public static byte[] next_field(byte[] header){
        int i = 0;
        while(header[i++] != ' ');
        int len = header.length - i;
        byte[] next = new byte[len];
        while(len-- > 0) next[len] = header[len + i];
        return next;
    }
    public static byte[] next_line(byte[] header) {
        int i = 0;
        while(header[i++] != '\n');
        int len = header.length - i;
        byte[] next = new byte[len];
        while(len-- > 0) next[len] = header[len + i];
        return next;
    }
    public static int document_type(byte[] document){
        int i = 0;
        int len = document.length;
        while(document[i] != '.' && i++ < len);
        if (! (i <= len)) return 0;
        else{
            len -= i;
            byte[] type = new byte[len];
            while(len-- > 0) type[len] = document[len + i];
            System.out.println("Type: " + new String(type));

            switch(new String(type)){
                case ".txt"      :   return 1;
                case ".html"     :   return 2;
                case ".css"      :   return 3;
            }
        }
        return 0;
    }
    
    public static byte[] response(int http, int content){
        boolean keep_alive = false;
        byte[] header = new byte[0];
        switch(http){
            case 200    :   header = response_200;
                            break;
            default     :   header = response_505;
                            break;
        }
        // For future implementations
        switch(1){
            case 1      :   header = append(header, connection_close);
                            break;
            case 2      :   header = append(header, connection_keep_alive);
                            keep_alive = true;
                            break;
        }
        switch(content){
            case 2      :   header = append(header, content_type_html);
                            break;
            case 3      :   header = append(header, content_type_css);
                            break;
        }
        if (keep_alive) header = append(header, keep_alive_timeout_max);

        return append(header, header_end);
    }
    public static byte[] response(int http){
        byte[] header = new byte[0];
        switch(http){
            case 400    :   header = response_400;
                            break;
            case 404    :   header = response_404;
                            break;
            case 501    :   header = response_501;
                            break;
            case 502    :   header = response_502;
                            break;
            case 505    :   header = response_505;
                            break;
            default     :   header = response_505;
                            break;
        }
        header = append(header, connection_close);
        return append(header, header_end);
    }
    private static byte[] append(byte[] a1, byte[] a2){
        int len1 = a1.length;
        int len2 = a2.length;
        byte[] appended = new byte[len1 + len2];
        while(len2-- > 0) appended[len1 + len2] = a2[len2]; 
        while(len1-- > 0) appended[len1] = a1[len1]; 
        return appended;
    }
    
}
