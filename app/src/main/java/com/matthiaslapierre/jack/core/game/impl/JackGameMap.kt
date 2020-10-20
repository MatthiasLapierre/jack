package com.matthiaslapierre.jack.core.game.impl

import com.matthiaslapierre.jack.Constants
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.core.game.GameMap
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.game.impl.sprites.collectibles.CandySprite
import com.matthiaslapierre.jack.core.game.impl.sprites.collectibles.PowerUpSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.obstacles.BatSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.obstacles.SpikeSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.jack.utils.Utils
import kotlin.math.abs
import kotlin.math.floor

internal class JackGameMap(
    override val resourceManager: ResourceManager,
    override val gameStates: GameStates
): GameMap {

    companion object {
        /*
        Patterns: Sprite type and layout of the elements.
         */
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

    private var screenWidth: Float = Constants.UNDEFINED
    private var screenHeight: Float = Constants.UNDEFINED

    override fun generate(): List<Sprite> {
        val generatedSprites = mutableListOf<Sprite>()

        if (screenHeight == Constants.UNDEFINED) {
            return generatedSprites
        }

        // Get the next sprite location.
        var nextSpriteY = screenHeight - (screenWidth * Constants.FIRST_SPRITE_Y)
        var nextSpriteX = getNextSpriteX(null)
        var previousPattern: Int? = null
        if (lastGeneratedSprite != null) {
            previousPattern = getPattern(lastGeneratedSprite!!)
            nextSpriteX = getNextSpriteX(lastGeneratedSprite!!.x)
            nextSpriteY = lastGeneratedSprite!!.y
        }
        var nextPattern = getNextPattern(previousPattern)
        nextSpriteY -= getSpriteMarginTop(previousPattern, nextPattern)

        // Generate sprites one by one.
        while (nextSpriteY > -(screenHeight * Constants.GENERATOR_HIGHEST_Y)) {
            // Build the next sprite.
            val sprites = buildSprites(nextSpriteX, nextSpriteY, nextPattern)
            generatedSprites.addAll(sprites)
            // Update counters.
            when (nextPattern) {
                PATTERN_BAT, PATTERN_SPIKE -> countObstaclesGenerated++
                PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP ->
                    countPowerUpsGenerated += sprites.filterIsInstance<PowerUpSprite>().size
            }
            // Get the next sprite pattern and its location depending on the previous sprite.
            val previousGeneratedSprite = if (lastGeneratedSprite != null) {
                lastGeneratedSprite
            } else {
                sprites.first()
            }
            lastGeneratedSprite = sprites.last()
            elevation += abs(lastGeneratedSprite!!.y - previousGeneratedSprite!!.y)
            nextSpriteY = lastGeneratedSprite!!.y
            nextSpriteX = getNextSpriteX(lastGeneratedSprite!!.x)
            previousPattern = nextPattern
            nextPattern = getNextPattern(previousPattern)
            nextSpriteY -= getSpriteMarginTop(previousPattern, nextPattern)
        }
        return generatedSprites
    }

    override fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
    }

    /**
     * Gets the next sprite pattern.
     */
    private fun getNextPattern(previousPattern: Int?): Int {
        return if (previousPattern == null) {
            // The first pattern is a group of jumping platform...
            PATTERN_INITIAL_JUMPING_PLATFORM
        } else if(previousPattern == PATTERN_INITIAL_JUMPING_PLATFORM) {
            // Next, its a group of candies.
            val patterns = arrayOf(
                PATTERN_CANDIES_GAP,
                PATTERN_CANDIES_LINE
            )
            patterns[Utils.getRandomInt(0, patterns.size)]
        } else if(obstacleGenerationAllowed()) {
            // If the obstacle generation is allowed, we add a bat or Spike.
            if (elevation > screenWidth * Constants.SPIKE_FIRST_INTERVAL) {
                if(Utils.getRandomInt(0, 100) > Constants.DRAW_CHANCE_SPIKE) {
                    PATTERN_SPIKE
                } else {
                    PATTERN_BAT
                }
            } else {
                PATTERN_BAT
            }
        } else if (powerUpGenerationAllowed()) {
            // If the special candy generation is allowed, we add a bonus candy.
            val patterns = arrayOf(
                PATTERN_CANDIES_GAP,
                PATTERN_CANDIES_LINE
            )
            patterns[Utils.getRandomInt(0, patterns.size)]
        } else {
            // Else, returns randomly a group of candies or a group of jumping platforms.
            val patterns = arrayOf(
                PATTERN_CANDIES_GAP,
                PATTERN_CANDIES_LINE,
                PATTERN_JUMPING_PLATFORM
            )
            patterns[Utils.getRandomInt(0, patterns.size)]
        }
    }

    /**
     * If the obstacle generation is allowed.
     */
    private fun obstacleGenerationAllowed(): Boolean {
        return floor(elevation / Utils.getRandomFloat(
            screenWidth * Constants.OBSTACLE_INTERVAL_MIN,
            screenWidth * Constants.OBSTACLE_INTERVAL_MAX
        )) > countObstaclesGenerated
    }

    /**
     * If the bonus candy generation is allowed.
     */
    private fun powerUpGenerationAllowed(): Boolean {
        return floor(elevation / Utils.getRandomFloat(
            screenWidth * Constants.POWER_UP_INTERVAL_MIN,
            screenWidth * Constants.POWER_UP_INTERVAL_MAX
        )) > countPowerUpsGenerated
    }

    /**
     * Gets the x-coordinate of the next sprite. Be careful not to place it too far away.
     */
    private fun getNextSpriteX(x: Float?): Float {
        return if (x != null) {
            val swing = screenWidth * Constants.SPRITE_SWING_X
            val minX = 0f.coerceAtLeast(x - swing)
            val maxX = screenWidth.coerceAtMost(x + swing)
            Utils.getRandomFloat(minX, maxX)
        } else {
            Utils.getRandomFloat(0f, screenWidth)
        }
    }

    /**
     * Builds the pattern.
     */
    private fun buildSprites(x: Float, y: Float, pattern: Int): List<Sprite> =
        when(pattern) {
            PATTERN_INITIAL_JUMPING_PLATFORM -> buildJumpingPlatformSprites(
                x,
                y,
                Constants.MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
            )
            PATTERN_JUMPING_PLATFORM -> buildJumpingPlatformSprites(
                x,
                y,
                Utils.getRandomInt(
                    Constants.MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS,
                    Constants.MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
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
                    Constants.MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS,
                    Constants.MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
                )
            )
        }

    /**
     * Builds a group of jumping platforms.
     */
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
                screenWidth * Constants.JUMPING_PLATFORM_MIN_MARGIN_TOP,
                screenWidth * Constants.JUMPING_PLATFORM_MAX_MARGIN_TOP
            )
            jumpingPlatformSprite
        }
    }

    /**
     * Builds a group of candies displayed in matrix.
     */
    private fun buildCandiesLineSprites(
        x: Float,
        y: Float
    ): List<Sprite> {
        val capacityX = Utils.getRandomInt(1,
            Constants.MAX_CANDIES_X_CAPACITY
        )
        val capacityY = Utils.getRandomInt(1,
            Constants.MAX_CANDIES_Y_CAPACITY
        )
        val outset = (screenWidth * Constants.CANDY_OUTSET)
        val spaceX = (screenWidth * Constants.CANDY_SPACE_X)
        val spaceY = (screenWidth * Constants.CANDY_SPACE_Y)
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
                    // Bonus candy generation is allowed. Add a new one.
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

    /**
     * Builds a group of candies displayed on two lines.
     */
    private fun buildCandiesGapSprites(
        y: Float
    ): List<Sprite> {
        val sprites = mutableListOf<Sprite>()
        val capacityY = Utils.getRandomInt(1,
            Constants.MAX_CANDIES_Y_CAPACITY
        )
        val spaceY = (screenWidth * Constants.CANDY_SPACE_Y)
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
            // Bonus candy generation is allowed. Add a new one.
            val powerUpX = screenWidth*.5f
            val powerUpY = Utils.getRandomFloat(y, spriteY + spaceY)
            sprites.add(
                0, // must not be the last in the list
                buildPowerUpSprite(powerUpX, powerUpY)
            )
        }

        return sprites
    }

    /**
     * Builds a new bat.
     */
    private fun buildBatSprites(x: Float, y: Float): List<Sprite> {
        val outset = (screenWidth * Constants.BAT_OUTSET)
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

    /**
     * Builds Spike.
     */
    private fun buildSpikeSprite(y: Float): List<Sprite> {
        return listOf(SpikeSprite(resourceManager, gameStates, y))
    }

    /**
     * Builds a jumping platform.
     */
    private fun buildJumpingPlatformSprite(x: Float, y: Float): Sprite {
        val outset = screenWidth * Constants.JUMPING_PLATFORM_OUTSET
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

    /**
     * Builds a new bonus candy.
     */
    private fun buildPowerUpSprite(x: Float, y: Float): Sprite {
        return PowerUpSprite(resourceManager, gameStates, x, y, getRandomPowerUp())
    }

    /**
     * Returns a power-up randomly.
     */
    private fun getRandomPowerUp(): Int {
        val randomInt = Utils.getRandomInt(1, 100)
        return when {
            randomInt <= Constants.DRAW_CHANCE_POWER_UP_COPTER -> GameStates.POWER_UP_COPTER
            randomInt <= Constants.DRAW_CHANCE_POWER_UP_COPTER + Constants.DRAW_CHANCE_POWER_UP_MAGNET -> GameStates.POWER_UP_MAGNET
            randomInt <= Constants.DRAW_CHANCE_POWER_UP_COPTER + Constants.DRAW_CHANCE_POWER_UP_MAGNET + Constants.DRAW_CHANCE_POWER_UP_ROCKET -> GameStates.POWER_UP_ROCKET
            randomInt <= Constants.DRAW_CHANCE_POWER_UP_COPTER + Constants.DRAW_CHANCE_POWER_UP_MAGNET + Constants.DRAW_CHANCE_POWER_UP_ROCKET + Constants.DRAW_CHANCE_POWER_UP_SHIELD -> GameStates.POWER_UP_ARMORED
            else -> GameStates.POWER_UP_ARMORED
        }
    }

    /**
     * Gets the margin between two sprites.
     */
    private fun getSpriteMarginTop(previousPattern: Int?, nextPattern: Int?): Float =
        if (previousPattern == null) {
            0f
        } else if (nextPattern == PATTERN_BAT || nextPattern == PATTERN_SPIKE) {
            when (previousPattern) {
                PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP -> screenWidth * Constants.CANDIES_MARGIN_TOP_BEFORE_OBSTACLE
                else -> screenWidth * Constants.JUMPING_PLATFORM_MARGIN_TOP_BEFORE_OBSTACLE
            }
        } else {
            when (previousPattern) {
                PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP -> Utils.getRandomFloat(
                    screenWidth * Constants.CANDIES_MIN_MARGIN_TOP,
                    screenWidth * Constants.CANDIES_MAX_MARGIN_TOP
                )
                PATTERN_BAT -> Utils.getRandomFloat(
                    screenWidth * Constants.BAT_MIN_MARGIN_TOP,
                    screenWidth * Constants.BAT_MAX_MARGIN_TOP
                )
                PATTERN_SPIKE -> Utils.getRandomFloat(
                    screenWidth * Constants.SPIKE_MIN_MARGIN_TOP,
                    screenWidth * Constants.SPIKE_MAX_MARGIN_TOP
                )
                else -> Utils.getRandomFloat(
                    screenWidth * Constants.JUMPING_PLATFORM_MIN_MARGIN_TOP,
                    screenWidth * Constants.JUMPING_PLATFORM_MAX_MARGIN_TOP
                )
            }
        }

    /**
     * Gets the pattern matching with the sprite.
     */
    private fun getPattern(sprite: Sprite): Int =
        when (sprite) {
            is CandySprite, is PowerUpSprite -> PATTERN_CANDIES_LINE
            is BatSprite -> PATTERN_BAT
            is SpikeSprite -> PATTERN_SPIKE
            else -> PATTERN_JUMPING_PLATFORM
        }

}