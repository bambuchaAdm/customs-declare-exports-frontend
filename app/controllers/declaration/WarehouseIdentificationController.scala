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

package controllers.declaration

import config.AppConfig
import controllers.actions.{AuthAction, JourneyAction}
import controllers.util.CacheIdGenerator.cacheId
import forms.declaration.WarehouseIdentification
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CustomsCacheService
import services.cache.{ExportsCacheModel, ExportsCacheService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration.warehouse_identification

import scala.concurrent.{ExecutionContext, Future}

class WarehouseIdentificationController @Inject()(
  appConfig: AppConfig,
  authenticate: AuthAction,
  journeyType: JourneyAction,
  customsCacheService: CustomsCacheService,
  exportsCacheService: ExportsCacheService,
  mcc: MessagesControllerComponents,
  warehouseIdentificationPage: warehouse_identification
)(implicit ec: ExecutionContext)
    extends {
  val cacheService = exportsCacheService
} with FrontendController(mcc) with I18nSupport with ModelCacheable with SessionIdAware {

  import forms.declaration.WarehouseIdentification._

  def displayForm(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    exportsCacheService.get(journeySessionId).map(_.flatMap(_.locations.warehouseIdentification)).map {
      case Some(data) => Ok(warehouseIdentificationPage(appConfig, form.fill(data)))
      case _          => Ok(warehouseIdentificationPage(appConfig, form))
    }
  }

  def saveWarehouse(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[WarehouseIdentification]) =>
          Future.successful(BadRequest(warehouseIdentificationPage(appConfig, formWithErrors))),
        form =>
          for {
            _ <- updateCache(journeySessionId, form)
            _ <- customsCacheService.cache[WarehouseIdentification](cacheId, formId, form)
          } yield Redirect(controllers.declaration.routes.BorderTransportController.displayForm())
      )
  }

  private def updateCache(sessionId: String, formData: WarehouseIdentification): Future[Option[ExportsCacheModel]] =
    getAndUpdateExportCacheModel(sessionId, model => {
      val updatedLocations = model.locations.copy(warehouseIdentification = Some(formData))
      cacheService.update(sessionId, model.copy(locations = updatedLocations))
    })
}
