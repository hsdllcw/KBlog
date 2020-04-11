package io.kblog.support.util

import org.apache.commons.codec.binary.Hex

/**
 * The EncodeUtil class.
 * @author hsdllcw on 2020/4/6.
 * @version 1.0.0
 */
object EncodeUtil {
    /**
     * Hex解码.
     */
    fun decodeHex(input: String?): ByteArray? = Hex.decodeHex(input?.toCharArray())
}