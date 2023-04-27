package com.stardust.auojs.inrt.automyjsa

import android.content.Context
import com.stardust.automyjsa.engine.LoopBasedJavaScriptEngine
import com.stardust.automyjsa.engine.encryption.ScriptEncryption
import com.stardust.automyjsa.script.EncryptedScriptFileHeader
import com.stardust.automyjsa.script.JavaScriptFileSource
import com.stardust.automyjsa.script.ScriptSource
import com.stardust.automyjsa.script.StringScriptSource
import com.stardust.pio.PFiles
import java.io.File
import java.security.GeneralSecurityException

class XJavaScriptEngine(context: Context) : LoopBasedJavaScriptEngine(context) {


    override fun execute(source: ScriptSource, callback: ExecuteCallback?) {
        if (source is JavaScriptFileSource) {
            try {
                if (execute(source.file)) {
                    return
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                return
            }
        }
        super.execute(source, callback)
    }

    private fun execute(file: File): Boolean {
        val bytes = PFiles.readBytes(file.path)
        if (!EncryptedScriptFileHeader.isValidFile(bytes)) {
            return false
        }
        try {
            super.execute(StringScriptSource(file.name, String(ScriptEncryption.decrypt(bytes, EncryptedScriptFileHeader.BLOCK_SIZE))))
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return true
    }

}