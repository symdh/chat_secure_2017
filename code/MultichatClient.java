
import java.net.*; 
import java.io.*; 
import java.util.*; 
import java.security.*;
import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

// 실행 방법 
// java MultichatClient 대화명  
public class MultichatClient {

     static symUtill sym = new symUtill ();
     static boolean is_R_S ;
     public static void main(String[] args) {
 
        // 첫번째 실행 인자를 추출한다. 
        if (args.length != 1) {
             System.out.println("USAGE : java MultichatClient 대화명");
             System.exit(0);// 시스템 종료
         } 

        Socket socket = null; 
        try { 

             // port config
            System.out.print("접속할 포트를 입력하세요. :");
            Scanner input = new Scanner(System.in);
            int port = input.nextInt();
            socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
            System.out.println("서버에 연결되었습니다.");
 
            // 메시지 수신용 Thread 생성 
            Thread receiver = new Thread(new ClientReceiver(socket, args[0]));

            // 메시지 전송용 Thread 생성 
            Thread sender = new Thread(new ClientSender(socket, args[0]));
 
            
 
            sender.start(); 
            receiver.start(); 
        } catch (ConnectException ce) { 
            ce.printStackTrace(); 
        } catch (Exception e) { 
        } 
    }// main 

    // 메시지 전송용 Thread 
     static class ClientSender implements Runnable {
         Socket socket; 
        DataOutputStream out; 
        String name; 
        String msg;


        ClientSender(Socket socket, String name) { 
            this.socket = socket; 
            this.name = name; 
        
            try { 
                this.out = new DataOutputStream(socket.getOutputStream());
             } catch (Exception e) { 
            } 
        } 

        public void run() {
             

            Scanner scanner = new Scanner(System.in);            

             
             try { 
                while (out != null) { 

//////////////////////////////////////암호화 적용
                    // 키보드로 입력받은 데이터를 서버로 전송
                    msg = "[" + name + "]" + scanner.nextLine();
                    out.writeUTF(sym.sym_encrypt(msg));    

                     // out.writeUTF("[" + name + "]" + scanner.nextLine());
////////////////////////////////////////
                 } 
            } catch (IOException e) { 
            } 
        } 
    } 


// 메시지 수신용 Thread 
     static class ClientReceiver implements Runnable {
         Socket socket; 
        DataInputStream in; 
        String name;
        String msg;
        String mod;
        DataOutputStream out; 

        // 생성자 
        ClientReceiver(Socket socket, String name) { 
            this.socket = socket; 
            this.name = name; 

            try { 
                // 서버로 부터 데이터를 받을 수 있도록 DataInputStream 생성
                 this.in = new DataInputStream(socket.getInputStream());
                  this.out = new DataOutputStream(socket.getOutputStream());
             } catch (IOException e) { 
            } 
        } 

        public void run() {
    

             try { 
//////////////////////////////////////////////////////////////암호화 적용                 
                 mod =  in.readUTF();

                 switch(mod) {
                    case "10":
                        sym.S_devel_mod(false);
                        sym.S_encrypt_mod(1);
                    break;

                    case "11":
                        sym.S_devel_mod(true);
                        sym.S_encrypt_mod(1);
                    break;

                     case "20":
                        sym.S_devel_mod(false);
                        sym.S_encrypt_mod(2);
                    break;

                     case "21":
                        sym.S_devel_mod(true);
                        sym.S_encrypt_mod(2);
                    break;

                     case "30":
                        sym.S_devel_mod(false);
                        sym.S_encrypt_mod(3);
                    break;

                     case "31":
                        sym.S_devel_mod(true);
                        sym.S_encrypt_mod(3);
                    break;

                    default:
                         System.out.println("에러발생. 다시 접속해주세요.");
                 }

                 is_R_S = true;

                 System.out.println("서버에 접속하였습니다.");


                   while (out != null && is_R_S ) {

                     // 시작하자 마자, 자신의 대화명을 서버로 전송 
                      // out.writeUTF(name); 
                    if(is_R_S){
                           out.writeUTF(sym.sym_encrypt(name));
                           is_R_S = false;
                        break;

                    }
                 } 



// ////////////////////////////////////////////////////////////// 

//                 // 대화명을 받아, 전에 클라이언트에게 대화방 참여 메시지를 보낸다. 
//                 ip =  "[" + socket.getInetAddress()  + ":" + socket.getPort() + "]" + "에서 ";
//                 sendToAll("#" + name + "님이 들어오셨습니다.", ip);
               
//                 // 대화명, 클라이언로 메시지를 보낼 수 있는 OutputStream 객체를
//                  // 대화방 Map에 저장한다.  
//                 clients.put(name, out); 


//                                  // 처음 접속시 메세지 전달
//                     msg = "메세지 테스트 ㅇㅇ";
//                     DataOutputStream out = clients.get(name); 
//                      out.writeUTF(sym.sym_encrypt(msg));                 
//                 System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
                  


            
               
//////////////////////////////////////암호화 적용
                    while (in != null) { 
                        msg = in.readUTF();
                        msg = sym.sym_decrypt(msg);
                        System.out.println(msg);
                    }
                    // 서버로 부터 전송되는 데이터를 출력  
                    // System.out.println(in.readUTF()); 

////////////////////////////////////////
                } catch (IOException e) { 


                } 
            
        } 
    } 
} 