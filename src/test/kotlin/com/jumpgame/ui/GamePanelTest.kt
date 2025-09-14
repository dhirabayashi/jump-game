package com.jumpgame.ui

import java.awt.Dimension
import kotlin.test.*

class GamePanelTest {
    
    private fun createGamePanel(): GamePanel = GamePanel()
    
    @Test
    fun `game panel initializes with correct preferred size`() {
        val gamePanel = createGamePanel()
        val expectedSize = Dimension(800, 600)
        
        assertEquals(expectedSize, gamePanel.preferredSize)
    }
    
    @Test
    fun `game panel is focusable for keyboard input`() {
        val gamePanel = createGamePanel()
        
        assertEquals(true, gamePanel.isFocusable)
    }
    
    @Test
    fun `game panel has game world instance`() {
        val gamePanel = createGamePanel()
        val gameWorld = gamePanel.getGameWorld()
        
        assertNotNull(gameWorld)
        assertEquals(800, gameWorld.gameWidth)
        assertEquals(600, gameWorld.gameHeight)
    }
    
    @Test
    fun `game panel has input handler instance`() {
        val gamePanel = createGamePanel()
        val inputHandler = gamePanel.getInputHandler()
        
        assertNotNull(inputHandler)
        assertEquals(0, inputHandler.getPressedKeysCount())
    }
    
    @Test
    fun `game starts running by default`() {
        val gamePanel = createGamePanel()
        
        assertEquals(true, gamePanel.isRunning())
    }
    
    @Test
    fun `stopGame changes running state to false`() {
        val gamePanel = createGamePanel()
        
        gamePanel.stopGame()
        
        assertEquals(false, gamePanel.isRunning())
    }
    
    @Test
    fun `startGame changes running state to true`() {
        val gamePanel = createGamePanel()
        gamePanel.stopGame() // First stop it
        
        gamePanel.startGame()
        
        assertEquals(true, gamePanel.isRunning())
    }
    
    @Test
    fun `startGame when already running does not change state`() {
        val gamePanel = createGamePanel()
        assertEquals(true, gamePanel.isRunning())
        
        gamePanel.startGame() // Call again
        
        assertEquals(true, gamePanel.isRunning())
    }
    
    @Test
    fun `stopGame when already stopped does not change state`() {
        val gamePanel = createGamePanel()
        gamePanel.stopGame()
        assertEquals(false, gamePanel.isRunning())
        
        gamePanel.stopGame() // Call again
        
        assertEquals(false, gamePanel.isRunning())
    }
    
    @Test
    fun `resetGame resets game world and input handler`() {
        val gamePanel = createGamePanel()
        val gameWorld = gamePanel.getGameWorld()
        val inputHandler = gamePanel.getInputHandler()
        
        // Modify game state
        val player = gameWorld.player
        player.position = com.jumpgame.util.Vector2D(500.0, 100.0)
        
        // Simulate some key presses
        inputHandler.keyPressed(java.awt.event.KeyEvent(
            gamePanel,
            java.awt.event.KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            java.awt.event.KeyEvent.VK_LEFT,
            java.awt.event.KeyEvent.CHAR_UNDEFINED
        ))
        
        gamePanel.resetGame()
        
        // Verify reset
        // Player should be spawned at a safe position
        assertTrue(player.position.x >= 0)
        assertTrue(player.position.y >= 0)
        assertEquals(0, inputHandler.getPressedKeysCount())
    }
    
    @Test
    fun `resetGame starts game if stopped`() {
        val gamePanel = createGamePanel()
        gamePanel.stopGame()
        assertEquals(false, gamePanel.isRunning())
        
        gamePanel.resetGame()
        
        assertEquals(true, gamePanel.isRunning())
    }
    
    @Test
    fun `resetGame keeps game running if already running`() {
        val gamePanel = createGamePanel()
        assertEquals(true, gamePanel.isRunning())
        
        gamePanel.resetGame()
        
        assertEquals(true, gamePanel.isRunning())
    }
}