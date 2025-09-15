package com.jumpgame.core

import com.jumpgame.util.Vector2D

/**
 * Manages the game world state, including the player and game logic.
 *
 * This class serves as the central coordinator for game entities, handling
 * player updates, input processing, timing, and boundary enforcement.
 * It maintains the game's coordinate system and world properties.
 */
class GameWorld {
    
    /** The player character in the game world */
    val player: Player = Player(Vector2D(0.0, 0.0)) // Temporary position, will be set in init
    
    /** List of enemies in the game world */
    private val enemies = mutableListOf<Enemy>()
    
    /** Flag indicating whether the game is over */
    var isGameOver: Boolean = false
        private set
    
    /** Number of remaining lives for the player */
    var remainingLives: Int = 5
        private set
    
    /** Timestamp of the last update for delta time calculation */
    private var lastUpdateTime: Long = System.nanoTime()
    
    /** Width of the game world in pixels */
    val gameWidth: Int = 800
    
    /** Height of the game world in pixels */
    val gameHeight: Int = 600
    
    /** Y-coordinate of the ground level */
    val groundLevel: Int = 400
    
    /** List of platforms (solid ground areas) defined by start and end X coordinates */
    val platforms: List<Platform> = generateGamePlatforms()

    /**
     * Generates all platforms for the game world including regular platforms, staircases, and floating platforms.
     *
     * @return List of all Platform objects in the game world
     */
    private fun generateGamePlatforms(): List<Platform> {
        val platformList = mutableListOf<Platform>()

        // Regular platforms (ground level - extend to bottom)
        platformList.addAll(listOf(
            Platform(0, 200, groundLevel, gameHeight - groundLevel),      // Left platform
            Platform(600, 800, groundLevel, gameHeight - groundLevel)     // Right platform
        ))

        // Add ascending staircase in the middle
        val staircase = Staircase(
            startX = 250,
            stepCount = 5,
            stepWidth = 60,
            stepHeight = 25,
            ascending = true
        )
        platformList.addAll(staircase.generatePlatforms(groundLevel))

        // Add descending staircase
        val descendingStaircase = Staircase(
            startX = 500,
            stepCount = 4,
            stepWidth = 50,
            stepHeight = 20,
            ascending = false
        )
        platformList.addAll(descendingStaircase.generatePlatforms(groundLevel - 100))

        // Add floating platforms that are reachable by jumping
        platformList.addAll(generateFloatingPlatforms())

        return platformList
    }

    /**
     * Generates floating platforms at various heights that are reachable by jumping.
     * These platforms are positioned in mid-air to provide challenge and vertical gameplay.
     *
     * @return List of floating Platform objects
     */
    private fun generateFloatingPlatforms(): List<Platform> {
        val floatingPlatforms = mutableListOf<Platform>()

        // High floating platform above the left side
        floatingPlatforms.add(Platform(80, 180, groundLevel - 120))

        // Medium height floating platform in the center-left area
        floatingPlatforms.add(Platform(180, 280, groundLevel - 80))

        // High floating platform in the center
        floatingPlatforms.add(Platform(320, 420, groundLevel - 140))

        // Small stepping stone platform
        floatingPlatforms.add(Platform(440, 500, groundLevel - 90))

        // High floating platform on the right side
        floatingPlatforms.add(Platform(520, 620, groundLevel - 110))

        // Very high challenge platform (requires precise jumping)
        floatingPlatforms.add(Platform(350, 400, groundLevel - 180))

        // Lower floating platforms for easier navigation
        floatingPlatforms.add(Platform(120, 200, groundLevel - 60))
        floatingPlatforms.add(Platform(650, 720, groundLevel - 70))

        return floatingPlatforms
    }

