package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.function.Consumer;

class PaintMenu extends JMenuBar {
    PaintMenu() {
        setOpaque(false);
        setBackground(Color.WHITE);
        initMenus();
    }

    private void initMenus() {
        add(makeMenu("File", KeyEvent.VK_F, this::initFileMenuItems));
        add(makeMenu("Edit", KeyEvent.VK_E, this::initEditMenuItems));
        add(makeMenu("Brush", KeyEvent.VK_B, this::initBrushMenuItems));
    }

    private JMenu makeMenu(String name, int mnemonic, Consumer<JMenu> initFunc) {
        JMenu menu = new JMenu(name);
        // Call the initializing function that adds the appropriate menu items.
        initFunc.accept(menu);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private void initFileMenuItems(JMenu menu) {
        menu.add(makeMenuItem("Open", this::addOpenMenuItemClickedListener, KeyEvent.VK_O));
        menu.add(makeMenuItem("Save", this::addSaveMenuItemClickedListener, KeyEvent.VK_S));
        menu.addSeparator();
        menu.add(makeMenuItem("About", this::addAboutMenuItemClickedListener, KeyEvent.VK_A));
    }

    private void initEditMenuItems(JMenu menu) {
        menu.add(makeMenuItem("Invert", this::addInvertMenuItemClickedListener, KeyEvent.VK_I));
        menu.add(makeMenuItem("Clear", this::addClearMenuItemClickedListener, KeyEvent.VK_C));
        menu.add(makeMenuItem("Fill", this::addFillMenuItemClickedListener, KeyEvent.VK_F));
    }

    private void initBrushMenuItems(JMenu menu) {
        menu.add(makeMenuItem("Color", this::addColorMenuItemClickedListener, KeyEvent.VK_C));
    }

    private JMenuItem makeMenuItem(String name, Consumer<JMenuItem> listener, int mnemonic) {
        JMenuItem menuItem = new JMenuItem(name);
        // Call the initializing function that attaches the appropriate action listener.
        listener.accept(menuItem);
        menuItem.setMnemonic(mnemonic);
        return menuItem;
    }

    private void addOpenMenuItemClickedListener(JMenuItem menuItem) {
        menuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(null);

            // If the user chose a file to open.
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    // Temporarily store original color and stroke.
                    Color oldColor = Main.canvas.g2.getColor();
                    Stroke oldStroke = Main.canvas.g2.getStroke();
                    // Try opening the image and updating the graphics.
                    Main.canvas.image = ImageIO.read(fileChooser.getSelectedFile());
                    Main.canvas.g2 = (Graphics2D) Main.canvas.image.getGraphics();
                    // Restore original color and stroke, then update the canvas.
                    Main.canvas.g2.setColor(oldColor);
                    Main.canvas.g2.setStroke(oldStroke);
                    Main.canvas.repaint();
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Couldn't open image!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void addSaveMenuItemClickedListener(JMenuItem menuItem) {
        menuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(null);

            // If the user chose a file to save in.
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    // Try saving the image, then show a success dialog.
                    if (ImageIO.write(Main.canvas.image, "png", fileChooser.getSelectedFile()))
                        JOptionPane.showMessageDialog(null, "Successfully saved image!",
                                "Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Couldn't save image!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void addAboutMenuItemClickedListener(JMenuItem menuItem) {
        menuItem.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "Created by LunarCoffee.", "About", JOptionPane.INFORMATION_MESSAGE));
    }

    private void addInvertMenuItemClickedListener(JMenuItem menuItem) {
        menuItem.addActionListener(e -> {
            Main.canvas.g2.setXORMode(Color.BLACK);
            Main.canvas.fillBackground(Color.WHITE);
            Main.canvas.g2.setPaintMode();
        });
    }

    private void addClearMenuItemClickedListener(JMenuItem menuItem) {
        menuItem.addActionListener(e -> Main.canvas.fillBackground(Color.WHITE));
    }

    private void addFillMenuItemClickedListener(JMenuItem menuItem) {
        menuItem.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Select fill color", Color.WHITE);
            // Fill the the user didn't press cancel.
            if (color != null)
                Main.canvas.fillBackground(color);
        });
    }

    private void addColorMenuItemClickedListener(JMenuItem menuItem) {
        menuItem.addActionListener(e -> Main.canvas.g2.setColor(
                JColorChooser.showDialog(null, "Pick brush color", Color.BLACK)));
    }
}