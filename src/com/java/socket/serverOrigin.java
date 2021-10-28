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
		// Ŭ���̾�Ʈ ������ ���� threadpool�� �����Ѵ�. ������ Ǯ�ȿ��� �ִ� 10���� �����带 ������ų �� �ִ�.
		ExecutorService clientService = Executors.newFixedThreadPool(10);
		// serverSocket�� �����Ѵ�.
		try (ServerSocket server = new ServerSocket()) {
			// ��Ʈ�� 9999�� �����Ѵ�.
			InetSocketAddress ipep = new InetSocketAddress(3000);
			server.bind(ipep);
			while (true) {
				// Ŭ���̾�Ʈ�� ������ ������ ����Ѵ�.
				Socket client = server.accept();
				// Ŭ���̾�Ʈ�� ������ �Ǹ� ������ Ǯ�� �����带 �ϳ� �����ϰ� inputstream�� outputstream�� �޴´�.
				clientService.submit(() -> {
					try (OutputStream sender = client.getOutputStream();
							InputStream receiver = client.getInputStream();) {
						// ���� ���� ��⸦ �Ѵ�.
						while (true) {
							byte[] data = new byte[4];
							// ������ ���̸� �޴´�.
							receiver.read(data, 0, 4);
							// ByteBuffer�� ���� little ����� �������� ������ ���̸� ���Ѵ�.
							ByteBuffer b = ByteBuffer.wrap(data);
							b.order(ByteOrder.LITTLE_ENDIAN);
							int length = b.getInt();
							// �����͸� ���� ���۸� �����Ѵ�.
							data = new byte[length];
							// �����͸� �޴´�.
							receiver.read(data, 0, length);
							// byte������ �����͸� string�������� ��ȯ�Ѵ�.
							String msg = new String(data, "UTF-8");
							// �ֿܼ� ����Ѵ�.
							System.out.println(msg);
							// echo�� ������.
//							msg = "Java server echo : " + msg;
//							// string�� byte�迭 �������� ��ȯ�Ѵ�.
//							data = msg.getBytes();
//							// ByteBuffer�� ���� ������ ���̸� byte�������� ��ȯ�Ѵ�.
//							b = ByteBuffer.allocate(4);
//							// byte������ little ������̴�.
//							b.order(ByteOrder.LITTLE_ENDIAN);
//							b.putInt(data.length);
//							// ������ ���� ����
//							sender.write(b.array(), 0, 4);
//							// ������ ����
//							sender.write(data);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						try {
							// ������ �߻��ϸ� ������ �����Ѵ�.
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
