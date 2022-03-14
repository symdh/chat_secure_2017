import java.io.*; 
import java.net.*; 
import java.util.*; 
import java.security.*;
import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;


public class MultichatServer {
    
    private boolean devel_mod;
    private int encryptS;
  
    // 대화명, 클라이언트 OutputStream 저장용 대화방(HashMap) 정의
     Map<String, DataOutputStream> clients; 

    // 생성자 
      MultichatServer(boolean devel_mod, int encryptS) { 
        this.devel_mod = devel_mod;
        this.encryptS = encryptS;
        clients = Collections.synchronizedMap( //
                 new HashMap<String, DataOutputStream>());
     } 

    // 비즈니스 로직을 처리하는 메서드 
    public void start() {
         ServerSocket serverSocket = null;    
         Socket socket = null; 

        try { 
             serverSocket = new ServerSocket();    
            //서버 소켓 생성  
             System.out.println("서버가 실행됩니다.");
             System.out.print("오픈할 포트를 입력하세요. :");
             Scanner input = new Scanner(System.in);
             int port = input.nextInt();  
             serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));  //현재 ip주소 기준으로 포트를염
             System.out.println(port+"번 포트가 열렸습니다. client 접속을 기다립니다.");

            while (true) { 
                // 클라이언트 접속 대기 accept() 
                socket = serverSocket.accept(); 
                
                // System.out.println("[" + socket.getInetAddress() // 
                         // + ":" + socket.getPort() + "]" + "에서 접속하였습니다.");

                // 서버에서 클라이언트로 메시지를 전송할 Thread 생성
                 ServerReceiver thread = new ServerReceiver(socket, this.devel_mod, this.encryptS);
                 thread.start(); 

            }// while 

        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            SocketUtil.close(serverSocket); 
        } 
    } // start() 

    // 대화방에 있는 전체 유저에게 메시지 전송 
    void sendToAll(String msg, String ip) {

        symUtill sym = new symUtill ();
        sym.S_devel_mod(this.devel_mod);
        sym.S_encrypt_mod(this.encryptS);
  
        //서버에도 대화 기록을 남김
        if (ip != "0") {
            System.out.println(ip+msg); 
        }


        // 대화방에 접속한 유저의 대화명 리스트 추출 
        Iterator<String> it = clients.keySet().iterator(); 
        msg = sym.sym_encrypt(msg);
        while (it.hasNext()) { 
            try { 
                String name = it.next(); 
                DataOutputStream out = clients.get(name); 
                out.writeUTF(msg); 
            } catch (IOException e) { 
            } 
        } // while 
    } // sendToAll 

    public static void main(String[] args) {
        boolean devel_mod = false;
        int encryptS = 0;

        // 첫번째 실행 인자를 추출한다. 
        if (args.length != 1) {
             System.out.println("USAGE : java MultichatServer mode");
             System.exit(0);// 시스템 종료
          
         } 

         if(args[0].equals("product")){
             devel_mod = false;
          } else if(args[0].equals("develop")){
             devel_mod = true;
          }else {
             System.out.println("USAGE : java MultichatServer mode");
             System.exit(0);// 시스템 종료
           }


               Scanner input = new Scanner(System.in);
              System.out.println("암호 모드를 선택해주세요.");
              System.out.println("1.ECB");
              System.out.println("2.CBC");
              System.out.println("3.CTR");
             encryptS = input.nextInt();  
              if(encryptS != 1 && encryptS != 2 && encryptS != 3) {
                System.out.println("USAGE : java MultichatServer encrypt mode");
                 System.exit(0);// 시스템 종료
                }

         new MultichatServer(devel_mod, encryptS).start(); 
        

    } 

    // Inner Class로 정의 하여, 대화방 field에 접근 할 수 있도록 한다. 
     // 서버에서 클라이언트로 메시지를 전송할 Thread 
    class ServerReceiver extends Thread {
         Socket socket; 
        DataInputStream in; 
        DataOutputStream out; 
        symUtill sym = new symUtill ();

        ServerReceiver(Socket socket,boolean devel_mod, int encryptS) { 

            sym.S_devel_mod(devel_mod);
            sym.S_encrypt_mod(encryptS);
            boolean is_R_S = false;

            this.socket = socket; 
            try { 
                // 클라이언트 소켓에서 데이터를 수신받기 위한 InputStream 생성
                 in = new DataInputStream(socket.getInputStream());
                  
                // 클라이언트 소켓에서 데이터를 전송하기 위한 OutputStream 생성
                 out = new DataOutputStream(socket.getOutputStream());
             } catch (IOException e) { 
            } 
        } 

        public void run() {
             String name = ""; 
              String msg = "";
              String ip = "";


            try { 

                if(devel_mod) {
                    msg = Integer.toString(encryptS*10+1);
                } else {
                    msg  = Integer.toString(encryptS*10+0);
                }


                out.writeUTF(msg); 

               
//////////////////////////////////////////////////////////////암호화 적용                 
                  // 서버에서는 최초에 클라이언트가 보낸 대화명을 받아야 한다. 
                 name = sym.sym_decrypt(in.readUTF());
                 // System.out.println(name);

                  clients.put(name, out); 
                // 처음 접속시 메세지 전달
                DataOutputStream out = clients.get(name); 
                 // out.writeUTF(msg); 
   

////////////////////////////////////////////////////////////// 

                // 대화명을 받아, 전에 클라이언트에게 대화방 참여 메시지를 보낸다. 
                ip =  "[" + socket.getInetAddress()  + ":" + socket.getPort() + "]" + "에서 ";
                sendToAll("#" + name + "님이 들어오셨습니다.", ip);
               

                System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
                  
                // 클라이언트가 전송한 메시지를 받아, 클라이언트에게 메시지를 보낸다. 
                 while (in != null) { 
                     ip = "[" + socket.getInetAddress()  + ":" + socket.getPort() + "]";


                    msg = sym.sym_decrypt(in.readUTF());
                    sendToAll(msg, ip); 
                }//while  
                 
            } catch (IOException e) { 
                // ignore 
            } finally { 

                //위에서 복호화 됨
                // finally절이 실행된다는 것은 클라이언트가 빠져나간 것을 의미한다. 
                 sendToAll("#" + name + "님이 나가셨습니다.", "0");
                  
                // 대화방에서 객체 삭제 
                clients.remove(name); 
                System.out.println("[" + socket.getInetAddress() //
                         + ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.");
                 System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
             } // try 
        } // run 
    } // ReceiverThread 
} // class 