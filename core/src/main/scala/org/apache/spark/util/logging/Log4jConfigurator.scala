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

package org.apache.spark.util.logging

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.{Level, Logger, LogManager}
import scala.collection.mutable


private[spark] class Log4jConfigurator extends Log4jConfiguratorMBean {

  def getLoggers: mutable.ArrayBuffer[String] = {
    var list = new mutable.ArrayBuffer[String]()
    val e = LogManager.getCurrentLoggers

    while (e.hasMoreElements) {
      val log: Logger = e.nextElement.asInstanceOf[Logger]
      if (log.getLevel != null) {
        list += log.getName + " = " + log.getLevel.toString
      }
      else
      {
        list += log.getName + " = " + "default"
      }
    }
    list += LogManager.getRootLogger.getName + "=" + LogManager.getRootLogger.getLevel.toString
    list
  }

  private def getRootLoggerLevel() : String = {
    var defaultLevel = Logger.getRootLogger.getLevel
    if (defaultLevel == null) return Level.DEBUG.toString
    return defaultLevel.toString
  }

  override def getLogLevel(logger: String): String = {
    var level: String = "default"

    if (StringUtils.isBlank(logger) || logger.equalsIgnoreCase("root")) {
      return getRootLoggerLevel
    }

    val log: Logger = Logger.getLogger(logger)
    if (log == null) return getRootLoggerLevel

    var logLevel = log.getLevel
    if (logLevel == null) return level

    level = logLevel.toString

    return level
  }

  override def setLogLevel(loggerName: String, level: String): String = {

    if (StringUtils.isBlank(level)) {return "no logger level specified"}

    var targetLogLevel = Level.toLevel(level.toUpperCase)

    if (loggerName.equalsIgnoreCase("ROOT") ||
        loggerName.equalsIgnoreCase("ROOTLOGGER") ||
        StringUtils.isBlank(loggerName)) {
      Logger.getRootLogger.setLevel(targetLogLevel)
      return "Root Logger log level has been set to " + targetLogLevel.toString + "."
    }

    var log = Logger.getLogger(loggerName)
    if (log != null) {
      log.setLevel(targetLogLevel)
      return loggerName + " logger log level has been set to " + targetLogLevel.toString + "."
    }

    return "no logger find for " + loggerName
  }
}