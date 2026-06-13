package me.marie.toolshot.clipboard

import com.mojang.blaze3d.platform.MacosUtil
import java.awt.Toolkit
import java.awt.image.BufferedImage

/**
 * Utility class taken from https://github.com/DeDiamondPro/ChatShot/blob/master/src/main/java/dev/dediamondpro/chatshot/util/clipboard/ClipboardUtil.java
 */
object ClipboardUtil {
    fun copy(image: BufferedImage): Boolean {
        return copy(TransferableImage(image))
    }

    fun copy(transferable: TransferableImage): Boolean {
        if (MacosUtil.IS_MACOS) return false
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferable, null)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}