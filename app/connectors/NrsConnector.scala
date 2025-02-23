/*
 * Copyright 2019 HM Revenue & Customs
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

package connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.{NRSSubmission, NrsSubmissionResponse}
import play.api.Logger
import uk.gov.hmrc.http.{HeaderCarrier, HttpException}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NrsConnector @Inject()(appConfig: AppConfig, httpClient: HttpClient) {

  private val logger: Logger = Logger(this.getClass)
  val xApiKeyHeader = "X-API-Key"

  val nrsSubmissionUrl: String = s"${appConfig.nrsServiceUrl}/submission"

  def submitNonRepudiation(
    nrsSubmission: NRSSubmission
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[NrsSubmissionResponse] =
    httpClient
      .POST[NRSSubmission, NrsSubmissionResponse](
        nrsSubmissionUrl,
        nrsSubmission,
        Seq[(String, String)](("Content-Type", "application/json"), (xApiKeyHeader, appConfig.nrsApiKey))
      )
      .map { res =>
        logger.info(s"Response received from nrs service submission id: $res")
        res
      }
      .recoverWith {
        case httpError: HttpException =>
          logger.error(s"Call to nrs service failed url=$nrsSubmissionUrl, HttpException=$httpError")
          Future.failed(new RuntimeException(httpError))
        case e: Throwable =>
          logger.error(s"Call to nrs service failed url=$nrsSubmissionUrl, exception=$e")
          Future.failed(e)
      }
}
