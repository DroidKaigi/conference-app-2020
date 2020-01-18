package io.github.droidkaigi.confsched2020

import androidx.core.content.ContextCompat
import com.facebook.stetho.Stetho
import com.facebook.stetho.dumpapp.DumperPlugin
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain
import java.io.File

class DebugApp : App() {
    override fun onCreate() {
        super.onCreate()
        Stetho
            .initialize(object : Stetho.Initializer(this) {
                override fun getDumperPlugins(): Iterable<DumperPlugin>? {
                    return Stetho.DefaultDumperPluginsBuilder(applicationContext).finish()
                }

                override fun getInspectorModules(): Iterable<ChromeDevtoolsDomain>? {
                    return Stetho
                        .DefaultInspectorModulesBuilder(applicationContext)
                        .databaseFiles {
                            val dataDir = ContextCompat.getDataDir(applicationContext)
                            // Add WorkManager database
                            File(dataDir, "no_backup").listFiles().toList() +
                                    File(dataDir, "databases").listFiles().toList()
                        }
                        .finish()
                }
            })
    }
}