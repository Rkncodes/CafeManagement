import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TopBarTest extends JFrame {
    private JTextField searchField;
    private JComboBox<String> categoryCombo;

    public TopBarTest() {
        FlatLightLaf.setup();

        setTitle("Top Bar Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 120);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));

        add(createTopBar(), BorderLayout.NORTH);
    }

    private JComponent createTopBar() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBorder(new EmptyBorder(8, 8, 8, 8));
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("☕ Cafe Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        top.add(title, BorderLayout.WEST);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        center.setBackground(Color.WHITE);

        searchField = new JTextField(30);
        searchField.setToolTipText("Search products...");
        JButton searchBtn = createStyledButton("Search");
        center.add(searchField);
        center.add(searchBtn);

        categoryCombo = new JComboBox<>(new String[]{"All", "Coffee", "Snacks", "Dessert", "Beverage"});
        center.add(new JLabel("Category:"));
        center.add(categoryCombo);

        top.add(center, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setPreferredSize(new Dimension(220, 40));
        rightPanel.setBackground(new Color(240, 240, 240)); // light bg to stand out

        JButton refreshBtn = createStyledButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> System.out.println("Refresh clicked!"));
        rightPanel.add(refreshBtn);

        JButton add = createStyledButton("➕ Add Product");
        add.addActionListener(e -> System.out.println("Add clicked!"));
        rightPanel.add(add);

        top.add(rightPanel, BorderLayout.EAST);

        return top;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 105, 217));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 123, 255));
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TopBarTest().setVisible(true);
        });
    }
}
