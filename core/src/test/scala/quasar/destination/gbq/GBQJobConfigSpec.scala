/*
 * Copyright 2020 Precog Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quasar.destination.gbq

import scala.List

import argonaut.{Argonaut, DecodeJson, Json}, Argonaut._

import org.specs2.mutable.Specification

object GBQJobConfigSpec extends Specification {
  val decode = DecodeJson.of[GBQJobConfig].decodeJson(_)

  val jsonJobCfg = Json.obj(
    "configuration" := Json.obj(
      "load" := Json.obj(
        "sourceFormat" := jString("CSV"),
        "skipLeadingRows" := jNumber(1),
        "allowQuotedNewlines" := jBool(true),
        "schema" := Json.obj(
          "fields" := Json.array(
            Json.obj(
              "type" := jString("STRING"),
              "name" := jString("Manager")
            ),
            Json.obj(
              "type" := jString("INT"),
              "name" := jString("Id")))),
        "timePartitioning" := Json.obj(
          "type" := jString("DAY")),
        "writeDisposition" := jString("WRITE_APPEND"),
        "destinationTable" := Json.obj(
          "projectId" := jString("myproject"),
          "datasetId" := jString("mydataset"),
          "tableId" := jString("mytable"))),
      "jobTimeoutMs" := jNumber(21600000),
      "jobType" := jString("LOAD")))

  val testJobCfg = GBQJobConfig(
    "CSV",
    1,
    true,
    List[GBQSchema](GBQSchema("STRING", "Manager"), GBQSchema("INT", "Id")),
    "DAY",
    WriteDisposition("WRITE_APPEND"),
    GBQDestinationTable("myproject", "mydataset", "mytable"),
    21600000,
    "LOAD")

  "decode json job config to GBQJobConfig" >> {
    decode(jsonJobCfg).toOption must beSome(testJobCfg)
  }

  "encode GBQJobConfig to json" >> {
    testJobCfg.asJson must_=== jsonJobCfg
  }
}