    /**
     * Finds a safe spawn position for the player on the leftmost platform.
     *
     * @return A Vector2D representing a safe spawn position
     */
    private fun findSafeSpawnPosition(): Vector2D {
        // Find the leftmost platform
        val leftmostPlatform = platforms.minByOrNull { it.startX }

        if (leftmostPlatform != null) {
            // Spawn at the left edge of the leftmost platform, on top of it
            val spawnX = leftmostPlatform.startX + 50.0 // Small offset from edge
            val spawnY = leftmostPlatform.y - 48.0 // Player height above platform
            return Vector2D(spawnX, spawnY)
        }

        // Fallback: spawn at a default safe position
        return Vector2D(100.0, groundLevel - 48.0)
    }

    init {
        // Initialize player to safe spawn position
        val spawnPos = findSafeSpawnPosition()
        player.reset(spawnPos)

        // Explicitly set player on ground at spawn
        player.isOnGround = true
        player.isJumping = false

        // Initialize lastUpdateTime to prevent huge deltaTime on first update
        lastUpdateTime = System.nanoTime()

        spawnInitialEnemies()
    }


    /**
     * Updates the game world state including player physics, enemies, and collision detection.
     * Calculates delta time for consistent physics simulation and updates all entities.
     * Should be called once per frame during the game loop.
     */
    fun update() {
        if (isGameOver) return

        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0
        lastUpdateTime = currentTime

        player.update(deltaTime)
        updateEnemies(deltaTime)

        keepPlayerInBounds()
        checkPlatformCollisions()
        checkCollisions()
        checkPitFalls()

        // Handle player death
        if (!player.isAlive) {
            handlePlayerDeath()
        }
    }

    /**
     * Processes game input and applies it to the player.
     * Handles movement commands and jump input based on the provided GameInput state.
     * Does nothing if the game is over.
     *
     * @param input The current input state containing pressed key information
     */
    fun handleInput(input: GameInput) {
        if (isGameOver) return
        
        val deltaTime = calculateDeltaTime()
        
        when {
            input.isLeftPressed -> player.moveLeft(deltaTime)
            input.isRightPressed -> player.moveRight(deltaTime)
            else -> player.stopHorizontalMovement()
        }
        
        if (input.isJumpPressed) {
            player.jump()
        }
    }

    /**
     * Calculates the time elapsed since the last update.
     * Caps the delta time to prevent large jumps that could break physics simulation.
     *
     * @return The delta time in seconds, capped at approximately 60 FPS
     */
    private fun calculateDeltaTime(): Double {
        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0
        return deltaTime.coerceAtMost(0.016) // Cap at ~60 FPS
    }

    /**
     * Ensures the player remains within the game world boundaries.
     * Prevents the player from moving outside the left and right edges of the screen.
     */
    private fun keepPlayerInBounds() {
        val playerBounds = player.getBounds()
        val currentPos = player.position
        
        when {
            playerBounds.x < 0 -> {
                player.position = Vector2D(0.0, currentPos.y)
            }
            playerBounds.x + playerBounds.width > gameWidth -> {
                player.position = Vector2D(gameWidth - playerBounds.width.toDouble(), currentPos.y)
            }
        }
    }

    /**
     * Resets the game world to its initial state.
     * Resets the player to the starting position, clears enemies, and reinitializes the update timer.
     */
    fun reset() {
        val resetPos = findSafeSpawnPosition()
        player.reset(resetPos)
        enemies.clear()
        spawnInitialEnemies()
        isGameOver = false
        remainingLives = 5
        lastUpdateTime = System.nanoTime()
    }

    /**
     * Returns the list of enemies for external access (e.g., rendering).
     *
     * @return List of Enemy objects currently in the game world
     */
    fun getEnemies(): List<Enemy> = enemies.toList()
    
    /**
     * Spawns the initial set of enemies in the game world.
     * Places enemies at strategic positions on the right side of the screen.
     */
    private fun spawnInitialEnemies() {
        enemies.clear()
        // Spawn enemy on the right side of the screen
        enemies.add(Enemy(Vector2D(gameWidth - 100.0, groundLevel - 32.0)))
    }
    
