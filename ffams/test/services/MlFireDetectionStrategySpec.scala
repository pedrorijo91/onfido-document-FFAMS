package services

import api.models.Image
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.Configuration
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class MlFireDetectionStrategySpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {

  "MlFireDetectionStrategySpec" should {

    "check happy path with fire risk" in {

      val scoringGatewayClient = mock[ScoringGatewayClient]
      val classifierClient = mock[VegetationClassifierClient]
      val configuration = Configuration(
        "app.conf.fire.threshold" -> 0.7d,
        "app.conf.min.vegetation.weight" -> 0.6d
      )

      val service = new MlFireDetectionStrategy(configuration, classifierClient, scoringGatewayClient)

      val image = Image("AABB")

      when(classifierClient.classify(image)).thenReturn(Map('A' -> 0.5d, 'B' -> 0.5d))
      when(scoringGatewayClient.getScores(Set('A', 'B'))).thenReturn(Map('A' -> Option(0.85d), 'B' -> Option(0.60)))

      val result = service.isItAboutToCatchFire(image)

      result mustBe(Right(true))
    }

    "check happy path without fire risk" in {

      val scoringGatewayClient = mock[ScoringGatewayClient]
      val classifierClient = mock[VegetationClassifierClient]
      val configuration = Configuration(
        "app.conf.fire.threshold" -> 0.7d,
        "app.conf.min.vegetation.weight" -> 0.6d
      )

      val service = new MlFireDetectionStrategy(configuration, classifierClient, scoringGatewayClient)

      val image = Image("AABB")

      when(classifierClient.classify(image)).thenReturn(Map('A' -> 0.5d, 'B' -> 0.5d))
      when(scoringGatewayClient.getScores(Set('A', 'B'))).thenReturn(Map('A' -> Option(0.7d), 'B' -> Option(0.60)))

      val result = service.isItAboutToCatchFire(image)

      result mustBe(Right(false))
    }

    "check weighted average is taken into account" in {

      val scoringGatewayClient = mock[ScoringGatewayClient]
      val classifierClient = mock[VegetationClassifierClient]
      val configuration = Configuration(
        "app.conf.fire.threshold" -> 0.3d,
        "app.conf.min.vegetation.weight" -> 0.6d
      )

      val service = new MlFireDetectionStrategy(configuration, classifierClient, scoringGatewayClient)

      val image = Image("AAAAB")

      // we assign a lot of weight to vegetation A, but we give it low fire risk score
      // we do the opposite to B
      // meaning A should 'master' the decision
      when(classifierClient.classify(image)).thenReturn(Map('A' -> 0.8d, 'B' -> 0.2d))
      when(scoringGatewayClient.getScores(Set('A', 'B'))).thenReturn(Map('A' -> Option(0.1d), 'B' -> Option(0.9)))

      val result = service.isItAboutToCatchFire(image)

      result mustBe(Right(false))
    }

    "check fire assessment is possible if only a small part of vegetation is not scored" in {

      val scoringGatewayClient = mock[ScoringGatewayClient]
      val classifierClient = mock[VegetationClassifierClient]
      val configuration = Configuration(
        "app.conf.fire.threshold" -> 0.7d,
        "app.conf.min.vegetation.weight" -> 0.6d
      )

      val service = new MlFireDetectionStrategy(configuration, classifierClient, scoringGatewayClient)

      val image = Image("ABCZ")

      when(classifierClient.classify(image)).thenReturn(Map('A' -> 0.25d, 'B' -> 0.25d, 'C' -> 0.25d, 'Z' -> 0.25d))
      when(scoringGatewayClient.getScores(Set('A', 'B', 'C', 'Z'))).thenReturn(Map('A' -> Option(0.5d), 'B' -> Option(0.5), 'C' -> Option(0.5d)))

      val result = service.isItAboutToCatchFire(image)

      result mustBe(Right(false))
    }

    "check fire assessment is NOT possible if a significant part of vegetation is not scored" in {

      val scoringGatewayClient = mock[ScoringGatewayClient]
      val classifierClient = mock[VegetationClassifierClient]
      val configuration = Configuration(
        "app.conf.fire.threshold" -> 0.7d,
        "app.conf.min.vegetation.weight" -> 0.6d
      )

      val service = new MlFireDetectionStrategy(configuration, classifierClient, scoringGatewayClient)

      val image = Image("ABCZ")

      when(classifierClient.classify(image)).thenReturn(Map('A' -> 0.1d, 'B' -> 0.1d, 'C' -> 0.1d, 'Z' -> 0.7d))
      when(scoringGatewayClient.getScores(Set('A', 'B', 'C', 'Z'))).thenReturn(Map('A' -> Option(0.5d), 'B' -> Option(0.5), 'C' -> Option(0.5d)))

      val result = service.isItAboutToCatchFire(image)

      result.isLeft mustBe(true)
    }

  }

}
