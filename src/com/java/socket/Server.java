package com.java.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	public static void main(String[] args){
		ExecutorService clientService = Executors.newFixedThreadPool(10);
		try (ServerSocket server = new ServerSocket()){
			InetSocketAddress ipep = new InetSocketAddress(3000);
			server.bind(ipep);
			while(true) {
				System.out.println("��� �� . . .");
				Socket client = server.accept();
				
				clientService.submit(() -> {
					loop1 : try (OutputStream sender = client.getOutputStream();
							InputStream receiver = client.getInputStream();){
						while(true) {
							byte[] data = new byte[4];
							receiver.read(data, 0, 4);
							ByteBuffer b= ByteBuffer.wrap(data);
							b.order(ByteOrder.LITTLE_ENDIAN);
							int length = b.getInt();
							data = new byte[length];
							receiver.read(data, 0, length);
							String msg = new String(data, "UTF-8");
							System.out.println("�޽��� : "+msg);
							if (msg.equals("")) {
								System.out.println("����");
								break loop1;
							}
							msg = "Java server echo : " + msg;
							// string�� byte�迭 �������� ��ȯ�Ѵ�.
							data = msg.getBytes();
							// ByteBuffer�� ���� ������ ���̸� byte�������� ��ȯ�Ѵ�.
							b = ByteBuffer.allocate(4);
							// byte������ little ������̴�.
							b.order(ByteOrder.LITTLE_ENDIAN);
							b.putInt(data.length);
							// ������ ���� ����
							sender.write(b.array(), 0, 4);
							// ������ ����
							sender.write(data);
						}
					}catch(Throwable e) {
						e.printStackTrace();
					}finally {
						try {
							System.out.println("Ŭ���̾�Ʈ ����");
							client.close();
						}catch(Throwable e) {
							e.printStackTrace();
						}
					}
				});
				
			}
			
		}catch(Throwable e) {
			e.printStackTrace();		
		}
	

	}
}
