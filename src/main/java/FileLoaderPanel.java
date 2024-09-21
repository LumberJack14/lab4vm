import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class FileLoaderPanel extends JPanel {
    private JTextField fileNameField;
    private JButton loadButton;
    private PointsInputPanel pointsInputPanel;

    private String defaultFileName = "test.txt";

    public FileLoaderPanel(PointsInputPanel pointsInputPanel) {
        this.pointsInputPanel = pointsInputPanel;

        setLayout(new BorderLayout());

        fileNameField = new JTextField(20);

        loadButton = new JButton("Загрузить из файла");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPointsFromFile(fileNameField.getText());
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Имя файла:"));
        inputPanel.add(fileNameField);
        inputPanel.add(loadButton);

        add(inputPanel, BorderLayout.CENTER);
    }

    private void loadPointsFromFile(String fileName) {
        if (Objects.equals(fileName, "")) {
            fileName = defaultFileName;
        }

        File file = new File(fileName);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Файл не найден!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Point> points = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                if (points.size() >= 12) {
                    System.out.println("Больше 12 точек в файле, лишние будут проигнорированы.");
                    break;
                }

                if (scanner.hasNextInt()) {
                    int x = scanner.nextInt();
                    if (scanner.hasNextInt()) {
                        int y = scanner.nextInt();
                        points.add(new Point(x, y));
                    } else {
                        System.out.println("Некорректный формат данных в файле.");
                        JOptionPane.showMessageDialog(this, "Некорректный формат данных в файле!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (points.size() < 8) {
                JOptionPane.showMessageDialog(this, "Недостаточно точек (минимум 8).", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            while (pointsInputPanel.getXFields().size() < points.size()) {
                pointsInputPanel.addPointFields();
            }

            for (int i = 0; i < points.size(); i++) {
                JTextField xField = pointsInputPanel.getXField(i);
                JTextField yField = pointsInputPanel.getYField(i);
                xField.setText(String.valueOf(points.get(i).x));
                yField.setText(String.valueOf(points.get(i).y));
            }

            pointsInputPanel.revalidate();
            pointsInputPanel.repaint();

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при чтении файла.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
