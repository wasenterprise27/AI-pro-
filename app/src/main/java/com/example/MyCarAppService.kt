package com.example

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.model.*
import androidx.car.app.validation.HostValidator

/**
 * MyCarAppService provides the entry point for Android Auto and Android Automotive OS
 * hosts connecting via the Car App Library (androidx.car.app.CarAppService).
 */
class MyCarAppService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        // Allows all hosts for development, emulator testing, and broad vehicle compatibility
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return MyCarAppSession()
    }
}

/**
 * Session manages the lifecycle of the in-vehicle display and creates the initial Screen.
 */
class MyCarAppSession : Session() {

    override fun onCreateScreen(intent: Intent): Screen {
        return MyCarAppScreen(carContext)
    }
}

/**
 * MyCarAppScreen presents the in-vehicle automotive UI using template-based components
 * adhering to driver distraction guidelines (large touch targets, minimal distraction).
 */
class MyCarAppScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("⚡ ContentGen AI — In-Car Studio")
                .addText("Hands-free viral hooks & video script generation for creators on the go.")
                .build()
        )

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("🎙️ Hands-Free Voice Dictation")
                .addText("Voice dictation ready. Capture inspiration while parked or charging.")
                .build()
        )

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("📱 Multi-Platform Schedule")
                .addText("Scheduled drops across TikTok, YouTube Shorts & Instagram Reels.")
                .build()
        )

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("💰 AdMob & In-App Purchases")
                .addText("Live ad revenue ($1,428.50) & Watch-to-Earn AI credits.")
                .build()
        )

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("🚗 Vehicle Sound System")
                .addText("Connected: EV Premium Audio • High Fidelity Text-to-Speech.")
                .build()
        )

        return ListTemplate.Builder()
            .setTitle("AI pro 001 — Car Studio")
            .setSingleList(itemListBuilder.build())
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}
