package com.moran.genitus.channel.tracker

import com.moran.genitus.channel.tracker.module.RestModule
import org.genitus.cardiac.http.{HttpConf, HttpModule, HttpService}
import org.genitus.cardiac.metrics.MetricsModule
import org.rogach.scallop.ScallopConf

/**
  * 程序启动入口
  * 继承老大的App
  */
object AppBoot extends org.genitus.cardiac.App{

  //收集相关参数设置
  lazy val conf = new ScallopConf(args) with HttpConf

  //使用参数配置各个模块
  override def modules() = {
    Seq(
      new HttpModule(conf),
      new MetricsModule,
      new RestModule
    )
  }

  initConf()

  //启动各项服务
  run(classOf[HttpService])
}
