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

final case class GBQJob(
  kind: String,
  etag: String,
  id: String,
  selfLink: String,
  status: GBQJobStatus
)

final case class GBQJobStatus(
  errorResult: GBQErrorResult,
  errors: List[GBQErrors],
  state: String
)

final case class GBQErrorResult(
  reason: String,
  location: String,
  debugInfo: String,
  message: String
)

final case class GBQErrors(
  reason: String,
  location: String,
  debugInfo: String,
  message: String
)

object GQBJob {
  
  implicit val GBQJobDecodeJson: DecodeJson[GBQJob] =
    DecodeJson(c => for {
      kind <- (c --\ "kind").as[String]
      etag <- (c --\ "age").as[String]
      id <- (c --\ "id").as[String]
      selfLink <- (c --\ "selfLink").as[String]
      status <- (c --\ "status").as[GBQJobStatus]
    } yield GBQJob(kind, etag, id, selfLink, status))

  implicit val GBQJobStatusDecodeJson: DecodeJson[GBQJobStatus] =
    DecodeJson(c => for {
      errorResult <- (c --\ "errorResult").as[GBQErrorResult]
      errors <- (c --\ "errors").as[List[GBQErrors]]
      state <- (c --\ "state").as[String]
    } yield GBQJobStatus(errorResult, errors, state))

  implicit val GBQErrorResultDecodeJson: DecodeJson[GBQErrorResult] =
    DecodeJson(c => for {
      reason <- (c --\ "reason").as[String]
      location <- (c --\ "location").as[String]
      debugInfo <- (c --\ "debugInfo").as[String]
      message <- (c --\ "message").as[String]
    } yield GBQErrorResult(reason, location, debugInfo, message))


  implicit val GBQErrorsDecodeJson: DecodeJson[GBQErrors] =
    DecodeJson(c => for {
      reason <- (c --\ "reason").as[String]
      location <- (c --\ "location").as[String]
      debugInfo <- (c --\ "debugInfo").as[String]
      message <- (c --\ "message").as[String]
    } yield GBQErrors(reason, location, debugInfo, message))
}

