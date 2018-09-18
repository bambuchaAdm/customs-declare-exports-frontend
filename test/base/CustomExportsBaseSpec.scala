/*
 * Copyright 2018 HM Revenue & Customs
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

package base

import java.util.UUID

import akka.stream.Materializer
import config.AppConfig
import connectors.CustomsDeclarationsConnector
import controllers.actions.{AuthAction, AuthActionImpl, FakeAuthAction}
import models.{CustomsDeclarationsResponse, SignedInUser}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.{ArgumentMatcher, ArgumentMatchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Execution.Implicits
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsJson}
import play.api.test.FakeRequest
import play.filters.csrf.CSRF.Token
import play.filters.csrf.{CSRFConfig, CSRFConfigProvider, CSRFFilter}
import services.CustomsCacheService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, ~}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.Random


trait CustomExportsBaseSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures{
  protected val contextPath: String = "/customs-declare-exports"

  val testAuthAction = mock[AuthActionImpl]

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val mockCustomsDeclarationsConnector: CustomsDeclarationsConnector = mock[CustomsDeclarationsConnector]
  lazy val mockCustomsCacheService: CustomsCacheService = mock[CustomsCacheService]

  override lazy val app: Application = GuiceApplicationBuilder().overrides(
    bind[AuthConnector].to(mockAuthConnector),
    bind[AuthAction].to(testAuthAction),
    bind[CustomsDeclarationsConnector].to(mockCustomsDeclarationsConnector),
    bind[CustomsCacheService].to(mockCustomsCacheService)
  ).build()

  implicit val mat: Materializer = app.materializer
  implicit val ec: ExecutionContext = Implicits.defaultContext
  def injector: Injector = app.injector

  def appConfig: AppConfig = injector.instanceOf[AppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def authenticate: AuthActionImpl = injector.instanceOf[AuthActionImpl]

  val cfg: CSRFConfig = injector.instanceOf[CSRFConfigProvider].get

  val token = injector.instanceOf[CSRFFilter].tokenProvider.generateToken

  def wsClient: WSClient = injector.instanceOf[WSClient]

  def fakeRequest = FakeRequest("", "")

  def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def uriWithContextPath(path: String): String = s"$contextPath$path"

  protected def component[T: ClassTag]: T = app.injector.instanceOf[T]

  protected def getRequest(uri: String, headers: Map[String, String] = Map.empty): FakeRequest[AnyContentAsEmpty.type] = {
    val session: Map[String, String] = Map(
      SessionKeys.sessionId -> s"session-${UUID.randomUUID()}",
      SessionKeys.userId -> FakeAuthAction.defaultUser.internalId.get
    )
    val tags = Map(
      Token.NameRequestTag -> cfg.tokenName,
      Token.RequestTag -> token
    )
    FakeRequest("GET", uri)
      .withHeaders((Map(cfg.headerName -> token) ++ headers).toSeq: _*)
      .withSession(session.toSeq: _*).copyFakeRequest(tags = tags)
  }

  protected def postRequest(uri: String, body: JsValue, headers: Map[String, String] = Map.empty): FakeRequest[AnyContentAsJson] = {
    val session: Map[String, String] = Map(
      SessionKeys.sessionId -> s"session-${UUID.randomUUID()}",
      SessionKeys.userId -> FakeAuthAction.defaultUser.internalId.get
    )
    val tags = Map(
      Token.NameRequestTag -> cfg.tokenName,
      Token.RequestTag -> token
    )
    FakeRequest("POST", uri)
      .withHeaders((Map(cfg.headerName -> token) ++ headers).toSeq: _*)
      .withSession(session.toSeq: _*).copyFakeRequest(tags = tags)
      .withJsonBody(body)
  }

  val enrolment:Predicate = Enrolment("HMRC-CUS-ORG")

  def cdsEnrollmentMatcher(user: SignedInUser): ArgumentMatcher[Predicate] = new ArgumentMatcher[Predicate] {
    override  def matches(p: Predicate): Boolean = p ==  enrolment && user.enrolments.getEnrolment("HMRC-CUS-ORG").isDefined
  }

  //noinspection ConvertExpressionToSAM
  val noBearerTokenMatcher: ArgumentMatcher[HeaderCarrier] = new ArgumentMatcher[HeaderCarrier] {
    override def matches(hc: HeaderCarrier): Boolean = hc != null && hc.authorization.isEmpty
  }

  def authorizedUser(user: SignedInUser = newUser("12345","external1")): Unit = {
    when(
      mockAuthConnector.authorise(
          ArgumentMatchers.argThat(cdsEnrollmentMatcher(user)),
          ArgumentMatchers.eq(credentials and name and email and affinityGroup and internalId and allEnrolments))(
          any(), any())
    ).thenReturn(
      Future.successful(new ~(new ~(new ~(new ~(new ~(user.credentials, user.name), user.email), user.affinityGroup),
        user.internalId), user.enrolments))
    )
  }

  def unAuthorizedUser(exceptionToReturn: Throwable): Unit =
    when(mockAuthConnector.authorise(any(),any())(any(),any())).thenReturn(Future.failed(exceptionToReturn))

  protected def randomString(length: Int): String = Random.alphanumeric.take(length).mkString

  def newUser(eori: String, externalId: String): SignedInUser = SignedInUser(
    Credentials("2345235235","GovernmentGateway"),
    Name(Some("Aldo"),Some("Rain")),
    Some("amina@hmrc.co.uk"),
    eori,
    externalId,
    Some("Int-ba17b467-90f3-42b6-9570-73be7b78eb2b"),
    Some(AffinityGroup.Individual),
    Enrolments(Set(
      Enrolment("IR-SA",List(EnrolmentIdentifier("UTR","111111111")),"Activated",None),
      Enrolment("IR-CT",List(EnrolmentIdentifier("UTR","222222222")),"Activated",None),
      Enrolment("HMRC-CUS-ORG",List(EnrolmentIdentifier("EORINumber", eori)),"Activated",None)
    ))
  )

  def succesfulCustomsDeclarationReponse() = {
    when(mockCustomsDeclarationsConnector.submitExportDeclaration(any(),
      any())(any(), any())).thenReturn(Future.successful(CustomsDeclarationsResponse(202,Some("1234"))))
  }

  def customsDeclaration400Reponse() = {
    when(mockCustomsDeclarationsConnector.submitExportDeclaration(any(),
      any())(any(), any())).thenReturn(Future.successful(CustomsDeclarationsResponse(400, None)))
  }
  def withCaching[T](form: Option[Form[T]]) = {
    when(mockCustomsCacheService.fetchAndGetEntry[Form[T]](any(),
      any())(any(), any(),any())).thenReturn(Future.successful(form))

    when(mockCustomsCacheService.cache[T](any(),
      any(),any())(any(), any(),any())).thenReturn(Future.successful(CacheMap("id1",Map.empty)))

  }
}
