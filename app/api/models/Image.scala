package api.models

/**
 * This class represents an image sent to the system for analysis
 *
 * @note: The image is currently implemented as an List of pixels, where pixels are represented as a char which
 *       translates into the vegation type observed in the pixel. Thus we use
 *       a String as a list of pixels representation, for the sake of simplicity in this code.
 *       To make this production-ready code we'd need to find a more suitable representation. Adding GPS position could also
 *       be useful.
 */
case class Image(pixels: String)

object Image {

  type Vegetation = Char

}
