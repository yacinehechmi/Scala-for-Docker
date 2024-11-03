package docker
import upickle.default._

case class AccessibilityRequirements(Requisite: List[Map[String, String]],
  Preferred: List[Map[String, String]]) derives ReadWriter

case class AccessMode(Scope: String,
  Sharing: String,
  MountVolume: Map[String, String],
  Secrets: List[Map[String, String]],
  AccessibilityRequirements: AccessibilityRequirements,
  CapacityRange: Map[String, Int],
  Availibility: String) derives ReadWriter

case class Spec(Group: String, AccessMode: AccessMode) derives ReadWriter

case class Info(CapacityBytes: Int,
  VolumeContext: Map[String, String],
  VolumeID: String,
  AccessibleTopology: List[Map[String, String]]) derives ReadWriter

case class PublishStatus(NodeID: String, State: String, PublishContext: Map[String, String]) derives ReadWriter

case class ClusterVolume(ID: String,
  Version: Map[String, Long],
  CreatedAt: String,
  UpdatedAt: String,
  Spec: Spec,
  Info: Info,
  publishStatus: List[PublishStatus],
  Options: Map[String, String],
  UsageData: Map[String, Int]) derives ReadWriter


case class HealthConfig(Test: List[String],
  Interval: Int,
  Timeout: Int,
  Retries: Int,
  StartPeriod: Int) derives ReadWriter

case class Volumes(CreatedAt: String,
  Driver: String,
  Labels: Map[String, String],
  Mountpoint: String,
  Name: String,
  Options: Map[String, String] = Map(),
  Scope: String) derives ReadWriter

case class VolumeWrapper(Volumes: List[Volumes], Warnings: String) derives ReadWriter



