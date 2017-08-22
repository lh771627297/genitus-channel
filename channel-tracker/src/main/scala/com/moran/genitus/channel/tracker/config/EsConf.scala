package com.moran.genitus.channel.tracker.config

import org.rogach.scallop.{ScallopConf, ScallopOption}

trait EsConf extends ScallopConf{
  lazy val esConfigs: ScallopOption[String] = opt[String](
    "es_configs",
    descr = "The configs of es cluster,json",
    required = true,
    noshort = true)
}
