import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;


public class Inputh extends Thread {
	private InputStream input = null;
	private InputStreamReader isr;
	private BufferedReader br;
	private Socket socket;
	private ArrayBlockingQueue<String> stack;
	
	public Inputh(Socket socket, ArrayBlockingQueue<String> stack) {
		this.setSocket(socket);
		this.stack = stack;
		try {
			input = socket.getInputStream();
		} catch (IOException e) {
			System.out.println("Inputh() error input");
			return;
		}
		isr = new InputStreamReader(input);
		br = new BufferedReader(isr);
	}

	public void run() {
		String str = "";
		while(true) {
			try {
				str = br.readLine();
				try {
					stack.put(str);
				} catch (InterruptedException e) {
					System.out.println("Inputh.run() error stack");
					return;
				}
			} catch (IOException e) {
				System.out.println("Inputh.run() error read");
				return;
			}
		}
	}
	
	public void close() {
		try {
			if(input != null) {
				input.close();
			}
		} catch (IOException e) {
			System.out.println("Inputh.close() error input");
			return;
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
