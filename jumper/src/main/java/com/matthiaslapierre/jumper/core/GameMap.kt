package com.matthiaslapierre.jumper.core

import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.PlayerPowerUp
import com.matthiaslapierre.jumper.JumperConstants.FIRST_CANDIES_BOTTOM
import com.matthiaslapierre.jumper.core.sprites.collectibles.CandySprite
import com.matthiaslapierre.jumper.core.sprites.collectibles.PowerUpSprite
import com.matthiaslapierre.jumper.core.sprites.obstacles.BatSprite
import com.matthiaslapierre.jumper.core.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.utils.Utils
import kotlin.math.abs

class GameMap(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
) {

    companion object {
        private const val PATTERN_JUMPING_PLATFORM = 1
        private const val PATTERN_CANDIES_LINE = 2
        private const val PATTERN_CANDIES_MATRIX = 3
        private const val PATTERN_BAT_STATIC = 4
        private const val PATTERN_BAT_DYNAMIC = 5

        private const val DRAW_CHANCE_PATTERN_CANDIES_LINE = 20
        private const val DRAW_CHANCE_PATTERN_CANDIES_MATRIX = 20
        private const val DRAW_CHANCE_PATTERN_BAT_STATIC = 10
        private const val DRAW_CHANCE_PATTERN_BAT_DYNAMIC = 10

        private const val DRAW_CHANCE_POWER_UP_COPTER = 25
        private const val DRAW_CHANCE_POWER_UP_MAGNET = 25
        private const val DRAW_CHANCE_POWER_UP_ROCKET = 25
        private const val DRAW_CHANCE_POWER_UP_SHIELD = 25

        private const val MAX_CANDIES_X_CAPACITY = 4
        private const val MAX_CANDIES_Y_CAPACITY = 5

        private const val DIRECTION_LEFT = 1
        private const val DIRECTION_RIGHT = 2

        private const val CANDY_SPACE_X_RATIO = .18f
        private const val CANDY_SPACE_Y_RATIO = .12f
        private const val CANDY_OUTSET_RATIO = .09f
        private const val JUMPING_PLATFORM_OUTSET_RATIO = .12f
        private const val BAT_OUTSET_RATIO = .14f

        private const val CANDIES_MIN_MARGIN_TOP_RATIO = 0.2f
        private const val CANDIES_MAX_MARGIN_TOP_RATIO = 0.3f
        private const val JUMPING_PLATFORM_MIN_MARGIN_TOP_RATIO = 0.2f
        private const val JUMPING_PLATFORM_MAX_MARGIN_TOP_RATIO = 0.3f
        private const val BAT_MIN_MARGIN_TOP_RATIO = 0.2f
        private const val BAT_MAX_MARGIN_TOP_RATIO = 0.3f

        private const val POWER_UP_MIN_INTERVAL_RATIO = 4f
        private const val POWER_UP_MAX_INTERVAL_RATIO = 8f
        private const val OBSTACLE_MIN_INTERVAL_RATIO = 4f
        private const val OBSTACLE_MAX_INTERVAL_RATIO = 8f

        private const val POWER_UP_FIRST_INTERVAL_RATIO = 6f
        private const val OBSTACLE_FIRST_INTERVAL_RATIO = 6f
    }

    private var lastGeneratedSprite: Sprite? = null
    private var lastObstacleSprite: Sprite? = null
    private var lastPowerUpSprite: Sprite? = null

    private var obstacleIntervalMinElevation: Float = UNDEFINED
    private var powerUpIntervalMinElevation: Float = UNDEFINED

    private var screenWidth: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED

    fun generate(): List<Sprite> {
        synchronized(this) {
            val generatedSprites = mutableListOf<Sprite>()

            if (screenHeight == UNDEFINED) {
                return generatedSprites
            }

            var nextSpriteY = screenHeight - (screenWidth * FIRST_CANDIES_BOTTOM)
            var nextSpriteX = getNextSpriteX()
            if (lastGeneratedSprite != null) {
                val previousPattern = getPattern(lastGeneratedSprite!!)
                nextSpriteX = getNextSpriteX()
                nextSpriteY = lastGeneratedSprite!!.y - getSpriteMarginTop(previousPattern)
            }
            while (nextSpriteY > -(screenHeight * 2f)) {
                val pattern = getRandomPattern(nextSpriteY)
                val sprites = buildSprites(nextSpriteX, nextSpriteY, pattern)
                generatedSprites.addAll(sprites)
                when (pattern) {
                    PATTERN_BAT_STATIC, PATTERN_BAT_DYNAMIC -> {
                        lastObstacleSprite = sprites.last()
                    }
                    PATTERN_CANDIES_LINE, PATTERN_CANDIES_MATRIX -> {
                        lastPowerUpSprite = sprites.firstOrNull { it is PowerUpSprite } ?:
                                lastPowerUpSprite
                    }
                }
                lastGeneratedSprite = sprites.last()
                nextSpriteY = lastGeneratedSprite!!.y
                nextSpriteX = getNextSpriteX()
                nextSpriteY -= getSpriteMarginTop(pattern)
            }

            return generatedSprites
        }
    }

    fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
        this.powerUpIntervalMinElevation = screenHeight * POWER_UP_FIRST_INTERVAL_RATIO
        this.obstacleIntervalMinElevation = screenHeight * OBSTACLE_FIRST_INTERVAL_RATIO
    }

    private fun getSpriteMarginTop(previousPattern: Int?): Float =
        when (previousPattern) {
            PATTERN_CANDIES_LINE, PATTERN_CANDIES_MATRIX -> Utils.getRandomFloat(
                screenWidth * CANDIES_MIN_MARGIN_TOP_RATIO,
                screenWidth * CANDIES_MAX_MARGIN_TOP_RATIO
            )
            PATTERN_BAT_STATIC, PATTERN_BAT_DYNAMIC -> Utils.getRandomFloat(
                screenWidth * BAT_MIN_MARGIN_TOP_RATIO,
                screenWidth * BAT_MAX_MARGIN_TOP_RATIO
            )
            else -> Utils.getRandomFloat(
                screenWidth * JUMPING_PLATFORM_MIN_MARGIN_TOP_RATIO,
                screenWidth * JUMPING_PLATFORM_MAX_MARGIN_TOP_RATIO
            )
        }

    private fun getPattern(sprite: Sprite): Int =
        when (sprite) {
            is CandySprite, is PowerUpSprite -> PATTERN_CANDIES_LINE
            is BatSprite -> PATTERN_BAT_STATIC
            else -> PATTERN_JUMPING_PLATFORM
        }

    private fun getNextSpriteX(): Float {
        return Utils.getRandomFloat(0f, screenWidth)
    }

    private fun getNextObstacleInterval(): Float {
        val minInterval = screenHeight * OBSTACLE_MIN_INTERVAL_RATIO
        val maxInterval = screenHeight * OBSTACLE_MAX_INTERVAL_RATIO
        return Utils.getRandomFloat(minInterval, maxInterval)
    }

    private fun getNextPowerUpInterval(): Float {
        val minInterval = screenHeight * POWER_UP_MIN_INTERVAL_RATIO
        val maxInterval = screenHeight * POWER_UP_MAX_INTERVAL_RATIO
        return Utils.getRandomFloat(minInterval, maxInterval)
    }

    private fun buildSprites(x: Float, y: Float, pattern: Int): List<Sprite> =
        when(pattern) {
            PATTERN_JUMPING_PLATFORM -> buildJumpingPlatformSprites(x, y)
            PATTERN_CANDIES_LINE -> buildCandiesLineSprites(x, y)
            PATTERN_CANDIES_MATRIX -> buildCandiesMatrixSprites(x, y)
            else -> buildBatSprites(x, y, pattern == PATTERN_BAT_STATIC)
        }

    private fun buildJumpingPlatformSprites(x: Float, y: Float): List<Sprite> {
        val outset = screenWidth * JUMPING_PLATFORM_OUTSET_RATIO
        val maxX = screenWidth - outset
        val spriteX = when {
            x > maxX -> {
                maxX
            }
            x < outset -> {
                outset
            }
            else -> {
                x
            }
        }
        val jumpingPlatformSprite = JumpingPlatformSprite(resourceManager, gameStates, spriteX, y)
        return arrayListOf(jumpingPlatformSprite)
    }

    private fun buildCandiesLineSprites(x: Float, y: Float): List<Sprite> {
        val sprites = mutableListOf<Sprite>()
        val capacity = Utils.getRandomInt(1,
            MAX_CANDIES_Y_CAPACITY
        )
        val direction = Utils.getRandomInt(1, 4)
        val outset = (screenWidth * CANDY_OUTSET_RATIO)
        val spaceX = (screenWidth * CANDY_SPACE_X_RATIO)
        val spaceY = (screenWidth * CANDY_SPACE_Y_RATIO)
        val maxX = screenWidth - outset
        var spriteX = x
        var spriteY = y
        var powerUpGenerated = false
        for (index in 0 until capacity) {
            spriteX = when {
                spriteX > maxX -> {
                    maxX
                }
                spriteX < outset -> {
                    outset
                }
                else -> {
                    spriteX
                }
            }
            val sprite = if (!powerUpGenerated
                && ((lastPowerUpSprite == null && y > powerUpIntervalMinElevation)
                || (lastPowerUpSprite != null && abs(lastPowerUpSprite!!.y - y) >= getNextPowerUpInterval()))) {
                powerUpGenerated = true
                PowerUpSprite(resourceManager, gameStates, spriteX, spriteY, getRandomPowerUp())
            } else {
                CandySprite(resourceManager, gameStates, spriteX, spriteY)
            }
            sprites.add(sprite)
            spriteX = when (direction) {
                DIRECTION_LEFT -> spriteX - Utils.getRandomFloat(0f, spaceX)
                DIRECTION_RIGHT -> spriteX + Utils.getRandomFloat(0f, spaceX)
                else -> spriteX
            }
            spriteY -= spaceY
        }
        return sprites
    }

    private fun buildCandiesMatrixSprites(x: Float, y: Float): List<Sprite> {
        val sprites = mutableListOf<Sprite>()
        val capacityX = Utils.getRandomInt(1,
            MAX_CANDIES_X_CAPACITY
        )
        val capacityY = Utils.getRandomInt(1,
            MAX_CANDIES_Y_CAPACITY
        )
        val outset = (screenWidth * CANDY_OUTSET_RATIO)
        val spaceX = (screenWidth * CANDY_SPACE_X_RATIO)
        val spaceY = (screenWidth * CANDY_SPACE_Y_RATIO)
        val maxX = screenWidth - ((capacityX - 1) * spaceX) - outset
        val startX = when {
            x > maxX -> {
                maxX
            }
            x < outset -> {
                outset
            }
            else -> {
                x
            }
        }
        var spriteX = startX
        var spriteY = y
        var powerUpGenerated = false
        for (indexY in 0 until capacityY) {
            for (indexX in 0 until capacityX) {
                val sprite: Sprite = if (!powerUpGenerated
                    && ((lastPowerUpSprite == null && y > powerUpIntervalMinElevation)
                    || (lastPowerUpSprite != null && abs(lastPowerUpSprite!!.y - y) >= getNextPowerUpInterval()))) {
                    powerUpGenerated = true
                    PowerUpSprite(resourceManager, gameStates, spriteX, spriteY, getRandomPowerUp())
                } else {
                    CandySprite(resourceManager, gameStates, spriteX, spriteY)
                }
                sprites.add(sprite)
                spriteX += spaceX
            }
            spriteX = startX
            spriteY -= spaceY
        }
        return sprites
    }

    private fun buildBatSprites(x: Float, y: Float, isStatic: Boolean): List<Sprite> {
        val outset = (screenWidth * BAT_OUTSET_RATIO)
        val minX = outset
        val maxX = screenWidth - outset
        val spriteX = when {
            x < minX -> {
                minX
            }
            x > maxX -> {
                maxX
            }
            else -> {
                x
            }
        }
        val swingMinX: Float
        val swingMaxX: Float
        if (isStatic) {
            swingMinX = spriteX
            swingMaxX = spriteX
        } else {
            swingMinX = Utils.getRandomFloat(minX, spriteX)
            swingMaxX = Utils.getRandomFloat(spriteX, maxX)
        }
        val batSprite = BatSprite(resourceManager, gameStates, spriteX, y, swingMinX, swingMaxX)
        return listOf(batSprite)
    }

    private fun getRandomPattern(y: Float): Int {
        val randomInt = Utils.getRandomInt(1, 100)
        val pattern = when {
            randomInt <= DRAW_CHANCE_PATTERN_BAT_STATIC -> PATTERN_BAT_STATIC
            randomInt <= DRAW_CHANCE_PATTERN_BAT_STATIC + DRAW_CHANCE_PATTERN_BAT_DYNAMIC -> PATTERN_BAT_DYNAMIC
            randomInt <= DRAW_CHANCE_PATTERN_BAT_STATIC + DRAW_CHANCE_PATTERN_BAT_DYNAMIC + DRAW_CHANCE_PATTERN_CANDIES_LINE -> PATTERN_CANDIES_LINE
            randomInt <= DRAW_CHANCE_PATTERN_BAT_STATIC + DRAW_CHANCE_PATTERN_BAT_DYNAMIC + DRAW_CHANCE_PATTERN_CANDIES_LINE + DRAW_CHANCE_PATTERN_CANDIES_MATRIX -> PATTERN_CANDIES_MATRIX
            else -> PATTERN_JUMPING_PLATFORM
        }
        /*return if ((pattern == PATTERN_BAT_STATIC || pattern == PATTERN_BAT_DYNAMIC)
            && ((lastObstacleSprite != null && abs(y - lastObstacleSprite!!.y) < getNextObstacleInterval())
                    || (lastObstacleSprite == null && gameStates.elevation < obstacleIntervalMinElevation))) {
            PATTERN_JUMPING_PLATFORM
        } else {*/
            return PATTERN_JUMPING_PLATFORM
        //}
    }

    private fun getRandomPowerUp(): PlayerPowerUp {
        val randomInt = Utils.getRandomInt(1, 100)
        return when {
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER -> PlayerPowerUp.COPTER
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER + DRAW_CHANCE_POWER_UP_MAGNET -> PlayerPowerUp.MAGNET
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER + DRAW_CHANCE_POWER_UP_MAGNET + DRAW_CHANCE_POWER_UP_ROCKET -> PlayerPowerUp.ROCKET
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER + DRAW_CHANCE_POWER_UP_MAGNET + DRAW_CHANCE_POWER_UP_ROCKET + DRAW_CHANCE_POWER_UP_SHIELD -> PlayerPowerUp.ROCKET
            else -> PlayerPowerUp.ARMORED
        }
    }

}