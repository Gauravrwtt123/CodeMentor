package com.codementor.model;
import java.time.LocalDateTime;
/** Immutable model for one persisted mentor conversation. */
public final class ChatMessage {
 private final long id; private final int userId; private final String prompt,response; private final LocalDateTime createdAt;
 public ChatMessage(long id,int userId,String prompt,String response,LocalDateTime createdAt){this.id=id;this.userId=userId;this.prompt=prompt;this.response=response;this.createdAt=createdAt;}
 public long getId(){return id;} public int getUserId(){return userId;} public String getPrompt(){return prompt;} public String getResponse(){return response;} public LocalDateTime getCreatedAt(){return createdAt;}
}
