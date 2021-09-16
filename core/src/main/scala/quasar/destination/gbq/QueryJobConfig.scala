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

import slamdata.Predef._

import argonaut._, Argonaut._

final case class QueryJobConfig(
  query: String,
  timeoutMs: Long)

object QueryJobConfig {
  implicit val encode: EncodeJson[QueryJobConfig] = EncodeJson { (jc: QueryJobConfig) =>
    ("query" := jc.query) ->:
    ("timeoutMs" := jc.timeoutMs) ->:
    ("useLegacySql" := false) ->:
    ("useQueryCache" := false) ->:
    jEmptyObject
  }
}

