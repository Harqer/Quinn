import com.meta.wearable.dat.core.Wearables
fun test() {
    val devices = Wearables.devices
    val session = Wearables.createSession(devices.value.first())
}