    /**
     * Updates all enemies in the game world.
     * Removes defeated enemies from the active list.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    private fun updateEnemies(deltaTime: Double) {
        enemies.forEach { enemy ->
            enemy.update(deltaTime, gameWidth)
        }
        
        // Remove defeated enemies
        enemies.removeAll { !it.isAlive }
    }
    
    /**
     * Checks for collisions between the player and enemies.
     * Handles both stomping (defeating enemies) and death collisions.
     */
    private fun checkCollisions() {
        if (!player.isAlive) return
        
        val playerBounds = player.getBounds()
        val playerVelocity = player.velocity
        
        enemies.forEach { enemy ->
            if (enemy.isAlive && enemy.collidesWith(playerBounds)) {
                if (enemy.isBeingStomped(playerBounds, playerVelocity)) {
                    // Player stomps on enemy - enemy dies, player bounces
                    enemy.defeat()
                    player.velocity = Vector2D(player.velocity.x, -200.0) // Small bounce
                } else {
                    // Player touches enemy from side/bottom - player dies
                    player.die()
                }
            }
        }
    }
    
    /**
     * Handles player death by decreasing lives and respawning or ending game.
     * If lives remain, respawns the player at the starting position.
     * If no lives remain, triggers game over.
     */
    private fun handlePlayerDeath() {
        if (remainingLives > 0) {
            remainingLives--
            
            if (remainingLives > 0) {
                // Still have lives, respawn player
                val deathRespawnPos = findSafeSpawnPosition()
                        player.reset(deathRespawnPos)
                // Clear enemies and respawn them to reset their positions
                enemies.clear()
                spawnInitialEnemies()
            } else {
                // No more lives, game over
                isGameOver = true
            }
        }
    }
    
    /**
     * Checks if the player has fallen into a pit (below the screen).
     * If so, kills the player to trigger the death handling system.
     */
    private fun checkPitFalls() {
        if (player.isAlive && player.position.y > gameHeight) {
            player.die()
        }
    }
    
    /**
     * Checks if a given X coordinate is on a platform (solid ground).
     * 
     * @param x The X coordinate to check
     * @return true if the position is on solid ground, false if it's a pit
     */
    fun isOnSolidGround(x: Double): Boolean {
        return platforms.any { platform ->
            x >= platform.startX && x <= platform.endX
        }
    }
    
    /**
     * Handles collision detection between the player and platforms.
     * Provides solid collision detection for all sides of platforms.
     */
    private fun checkPlatformCollisions() {
        // Don't do platform collision if player is far below screen (they should fall and die)
        if (player.position.y > gameHeight) {
            player.isOnGround = false
            return
        }

        // First check for side collisions (wall hits)
        checkSideCollisions()

        // Then check for vertical collisions (top/bottom)
        checkVerticalCollisions()
    }

    /**
     * Checks for side collisions (left and right) with platforms.
     */
    private fun checkSideCollisions() {
        val playerX = player.position.x
        val playerY = player.position.y
        val playerWidth = player.width.toDouble()
        val playerHeight = player.height.toDouble()
        val velocity = player.velocity

        for (platform in platforms) {
            // Only check side collision if player is at roughly the same height as platform
            val platformTop = platform.y.toDouble()
            val platformBottom = platform.bottom.toDouble()
            val playerBottom = playerY + playerHeight

            // Check if player's vertical range overlaps with platform
            if (playerY < platformBottom && playerBottom > platformTop) {
                val platformLeft = platform.startX.toDouble()
                val platformRight = platform.endX.toDouble()

                // Check left side collision (player moving right into platform)
                if (velocity.x > 0 && playerX + playerWidth > platformLeft && playerX < platformLeft) {
                    player.position = Vector2D(platformLeft - playerWidth, playerY)
                    player.velocity = Vector2D(0.0, velocity.y)
                    return
                }

                // Check right side collision (player moving left into platform)
                if (velocity.x < 0 && playerX < platformRight && playerX + playerWidth > platformRight) {
                    player.position = Vector2D(platformRight, playerY)
                    player.velocity = Vector2D(0.0, velocity.y)
                    return
                }
            }
        }
    }

