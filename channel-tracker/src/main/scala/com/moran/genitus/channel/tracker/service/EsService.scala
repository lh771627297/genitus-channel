package com.moran.genitus.channel.tracker.service


import client.ESClient
import com.google.common.util.concurrent.AbstractIdleService
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import service.ESService

import scala.collection.JavaConverters._


class EsService @Inject()(val eSClientMap: Map[String,ESClient]) extends AbstractIdleService{
  /** logger. */
  private[this] val log = LoggerFactory.getLogger(getClass.getName)
  private val esService:ESService = new ESService(eSClientMap.asJava)

  override def shutDown(): Unit = {
    esService.closeESClient()
    log.info("shutdown EsService ...")
  }

  override def startUp(): Unit = {
    log.info("start EsService ...")
  }

  def search(json:String):String = {
    esService.search(json)
  }
}
