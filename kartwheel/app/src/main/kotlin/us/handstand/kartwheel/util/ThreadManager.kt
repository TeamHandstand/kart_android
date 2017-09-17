package us.handstand.kartwheel.util

import java.util.concurrent.Executors


object ThreadManager {
    val databaseExecutor = Executors.newSingleThreadExecutor()!!
    val scheduler = Executors.newSingleThreadScheduledExecutor()!!
}