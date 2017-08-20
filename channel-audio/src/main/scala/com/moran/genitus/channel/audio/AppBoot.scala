package com.moran.genitus.channel.audio

import org.genitus.cardiac.http.{HttpConf, HttpModule, HttpService}
import org.genitus.cardiac.metrics.MetricsModule
import org.rogach.scallop.ScallopConf

/**
  * 程序启动入口
  * 继承老大的App
  */
object AppBoot extends org.genitus.cardiac.App{

  //收集相关参数设置
  lazy val conf = new ScallopConf(args) with HttpConf with HBaseConf with AppConf

  //使用参数配置各个模块
  override def modules() = {
    Seq(
      new AppModule(conf),
      new HttpModule(conf),
      new MetricsModule,
      new HBaseModule(conf),
      new RestApiModule
    )
  }

  initConf()

  //启动各项服务
  run(classOf[HttpService])
  run(classOf[HBaseService])
}
