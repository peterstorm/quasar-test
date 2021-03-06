/*
 * Copyright 2014 http4s.org
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

package org.http4s
package argonaut

import _root_.argonaut.{DecodeResult => ArgDecodeResult, _}
import _root_.argonaut.Argonaut._
import cats.effect.Sync
import org.http4s.argonaut.Parser.facade
import org.http4s.headers.`Content-Type`
import jawn.JawnInstances
import org.typelevel.jawn.ParseException
import org.http4s.argonaut.ArgonautInstances.DecodeFailureMessage
import scala.annotation.nowarn

trait ArgonautInstances extends JawnInstances {
  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  implicit def jsonDecoder[F[_]: Sync]: EntityDecoder[F, Json] =
    jawnDecoder

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  protected def jsonDecodeError: (Json, DecodeFailureMessage, CursorHistory) => DecodeFailure =
    ArgonautInstances.defaultJsonDecodeError

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  def jsonOf[F[_]: Sync, A](implicit decoder: DecodeJson[A]): EntityDecoder[F, A] =
    jsonDecoder[F].flatMapR { json =>
      decoder
        .decodeJson(json)
        .fold(
          (message, history) => DecodeResult.failure(jsonDecodeError(json, message, history)),
          DecodeResult.success(_)
        )
    }

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  protected def defaultPrettyParams: PrettyParams = PrettyParams.nospace

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  implicit def jsonEncoder[F[_]]: EntityEncoder[F, Json] =
    jsonEncoderWithPrettyParams[F](defaultPrettyParams)

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  def jsonEncoderWithPrettyParams[F[_]](prettyParams: PrettyParams): EntityEncoder[F, Json] =
    EntityEncoder
      .stringEncoder(Charset.`UTF-8`)
      .contramap[Json](prettyParams.pretty)
      .withContentType(`Content-Type`(MediaType.application.json))

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  def jsonEncoderOf[F[_], A](implicit encoder: EncodeJson[A]): EntityEncoder[F, A] =
    jsonEncoderWithPrinterOf(defaultPrettyParams)

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  def jsonEncoderWithPrinterOf[F[_], A](prettyParams: PrettyParams)(implicit
      encoder: EncodeJson[A]): EntityEncoder[F, A] =
    jsonEncoderWithPrettyParams[F](prettyParams).contramap[A](encoder.encode)

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  implicit val uriCodec: CodecJson[Uri] = CodecJson(
    (uri: Uri) => Json.jString(uri.toString),
    c =>
      c.as[String]
        .flatMap(str =>
          Uri
            .fromString(str)
            .fold(err => ArgDecodeResult.fail(err.toString, c.history), ArgDecodeResult.ok))
  )

  @deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
  @nowarn("cat=deprecation")
  implicit class MessageSyntax[F[_]: Sync](self: Message[F]) {
    def decodeJson[A](implicit decoder: DecodeJson[A]): F[A] =
      self.as(implicitly, jsonOf[F, A])
  }
}

@deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
sealed abstract case class ArgonautInstancesBuilder private[argonaut] (
    defaultPrettyParams: PrettyParams = PrettyParams.nospace,
    jsonDecodeError: (Json, String, CursorHistory) => DecodeFailure =
      ArgonautInstances.defaultJsonDecodeError,
    jawnParseExceptionMessage: ParseException => DecodeFailure =
      JawnInstances.defaultJawnParseExceptionMessage,
    jawnEmptyBodyMessage: DecodeFailure = JawnInstances.defaultJawnEmptyBodyMessage
) { self =>
  def withPrettyParams(pp: PrettyParams): ArgonautInstancesBuilder =
    this.copy(defaultPrettyParams = pp)

  def withJsonDecodeError(
      f: (Json, String, CursorHistory) => DecodeFailure): ArgonautInstancesBuilder =
    this.copy(jsonDecodeError = f)

  def withParseExceptionMessage(f: ParseException => DecodeFailure): ArgonautInstancesBuilder =
    this.copy(jawnParseExceptionMessage = f)

  def withEmptyBodyMessage(df: DecodeFailure): ArgonautInstancesBuilder =
    this.copy(jawnEmptyBodyMessage = df)

  protected def copy(
      defaultPrettyParams: PrettyParams = self.defaultPrettyParams,
      jsonDecodeError: (Json, String, CursorHistory) => DecodeFailure = self.jsonDecodeError,
      jawnParseExceptionMessage: ParseException => DecodeFailure = self.jawnParseExceptionMessage,
      jawnEmptyBodyMessage: DecodeFailure = self.jawnEmptyBodyMessage
  ): ArgonautInstancesBuilder =
    new ArgonautInstancesBuilder(
      defaultPrettyParams,
      jsonDecodeError,
      jawnParseExceptionMessage,
      jawnEmptyBodyMessage) {}

  def build: ArgonautInstances =
    new ArgonautInstances {
      override val defaultPrettyParams: PrettyParams = self.defaultPrettyParams
      override val jsonDecodeError: (Json, String, CursorHistory) => DecodeFailure =
        self.jsonDecodeError
      override val jawnParseExceptionMessage: ParseException => DecodeFailure =
        self.jawnParseExceptionMessage
      override val jawnEmptyBodyMessage: DecodeFailure = self.jawnEmptyBodyMessage
    }
}

@deprecated("http4s-argonaut support will be dropped in 0.22", "0.21.19")
object ArgonautInstances {
  type DecodeFailureMessage = String
  def withPrettyParams(pp: PrettyParams): ArgonautInstancesBuilder =
    builder.withPrettyParams(pp)

  val builder: ArgonautInstancesBuilder = new ArgonautInstancesBuilder() {}

  private[argonaut] def defaultJsonDecodeError
      : (Json, DecodeFailureMessage, CursorHistory) => DecodeFailure =
    (json, message, history) =>
      InvalidMessageBodyFailure(s"Could not decode JSON: $json, error: $message, cursor: $history")
}
