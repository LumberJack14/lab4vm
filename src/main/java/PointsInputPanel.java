import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PointsInputPanel extends JPanel {
    private static final int INITIAL_POINTS = 8;
    private static final int MAX_POINTS = 12;
    private static final int MIN_POINTS = 8;

    private ArrayList<JTextField> xFields;
    private ArrayList<JTextField> yFields;
    private JButton addButton;
    private JButton removeButton;
    private JPanel pointsPanel;

    public PointsInputPanel() {
        xFields = new ArrayList<>();
        yFields = new ArrayList<>();

        setLayout(new BorderLayout());

        pointsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < INITIAL_POINTS; i++) {
            addPointFields(gbc);
        }

        add(pointsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        addButton = new JButton("Добавить точку");
        removeButton = new JButton("Удалить точку");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (xFields.size() < MAX_POINTS) {
                    addPointFields(gbc);
                    pointsPanel.revalidate();
                    pointsPanel.repaint();
                    updateButtonsState();
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (xFields.size() > MIN_POINTS) {
                    removePointFields();
                    pointsPanel.revalidate();
                    pointsPanel.repaint();
                    updateButtonsState();
                }
            }
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);

        add(buttonsPanel, BorderLayout.SOUTH);

        updateButtonsState();
    }

    public JTextField getXField(int index) {
        return xFields.get(index);
    }

    public JTextField getYField(int index) {
        return yFields.get(index);
    }

    public ArrayList<JTextField> getXFields() {
        return xFields;
    }

    public ArrayList<JTextField> getYFields() {
        return yFields;
    }

    public void addPointFields() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPointFields(gbc);
    }


    private void addPointFields(GridBagConstraints gbc) {
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);

        xFields.add(xField);
        yFields.add(yField);

        gbc.gridx = 0;
        gbc.gridy = xFields.size() - 1;
        pointsPanel.add(new JLabel("X" + (xFields.size()) + ":"), gbc);

        gbc.gridx = 1;
        pointsPanel.add(xField, gbc);

        gbc.gridx = 2;
        pointsPanel.add(new JLabel("Y" + (yFields.size()) + ":"), gbc);

        gbc.gridx = 3;
        pointsPanel.add(yField, gbc);

        int panelHeight = (xFields.size() + 1) * 30;
        pointsPanel.setPreferredSize(new Dimension(400, panelHeight));
    }

    private void removePointFields() {
        if (xFields.size() > MIN_POINTS) {
            int lastIndex = xFields.size() - 1;

            pointsPanel.remove(xFields.get(lastIndex));
            pointsPanel.remove(yFields.get(lastIndex));

            pointsPanel.remove(pointsPanel.getComponent(pointsPanel.getComponentCount() - 1));
            pointsPanel.remove(pointsPanel.getComponent(pointsPanel.getComponentCount() - 1));

            xFields.remove(lastIndex);
            yFields.remove(lastIndex);

            int panelHeight = (xFields.size() + 1) * 30;
            pointsPanel.setPreferredSize(new Dimension(400, panelHeight));
        }
    }

    private void updateButtonsState() {
        addButton.setEnabled(xFields.size() < MAX_POINTS);
        removeButton.setEnabled(xFields.size() > MIN_POINTS);
    }

    public ArrayList<MyPoint> getPoints() {
        ArrayList<MyPoint> points = new ArrayList<>();
        for (int i = 0; i < xFields.size(); i++) {
            try {
                double x = Double.parseDouble(xFields.get(i).getText().replace(",", "."));
                double y = Double.parseDouble(yFields.get(i).getText().replace(",", "."));
                points.add(new MyPoint(x, y));
            } catch (NumberFormatException e) {
                System.out.println("Некорректное значение в точке " + (i + 1));
            }
        }
        return points;
    }

    public static void test(String[] args) {
        JFrame frame = new JFrame("Ввод координат точек");
        PointsInputPanel pointsInputPanel = new PointsInputPanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.add(pointsInputPanel);
        frame.setVisible(true);
    }
}
