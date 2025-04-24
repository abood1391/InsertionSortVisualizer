package com.example.insertionsortvisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

public class InsertionSortVisualizer extends JFrame {
    private JPanel controlPanel, visualPanel;
    private JTextField inputField;
    private JButton sortButton, resetButton, randomButton;
    private JSlider speedSlider;
    private JLabel statusLabel;

    private ArrayList<Integer> numbers = new ArrayList<>();
    private int[] currentArray;
    private int currentIndex = -1;
    private int currentKey = -1;
    private int j = -1;
    private boolean isSorting = false;
    private Timer timer;
    private final int BAR_WIDTH = 40;
    private final int MARGIN = 10;
    private final int MAX_HEIGHT = 400;
    private final int MAX_NUMBER = 100;

    public InsertionSortVisualizer() {
        setTitle("Insertion Sort Visualizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // Initialize control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(new Color(200, 220, 240));

        JLabel inputLabel = new JLabel("Enter numbers (comma-separated):");
        inputField = new JTextField(20);
        sortButton = new JButton("Sort");
        resetButton = new JButton("Reset");
        randomButton = new JButton("Random Numbers");

        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        JLabel speedLabel = new JLabel("Animation Speed:");
        //speedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel = new JLabel("Enter numbers and press Sort");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Add components to control panel
        controlPanel.add(inputLabel);
        controlPanel.add(inputField);
        controlPanel.add(sortButton);
        controlPanel.add(resetButton);
        controlPanel.add(randomButton);
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);

        // Initialize visualization panel
        visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Create blue gradient background
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(100, 150, 255),
                        0, getHeight(), new Color(0, 20, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw array bars
                int x = MARGIN;
                int maxHeight = getHeight() - 100;

                if (currentArray != null && currentArray.length > 0) {
                    double scale = (double) maxHeight / MAX_NUMBER;

                    for (int i = 0; i < currentArray.length; i++) {
                        int height = (int)(currentArray[i] * scale);

                        // Choose color based on current state
                        if (i == currentIndex) {
                            g2d.setColor(Color.RED);           // Current index being processed
                        } else if (i == j + 1 && currentIndex > j + 1) {
                            g2d.setColor(Color.GREEN);         // Key value
                        } else if (i <= currentIndex && i > j) {
                            g2d.setColor(Color.YELLOW);        // Values being compared
                        } else if (i <= j) {
                            g2d.setColor(Color.WHITE);         // Sorted portion
                        } else {
                            g2d.setColor(Color.LIGHT_GRAY);    // Unsorted portion
                        }

                        // Draw the bar
                        g2d.fillRect(x, getHeight() - height - MARGIN, BAR_WIDTH, height);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(x, getHeight() - height - MARGIN, BAR_WIDTH, height);

                        // Draw the value
                        String value = String.valueOf(currentArray[i]);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(value);
                        g2d.setColor(Color.WHITE);
                        g2d.drawString(value, x + (BAR_WIDTH - textWidth) / 2,
                                getHeight() - MARGIN - height - 5);

                        x += BAR_WIDTH + 5;
                    }
                }
            }
        };

        // Add action listeners
        sortButton.addActionListener(e -> startSorting());
        resetButton.addActionListener(e -> reset());
        randomButton.addActionListener(e -> generateRandomNumbers());

        // Add components to frame
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(statusLabel);
        bottomPanel.setBackground(new Color(200, 220, 240));

        add(controlPanel, BorderLayout.NORTH);
        add(visualPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void generateRandomNumbers() {
        if (isSorting) {
            stopSorting();
        }

        numbers.clear();
        Random rand = new Random();
        int count = 10 + rand.nextInt(6); // Generate 10-15 random numbers

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int num = 5 + rand.nextInt(MAX_NUMBER - 5);
            numbers.add(num);
            sb.append(num);
            if (i < count - 1) {
                sb.append(", ");
            }
        }

        inputField.setText(sb.toString());
        currentArray = numbers.stream().mapToInt(i -> i).toArray();
        currentIndex = -1;
        j = -1;
        visualPanel.repaint();
        statusLabel.setText("Random numbers generated. Press Sort to begin.");
    }

    private void parseInput() {
        numbers.clear();
        String input = inputField.getText().trim();

        try {
            for (String numStr : input.split(",")) {
                numStr = numStr.trim();
                if (!numStr.isEmpty()) {
                    int num = Integer.parseInt(numStr);
                    if (num > 0 && num <= MAX_NUMBER) {
                        numbers.add(num);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Please enter positive numbers less than or equal to " + MAX_NUMBER,
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (numbers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter at least one number",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentArray = numbers.stream().mapToInt(i -> i).toArray();
            currentIndex = -1;
            j = -1;
            visualPanel.repaint();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input. Please enter numbers separated by commas.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startSorting() {
        if (isSorting) {
            stopSorting();
            return;
        }

        if (currentArray == null || currentArray.length == 0) {
            parseInput();
        }

        if (currentArray != null && currentArray.length > 0) {
            isSorting = true;
            sortButton.setText("Pause");
            inputField.setEnabled(false);
            randomButton.setEnabled(false);

            // Initialize variables for insertion sort
            currentIndex = 1;  // Insertion sort starts from the second element
            j = 0;

            // Start animation timer
            int delay = 1100 - speedSlider.getValue() * 100;  // Adjust speed (100ms to 1000ms)
            timer = new Timer(delay, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertionSortStep();
                    visualPanel.repaint();
                }
            });
            timer.start();
        }
    }

    private void insertionSortStep() {
        if (currentIndex >= currentArray.length) {
            stopSorting();
            statusLabel.setText("Sorting completed!");
            return;
        }

        if (j == currentIndex - 1) {
            // Save the key value
            currentKey = currentArray[currentIndex];
            statusLabel.setText("Selecting element at position " + currentIndex + " (value: " + currentKey + ")");
        }

        if (j >= 0 && currentArray[j] > currentKey) {
            // Shift element right
            currentArray[j + 1] = currentArray[j];
            statusLabel.setText("Shifting " + currentArray[j] + " to the right");
            j--;
        } else {
            // Place key in correct position
            currentArray[j + 1] = currentKey;
            statusLabel.setText("Placing " + currentKey + " at position " + (j + 1));
            currentIndex++;
            j = currentIndex - 1;
        }
    }

    private void stopSorting() {
        if (timer != null) {
            timer.stop();
        }
        isSorting = false;
        sortButton.setText("Sort");
        inputField.setEnabled(true);
        randomButton.setEnabled(true);
    }

    private void reset() {
        stopSorting();
        numbers.clear();
        currentArray = null;
        currentIndex = -1;
        j = -1;
        inputField.setText("");
        statusLabel.setText("Enter numbers and press Sort");
        visualPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InsertionSortVisualizer());
    }
}