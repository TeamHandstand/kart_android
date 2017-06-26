package us.handstand.kartwheel.layout

import android.content.res.AssetManager
import android.graphics.Typeface
import android.support.annotation.IntDef
import javax.inject.Inject


// TODO: The line and letter spacing are off (letters: W, A, O)
object Font {
    @IntDef(REGULAR, LIGHT, EXTRA_LIGHT, MEDIUM, BOLD, SEMI_BOLD, EXTRA_BOLD, BLACK, PHOSPHATE, ZAMENHOF)
    annotation class Kart

    const val REGULAR = 1L
    const val LIGHT = 2L
    const val EXTRA_LIGHT = 3L
    const val MEDIUM = 4L
    const val BOLD = 5L
    const val SEMI_BOLD = 6L
    const val EXTRA_BOLD = 7L
    const val BLACK = 8L
    const val PHOSPHATE = 9L
    const val ZAMENHOF = 10L

    private const val REGULAR_ = "Nunito-Regular.ttf"
    private const val LIGHT_ = "Nunito-Light.ttf"
    private const val EXTRA_LIGHT_ = "Nunito-ExtraLight.ttf"
    private const val MEDIUM_ = "Nunito-Medium.ttf"
    private const val BOLD_ = "Nunito-Bold.ttf"
    private const val SEMI_BOLD_ = "Nunito-SemiBold.ttf"
    private const val EXTRA_BOLD_ = "Nunito-ExtraBold.ttf"
    private const val BLACK_ = "Nunito-Black.ttf"
    private const val PHOSPHATE_ = "PhosphateSolid.ttf"
    private const val ZAMENHOF_ = "ZamenhofSolid.ttf"

    lateinit var get: Map<Long, Typeface>

    @Inject
    fun setUp(assetManager: AssetManager) {
        val fontDir = "kart_fonts"
        val initMap = mutableMapOf<Long, Typeface>()
        initMap[REGULAR] = Typeface.createFromAsset(assetManager, "$fontDir/$REGULAR_")
        initMap[LIGHT] = Typeface.createFromAsset(assetManager, "$fontDir/$LIGHT_")
        initMap[EXTRA_LIGHT] = Typeface.createFromAsset(assetManager, "$fontDir/$EXTRA_LIGHT_")
        initMap[MEDIUM] = Typeface.createFromAsset(assetManager, "$fontDir/$MEDIUM_")
        initMap[BOLD] = Typeface.createFromAsset(assetManager, "$fontDir/$BOLD_")
        initMap[SEMI_BOLD] = Typeface.createFromAsset(assetManager, "$fontDir/$SEMI_BOLD_")
        initMap[EXTRA_BOLD] = Typeface.createFromAsset(assetManager, "$fontDir/$EXTRA_BOLD_")
        initMap[BLACK] = Typeface.createFromAsset(assetManager, "$fontDir/$BLACK_")
        initMap[PHOSPHATE] = Typeface.createFromAsset(assetManager, "$fontDir/$PHOSPHATE_")
        initMap[ZAMENHOF] = Typeface.createFromAsset(assetManager, "$fontDir/$ZAMENHOF_")
        get = initMap
    }
}