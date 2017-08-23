package com.moran.genitus.channel.tracker.module

import client.ESClient
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.moran.genitus.channel.tracker.config.EsConf
import com.moran.genitus.channel.tracker.service.EsService
import org.slf4j.LoggerFactory

class EsModule(val esConfig: EsConf) extends AbstractModule{
  //logger
  private val log = LoggerFactory.getLogger(classOf[EsModule])

  override def configure(): Unit = {
    bind(classOf[EsConf]).toInstance(esConfig)
  }

  //生成EsClintMap并注出供EsService使用
  @Singleton
  @Provides
  def provideESClientMap(esConfig: EsConf): Map[String,ESClient] = {
    log.info("启动esClientMap...")
    try {
      val eSClientmap: Map[String, ESClient] = Map(
        "sc"->new ESClient("cluster.name","eureka-dev", "10.1.86.58", 9300),
        "nc"->new ESClient("cluster.name","eureka-dev","10.1.86.58",9300)
      )
      log.info("完成esClientMap启动")
      eSClientmap
    }catch {
      case ex: Exception => log.error("启动ecClientMap出错", ex)
        //中断程序启动
        throw new RuntimeException()
    }
  }

  //生成EsService并注出供router使用
  @Singleton
  @Provides
  def provideEsService(eSClientMap:Map[String,ESClient]): EsService = {
    new EsService(eSClientMap)
  }
}
