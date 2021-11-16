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
	            // 1. ���� ���� ��ü ����
	            serverSocket = new ServerSocket();

	            // 2. ������ ȣ��Ʈ�� ��Ʈ�� binding
	            String localHostAddress = InetAddress.getLocalHost().getHostAddress();
	            serverSocket.bind(new InetSocketAddress(localHostAddress, SERVER_PORT));
	            System.out.println("[server] binding! \naddress:" + localHostAddress + ", port:" + SERVER_PORT);

	            // 3. Ŭ���̾�Ʈ�κ��� ���� ��û�� �� ������ ���
	            // ���� ��û�� ���� ������ ������ block �����̸�,
	            // Ŭ���̾�Ʈ�� ������ newProcessThread �����ڿ� ������ 
	            // ä�ù� ������ ��� (senders) �� ���� �Ѱ���
	            
	            while(true) {
	            	Socket socket = serverSocket.accept();
	            	InetSocketAddress remoteSocketAddress =(InetSocketAddress)socket.getRemoteSocketAddress();
	            	
	                String remoteHostName = remoteSocketAddress.getAddress().getHostAddress();
	                int remoteHostPort = remoteSocketAddress.getPort();
	                
	                System.out.println("[server] ä�ù� ���� ip :" + remoteHostName
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
