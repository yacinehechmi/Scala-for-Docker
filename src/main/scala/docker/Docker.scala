package docker

import upickle.default._
import client.{HttpSocket, Header, Request, Path}
import scala.util.{Failure, Success, Try}
import java.net.HttpCookie

import docker.globals._
import docker.containers._


class Docker(implicit client: Client){
  // -- for now this will return the responseString if it failes to parse

// /v1.43/info
  /*--work on parameters and filters--*/

  //def version(): Option[String] = {
    //val request = Request(s"$INFO_ENDPOINT", client._host)
    //val (header, body) = client.connector.get(request)
    //body match {
    //case Some(body) => Some(body)
    //case None => None
    //}
  //}

  def version(): Option[String] = {
    client.send[String](Request(s"$INFO_ENDPOINT", client._host), client.connector.get)
  }
  
  /* -------------- Images ------------*/

 // /v1.43/images/json
 /*--done working with this endpoint--*/
def listImages(listAll: Boolean = false,
  sharedSize: Boolean = false,
  digets: Boolean = false,
  manifests: Boolean = false,
  parameters: Filters): Option[List[Image]] = {
    client.send[List[ImageState]](Request(
      s"$IMAGES_ENDPOINT/json",
      client._host, 
      Map("all" -> listAll,
        "shared-size" -> false,
        "digest" -> false,
        "manifests" -> false)), client.connector.get) match {
          case Some(images) => Some(images.map(img => new Image(client, img)))
          case _ => println("could not find any images"); None
        }
}


 /*--done working with this endpoint--*/

  /* -------------- Networks ------------*/
  // /v1.43/networks
  /*--work on parameters and filters (filters are not working)--*/
  def listNetworks(parameters: Filters = Map()): Option[List[Network]] =
    client.send[List[Network]](Request(NETWORKS_ENDPOINT, client._host, parameters = parameters), client.connector.get)
  /*--work on parameters and filters (filters are not working)--*/

  /* -------------- Volumes ------------*/
  // /v1.43/volumes
  /*--work on parameters and filters (filters are not working)--*/
  def listVolumes(parameters: Filters = Map()): Option[VolumeWrapper] = {
    client.send[VolumeWrapper](Request(VOLUMES_ENDPOINT, client._host, parameters = parameters), client.connector.get)
  }
  /*--work on parameters and filters (filters are not working)--*/


  /* -------------- Containers ------------*/
    // /v1.43/containers/create
    /*--work on parameters and filters--*/
    def createContainer(name: String, hostname: String = "", user: String = "", config: PostContainer = null): Option[String] = {
      Serializer.serialize[PostContainer](config) match {
        case Success(containerConfig) =>
          client.send[String](Request(s"$CONTAINERS_ENDPOINT/create", client._host, Map("name" -> name), body = containerConfig), client.connector.post)
        case Failure(e) => println(s"[Docker.Container.createContainer]: Failed to create a container with error:\n$e"); None
      }
    }

  // /v1.43/containers/json by default it will list all
  def listContainers(parameters: Filters = Map(), listAll: Boolean = true, getSize: Boolean = false, filters: Filters = Map()): Option[List[Container]] = {
    client.send[List[ContainerState]](Request(s"$CONTAINERS_ENDPOINT/json", client._host, parameters,
      filters), client.connector.get) match {
        case Some(containers) => {
          Option(containers.map(x =>
              new Container(client, x)
              ))
        }
        case _ => Option(List())
      }
  }

  def getContainer(name: String = "", id: String = "", listAll: Boolean = true): Option[Container] = {
    (name.isBlank, id.isBlank) match {
      case (false, true) =>
          this.listContainers(listAll = true) match {
            case Some(containers) => containers.find(ctr => ctr.container.Id == id)
            case _ => println(s"no container found with id $id"); None
          }
      case (true, false) =>
          this.listContainers(listAll = true) match {
            case Some(containers) => containers.find(ctr => ctr.container.Names.contains(name))
            case _ => println(s"no container found with name $name"); None
          }
      case _ => println(s"[docker:getContainer] please provide a container id or name"); null
    }
  }

