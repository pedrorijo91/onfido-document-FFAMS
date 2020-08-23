package controllers

import api.models.Image
import javax.inject._
import play.api.Configuration
import play.api.mvc._
import services.{FireDetectionStrategy, MlFireDetectionStrategy}
import play.api.libs.json._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(config: Configuration, val controllerComponents: ControllerComponents) extends BaseController {

  // TODO proper DI
  private val fireDetectionStrategy: FireDetectionStrategy = new MlFireDetectionStrategy(config)

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def fakeRequest() = Action { implicit request: Request[AnyContent] =>

    val image = Image(request.body.asText.get) // TODO validate request body => use proper body parse mechanism

    val isItAboutToCatchFire = fireDetectionStrategy.isItAboutToCatchFire(image)

    isItAboutToCatchFire match {
      case Left(err) =>  Ok(Json.obj(("error", err)))
      case Right(risk) =>  Ok(Json.obj(("isItAboutToCatchFire", JsBoolean(risk))))
    }
  }

}
