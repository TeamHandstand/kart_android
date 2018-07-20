package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo

class AvatarUtil(context: Context) {
    private val context: Context = context

    companion object {
        private fun isGrayScale(isCurrentUser: Boolean, userState: UserRaceInfo.UserState): Boolean {
            val disconnected = (userState == UserRaceInfo.UserState.DISCONNECTED)
            val detachedOrInjured = (userState == UserRaceInfo.UserState.DETACHED || userState == UserRaceInfo.UserState.INJURED)
            return (disconnected || (detachedOrInjured && !isCurrentUser))
        }
    }

    //region - Public

    // TODO: Update with UserRaceInfo
    fun createAvatarMarkerBitmap(profileBitmap: Bitmap,
                                 userId: String,
                                 userState: UserRaceInfo.UserState,
                                 isTargeted: Boolean,
                                 ranking: Int,
                                 armored: Boolean): Bitmap {
        val canvasDimen = ViewUtil.dpToPx(context, 200)
        val canvasBitmap = Bitmap.createBitmap(canvasDimen, canvasDimen, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)

        val center = Point(canvasBitmap.width / 2, canvasBitmap.height / 2)
        val borderWidth = ViewUtil.dpToPx(context, 2)

        val isCurrentUser = (userId == Storage.userId)

        // Bound the profile bitmap to fit our coordinate system and draw the image
        // TODO: If we pass in 'null' we should default to a placeholder image
        var boundedProfileBitmap = BitmapUtils.getBoundedDimensionBitmap(profileBitmap, ViewUtil.dpToPx(context, 50))
        val borderColor = getBorderColor(isCurrentUser, userState)

        if (isGrayScale(isCurrentUser, userState)) {
            boundedProfileBitmap = BitmapUtils.getGrayscaleBitmap(boundedProfileBitmap)
        }

        BitmapUtils.drawBitmap(BitmapUtils.getCircularCroppedBitmap(
                boundedProfileBitmap,
                borderWidth.toFloat(),
                Color.WHITE,
                borderColor),
                center,
                canvas)

        // If we are being targeted, draw the targeted bitmap
        if (isTargeted) {
            val targetingBitmap = BitmapUtils.getBitmap(context, R.drawable.ic_targeting_crosshairs)
            BitmapUtils.drawBitmap(targetingBitmap, center, canvas)
        }

        val profileHeight = profileBitmap.height
        val profileWidth = profileBitmap.width

        val profileCenterTop = Point(center.x, center.y - profileHeight / 2)

        // Overlay state affliction
        when (userState) {
            UserRaceInfo.UserState.DISCONNECTED -> BitmapUtils.drawTextBitmapToCanvas(
                    canvas,
                    "☠",
                    ViewUtil.spToPx(context, 35).toFloat(),
                    Color.DKGRAY,
                    center)
            UserRaceInfo.UserState.INJURED -> {
                val injuredBitmap = BitmapUtils.getBitmap(context, R.drawable.ic_injured_bandage)
                var injuredBitmapHeight = injuredBitmap.height
                val injuredBitmapCenter = Point(profileCenterTop.x,
                        (profileCenterTop.y + injuredBitmapHeight / 2 - borderWidth))
                BitmapUtils.drawBitmap(injuredBitmap, injuredBitmapCenter, canvas)
            }
        }

        // If we are in first place, let's add the crown
        if (ranking == 1) {
            val crownBitmap = BitmapUtils.getBitmap(context, R.drawable.first_place_crown)
            val crownCenter = Point(profileCenterTop.x, (profileCenterTop.y - crownBitmap.height / 4))
            BitmapUtils.drawBitmap(crownBitmap, crownCenter, canvas)
        }

        // Create item (if it exists)
        if (armored) {
            val shieldBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar_shield)
            val shieldCenter = Point(center.x - profileWidth / 2, center.y + profileHeight / 2)
            BitmapUtils.drawBitmap(shieldBitmap, shieldCenter, canvas)
        }

        // Create the buddy (always exists)
        // TODO: For testing purposes, we will just draw the shield as a placeholder for the buddy image
//            Storage.userBuddyUrl
        val buddyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar_shield)
        val buddyCenter = Point(center.x + profileWidth / 2, center.y + profileHeight / 2)
        BitmapUtils.drawBitmap(buddyBitmap, buddyCenter, canvas)

        return canvasBitmap
    }

    // TODO: Pass in the item zone model object here
    fun createItemZoneBitmap(activeBlockCount: Int): Bitmap {
        return if (activeBlockCount > 0) BitmapUtils.getBitmap(context, R.drawable.item_zone_active) else BitmapUtils.getBitmap(context, R.drawable.item_zone_inactive)
    }

    //endregion

    //region - Private

    private fun getBorderColor(isCurrentUser: Boolean, userState: UserRaceInfo.UserState): Int {
        return when (userState) {
            UserRaceInfo.UserState.DISCONNECTED -> ContextCompat.getColor(context, R.color.light_grey)
            UserRaceInfo.UserState.ATTACHED -> ContextCompat.getColor(context, R.color.blue)
            UserRaceInfo.UserState.DETACHED -> if (isCurrentUser) ContextCompat.getColor(context, R.color.red) else ContextCompat.getColor(context, R.color.blue)
            UserRaceInfo.UserState.INJURED -> ContextCompat.getColor(context, R.color.light_grey)
        }
    }

    //endregion
}