    //def getContainer(name: String = "", id: String = "", listAll: Boolean = true, parameters: Filter): Option[List[Container]] = {
    //(name.isBlank, id.isBlank) match {
      //case (false, true) =>
        //send[List[Container]](Request(_containersEndpoint, _host, Map("all" -> listAll, "filters" -> s"""{"name":["$name"]}""")), client.connector.get)
      //case (true, false) =>
        //send[List[Container]](Request(_containersEndpoint, _host, Map("all" -> listAll, "filters" -> s"""{"id": ["$id"]}""")), client.connector.get)
      //case _ => println(s"[docker:getContainer] please provide a container id or name"); null
    //}
  //}

  // for now just return the jsonString response
  // inspecting a container
  // /containers/<id>/json
  /*--work on parameters and filters--*/
 def inspectContainer(id: String = "", size: Boolean = false): Option[String] = {
   (id.isEmpty) match {
     // check this later
     case true => println("please porvide the a container id"); None
     case false => client.send[String](Request(s"$CONTAINERS_ENDPOINT/$id/json", parameters = Map("size" -> size)), client.connector.get) match {
       case Some(body) => Some(body)
       case None => println("could not find container"); None
     }
   }
 }

  // get container by name or Id

  // listing processes running inside a container
  // /containers/<id>/top
  /*--work on parameters and filters--*/
  def top(id: String = "", psArgs: String = ""): Option[String] = {
     if (id.isEmpty) {
       // check this later
       throw new RuntimeException("please porvide a container id")
       None
     }
     val (header, body) = client.connector.get(Request(s"$CONTAINERS_ENDPOINT/$id/top", client._host, Map("ps_args" -> psArgs)))
     body match {
       case Some(body) => Some(body)
       case None => None
     }
  }

  // getting container logs by ID
  // /containers/<id>/logs
  // this needs to be a stream
  /*--work on parameters and filters--*/
  def logs(id: String = "",
           follow: Boolean = false,
           stdout: Boolean = false,
           stderr: Boolean = false,
           since: Int = 0,
           until: Int = 0,
           timestamps: Boolean = false,
           tail: String = "all"): Option[String] = {
     if (id.isEmpty) {
       // check this later
       throw new RuntimeException("please porvide a container id")
       None
     }

     val req = Request(s"$CONTAINERS_ENDPOINT/$id/logs", client._host, Map("follow" -> follow,
       "stdout" -> stdout,
       "stderr" -> stderr,
       "since" -> since,
       "until" -> until,
       "timestamps" -> timestamps,
       "tail" -> tail))
     val (header, body) = client.connector.get(req)
     body match {
       case Some(body) => Some(body)
       case None => None
     }
  }

  // the output of this is to long and the format is not suitable
  def listFsChanges(id: String = ""): Option[String] = {
    if id.isEmpty then throw new RuntimeException("please provide a container id")
    else {
     val (header, body) = client.connector.get(Request(s"$CONTAINERS_ENDPOINT/$id/changes", client._host))
     body match {
       case Some(body) => Some(body)
       case None => None
     }
    }
  }


  def exportContainer(id: String = ""): Option[String] = {
    if id.isEmpty then throw new RuntimeException("please provide a container id")
    else {
     val (header, body) = client.connector.get(Request(s"$CONTAINERS_ENDPOINT/$id/export", client._host))
     body match {
       case Some(body) => Some(body)
       case None => None
     }
    }
  }

  def listProcesses(id: String = ""): Unit = { if (id.isEmpty) {
      throw new RuntimeException("please provide ")
    }
  }

  def containerStats(id: String = "", stream: Boolean = true, oneShot: Boolean = false): Option[Container] = {
    if (id.isEmpty) {
      throw new RuntimeException("please provide a container ID")
    } else {
      client.send[ContainerState](Request(
        s"$CONTAINERS_ENDPOINT/$id/stats", client._host, Map("stream" -> stream, "one-shot" -> oneShot)
      ), client.connector.get) match {
        case Some(res) =>
          Option(new Container(client, res))
        case _ => throw new RuntimeException
      }
    }
  }
}
