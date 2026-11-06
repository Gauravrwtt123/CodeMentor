package com.codementor.service;
import java.util.concurrent.*;
/** Core Java service layer with interface-based polymorphism and async task execution. */
public class MentorService implements AutoCloseable {
 private final ExecutorService executor; private final Mentor mentor;
 public MentorService(){this(new RuleBasedMentor(),Executors.newFixedThreadPool(2));}
 public MentorService(Mentor mentor,ExecutorService executor){this.mentor=mentor;this.executor=executor;}
 public CompletableFuture<String> answerAsync(String question){return CompletableFuture.supplyAsync(()->mentor.answer(question),executor);}
 @Override public void close(){executor.shutdown();try{if(!executor.awaitTermination(3,TimeUnit.SECONDS))executor.shutdownNow();}catch(InterruptedException e){executor.shutdownNow();Thread.currentThread().interrupt();}}
 private static final class RuleBasedMentor implements Mentor {
  public String answer(String question){String q=question.toLowerCase();
   if(q.contains("inheritance"))return "Inheritance lets a child class reuse or extend a parent class. Example: class Dog extends Animal {}. Use it for an IS-A relationship.";
   if(q.contains("jdbc")||q.contains("database")||q.contains("sql"))return "JDBC steps: obtain Connection, create PreparedStatement, bind parameters, execute, process ResultSet, and close resources with try-with-resources.";
   if(q.contains("array"))return "Arrays have fixed size and indexed access is O(1). ArrayList is resizable; insertion in the middle may require shifting.";
   if(q.contains("thread"))return "A thread is an independent execution path. ExecutorService manages task execution; synchronization protects shared mutable state.";
   if(q.contains("exception"))return "Use try/catch for handling errors, finally when appropriate, and try-with-resources for AutoCloseable objects. Avoid swallowing exceptions.";
   return "Break the problem into inputs, outputs, constraints, a small example, pseudocode, implementation, and edge-case tests.";
  }
 }
}
