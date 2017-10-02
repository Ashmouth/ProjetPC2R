import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Stack {
	private ArrayList<String> stack;
	private Lock lock;
	final Condition notEmpty; 
	
	public Stack() {
		lock = new ReentrantLock();
		notEmpty = lock.newCondition();
		stack = new ArrayList<String>();
	}

	public String get() {
		if(stack.size() == 0) {
			try {
				notEmpty.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return stack.remove(0);
	}
	
	public void take() {
		lock.lock();
	}
	
	public void release() {
		lock.unlock();
	}
	
	public void add(String str) {
		stack.add(str);
		notEmpty.notify();
	}
}
