package docker.containers

import upickle.default._
import client.{HttpSocket, Header, Request, Path}
import docker.globals._
import docker.shared._


case class ContainerState(Id: String, Names: List[String], Image: String, ImageID: String, Command: String, Created: String,
  Ports: List[Port], Labels: Map[String, String], State: String, Status: String,
  HostConfig: Map[String, String], NetworkSettings: Map[String, Map[String, Driver]] = Map(),
   Mounts: List[Mount] = List()) derives ReadWriter

case class Port(IP: String = "", PrivatePort: Int = 0,
  PublicPort: Int = 0, Type: String = "") derives ReadWriter

case class Driver(IPAMConfig: Map[String, String], Links: String, Aliases: String, MacAddress: String,
 DriverOpts: String, NetworkID: String, EndpointID: String, Gateway: String, IPAddress: String, IPPrefixLen: Int,
  IPv6Gateway: String, GlobalIPv6Address: String, GlobalIPv6PrefixLen: Int, DNSNames: List[String]) derives ReadWriter

case class Mount(Type: String = "", Name: String = "", Source: String = "", Destination: String = "",
  Driver: String = "", Mode: String = "", RW: Boolean = false, Propagation: String = "") derives ReadWriter

class Container(private val client: Client, val container: ContainerState) {

   def stop(): Unit = {
     if (this.container.Status.startsWith("Up")) {
       val req = Request(CONTAINERS_ENDPOINT+this.container.Id.substring(0, 12)+"/stop", client._host)
       client.send[String](req, client.connector.post)
     }

   else return
   }

   /*--work on params and filters--*/
  // POST /v1.43/containers/{id}/start
  // still working on this, should not return Unit
  def start(): Unit = {
    if (this.container.Status.startsWith("Exited")) {
      val req = Request(CONTAINERS_ENDPOINT+this.container.Id+"/start", client._host)
      client.send[String](req, client.connector.post)
    }

  else return
  }

  // /v1.43/containers/{id}/json = docker inspect <container_id>
  // still working on this, should not return Unit

  /*--work on params and filters--*/
  // POST /v1.43/containers/{id}/restart
  def restart(): Unit = {
   val req = Request(CONTAINERS_ENDPOINT+this.container.Id+"/restart", client._host)
   client.send[String](req, client.connector.post)
  }


  def kill(): Unit = {
    val req = Request(CONTAINERS_ENDPOINT+this.container.Id+"/kill", client._host)
  }

   /*--work on params and filters--*/
  // DELETE /v1.43/containers/{id}/
  def remove(): Unit = {
    val req = Request(CONTAINERS_ENDPOINT+this.container.Id, client._host)
    client.send[String](req, client.connector.delete)
  }

  def getContainer: ContainerState = container
}

  case class EndpointSettings(IPAMConfig: IPAMConfig,
                              Links: List[String],
                              Aliases: List[String],
                              NetworkID: String,
                              EndpointID: String,
                              Gateway: String,
                              IPAddress: String,
                              IPPrefixLen: Int,
                              IPv6Gateway: String,
                              GlobalIPv6PrefixLen: Int,
                              MacAddress: String,
                              DriverOpts: Map[String, String]) derives ReadWriter

  case class HostConfig(CpuShares: Int,
                        Memory: Int,
                        CgroupParent: String,
                        BlkioWeight: Int,
                        BlkioWeightDevice: BlkioWeightDevice) derives ReadWriter

  case class PostContainer(Hostname: String = "",
                           Domainname: String = "",
                           User: String = "",
                           AttachStdin: Boolean = false,
                           AttachStdout: Boolean = true,
                           AttachStderr: Boolean = true,
                           Tty: Boolean = false,
                           OpenStdin: Boolean = false,
                           Env: List[String] = List(),
                           Cmd: List[String] = List(),
                           Entrypoint: String = "",
                           Image: String = "",
                           Labels: Map[String, String] = Map(),
                           Volumes: Map[String, Map[String, String]] = Map(),
                           WorkingDir: String = "",
                           NetworkDisabled: Boolean = false,
                           MacAddress: String = "",
                           ExposedPorts: Map[String, Map[String, String]] = Map(),
                           StopSignal: String = "",
                           StopTimeout: Int = 10,
                           HostConfig: HostConfig = null,
                           NetworkingConfig: Map[String, EndpointSettings] = Map()) derives ReadWriter
