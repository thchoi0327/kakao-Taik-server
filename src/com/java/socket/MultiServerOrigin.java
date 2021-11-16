package com.java.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MultiServerOrigin {

	public static void main(String[] args) {
		
		 	final int SERVER_PORT = 3000;
	        ServerSocket serverSocket = null;
	        ArrayList<Socket> senderList = new ArrayList<Socket>();
	        ArrayList<String> senderNameList = new ArrayList<String>();
	        
	        try{
	            // 1. 서버 소켓 객체 생성
	            serverSocket = new ServerSocket();

	            // 2. 소켓을 호스트의 포트와 binding
	            String localHostAddress = InetAddress.getLocalHost().getHostAddress();
	            serverSocket.bind(new InetSocketAddress(localHostAddress, SERVER_PORT));
	            System.out.println("[server] binding! \naddress:" + localHostAddress + ", port:" + SERVER_PORT);

	            // 3. 클라이언트로부터 연결 요청이 올 때까지 대기
	            // 연결 요청이 오기 전까지 서버는 block 상태이며,
	            // 클라이언트의 소켓을 newProcessThread 생성자에 던져줌 
	            // 채팅방 참가자 명단 (senders) 랑 같이 넘겨줌
	            
	            while(true) {
	            	Socket socket = serverSocket.accept();
	            	InetSocketAddress remoteSocketAddress =(InetSocketAddress)socket.getRemoteSocketAddress();
	            	
	                String remoteHostName = remoteSocketAddress.getAddress().getHostAddress();
	                int remoteHostPort = remoteSocketAddress.getPort();
	                
	                System.out.println("[server] 채팅방 참가 ip :" + remoteHostName
	                        + ", port:" + remoteHostPort);
	            	new MultiServerThread(socket,senderList,senderNameList).start();
	            }
	        }
	        catch(IOException e){
	            e.printStackTrace();
	        }
	        finally{
	            try{
	                if( serverSocket != null && !serverSocket.isClosed() ){
	                    serverSocket.close();
	                }
	            }
	            catch(IOException e){
	                e.printStackTrace();
	            }
	        }
	}
}
