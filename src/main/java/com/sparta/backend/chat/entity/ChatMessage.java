//package com.sparta.backend.chat.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@NoArgsConstructor
//@Table(name = "chatMessage")
//public class ChatMessage {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long messageId;
//
//    @Column(name = "message", nullable = false)
//    private String message;
//
//    @Column(name = "sendTime", nullable = false)
//    private LocalDateTime sendTime;
//
//    @ManyToOne
//    @JoinColumn(name="userId")
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name="chatRoomId")
//    private ChatRoom chatRoom;
//}
//
