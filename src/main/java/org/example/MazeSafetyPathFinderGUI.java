package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MazeSafetyPathFinderGUI extends JFrame {
    private Tile[][] maze;
    private List<Node> path;
    private List<String> testedPath;
    private Perceptron perceptron;
    private int startX = -1, startY = -1, endX = -1, endY = -1;
    private MazePanel mazePanel;
    private JTextField widthField, heightField;
    private JTextArea infoArea;
    private JButton generateButton, solveButton, modeButton;
    private static final int CELL_SIZE = 30;
    private static final int CIRCLE_SIZE = CELL_SIZE / 2; // Smaller circle for the path
    private boolean editMazeMode = false; // Start in Set Start/End mode
    private boolean settingStart = true; // Toggle between setting start and end

    public MazeSafetyPathFinderGUI() {
        setTitle("Maze Safety Path Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        controlPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        // Initialize labels with styling
        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        JLabel[] labels = {new JLabel("Width:"), new JLabel("Height:")};
        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setForeground(new Color(50, 50, 50)); // Dark gray text
        }

        // Initialize text fields with styling
        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);
        widthField = new JTextField(null);
        heightField = new JTextField(null);
        JTextField[] textFields = {widthField, heightField};
        for (JTextField tf : textFields) {
            tf.setFont(textFieldFont);
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setColumns(6); // Slightly wider for better readability
            tf.setBackground(new Color(255, 255, 255)); // White background
            tf.setForeground(new Color(0, 0, 0)); // Black text
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 150, 150), 1), // Gray border
                    BorderFactory.createEmptyBorder(5, 10, 5, 10))); // Padding
        }

        controlPanel.add(labels[0]);
        controlPanel.add(widthField);
        controlPanel.add(labels[1]);
        controlPanel.add(heightField);

        // Initialize and style buttons
        generateButton = new JButton("Generate Maze");
        modeButton = new JButton("Switch to Edit Maze Mode");
        solveButton = new JButton("Solve Maze");
        JButton[] buttons = {generateButton, modeButton, solveButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBackground(new Color(70, 130, 180)); // Steel blue
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        solveButton.setEnabled(false);

        controlPanel.add(generateButton);
        controlPanel.add(modeButton);
        controlPanel.add(solveButton);

        add(controlPanel, BorderLayout.NORTH);

        // Maze Panel wrapped in a JScrollPane
        mazePanel = new MazePanel();
        JScrollPane mazeScrollPane = new JScrollPane(mazePanel);
        mazeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mazeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(mazeScrollPane, BorderLayout.CENTER);

        // Info Panel
        infoArea = new JTextArea(15, 30);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoArea.setBackground(new Color(230, 230, 230)); // Light gray background
        infoArea.setForeground(new Color(0, 0, 0)); // Black text
        JScrollPane infoScroll = new JScrollPane(infoArea);
        add(infoScroll, BorderLayout.SOUTH);

        // Event Listeners
        generateButton.addActionListener(e -> generateMaze());
        solveButton.addActionListener(e -> solveMaze());
        modeButton.addActionListener(e -> toggleMode());

        // Window settings
        setMinimumSize(new Dimension(600, 600)); // Increased default height to better fit larger mazes
        pack();
        setLocationRelativeTo(null);
    }

    private void toggleMode() {
        editMazeMode = !editMazeMode;
        modeButton.setText(editMazeMode ? "Switch to Set Start/End Mode" : "Switch to Edit Maze Mode");
        if (editMazeMode) {
            infoArea.append("\nNow in Edit Maze Mode. Click cells to cycle: Grass → Water → Obstacle.\n");
        } else {
            infoArea.append("\nNow in Set Start/End Mode. Click Grass tiles to set Start (green) and End (red).\n");
        }
        mazePanel.repaint();
    }

    private void generateMaze() {
        try {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            if (width <= 0 || height <= 0) {
                JOptionPane.showMessageDialog(this, "Width and height must be positive integers.");
                return;
            }

            maze = MazeGenerator.generateMaze(width, height);
            startX = -1;
            startY = -1;
            endX = -1;
            endY = -1;
            path = null;
            testedPath = new ArrayList<>();
            settingStart = true;
            solveButton.setEnabled(false);

            // Initialize perceptron with dummy training data
            perceptron = new Perceptron(3);
            double[][] trainingData = {
                    {0, 1, 10}, {1, 9, 8}, {0, 1, 4}, {0, 9, 0}, {0, 3, 2}, {1, 7, 9}, {0, 6, 7}, {0, 8, 10},
                    {0, 7, 5}, {1, 4, 7}, {0, 1, 8}, {0, 4, 3}, {0, 7, 0}, {0, 9, 0}, {1, 8, 9}, {0, 8, 3},
                    {1, 0, 6}, {1, 8, 1}, {1, 6, 2}, {0, 8, 0}, {1, 7, 4}, {0, 0, 0}, {1, 7, 7}, {1, 7, 0},
                    {1, 10, 10}, {1, 2, 0}, {1, 0, 1}, {1, 7, 1}, {1, 2, 5}, {1, 2, 6}, {0, 0, 4}, {0, 10, 0},
                    {1, 4, 0}, {1, 9, 2}, {1, 6, 1}, {0, 9, 4}, {1, 8, 9}, {0, 6, 5}, {0, 8, 6}, {0, 7, 3},
                    {0, 1, 6}, {0, 0, 10}, {1, 6, 7}, {1, 6, 10}, {1, 7, 0}, {1, 4, 5}, {1, 2, 7}, {0, 7, 4},
                    {1, 5, 3}, {1, 10, 1}, {0, 2, 5}, {1, 0, 5}, {0, 2, 10}, {1, 4, 0}, {0, 2, 8}, {1, 0, 10},
                    {1, 4, 5}, {0, 9, 2}, {0, 6, 3}, {0, 6, 10}, {0, 10, 3}, {0, 8, 2}, {0, 9, 9}, {0, 9, 2},
                    {0, 2, 2}, {1, 6, 3}, {1, 0, 6}, {0, 3, 3}, {1, 3, 8}, {1, 4, 0}, {1, 6, 7}, {1, 6, 6},
                    {0, 10, 1}, {1, 3, 7}, {0, 6, 0}, {1, 10, 10}, {1, 2, 8}, {1, 5, 8}, {0, 1, 1}, {1, 9, 6},
                    {0, 8, 9}, {1, 4, 2}, {0, 5, 6}, {1, 3, 9}, {0, 10, 8}, {0, 9, 3}, {1, 6, 0}, {0, 8, 1},
                    {1, 6, 0}, {1, 0, 4}, {1, 0, 4}, {1, 8, 10}, {1, 10, 6}, {1, 8, 8}, {1, 3, 8}, {1, 8, 2},
                    {1, 2, 2}, {1, 6, 2}, {1, 5, 3}, {0, 7, 7}
            };
            int[] labels = {
                    1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0,
                    1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1
            };
            perceptron.train(trainingData, labels, 100);

            updateInfo();
            mazePanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for width and height.");
            widthField.setText("");
            heightField.setText("");
        }
    }

    private void solveMaze() {
        if (maze == null || startX == -1 || startY == -1 || endX == -1 || endY == -1) {
            JOptionPane.showMessageDialog(this, "Set Start (green) and End (red) points first.");
            return;
        }

        testedPath.clear();
        path = AStar.findPath(maze, startX, startY, endX, endY, perceptron, testedPath);
        updateInfo();
        mazePanel.repaint();
    }

    private void updateInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Maze Status:\n");
        if (maze == null) {
            info.append("No maze generated.\n");
        } else {
            info.append("Current Mode: ").append(editMazeMode ? "Edit Maze" : "Set Start/End").append("\n");
            if (editMazeMode) {
                info.append("Click on cells to cycle types: Grass → Water → Obstacle.\n");
            } else {
                info.append("Click Grass tiles to set Start (green) and End (red). Solve after setting both.\n");
            }
            info.append("Start: (").append(startX).append(", ").append(startY).append(")\n");
            info.append("End: (").append(endX).append(", ").append(endY).append(")\n");
            if (path != null && !path.isEmpty()) {
                info.append("Path found! Steps: ").append(path.size()).append("\n");
            } else if (!testedPath.isEmpty()) {
                info.append("No safe path found.\n");
            }
            info.append("\nTested Path:\n");
            for (String step : testedPath) {
                info.append(step).append(" ");
            }
            info.append("\n\nColor Meanings:\n");
            info.append(" Green → Start \n");
            info.append(" Red → End \n");
            info.append(" Yellow Circle → Path \n");
            info.append(" Black → Obstacle \n");
            info.append(" Dark Green → Grass \n");
            info.append(" Blue → Water \n");
        }
        infoArea.setText(info.toString());
    }

    private class MazePanel extends JPanel {
        public MazePanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (maze == null) return;

                    int x = e.getX() / CELL_SIZE;
                    int y = e.getY() / CELL_SIZE;
                    if (x >= maze.length || y >= maze[0].length) return;

                    if (editMazeMode) {
                        // Cycle the tile type: Grass → Water → Obstacle
                        Tile tile = maze[x][y];
                        if (tile.type == Tile.Type.GRASS) {
                            tile.type = Tile.Type.WATER;
                        } else if (tile.type == Tile.Type.WATER) {
                            tile.type = Tile.Type.OBSTACLE;
                        } else {
                            tile.type = Tile.Type.GRASS;
                        }
                        // Recalculate manhattan distances after changing tile type
                        recalculateManhattanDistances();
                    } else {
                        // Set start or end point
                        if (maze[x][y].type != Tile.Type.GRASS) return;
                        if (settingStart) {
                            startX = x;
                            startY = y;
                            settingStart = false;
                            infoArea.append("\nStart set at (" + startX + ", " + startY + "). Click another Grass tile to set End.\n");
                        } else {
                            // Avoid setting End on the same tile as Start
                            if (x == startX && y == startY) {
                                infoArea.append("\nEnd cannot be the same as Start. Choose a different Grass tile.\n");
                                return;
                            }
                            endX = x;
                            endY = y;
                            settingStart = true;
                            infoArea.append("End set at (" + endX + ", " + endY + "). Click Solve Maze to find the path.\n");
                            solveButton.setEnabled(true);
                        }
                    }
                    repaint();
                }
            });
        }

        private void recalculateManhattanDistances() {
            int width = maze.length;
            int height = maze[0].length;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int minDist = Integer.MAX_VALUE;
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            if (maze[i][j].type == Tile.Type.OBSTACLE) {
                                int dist = Math.abs(x - i) + Math.abs(y - j);
                                if (dist < minDist) minDist = dist;
                            }
                        }
                    }
                    maze[x][y].manhattanToObstacle = minDist;
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (maze == null) return;

            int width = maze.length;
            int height = maze[0].length;

            // First pass: Draw the base tiles (Grass, Water, Obstacles, Start, End)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Set color based on tile type
                    if (x == startX && y == startY) {
                        g.setColor(Color.GREEN); // Start position
                    } else if (x == endX && y == endY) {
                        g.setColor(Color.RED); // End position
                    } else if (maze[x][y].type == Tile.Type.OBSTACLE) {
                        g.setColor(Color.BLACK); // Obstacle
                    } else if (maze[x][y].type == Tile.Type.GRASS) {
                        g.setColor(new Color(0, 128, 0)); // Dark green for Grass
                    } else {
                        g.setColor(Color.BLUE); // Water
                    }

                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.GRAY);
                    g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }

            // Second pass: Draw the path as small yellow circles
            if (path != null) {
                for (Node node : path) {
                    int x = node.x;
                    int y = node.y;
                    // Skip the start and end points to avoid overlapping circles
                    if ((x == startX && y == startY) || (x == endX && y == endY)) {
                        continue;
                    }
                    g.setColor(Color.YELLOW); // Path as yellow circles
                    // Center the circle within the cell
                    int circleX = x * CELL_SIZE + (CELL_SIZE - CIRCLE_SIZE) / 2;
                    int circleY = y * CELL_SIZE + (CELL_SIZE - CIRCLE_SIZE) / 2;
                    g.fillOval(circleX, circleY, CIRCLE_SIZE, CIRCLE_SIZE);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            if (maze == null) {
                return new Dimension(300, 300);
            }
            return new Dimension(maze.length * CELL_SIZE, maze[0].length * CELL_SIZE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MazeSafetyPathFinderGUI().setVisible(true);
        });
    }
}