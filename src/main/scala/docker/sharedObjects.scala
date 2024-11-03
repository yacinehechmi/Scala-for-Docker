package docker.shared
import upickle.default._

case class BlkioWeightDevice(Path: String, Weight: Int) derives ReadWriter 

case class IPAMConfig(IPv4Address: String, IPv6Address: String, LinkLocalIPs: List[String]) derives ReadWriter 
