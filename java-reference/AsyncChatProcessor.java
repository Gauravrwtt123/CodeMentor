public class AsyncChatProcessor implements Runnable {
    // Shared resource
    private final List<String> chatQueue = new ArrayList<>();
    
    // 1. Synchronization to prevent race conditions
    public synchronized void enqueueMessage(String userMessage) {
        chatQueue.add(userMessage);
        notifyAll(); // Wake up the processing thread
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (chatQueue.isEmpty()) {
                    try { wait(); } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
            // 2. Multithreading: Process the message in the background
            String msg = chatQueue.remove(0);
            processAndGenerateResponse(msg);
        }
    }

    private void processAndGenerateResponse(String msg) {
        // Simulate AI thinking time
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        System.out.println("Bot: Here is the answer for -> " + msg);
    }
}