package services

import api.models.Image
import api.models.Image.Vegetation

/**
 * This class represents a client to access the vegetation classifier public API
 *
 * @note for the sake of simplicity, we fake the request. Otherwise we'd need to have yet another application running
 */
class VegetationClassifierClient {

  def classify(image: Image): Map[Vegetation, Double] = {

    // TODO call vegetation API - for now we add some fake logic for extracting vegetation weights

    image.pixels
      .groupBy(identity)
      .mapValues(_.length)
      .mapValues(count => count.toDouble / image.pixels.length)
      .toMap
  }

}
