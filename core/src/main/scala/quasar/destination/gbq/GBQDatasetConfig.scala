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

import scala.Predef.String

import argonaut._, Argonaut._

final case class GBQDatasetConfig(projectId: String, datasetId: String)

object GBQDatasetConfig {
  implicit val codecJson: CodecJson[GBQDatasetConfig] =
    CodecJson(
      (g: GBQDatasetConfig) => Json.obj(
        "datasetReference" := Json.obj(
          "projectId" := g.projectId,
          "datasetId" := g.datasetId
        )),
      c => for {
        projectId <- (c --\ "datasetReference" --\ "projectId").as[String]
        datasetId <- (c --\ "datasetReference" --\ "datasetId").as[String]
      } yield GBQDatasetConfig(projectId, datasetId))
}
