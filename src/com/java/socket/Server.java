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
				System.out.println("대기 중 . . .");
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
							System.out.println("메시지 : "+msg);
							if (msg.equals("")) {
								System.out.println("멈춤");
								break loop1;
							}
							msg = "Java server echo : " + msg;
							// string을 byte배열 형식으로 변환한다.
							data = msg.getBytes();
							// ByteBuffer를 통해 데이터 길이를 byte형식으로 변환한다.
							b = ByteBuffer.allocate(4);
							// byte포멧은 little 엔디언이다.
							b.order(ByteOrder.LITTLE_ENDIAN);
							b.putInt(data.length);
							// 데이터 길이 전송
							sender.write(b.array(), 0, 4);
							// 데이터 전송
							sender.write(data);
						}
					}catch(Throwable e) {
						e.printStackTrace();
					}finally {
						try {
							System.out.println("클라이언트 닫음");
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
