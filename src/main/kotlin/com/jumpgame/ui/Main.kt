package com.jumpgame.ui

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*

/**
 * Main application class that manages the game window and user interface.
 *
 * This class is responsible for creating and configuring the main game window,
 * setting up the menu system, and handling application-level events.
 */
class JumpGameApp {
    
    /** The main application window frame */
    private lateinit var frame: JFrame
    
    /** The game panel that contains the actual game */
    private lateinit var gamePanel: GamePanel
    
    /**
     * Starts the application by creating and showing the GUI on the Event Dispatch Thread.
     * This method should be called to launch the game application.
     */
    fun start() {
        SwingUtilities.invokeLater {
            createAndShowGUI()
        }
    }
    
    /**
     * Creates and configures the main GUI components.
     * Sets up the main frame, game panel, menu bar, and makes the window visible.
     */
    private fun createAndShowGUI() {
        System.setProperty("apple.awt.application.name", "Jump Game")
        
        frame = JFrame("Jump Game")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isResizable = false
        
        gamePanel = GamePanel()
        frame.add(gamePanel)
        
        setupMenuBar()
        
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
        
        gamePanel.requestFocus()
    }
    
    /**
     * Sets up the application menu bar with Game and Help menus.
     * Configures menu items with keyboard shortcuts and action listeners.
     */
    private fun setupMenuBar() {
        val menuBar = JMenuBar()
        
        val gameMenu = JMenu("Game")
        gameMenu.mnemonic = KeyEvent.VK_G
        
        val newGameItem = JMenuItem("New Game", KeyEvent.VK_N)
        newGameItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)
        newGameItem.addActionListener {
            gamePanel.resetGame()
        }
        
        val pauseItem = JMenuItem("Pause/Resume", KeyEvent.VK_P)
        pauseItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK)
        pauseItem.addActionListener {
            if (gamePanel.isRunning()) {
                gamePanel.stopGame()
            } else {
                gamePanel.startGame()
            }
        }
        
        val exitItem = JMenuItem("Exit", KeyEvent.VK_X)
        exitItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK)
        exitItem.addActionListener {
            System.exit(0)
        }
        
        gameMenu.add(newGameItem)
        gameMenu.add(pauseItem)
        gameMenu.addSeparator()
        gameMenu.add(exitItem)
        
        val helpMenu = JMenu("Help")
        helpMenu.mnemonic = KeyEvent.VK_H
        
        val controlsItem = JMenuItem("Controls", KeyEvent.VK_C)
        controlsItem.addActionListener {
            showControlsDialog()
        }
        
        val aboutItem = JMenuItem("About", KeyEvent.VK_A)
        aboutItem.addActionListener {
            showAboutDialog()
        }
        
        helpMenu.add(controlsItem)
        helpMenu.add(aboutItem)
        
        menuBar.add(gameMenu)
        menuBar.add(helpMenu)
        
        frame.jMenuBar = menuBar
    }
    
    /**
     * Shows a dialog with game control instructions.
     * Displays keyboard shortcuts for movement, jumping, and menu operations.
     */
    private fun showControlsDialog() {
        val message = """
            Game Controls:
            
            Movement:
            • Arrow Keys or A/D - Move left/right
            
            Jumping:
            • Space, Up Arrow, or W - Jump
            
            Menu:
            • F2 - New Game
            • Ctrl+P - Pause/Resume
            • Alt+F4 - Exit
        """.trimIndent()
        
        JOptionPane.showMessageDialog(
            frame,
            message,
            "Game Controls",
            JOptionPane.INFORMATION_MESSAGE
        )
    }
    
    /**
     * Shows a dialog with information about the application.
     * Displays the application name, description, and purpose.
     */
    private fun showAboutDialog() {
        val message = """
            Jump Game
            
            A simple 2D platformer game built with Kotlin and Swing.
            Control the blue character and jump around!
            
            Created as a demonstration of game development
            with proper class separation and testing.
        """.trimIndent()
        
        JOptionPane.showMessageDialog(
            frame,
            message,
            "About Jump Game",
            JOptionPane.INFORMATION_MESSAGE
        )
    }
}

/**
 * Entry point of the application.
 * Creates a new JumpGameApp instance and starts the game.
 */
fun main() {
    val app = JumpGameApp()
    app.start()
}