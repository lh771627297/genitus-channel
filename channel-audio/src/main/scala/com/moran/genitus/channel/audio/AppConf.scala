/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moran.genitus.channel.audio

import org.rogach.scallop.ScallopConf

/**
  * Application configuration.
  *
  * @author gwjiang (gwjiang@iflytek.com), 2016/12/30.
  */
trait AppConf extends ScallopConf {
  /** hostname. */
  lazy val appHostname = opt[String]("hostname",
    descr = "The advertised appHostname of this instance for network " +
      "communication. This is used by other instances or server " +
      "to communicate with this instance",
    default = Some(java.net.InetAddress.getLocalHost.getHostName))

  /** version. */
  lazy val appVer = Option(classOf[AppConf].getPackage.getImplementationVersion).getOrElse("unknown")

  /** conf dir. */
  lazy val confDir = opt[String]("conf_dir",
    descr = "conf dir",
    default = Some("./conf"),
    required = false,
    noshort = true)

  /** log schema. */
  lazy val schema = opt[String]("schema",
    descr = "log schema",
    required = false,
    noshort = true)
}
