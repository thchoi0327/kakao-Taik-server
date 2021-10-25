package com.java.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Server {

	ServerSocket server;
	int port;
	Socket serverSocket;
	
	OutputStream outputStream;
	InputStream inputStream;
	
	public Server(int port) {
		this.port = port;
		try {
			serverSocket = makeServer(port);
			inputStream = connectInputStream();
			outputStream = connectOutputStream();
			while(true) {
				String msg = receiveMessageFromClient();
				System.out.println("�͸� 1 :"+msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Socket makeServer(int port) throws IOException {
		server = new ServerSocket(port);
		System.out.println("���� ��� ��  . . . . ");
		serverSocket = server.accept();
		System.out.println("������ ����");
		return serverSocket;
	}
	
	public InputStream connectInputStream() throws IOException {
		inputStream = serverSocket.getInputStream();
		System.out.println("[Server] �����͸� �޴� ��� ����");
		return inputStream;
	}
	public OutputStream connectOutputStream() throws IOException {
		outputStream = serverSocket.getOutputStream();
		System.out.println("[Server] �����͸� ������ ��� ����");
		return outputStream;
	}
	
	public void sendMessageToClient(String msg) throws IOException {
		System.out.println("[Server] Ŭ���̾�Ʈ�� ������ �޽��� ==>"+msg);
	}
	public String receiveMessageFromClient() throws IOException {
		byte[] data = new byte[4];
		inputStream.read(data, 0, 4);
		ByteBuffer b = ByteBuffer.wrap(data);
		b.order(ByteOrder.LITTLE_ENDIAN);
		int length = b.getInt();
		data = new byte[length];
		inputStream.read(data, 0, length);
		String msg = new String(data, "UTF-8");
		
		return msg;
	}
	
	public static void main(String[] args){
		Server server = new Server(3000);
	}
}
