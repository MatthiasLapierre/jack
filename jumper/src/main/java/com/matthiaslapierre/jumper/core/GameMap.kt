package com.matthiaslapierre.jumper.core

import android.util.Log
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.BAT_MAX_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.BAT_MIN_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.BAT_OUTSET
import com.matthiaslapierre.jumper.JumperConstants.CANDIES_MAX_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.CANDIES_MIN_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.CANDY_OUTSET
import com.matthiaslapierre.jumper.JumperConstants.CANDY_SPACE_X
import com.matthiaslapierre.jumper.JumperConstants.CANDY_SPACE_Y
import com.matthiaslapierre.jumper.JumperConstants.DRAW_CHANCE_POWER_UP_COPTER
import com.matthiaslapierre.jumper.JumperConstants.DRAW_CHANCE_POWER_UP_MAGNET
import com.matthiaslapierre.jumper.JumperConstants.DRAW_CHANCE_POWER_UP_ROCKET
import com.matthiaslapierre.jumper.JumperConstants.DRAW_CHANCE_POWER_UP_SHIELD
import com.matthiaslapierre.jumper.JumperConstants.DRAW_CHANCE_SPIKE
import com.matthiaslapierre.jumper.JumperConstants.FIRST_SPRITE_Y
import com.matthiaslapierre.jumper.JumperConstants.GENERATOR_HIGHEST_Y
import com.matthiaslapierre.jumper.JumperConstants.JUMPING_PLATFORM_MAX_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.JUMPING_PLATFORM_MIN_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.JUMPING_PLATFORM_OUTSET
import com.matthiaslapierre.jumper.JumperConstants.MAX_CANDIES_X_CAPACITY
import com.matthiaslapierre.jumper.JumperConstants.MAX_CANDIES_Y_CAPACITY
import com.matthiaslapierre.jumper.JumperConstants.MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
import com.matthiaslapierre.jumper.JumperConstants.MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
import com.matthiaslapierre.jumper.JumperConstants.OBSTACLE_INTERVAL_MAX
import com.matthiaslapierre.jumper.JumperConstants.OBSTACLE_INTERVAL_MIN
import com.matthiaslapierre.jumper.JumperConstants.POWER_UP_INTERVAL_MAX
import com.matthiaslapierre.jumper.JumperConstants.POWER_UP_INTERVAL_MIN
import com.matthiaslapierre.jumper.JumperConstants.SPIKE_FIRST_INTERVAL
import com.matthiaslapierre.jumper.JumperConstants.SPIKE_MAX_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.SPIKE_MIN_MARGIN_TOP
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_SWING_X
import com.matthiaslapierre.jumper.core.sprites.collectibles.CandySprite
import com.matthiaslapierre.jumper.core.sprites.collectibles.PowerUpSprite
import com.matthiaslapierre.jumper.core.sprites.obstacles.BatSprite
import com.matthiaslapierre.jumper.core.sprites.obstacles.SpikeSprite
import com.matthiaslapierre.jumper.core.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.utils.Utils
import kotlin.math.abs
import kotlin.math.floor

