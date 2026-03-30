package org.bzu.antifzusportanticheat

import android.location.Location
import android.util.Log
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam


class ModuleMain : XposedModule() {
    companion object {
        const val TAG = "FuckFzuSport"
    }

    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != "com.hz.smartsports") return

        log(Log.INFO, TAG, "Hooking mediaService...")

        val mediaServiceClass = Class.forName("com.hz.smartsports.ui.activity.smartrun2.MediaServiceV2", true, param.classLoader)
        val updateGpsMethod = mediaServiceClass.getDeclaredMethod("updateGps", Location::class.java)
        val isCheatField = mediaServiceClass.getDeclaredField("isCheat").apply { isAccessible = true }
        val cheatCountFiled = mediaServiceClass.getDeclaredField("cheatCount").apply { isAccessible = true }
        hook(updateGpsMethod).intercept { chain ->
            chain.proceed()

            val location = chain.args[0] as Location

            log(Log.INFO, TAG, "IsMock: " + location.isFromMockProvider)
            log(Log.INFO, TAG, "CheatCount: " + cheatCountFiled.getInt(chain.thisObject))
            log(Log.INFO, TAG, "IsCheat: " + isCheatField.getBoolean(chain.thisObject))

            isCheatField.set(chain.thisObject, false)
            cheatCountFiled.setInt(chain.thisObject, 0)
        }

        log(Log.INFO, TAG, "Hooking mediaService completed")
    }
}
