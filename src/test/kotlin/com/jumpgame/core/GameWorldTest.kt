package com.jumpgame.core

import com.jumpgame.util.Vector2D
import kotlin.test.*

class GameWorldTest {
    
    private fun createGameWorld(): GameWorld = GameWorld()
    
    @Test
    fun `game world initializes with correct constants`() {
        val gameWorld = createGameWorld()
        
        assertEquals(800, gameWorld.gameWidth)
        assertEquals(600, gameWorld.gameHeight)
        assertEquals(400, gameWorld.groundLevel)
    }
    
    @Test
    fun `game world initializes player at correct position`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        assertEquals(Vector2D(100, 300), player.position)
    }
    
    @Test
    fun `game world initializes with enemies`() {
        val gameWorld = createGameWorld()
        val enemies = gameWorld.getEnemies()
        
        assertEquals(1, enemies.size)
        assertTrue(enemies[0].isAlive)
    }
    
    @Test
    fun `game world initializes without game over`() {
        val gameWorld = createGameWorld()
        
        assertFalse(gameWorld.isGameOver)
    }
    
    @Test
    fun `game world initializes with 5 lives`() {
        val gameWorld = createGameWorld()
        
        assertEquals(5, gameWorld.remainingLives)
    }
    
    @Test
    fun `handleInput processes left movement`() {
        val gameWorld = createGameWorld()
        val input = GameInput(isLeftPressed = true)
        
        gameWorld.handleInput(input)
        
        val player = gameWorld.getPlayer()
        assertEquals(-200.0, player.velocity.x)
    }
    
    @Test
    fun `handleInput processes right movement`() {
        val gameWorld = createGameWorld()
        val input = GameInput(isRightPressed = true)
        
        gameWorld.handleInput(input)
        
        val player = gameWorld.getPlayer()
        assertEquals(200.0, player.velocity.x)
    }
    
    @Test
    fun `handleInput processes jump command`() {
        val gameWorld = createGameWorld()
        
        // First move player to ground
        val player = gameWorld.getPlayer()
        player.position = Vector2D(100.0, 352.0)
        gameWorld.update()
        
        val input = GameInput(isJumpPressed = true)
        gameWorld.handleInput(input)
        
        assertEquals(-400.0, player.velocity.y)
        assertEquals(true, player.isJumping)
    }
    
    @Test
    fun `handleInput stops horizontal movement when no directional input`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // First give player some horizontal movement
        gameWorld.handleInput(GameInput(isRightPressed = true))
        
        val input = GameInput() // No keys pressed
        gameWorld.handleInput(input)
        
        assertEquals(0.0, player.velocity.x)
    }
    
    @Test
    fun `handleInput processes combined left and jump input`() {
        val gameWorld = createGameWorld()
        
        // Move player to ground first
        val player = gameWorld.getPlayer()
        player.position = Vector2D(100.0, 352.0)
        gameWorld.update()
        
        val input = GameInput(isLeftPressed = true, isJumpPressed = true)
        gameWorld.handleInput(input)
        
        assertEquals(-200.0, player.velocity.x)
        assertEquals(-400.0, player.velocity.y)
    }
    
    @Test
    fun `update calls player update`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        val initialPosition = player.position
        
        // Give player some velocity by moving right to see if update is called
        gameWorld.handleInput(GameInput(isRightPressed = true))
        
        Thread.sleep(50) // Small delay to ensure time difference
        gameWorld.update()
        
        // Position should have changed if update was called
        assertNotEquals(initialPosition, player.position)
    }
    
    @Test
    fun `player is kept within left boundary`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Move player beyond left boundary
        player.position = Vector2D(-10.0, 300.0)
        gameWorld.update()
        
        assertEquals(0.0, player.position.x)
    }
    
    @Test
    fun `player is kept within right boundary`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Move player beyond right boundary
        player.position = Vector2D(800.0, 300.0)
        gameWorld.update()
        
        assertEquals(768.0, player.position.x) // 800 - 32 (player width)
    }
    
    @Test
    fun `player position is not changed when within boundaries`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        val validPosition = Vector2D(400.0, 300.0)
        
        player.position = validPosition
        gameWorld.update()
        
        assertEquals(validPosition.x, player.position.x)
    }
    
    @Test
    fun `reset restores initial game state`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Change player state
        player.position = Vector2D(500.0, 100.0)
        
        gameWorld.reset()
        
        assertEquals(Vector2D(100, 300), player.position)
    }
    
    @Test
    fun `GameInput data class has correct default values`() {
        val input = GameInput()
        
        assertEquals(false, input.isLeftPressed)
        assertEquals(false, input.isRightPressed)
        assertEquals(false, input.isJumpPressed)
    }
    
    @Test
    fun `GameInput data class accepts custom values`() {
        val input = GameInput(
            isLeftPressed = true,
            isRightPressed = false,
            isJumpPressed = true
        )
        
        assertEquals(true, input.isLeftPressed)
        assertEquals(false, input.isRightPressed)
        assertEquals(true, input.isJumpPressed)
    }
    
    @Test
    fun `GameInput data class equality works correctly`() {
        val input1 = GameInput(isLeftPressed = true, isJumpPressed = true)
        val input2 = GameInput(isLeftPressed = true, isJumpPressed = true)
        val input3 = GameInput(isRightPressed = true)
        
        assertEquals(input1, input2)
        assertNotEquals(input1, input3)
    }
    
    @Test
    fun `update updates enemies`() {
        val gameWorld = createGameWorld()
        val enemies = gameWorld.getEnemies()
        val initialPosition = enemies[0].position
        
        Thread.sleep(50) // Ensure time passes
        gameWorld.update()
        
        val updatedEnemies = gameWorld.getEnemies()
        assertNotEquals(initialPosition, updatedEnemies[0].position)
    }
    
    @Test
    fun `reset clears and respawns enemies`() {
        val gameWorld = createGameWorld()
        val enemies = gameWorld.getEnemies()
        
        // Defeat the enemy
        enemies[0].defeat()
        gameWorld.update() // This should remove defeated enemies
        
        gameWorld.reset()
        
        val newEnemies = gameWorld.getEnemies()
        assertEquals(1, newEnemies.size)
        assertTrue(newEnemies[0].isAlive)
    }
    
    @Test
    fun `reset clears game over state`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Simulate game over by using all lives
        repeat(5) {
            player.die()
            gameWorld.update()
        }
        assertTrue(gameWorld.isGameOver)
        
        gameWorld.reset()
        
        assertFalse(gameWorld.isGameOver)
        assertTrue(gameWorld.getPlayer().isAlive)
        assertEquals(5, gameWorld.remainingLives)
    }
    
    @Test
    fun `game over prevents input handling`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Use all lives to trigger game over
        repeat(5) {
            player.die()
            gameWorld.update()
        }
        assertTrue(gameWorld.isGameOver)
        
        val initialPosition = player.position
        gameWorld.handleInput(GameInput(isRightPressed = true))
        
        assertEquals(initialPosition, player.position)
        assertEquals(Vector2D.ZERO, player.velocity)
    }
    
    @Test
    fun `game over prevents world updates`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Use all lives to trigger game over
        repeat(5) {
            player.die()
            gameWorld.update()
        }
        assertTrue(gameWorld.isGameOver)
        
        val enemies = gameWorld.getEnemies()
        val enemyPosition = enemies[0].position
        
        Thread.sleep(50)
        gameWorld.update() // Update should do nothing when game over
        
        val updatedEnemies = gameWorld.getEnemies()
        assertEquals(enemyPosition, updatedEnemies[0].position)
    }
    
    @Test
    fun `player death from enemy collision decreases lives`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        val enemies = gameWorld.getEnemies()
        
        // Position player next to enemy (not stomping)
        player.position = Vector2D(enemies[0].position.x + 10, enemies[0].position.y)
        player.velocity = Vector2D(0.0, 0.0) // Not falling
        
        gameWorld.update()
        
        // Player should be alive (respawned) but with one less life
        assertTrue(player.isAlive)
        assertEquals(4, gameWorld.remainingLives)
        assertFalse(gameWorld.isGameOver)
    }
    
    @Test
    fun `defeated enemies are removed from world`() {
        val gameWorld = createGameWorld()
        val enemies = gameWorld.getEnemies()
        
        enemies[0].defeat()
        gameWorld.update()
        
        val updatedEnemies = gameWorld.getEnemies()
        assertEquals(0, updatedEnemies.size)
    }
    
    @Test
    fun `player death decreases lives and respawns when lives remain`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Kill player
        player.die()
        gameWorld.update()
        
        // Should have 4 lives left and player should be alive again (respawned)
        assertEquals(4, gameWorld.remainingLives)
        assertTrue(player.isAlive)
        assertFalse(gameWorld.isGameOver)
    }
    
    @Test
    fun `game over occurs when last life is lost`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Die 5 times to use up all lives
        repeat(5) {
            player.die()
            gameWorld.update()
        }
        
        // Should be game over now
        assertEquals(0, gameWorld.remainingLives)
        assertFalse(player.isAlive)
        assertTrue(gameWorld.isGameOver)
    }
    
    @Test
    fun `reset restores 5 lives`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Use up some lives
        repeat(3) {
            player.die()
            gameWorld.update()
        }
        assertEquals(2, gameWorld.remainingLives)
        
        gameWorld.reset()
        
        assertEquals(5, gameWorld.remainingLives)
        assertTrue(player.isAlive)
        assertFalse(gameWorld.isGameOver)
    }
    
    @Test
    fun `enemies reset position when player respawns`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        val initialEnemyPosition = gameWorld.getEnemies()[0].position
        
        // Move enemy by updating world multiple times
        repeat(10) {
            Thread.sleep(10)
            gameWorld.update()
        }
        val movedEnemyPosition = gameWorld.getEnemies()[0].position
        assertNotEquals(initialEnemyPosition, movedEnemyPosition)
        
        // Kill player to trigger respawn
        player.die()
        gameWorld.update()
        
        // Enemy should be back at initial position
        val respawnedEnemyPosition = gameWorld.getEnemies()[0].position
        assertEquals(initialEnemyPosition, respawnedEnemyPosition)
    }
    
    @Test
    fun `multiple deaths in quick succession handled correctly`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Kill player multiple times quickly
        player.die()
        gameWorld.update()
        assertEquals(4, gameWorld.remainingLives)
        assertTrue(player.isAlive)
        
        player.die()
        gameWorld.update()
        assertEquals(3, gameWorld.remainingLives)
        assertTrue(player.isAlive)
        
        player.die()
        gameWorld.update()
        assertEquals(2, gameWorld.remainingLives)
        assertTrue(player.isAlive)
    }
    
    @Test
    fun `game world has platforms defined`() {
        val gameWorld = createGameWorld()

        // New layout includes 2 regular platforms + 5 ascending stairs + 4 descending stairs = 11 platforms
        assertEquals(11, gameWorld.platforms.size)

        // Check first regular platform
        assertEquals(0, gameWorld.platforms[0].startX)
        assertEquals(200, gameWorld.platforms[0].endX)

        // Check that we have the rightmost platform at the expected position
        val rightmostPlatform = gameWorld.platforms.maxByOrNull { it.endX }!!
        assertEquals(800, rightmostPlatform.endX)
    }
    
    @Test
    fun `isOnSolidGround returns true for platform positions`() {
        val gameWorld = createGameWorld()

        assertTrue(gameWorld.isOnSolidGround(100.0)) // On first platform
        assertTrue(gameWorld.isOnSolidGround(280.0)) // On staircase
        assertTrue(gameWorld.isOnSolidGround(700.0)) // On last platform
    }

    @Test
    fun `isOnSolidGround returns false for pit positions`() {
        val gameWorld = createGameWorld()

        assertFalse(gameWorld.isOnSolidGround(225.0)) // Between first platform and stairs
        assertFalse(gameWorld.isOnSolidGround(249.0)) // Just before stairs start (stairs start at 250)
    }
    
    @Test
    fun `player dies when falling below screen`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Position player below screen
        player.position = Vector2D(400.0, gameWorld.gameHeight + 10.0)
        
        gameWorld.update()
        
        // Player should have died and respawned (lost a life)
        assertEquals(4, gameWorld.remainingLives)
        assertTrue(player.isAlive) // Should be respawned
        assertEquals(Vector2D(100, 300), player.position) // Back at spawn
    }
    
    @Test
    fun `player can walk on platforms`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Position player on a platform
        player.position = Vector2D(150.0, gameWorld.groundLevel - 48.0)
        player.velocity = Vector2D(0.0, 0.0)
        
        gameWorld.update()
        
        // Player should be on ground
        assertTrue(player.isOnGround)
        assertEquals(gameWorld.groundLevel - 48.0, player.position.y, 0.1)
    }
    
    @Test
    fun `player falls through pits`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Position player over a pit at ground level
        player.position = Vector2D(350.0, gameWorld.groundLevel - 48.0)
        player.velocity = Vector2D(0.0, 0.0)
        
        gameWorld.update()
        
        // Player should not be on ground and should start falling
        assertFalse(player.isOnGround)
        assertTrue(player.velocity.y > 0) // Falling due to gravity
    }
    
    @Test
    fun `player can jump between platforms`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Position player on first platform
        player.position = Vector2D(250.0, gameWorld.groundLevel - 48.0)
        player.velocity = Vector2D(0.0, 0.0)
        gameWorld.update()
        assertTrue(player.isOnGround)
        
        // Jump and move right
        player.jump()
        gameWorld.handleInput(GameInput(isRightPressed = true))
        
        // Player should be in air moving right
        assertFalse(player.isOnGround)
        assertTrue(player.velocity.x > 0)
        assertTrue(player.velocity.y < 0) // Moving up from jump
    }
}