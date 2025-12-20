public class Stack<E> {
    private final Object[] array;
    private int front;
    private final int capacity;
    Stack(int capacity) {
        this.capacity = capacity;
        array = new Object[capacity];
        front = 0;
    }
    public boolean isEmpty() {
        return front == 0;
    }
    public boolean isFull() {
        return front == capacity;
    }
    public int size() {
        return front;
    }
    public E pop(){
        if(isEmpty()) return null;
        front--;
        E temp = (E) array[front];
        array[front] = null;
        return temp;
    }
    public void push(E e) throws Exception{
        if(isFull()) throw new Exception("Stack is full");
        array[front] = e;
        front++;
    }
}
