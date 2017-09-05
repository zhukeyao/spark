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

import java.lang.management.ManagementFactory
import javax.management.{MBeanServer, ObjectName}

private[spark] object LogJmxConfigurator {
  private var log4jConfigurator: Log4jConfigurator = null

  def init(): Unit = {
    if (log4jConfigurator != null) {
      return
    }
    log4jConfigurator = new Log4jConfigurator();
    // register log4jConfigurator
    val mbs: MBeanServer = ManagementFactory.getPlatformMBeanServer()
    val name = "Log4jConfigurator:name=LogConfigurator"
    val mBeanName: ObjectName = new ObjectName(name)
    mbs.registerMBean(log4jConfigurator, mBeanName)
  }

}