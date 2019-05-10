package acceptance

import java.util.concurrent.TimeUnit

import base.{MockAuthAction, MockConnectors}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfter
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.play.{ChromeFactory, OneBrowserPerSuite, PlaySpec}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import services.CustomsCacheService
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.Future

class AcceptanceSpec
    extends PlaySpec with BeforeAndAfter with GuiceOneServerPerSuite with OneBrowserPerSuite
    with ChromeFactory with MockAuthAction with MockConnectors {

  val mockCustomsCacheService: CustomsCacheService = mock[CustomsCacheService]

  // override settings for chrome driver
  System.setProperty("webdriver.chrome.driver", "./driver/chromedriver")
  webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS)

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = scaled(Span(10, Seconds)), interval = scaled(Span(100, Millis)))

  override lazy val app: Application = GuiceApplicationBuilder()
    .overrides(
      bind[AuthConnector].to(mockAuthConnector),
      bind[CustomsCacheService].to(mockCustomsCacheService)
    ).build()

  def withCaching[T](dataToReturn: Option[T], id: String): OngoingStubbing[Future[CacheMap]] = {
    when(
      mockCustomsCacheService
        .fetchAndGetEntry[T](any(), ArgumentMatchers.eq(id))(any(), any(), any())
    ).thenReturn(Future.successful(dataToReturn))

    when(mockCustomsCacheService.cache[T](any(), any(), any())(any(), any(), any()))
      .thenReturn(Future.successful(CacheMap(id, Map.empty)))
  }
}
