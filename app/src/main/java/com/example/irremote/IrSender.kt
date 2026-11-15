package com.example.irremote

import android.content.Context
import android.hardware.ConsumerIrManager

class IrSender(context: Context) {
    private val ir = context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
    fun isSupported() = ir?.hasIrEmitter() == true

    /** NEC protocol, 38 kHz */
    fun sendNec(address: Int, command: Int) {
        val u = 560
        val p = mutableListOf(16 * u, 8 * u)
        val data = (address and 0xFF) or ((address.inv() and 0xFF) shl 8) or
                ((command and 0xFF) shl 16) or ((command.inv() and 0xFF) shl 24)
        for (i in 0 until 32) { p += u; p += if ((data shr i) and 1 == 1) 3 * u else u }
        p += u
        ir?.transmit(38000, p.toIntArray())
    }
}
