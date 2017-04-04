import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Inputh implements Runnable{
	static ArrayList<String> pile;
	static InputStream input = null;
	Socket socket;
	Lock lock;
	
	public Inputh(Socket socket) {
		this.socket = socket;
		lock = new ReentrantLock();
		pile = new ArrayList<String>();
		try {
			input = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String str = "";
		while(true) {
			try {
				str = new BufferedReader(new InputStreamReader(input)).readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			take();
			add(str);
			release();
		}
	}

	public String get() {
		return pile.remove(0);
	}
	
	public void take() {
		lock.lock();
	}
	
	public void release() {
		lock.unlock();
	}
	
	public static void add(String str) {
		pile.add(str);
	}
	
	public void close() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
