package docker

import upickle.default._

import docker.globals._
import docker.shared._

case class ImageState(Id: String,
  ParentId: String,
  RepoTags: List[String], RepoDigests: List[String],
  Created: String,
  Size: Long,
  SharedSize: Long,
  VirtualSize: Long,
  Labels: Map[String, String],
  Containers: Int) derives ReadWriter

class Image(private val _client: Client, val image: ImageState)
{
  // methods that can be done inside images
}
