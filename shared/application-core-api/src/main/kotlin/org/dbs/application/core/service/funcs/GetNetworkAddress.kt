package org.dbs.application.core.service.funcs

import org.dbs.application.core.exception.NetworkAdapterException
import org.dbs.consts.RestHttpConsts.URI_IP
import org.dbs.consts.RestHttpConsts.URI_MAC
import org.dbs.consts.SysConst.STRING_NULL
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException

object GetNetworkAddress {

    val currentHostName by lazy { InetAddress.getLocalHost().hostName }

    @JvmStatic
    val allAddresses: String
        get() = "$URI_IP = ${getAddress(URI_IP)}, $URI_MAC = ${getAddress(URI_MAC)}"

    fun getAddress(addressType: String): String? {
        var address: String? = ""
        var lanIp: InetAddress? = null
        try {
            var ipAddress: String?
            val net = NetworkInterface.getNetworkInterfaces()
            while (net.hasMoreElements()) {
                val element = net.nextElement()
                val addresses = element.inetAddresses
                val hardwareAddress = element.hardwareAddress
                if (hardwareAddress != null) {
                    while (addresses.hasMoreElements() && (hardwareAddress.isNotEmpty()) && !isVMMac(hardwareAddress)) {
                        val ip = addresses.nextElement()
                        if (ip is Inet4Address) {
                            if (ip.isSiteLocalAddress()) {
                                ipAddress = ip.getHostAddress()
                                lanIp = InetAddress.getByName(ipAddress)
                            }
                        }
                    }
                }
            }
            if (lanIp == null) {
                return null
            }
            address = if (addressType == URI_IP) {
                lanIp.toString().replace("^/+".toRegex(), "")
            } else if (addressType == "mac") {
                getMacAddress(lanIp)
            } else {
                throw NetworkAdapterException("Specify \"ip\" or \"mac\"")
            }
        } catch (ex: UnknownHostException) {
            println(ex)
        } catch (ex: SocketException) {
            println(ex)
        } catch (ex: Exception) {
            println(ex)
        }
        return address
    }

    private fun getMacAddress(ip: InetAddress): String? {
        var address: String? = STRING_NULL
        try {
            val network = NetworkInterface.getByInetAddress(ip)
            val mac = network.hardwareAddress
            val sb = StringBuilder()
            for (i in mac.indices) {
                sb.append(String.format("%02X%s", mac[i], if (i < mac.size - 1) "-" else ""))
            }
            address = sb.toString()
        } catch (ex: SocketException) {
            println(ex)
        }
        return address
    }

    private fun isVMMac(mac: ByteArray?) = if (null == mac) false else {
        val invalidMacs = arrayOf(
            byteArrayOf(0x00, 0x05, 0x69),
            byteArrayOf(0x00, 0x1C, 0x14),
            byteArrayOf(0x00, 0x0C, 0x29),
            byteArrayOf(0x00, 0x50, 0x56),
            byteArrayOf(0x08, 0x00, 0x27),
            byteArrayOf(0x0A, 0x00, 0x27),
            byteArrayOf(0x00, 0x03, 0xFF.toByte()),
            byteArrayOf(0x00, 0x15, 0x5D)
        )
        for (invalid in invalidMacs) {
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) {
                break
            }
        }
        false
    }
}
