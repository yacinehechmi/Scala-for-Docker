package docker.globals

import upickle.default._
import scala.util.{Failure, Success, Try}
import client.{HttpSocket, Header, Request, Path}

val VERSION = "v1.47"
val CONTAINERS_ENDPOINT = s"/$VERSION/containers"
val IMAGES_ENDPOINT = s"/$VERSION/images"
val INFO_ENDPOINT = s"/$VERSION/info"
val NETWORKS_ENDPOINT = s"/$VERSION/networks"
val VOLUMES_ENDPOINT = s"/$VERSION/volumes"

type Filters = Map[String, String | Int | Boolean]

/* _host should be removed */

object Serializer {
  def deserialize[T: Reader](jsonString: String): Try[T] = Try(upickle.default.read[T](jsonString))
  def serialize[T: Writer](caseClass: T): Try[String] = Try(upickle.default.write[T](caseClass))
}

class Client(val path: String = null, val host: String = null, secure: Boolean = false) {
  val _host = host match {
    case host => host
    case _ => throw new RuntimeException("Could not set Host")
  }

  val connector = (path, host, secure) match 
  {
    case (path, host, false) => new HttpSocket(Path(path))
    //case (Some(path), null, false) => new HttpSocket(Path(path))
    //case (null, Some(host), false) => new Http(host)
    //case (null, Some(host), true) => new Https(host)
    case _ => throw new RuntimeException("Bad parameters")
  }

  def send[T: Reader](requestBody: Request, requestMethod: (Request => (Option[Header], Option[String]))): Option[T] = {
    requestMethod(requestBody) match {
      case (Some(header), Some(body)) => Serializer.deserialize[T](body) match {
        case Success(deserializableBody) => Option(deserializableBody)
        case Failure(e) => println(s"[Docker.send]: failed to deserialize \n $e"); None
      }
        case (Some(header), None) => None
        case _ => println("[Docker.send]: something went wrong"); None
    }
  }

  def close(): Unit = connector.close()
}
