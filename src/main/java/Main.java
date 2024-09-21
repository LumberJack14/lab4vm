import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

public class Main {

    private JFrame frame;
    private ConsolePanel consolePanel;
    private JComboBox<String> functionsComboBox;
    private FunctionType selectedFunction = FunctionType.LINEAR;

    private int calculationCount = 1;

    private class ItemListenerImplementation implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedFunction = FunctionType.getFunctionTypeByName((String) functionsComboBox.getSelectedItem());
                System.out.println("Selected function: " + functionsComboBox.getSelectedItem());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        frame = new JFrame("LAB 4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel initialPanel = createInitialPanel();
        frame.add(initialPanel);
        frame.setVisible(true);
    }

    private JPanel createInitialPanel() {
        JPanel initialPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel dropDownLabel = new JLabel("Выберите аппроксимирующую функцию");
        gbc.gridx = 0;
        gbc.gridy = 0;
        initialPanel.add(dropDownLabel, gbc);

        functionsComboBox = createDropDown();
        gbc.gridx = 0;
        gbc.gridy = 1;
        initialPanel.add(functionsComboBox, gbc);

        PlotPanel plotPanel = new PlotPanel();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1;
        initialPanel.add(plotPanel, gbc);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        PointsInputPanel pointsInputPanel = new PointsInputPanel();
        FileLoaderPanel fileLoaderPanel = new FileLoaderPanel(pointsInputPanel);

        JButton approxButton = new JButton("Аппроксимировать");
        approxButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consolePanel.print("Апроксимация №" + calculationCount);
                calculationCount++;

                consolePanel.print("Выбранная функция: " + selectedFunction.getName());

                List<MyPoint> points = pointsInputPanel.getPoints();
                double[] coeffs;
                if (selectedFunction == FunctionType.BEST) {
                    selectedFunction = Algorithm.findBestFit(points);
                    consolePanel.print("Лучшая аппроксимирующая функция: " + selectedFunction.getName());
                    coeffs = Algorithm.bestFit(points);
                } else {
                    coeffs = Algorithm.approximate(selectedFunction, points);
                }

                if (selectedFunction == FunctionType.LINEAR) {
                    double correlation = Algorithm.calculatePearsonCorrelation(points);
                    consolePanel.print("Коэффициент корреляции пирсона: " + correlation);
                }

                consolePanel.print("Коэффициенты: ");
                for (double coef: coeffs) {
                    consolePanel.print(coef + " ");
                }

                double r2 = Algorithm.calculateR2(selectedFunction, points,coeffs);
                consolePanel.print("Коэффициент детерменации R²: " + r2);
                consolePanel.print("");

                plotPanel.updateScatterDataset(points);
                plotPanel.updateFunctionDataset(selectedFunction, coeffs, points);
            }
        });

        consolePanel = new ConsolePanel();

        rightPanel.add(fileLoaderPanel);
        rightPanel.add(pointsInputPanel);

        JPanel spacer1 = new JPanel();
        spacer1.setPreferredSize(new Dimension(0, 20));
        rightPanel.add(spacer1);

        rightPanel.add(approxButton);

        JPanel spacer2 = new JPanel();
        spacer2.setPreferredSize(new Dimension(0, 20));
        rightPanel.add(spacer2);

        rightPanel.add(consolePanel);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.gridheight = 3;
        initialPanel.add(rightPanel, gbc);

        return initialPanel;
    }

    private JComboBox<String> createDropDown() {
        ItemListener itemListener = new ItemListenerImplementation();
        JComboBox<String> dropDown = new JComboBox<>(
                Arrays.stream(FunctionType.values())
                        .map(Enum::toString)
                        .toArray(String[]::new)
        );
        dropDown.addItemListener(itemListener);
        return dropDown;
    }
}
