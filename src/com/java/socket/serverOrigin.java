package com.java.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class serverOrigin {
	public static void main(String... args) {
		// 클라이언트 소켓을 받을 threadpool를 선언한다. 쓰레드 풀안에는 최대 10개의 쓰레드를 가동시킬 수 있다.
		ExecutorService clientService = Executors.newFixedThreadPool(10);
		// serverSocket를 선언한다.
		try (ServerSocket server = new ServerSocket()) {
			// 포트는 9999로 오픈한다.
			InetSocketAddress ipep = new InetSocketAddress(3000);
			server.bind(ipep);
			while (true) {
				// 클라이언트가 접속할 때까지 대기한다.
				Socket client = server.accept();
				// 클라이언트가 접속이 되면 쓰레드 풀에 쓰레드를 하나 생성하고 inputstream과 outputstream을 받는다.
				clientService.submit(() -> {
					try (OutputStream sender = client.getOutputStream();
							InputStream receiver = client.getInputStream();) {
						// 서버 무한 대기를 한다.
						while (true) {
							byte[] data = new byte[4];
							// 데이터 길이를 받는다.
							receiver.read(data, 0, 4);
							// ByteBuffer를 통해 little 엔디언 형식으로 데이터 길이를 구한다.
							ByteBuffer b = ByteBuffer.wrap(data);
							b.order(ByteOrder.LITTLE_ENDIAN);
							int length = b.getInt();
							// 데이터를 받을 버퍼를 선언한다.
							data = new byte[length];
							// 데이터를 받는다.
							receiver.read(data, 0, length);
							// byte형식의 데이터를 string형식으로 변환한다.
							String msg = new String(data, "UTF-8");
							// 콘솔에 출력한다.
							System.out.println(msg);
							// echo를 붙힌다.
//							msg = "Java server echo : " + msg;
//							// string을 byte배열 형식으로 변환한다.
//							data = msg.getBytes();
//							// ByteBuffer를 통해 데이터 길이를 byte형식으로 변환한다.
//							b = ByteBuffer.allocate(4);
//							// byte포멧은 little 엔디언이다.
//							b.order(ByteOrder.LITTLE_ENDIAN);
//							b.putInt(data.length);
//							// 데이터 길이 전송
//							sender.write(b.array(), 0, 4);
//							// 데이터 전송
//							sender.write(data);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						try {
							// 에러가 발생하면 접속을 종료한다.
							client.close();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
