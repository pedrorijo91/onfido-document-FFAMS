package services

import api.models.Image.Vegetation

/**
 * This class allows to call the Scoring Gateway public endpoints.
 */
class ScoringGatewayClient {

  // TODO delete when using the scoring gateway API
  private val hardcodedScores = Map(
    'A' -> 0.3d,
    'B' -> 0.1d,
    'C' -> 0.4d,
    'D' -> 0.8d,
    'E' -> 0.2d,
  )

  /**
   *
   * Gets the fire risk score for each of the given [[Vegetation]]
   *
   * @note If a given vegetation is send, then the resulting map will contain a key for that vegetation. In case of
   *       an error calling the corresponding model, [[None]] will be assigned
   */
  def getScores(vegetations: Set[Vegetation]): Map[Vegetation, Option[Double]] = {

    // TODO actually call the score gateway public API
    vegetations.map(vegetation => (vegetation, hardcodedScores.get(vegetation))).toMap
  }

}