    /**
     * Checks for vertical collisions (top and bottom) with platforms.
     */
    private fun checkVerticalCollisions() {
        val playerX = player.position.x
        val playerY = player.position.y
        val playerWidth = player.width.toDouble()
        val playerHeight = player.height.toDouble()
        val playerBottomY = playerY + playerHeight

        var onGround = false

        for (platform in platforms) {
            val platformLeft = platform.startX.toDouble()
            val platformRight = platform.endX.toDouble()
            val platformTop = platform.y.toDouble()
            val platformBottom = platform.bottom.toDouble()

            // Check if player horizontally overlaps with platform
            if (playerX < platformRight && playerX + playerWidth > platformLeft) {

                // Top collision (landing on platform)
                if (player.velocity.y >= 0 && playerBottomY >= platformTop && playerY < platformTop) {
                    player.position = Vector2D(playerX, platformTop - playerHeight)
                    player.velocity = Vector2D(player.velocity.x, 0.0)
                    player.isOnGround = true
                    player.isJumping = false
                    onGround = true
                    return
                }

                // Bottom collision (hitting platform from below)
                if (player.velocity.y < 0 && playerY <= platformBottom && playerBottomY > platformBottom) {
                    player.position = Vector2D(playerX, platformBottom)
                    player.velocity = Vector2D(player.velocity.x, 0.0)
                    return
                }
            }
        }

        if (!onGround) {
            player.isOnGround = false
        }
    }

}

/**
 * Represents the current input state for the game.
 *
 * This data class encapsulates all the input information needed to control
 * the player character, including movement and jumping commands.
 *
 * @property isLeftPressed Whether the left movement key is currently pressed
 * @property isRightPressed Whether the right movement key is currently pressed
 * @property isJumpPressed Whether the jump key is currently pressed
 */
data class GameInput(
    val isLeftPressed: Boolean = false,
    val isRightPressed: Boolean = false,
    val isJumpPressed: Boolean = false
)

/**
 * Represents a platform (solid ground area) in the game world.
 *
 * @property startX The left edge X coordinate of the platform
 * @property endX The right edge X coordinate of the platform
 * @property y The Y coordinate of the platform (default is ground level)
 * @property thickness The thickness/height of the platform for collision detection
 */
data class Platform(val startX: Int, val endX: Int, val y: Int = 400, val thickness: Int = 20) {
    /**
     * Gets the bottom Y coordinate of the platform.
     */
    val bottom: Int get() = y + thickness

    /**
     * Checks if this platform intersects with a rectangular area.
     */
    fun intersects(x: Double, y: Double, width: Double, height: Double): Boolean {
        return x < endX && x + width > startX && y < bottom && y + height > this.y
    }

    /**
     * Gets the rectangular bounds of this platform.
     */
    fun getBounds(): Rectangle {
        return Rectangle(startX, y, endX - startX, thickness)
    }
}

/**
 * Represents a rectangular area for collision detection.
 */
data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int) {
    val right: Int get() = x + width
    val bottom: Int get() = y + height
}

/**
 * Represents a staircase terrain feature.
 *
 * @property startX The starting X coordinate of the staircase
 * @property stepCount Number of steps in the staircase
 * @property stepWidth Width of each step
 * @property stepHeight Height difference between steps
 * @property ascending Whether the staircase goes up (true) or down (false)
 */
data class Staircase(
    val startX: Int,
    val stepCount: Int,
    val stepWidth: Int,
    val stepHeight: Int,
    val ascending: Boolean = true
) {
    /**
     * Generates platform objects for each step of the staircase.
     *
     * @param baseY The base Y coordinate to start from
     * @return List of Platform objects representing the staircase steps
     */
    fun generatePlatforms(baseY: Int): List<Platform> {
        val platforms = mutableListOf<Platform>()

        for (i in 0 until stepCount) {
            val stepStartX = startX + (i * stepWidth)
            val stepEndX = stepStartX + stepWidth
            val stepY = if (ascending) {
                baseY - (i * stepHeight)
            } else {
                baseY + (i * stepHeight)
            }

            // Staircase platforms have fixed thickness of 20 pixels
            platforms.add(Platform(stepStartX, stepEndX, stepY, 20))
        }

        return platforms
    }
}