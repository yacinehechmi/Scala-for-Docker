package docker
import upickle.default._

case class Ipam(Driver: String = "",
  Option: String = "",
  Config: List[Map[String, String]] = List(Map())) derives ReadWriter

case class Network(Name: String,
  Id: String = "",
  Created: String = "",
  Scope: String = "",
  Driver: String = "",
  EnableIPv6: Boolean,
  IPAM: Ipam,
  Internal: Boolean,
  Ingress: Boolean,
  ConfigFrom: Map[String, String] = Map(),
  ConfigOnly: Boolean,
  Containers: Map[String, String] = Map(),
  Options: Map[String, String] = Map(),
  Labels: Map[String, String] = Map()) derives ReadWriter
