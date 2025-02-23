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
import forms.declaration.ConsignmentReferences
import handlers.ErrorHandler
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CustomsCacheService
import services.cache.{ExportsCacheModel, ExportsCacheService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration.consignment_references

import scala.concurrent.{ExecutionContext, Future}

class ConsignmentReferencesController @Inject()(
  appConfig: AppConfig,
  authenticate: AuthAction,
  journeyType: JourneyAction,
  errorHandler: ErrorHandler,
  customsCacheService: CustomsCacheService,
  exportsCacheService: ExportsCacheService,
  mcc: MessagesControllerComponents,
  consignmentReferencesPage: consignment_references
)(implicit ec: ExecutionContext)
    extends {
  val cacheService = exportsCacheService
} with FrontendController(mcc) with I18nSupport with ModelCacheable with SessionIdAware {

  def displayPage(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    exportsCacheService.get(journeySessionId).map(_.flatMap(_.consignmentReferences)).map {
      case Some(data) => Ok(consignmentReferencesPage(appConfig, ConsignmentReferences.form().fill(data)))
      case _          => Ok(consignmentReferencesPage(appConfig, ConsignmentReferences.form()))
    }
  }

  def submitConsignmentReferences(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    ConsignmentReferences.form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[ConsignmentReferences]) =>
          Future.successful(BadRequest(consignmentReferencesPage(appConfig, formWithErrors))),
        validConsignmentReferences =>
          for {
            _ <- updateCache(journeySessionId, validConsignmentReferences)
            _ <- customsCacheService
              .cache[ConsignmentReferences](cacheId, ConsignmentReferences.id, validConsignmentReferences)
          } yield Redirect(controllers.declaration.routes.ExporterDetailsController.displayForm())
      )
  }

  private def updateCache(sessionId: String, formData: ConsignmentReferences): Future[Option[ExportsCacheModel]] =
    getAndUpdateExportCacheModel(sessionId, model => {
      exportsCacheService.update(sessionId, model.copy(consignmentReferences = Some(formData)))
    })

}
