package com.yapp.web2.infra.fcm

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FirebaseService {

    private val log = LoggerFactory.getLogger(javaClass)

    fun sendMessage(targetToken: String, key: String, value: String, title: String, body: String) {
        // 로컬 테스트
//        val firebaseInit = FirebaseInit()
//        firebaseInit.init()

        val notification = makeNotification(title, body)
        val message = makeMessage(targetToken, key, value, notification)
        val firebaseApp = FirebaseApp.getInstance()
        val response = FirebaseMessaging.getInstance(firebaseApp).send(message)
        log.info("Firebase Cloud Messaging Response : $response")
    }

    fun makeMessage(targetToken: String, key: String, value: String, notification: Notification): Message {
        return Message.builder()
            .setToken(targetToken)
            .setNotification(notification)
            .putData(key, value)
            .build()
    }

    fun makeNotification(title: String, body: String): Notification {
        return Notification(title, body)
    }
}