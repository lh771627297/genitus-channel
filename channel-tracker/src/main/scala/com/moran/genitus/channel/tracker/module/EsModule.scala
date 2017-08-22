package com.moran.genitus.channel.tracker.module

import client.ESClient
import com.fasterxml.jackson.databind._
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.moran.genitus.channel.tracker.config.EsConf

class EsModule(val esConfig: EsConf) extends AbstractModule{
  override def configure(): Unit = {
    bind(classOf[EsConf]).toInstance(esConfig)
  }

  case class EsConfig (dc:String,cluster_Name: String, name: String, ipAddress: String,port:Int)

  @Singleton
  @Provides
  def provideESClientMap(esConfig: EsConf): Map[String,ESClient] = {
    val eSClientmap:Map[String,ESClient] = Map[String,ESClient]()
    //es config 配置启动获取相应的esClient
    var configs:List[EsConfig] = new ObjectMapper().readValue(esConfig.esConfigs.toString(),classOf[List[EsConfig]])
    configs.foreach {
      config => eSClientmap+(config.dc->new ESClient(config.cluster_Name,config.name,config.ipAddress,config.port))
    }
    eSClientmap
  }
}
