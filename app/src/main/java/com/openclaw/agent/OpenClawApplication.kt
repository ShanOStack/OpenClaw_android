package com.openclaw.agent

import android.app.Application
import com.openclaw.agent.data.TaskDatabase

class OpenClawApplication : Application() {
    val database: TaskDatabase by lazy { TaskDatabase.getDatabase(this) }

    companion object {
        lateinit var instance: OpenClawApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
