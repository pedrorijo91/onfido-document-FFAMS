package services

import api.models.Image
import api.models.Image.Vegetation
import javax.inject.Inject
import play.api.{Configuration, Logging}

trait FireDetectionStrategy {

  def isItAboutToCatchFire(image: Image): Either[String, Boolean]

}

class MlFireDetectionStrategy @Inject()(
                                         config: Configuration,
                                         // TODO proper DI
                                         vegetationClassifierClient: VegetationClassifierClient = new VegetationClassifierClient,
                                         scoringGateway: ScoringGatewayClient = new ScoringGatewayClient
                                       ) extends FireDetectionStrategy with Logging {

  private val riskThreshold = config.get[Double]("app.conf.fire.threshold")
  private val minVegetationWeight = config.get[Double]("app.conf.min.vegetation.weight")

  override def isItAboutToCatchFire(image: Image): Either[String, Boolean] = {
    val vegetationWeights: Map[Vegetation, Double] = vegetationClassifierClient.classify(image)
    val vegetationFireScores: Map[Vegetation, Option[Double]] = scoringGateway.getScores(vegetationWeights.keys.toSet)

    computeWeightedRisk(vegetationWeights, vegetationFireScores)
      .map(risk => Right(risk > riskThreshold))
      .getOrElse(Left("There was an error analysing the image"))
  }

  /**
   * @return The weighted fire risk given the weight and fire score risk of each vegetation type. It returns [[None]]
   *         there was a problem assessing the fire risk for too many vegetation types
   */
    // TODO it may make sense to create an easy way to exchange this computation strategy. In the future we may want to
    // look at the highest risk for instance
  private def computeWeightedRisk(
                                   vegetationWeights: Map[Vegetation, Double],
                                   vegetationFireScores: Map[Vegetation, Option[Double]]
                                 ): Option[Double] = {
    var totalWeight = 0.0d
    var totalFireRisk = 0.0d

    vegetationFireScores.foreach { case (vegetation, fireScore) =>

      fireScore.fold {
        // if there's no fireScore it means there was some problem calling the model and we ignore that vegetation type
        logger.warn(s"Did not receive fire score for vegetation: $vegetation")
      } { score =>
        totalFireRisk += score * vegetationWeights(vegetation)
        totalWeight += vegetationWeights(vegetation)
      }
    }

    if( totalWeight > minVegetationWeight) {
      Option(totalFireRisk)
    } else {
      logger.error(s"Could not get enough scoring to assess risk. Weight threshold: $minVegetationWeight vs achieved weight: $totalWeight")
      None
    }
  }
}
