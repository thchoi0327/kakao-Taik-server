package com.java.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MultiServer extends Thread{
	
	private String nickName = null;
	private Socket socket = null;
	ArrayList<Socket> senderList = null;
	ArrayList<String> senderNameList = null;
	
	public MultiServer(Socket socket, ArrayList<Socket> senderlist,ArrayList<String> senderNameList) throws IOException {
		this.socket = socket;
		this.senderList = senderlist;
		this.senderNameList = senderNameList;
	}
	
	@Override
	public void run() {
        try {
        	OutputStream send = socket.getOutputStream();
			InputStream rcv= socket.getInputStream();

            while(true) {
            	byte[] data = new byte[4];
            	rcv.read(data, 0, 4);
				ByteBuffer b = ByteBuffer.wrap(data);
				b.order(ByteOrder.LITTLE_ENDIAN);
				int length = b.getInt();
				data = new byte[length];
				rcv.read(data, 0, length);
				String msg = new String(data, "UTF-8");
				
                if(msg == null) {
                    consoleLog("Å¬¶óÀÌ¾ðÆ®·ÎºÎÅÍ ¿¬°á ²÷±è");
                    doQuit(socket);
                    break;
                }
                	
                String[] tokens = msg.split(":");
                if("join".equals(tokens[0])) {
                    doJoin(tokens[1], socket);
                }
                else if("message".equals(tokens[0])) {
                    doMessage(tokens[1],socket);
                }
                else if("quit".equals(tokens[0])) {
                    doQuit(socket);
                }
                else if("/w".equals(tokens[0])) {
                	if (tokens[1].equals("") || tokens[2].equals("")) {
                		System.out.println("±Ó¼Ó¸» Çü½ÄÀÌ Àß¸øµÊ");
                	}else {
                		String recName = tokens[1]; 
                		System.out.println(this.nickName+"(±Ó¼Ó¸») : "+tokens[2]);
                		dowhisper(this.nickName+"(±Ó¼Ó¸») : "+tokens[2],socket,recName);
                		
                	}
                }
            }
        }
        catch(IOException e) {
        	doQuit(socket);
        	e.printStackTrace();
        }
	}
	private void doQuit(Socket socket) {
        removeWriter(socket);
        String data = this.nickName + "´ÔÀÌ ÅðÀåÇß½À´Ï´Ù.";
        broadcast(data,socket);
    }

    private void removeWriter(Socket socket) {
        synchronized (senderList) {
        	senderList.remove(socket);
        	try {
        		socket.close();
        		System.out.println("socket close ½ÇÇàµÊ ");
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }
    }

    private void doMessage(String data, Socket socket) {
        broadcast(this.nickName + ":" + data, socket);
    }

    private void doJoin(String nickname, Socket socket) {
        this.nickName = nickname;

        String msg = nickname + "´ÔÀÌ ÀÔÀåÇÏ¿´½À´Ï´Ù.";
        System.out.println(msg);
        // writer pool¿¡ ÀúÀå
        broadcast(msg,socket);
        
        addWriter(socket);

    }

    private void addWriter(Socket writer) {
        synchronized (senderList) {
        	senderList.add(socket);
        	senderNameList.add(this.nickName);
        }
    }

    private void broadcast(String msg, Socket socket) {
        synchronized (senderList) {
        	System.out.println("broadcast MSG = > "+msg);
            for(Socket sender : senderList) {
            	if (sender == socket) {
            		continue;
            	}
            	byte[] data = new byte[4];
            	data = msg.getBytes();
            	ByteBuffer b = ByteBuffer.wrap(data);
            	b = ByteBuffer.allocate(4);
				b.order(ByteOrder.LITTLE_ENDIAN);
				b.putInt(data.length);
				try {
					sender.getOutputStream().write(b.array(), 0, 4);
					sender.getOutputStream().write(data);
				}catch( Throwable e) {
					e.printStackTrace();
				}
            }
        }
    }
    
    private void dowhisper(String msg, Socket socket,String recName) {
    	synchronized (senderNameList) {
    		int i = 0;
    		for(String sender : senderNameList) {
    			if(recName.equals(sender)) {
    				Socket recSocket = senderList.get(i);
    				byte[] data = new byte[4];
    				data = msg.getBytes();
    				ByteBuffer b = ByteBuffer.wrap(data);
    				b = ByteBuffer.allocate(4);
    				b.order(ByteOrder.LITTLE_ENDIAN);
    				b.putInt(data.length);
    				try {
    					recSocket.getOutputStream().write(b.array(), 0, 4);
    					recSocket.getOutputStream().write(data);
    				}catch( Throwable e) {
    					e.printStackTrace();
    				}
    				return;
    			}else {
    				i++;
    			}
    		}
		}
    }

    private void consoleLog(String log) {
        System.out.println(log);
    }
}