internal class GameMap(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
) {

    companion object {
        private const val PATTERN_INITIAL_JUMPING_PLATFORM = 1
        private const val PATTERN_JUMPING_PLATFORM = 2
        private const val PATTERN_CANDIES_LINE = 3
        private const val PATTERN_CANDIES_GAP = 4
        private const val PATTERN_BAT = 5
        private const val PATTERN_SPIKE = 6
    }

    private var lastGeneratedSprite: Sprite? = null
    private var countObstaclesGenerated: Int = 0
    private var countPowerUpsGenerated: Int = 0
    private var elevation: Float = 0f

    private var screenWidth: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED

    fun generate(): List<Sprite> {
        synchronized(this) {
            val generatedSprites = mutableListOf<Sprite>()

            if (screenHeight == UNDEFINED) {
                return generatedSprites
            }

            var nextSpriteY = screenHeight - (screenWidth * FIRST_SPRITE_Y)
            var nextSpriteX = getNextSpriteX(null)
            var previousPattern: Int? = null
            if (lastGeneratedSprite != null) {
                previousPattern = getPattern(lastGeneratedSprite!!)
                nextSpriteX = getNextSpriteX(lastGeneratedSprite!!.x)
                nextSpriteY = lastGeneratedSprite!!.y - getSpriteMarginTop(previousPattern)
            }
            while (nextSpriteY > -(screenHeight * GENERATOR_HIGHEST_Y)) {
                val nextPattern = getNextPattern(previousPattern)
                val sprites = buildSprites(nextSpriteX, nextSpriteY, nextPattern)
                generatedSprites.addAll(sprites)
                when (nextPattern) {
                    PATTERN_BAT, PATTERN_SPIKE -> countObstaclesGenerated++
                    PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP ->
                        countPowerUpsGenerated += sprites.filterIsInstance<PowerUpSprite>().size
                }
                val previousGeneratedSprite = if (lastGeneratedSprite != null) {
                    lastGeneratedSprite
                } else {
                    sprites.first()
                }
                previousPattern = nextPattern
                lastGeneratedSprite = sprites.last()
                elevation += abs(lastGeneratedSprite!!.y - previousGeneratedSprite!!.y)
                nextSpriteY = lastGeneratedSprite!!.y
                nextSpriteX = getNextSpriteX(lastGeneratedSprite!!.x)
                nextSpriteY -= getSpriteMarginTop(nextPattern)
            }

            return generatedSprites
        }
    }

    fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
    }

    private fun getNextPattern(previousPattern: Int?): Int {
        return if (previousPattern == null) {
            PATTERN_INITIAL_JUMPING_PLATFORM
        } else if(previousPattern == PATTERN_INITIAL_JUMPING_PLATFORM) {
            val patterns = arrayOf(
                PATTERN_CANDIES_GAP,
                PATTERN_CANDIES_LINE
            )
            patterns[Utils.getRandomInt(0, patterns.size)]
        } else if(obstacleGenerationAllowed()) {
            if (elevation > screenWidth * SPIKE_FIRST_INTERVAL) {
                if(Utils.getRandomInt(0, 100) > DRAW_CHANCE_SPIKE) {
                    PATTERN_SPIKE
                } else {
                    PATTERN_BAT
                }
            } else {
                PATTERN_BAT
            }
        } else {
            val patterns = arrayOf(
                PATTERN_CANDIES_GAP,
                PATTERN_CANDIES_LINE,
                PATTERN_JUMPING_PLATFORM
            )
            patterns[Utils.getRandomInt(0, patterns.size)]
        }
    }

    private fun obstacleGenerationAllowed(): Boolean {
        return floor(elevation / Utils.getRandomFloat(
            screenWidth * OBSTACLE_INTERVAL_MIN,
            screenWidth * OBSTACLE_INTERVAL_MAX
        )) > countObstaclesGenerated
    }

    private fun powerUpGenerationAllowed(): Boolean {
        return floor(elevation / Utils.getRandomFloat(
            screenWidth * POWER_UP_INTERVAL_MIN,
            screenWidth * POWER_UP_INTERVAL_MAX
        )) > countPowerUpsGenerated
    }

    private fun getNextSpriteX(x: Float?): Float {
        return if (x != null) {
            val swing = screenWidth * SPRITE_SWING_X
            val minX = 0f.coerceAtLeast(x - swing)
            val maxX = screenWidth.coerceAtMost(x + swing)
            Utils.getRandomFloat(minX, maxX)
        } else {
            Utils.getRandomFloat(0f, screenWidth)
        }
    }

    private fun buildSprites(x: Float, y: Float, pattern: Int): List<Sprite> =
        when(pattern) {
            PATTERN_INITIAL_JUMPING_PLATFORM -> buildJumpingPlatformSprites(
                x,
                y,
                MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
            )
            PATTERN_JUMPING_PLATFORM -> buildJumpingPlatformSprites(
                x,
                y,
                Utils.getRandomInt(
                    MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS,
                    MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
                )
            )
            PATTERN_CANDIES_LINE -> buildCandiesLineSprites(x, y)
            PATTERN_CANDIES_GAP -> buildCandiesGapSprites(y)
            PATTERN_SPIKE -> buildSpikeSprite(y)
            PATTERN_BAT -> buildBatSprites(x, y)
            else -> buildJumpingPlatformSprites(
                x,
                y,
                Utils.getRandomInt(
                    MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS,
                    MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
                )
            )
        }

    private fun buildJumpingPlatformSprites(
        x: Float,
        y: Float,
        capacity: Int
    ): List<Sprite> {
        var nextX = x
        var nextY = y
        return (1..capacity).map {
            val jumpingPlatformSprite = buildJumpingPlatformSprite(nextX, nextY)
            nextX = getNextSpriteX(nextX)
            nextY -= Utils.getRandomFloat(
                screenWidth * JUMPING_PLATFORM_MIN_MARGIN_TOP,
                screenWidth * JUMPING_PLATFORM_MAX_MARGIN_TOP
            )
            jumpingPlatformSprite
        }
    }

    private fun buildCandiesLineSprites(
        x: Float,
        y: Float
    ): List<Sprite> {
        val capacityX = Utils.getRandomInt(1,
            MAX_CANDIES_X_CAPACITY
        )
        val capacityY = Utils.getRandomInt(1,
            MAX_CANDIES_Y_CAPACITY
        )
        val outset = (screenWidth * CANDY_OUTSET)
        val spaceX = (screenWidth * CANDY_SPACE_X)
        val spaceY = (screenWidth * CANDY_SPACE_Y)
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
        val sprites = mutableListOf<Sprite>()
        var powerUpGenerated = false
        for (indexY in 0 until capacityY) {
            for (indexX in 0 until capacityX) {
                if (powerUpGenerationAllowed() && !powerUpGenerated) {
                    sprites.add(buildPowerUpSprite(spriteX, spriteY))
                    powerUpGenerated = true
                } else {
                    sprites.add(CandySprite(resourceManager, gameStates, spriteX, spriteY))
                }
                spriteX += spaceX
            }
            spriteX = startX
            spriteY -= spaceY
        }
        return sprites
    }

    private fun buildCandiesGapSprites(
        y: Float
    ): List<Sprite> {
        val sprites = mutableListOf<Sprite>()
        val capacityY = Utils.getRandomInt(1,
            MAX_CANDIES_Y_CAPACITY
        )
        val spaceY = (screenWidth * CANDY_SPACE_Y)
        var spriteY = y
        for (indexY in (1..capacityY)) {
            for (indexX in (1..2)) {
                val sprite = CandySprite(
                    resourceManager,
                    gameStates,
                    screenWidth * .33f * indexX,
                    spriteY
                )
                sprites.add(sprite)
            }
            spriteY -= spaceY
        }

        if (powerUpGenerationAllowed()) {
            val powerUpX = screenWidth*.5f
            val powerUpY = Utils.getRandomFloat(y, spriteY + spaceY)
            sprites.add(buildPowerUpSprite(powerUpX, powerUpY))
        }

        return sprites
    }

    private fun buildBatSprites(x: Float, y: Float): List<Sprite> {
        val outset = (screenWidth * BAT_OUTSET)
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
        val batSprite = BatSprite(
            resourceManager,
            gameStates,
            spriteX,
            y,
            minX,
            maxX
        )
        return listOf(batSprite)
    }

    private fun buildSpikeSprite(y: Float): List<Sprite> {
        return listOf(SpikeSprite(resourceManager, gameStates, y))
    }

    private fun buildJumpingPlatformSprite(x: Float, y: Float): Sprite {
        val outset = screenWidth * JUMPING_PLATFORM_OUTSET
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
        return JumpingPlatformSprite(resourceManager, gameStates, spriteX, y)
    }

    private fun buildPowerUpSprite(x: Float, y: Float): Sprite {
        return PowerUpSprite(resourceManager, gameStates, x, y, getRandomPowerUp())
    }

    private fun getRandomPowerUp(): Int {
        val randomInt = Utils.getRandomInt(1, 100)
        return when {
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER -> GameStates.POWER_UP_COPTER
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER + DRAW_CHANCE_POWER_UP_MAGNET -> GameStates.POWER_UP_MAGNET
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER + DRAW_CHANCE_POWER_UP_MAGNET + DRAW_CHANCE_POWER_UP_ROCKET -> GameStates.POWER_UP_ROCKET
            randomInt <= DRAW_CHANCE_POWER_UP_COPTER + DRAW_CHANCE_POWER_UP_MAGNET + DRAW_CHANCE_POWER_UP_ROCKET + DRAW_CHANCE_POWER_UP_SHIELD -> GameStates.POWER_UP_ARMORED
            else -> GameStates.POWER_UP_ARMORED
        }
    }

    private fun getSpriteMarginTop(previousPattern: Int?): Float =
        when (previousPattern) {
            PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP -> Utils.getRandomFloat(
                screenWidth * CANDIES_MIN_MARGIN_TOP,
                screenWidth * CANDIES_MAX_MARGIN_TOP
            )
            PATTERN_BAT -> Utils.getRandomFloat(
                screenWidth * BAT_MIN_MARGIN_TOP,
                screenWidth * BAT_MAX_MARGIN_TOP
            )
            PATTERN_SPIKE -> Utils.getRandomFloat(
                screenWidth * SPIKE_MIN_MARGIN_TOP,
                screenWidth * SPIKE_MAX_MARGIN_TOP
            )
            else -> Utils.getRandomFloat(
                screenWidth * JUMPING_PLATFORM_MIN_MARGIN_TOP,
                screenWidth * JUMPING_PLATFORM_MAX_MARGIN_TOP
            )
        }

    private fun getPattern(sprite: Sprite): Int =
        when (sprite) {
            is CandySprite, is PowerUpSprite -> PATTERN_CANDIES_LINE
            is BatSprite -> PATTERN_BAT
            is SpikeSprite -> PATTERN_SPIKE
            else -> PATTERN_JUMPING_PLATFORM
        }

}