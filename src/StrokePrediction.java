import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.*;
import java.io.*;

public class StrokePrediction {
    public static void main(String[] args) {
        DataReader dataReader = new DataReader();
        String filePath = selectFile();
        if (filePath == null) {
            System.err.println("No file selected. Exiting...");
            return;
        }

        List<double[]> data = dataReader.loadDataset(filePath);
        if (data.isEmpty()) {
            System.err.println("No data loaded. Exiting...");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of instances to use for training: ");
        int numInstances = Integer.parseInt(scanner.nextLine().trim());

        if (numInstances > data.size()) {
            System.err.println("Error: Number of instances exceeds the dataset size. Using all " + data.size() + " instances instead.");
            numInstances = data.size();
        }

        Collections.shuffle(data, new Random(42));
        List<double[]> trainData = data.subList(0, numInstances);
        List<double[]> testData = data.subList(numInstances, data.size());

        System.out.print("Enter the number of trees for Random Forest: ");
        int numTrees = Integer.parseInt(scanner.nextLine().trim());

        RandomForest rf = new RandomForest(numTrees);
        rf.train(trainData);
        double accuracy = rf.evaluate(testData);

        System.out.println("Number of Instances: " + numInstances);
        System.out.println("Number of Features: " + (data.get(0).length - 1));
        System.out.println("Test Accuracy: " + String.format("%.2f", accuracy * 100) + "%");
        System.out.println("Entropy of Training Set: " + calculateEntropy(trainData));
        System.out.println("Entropy of Test Set: " + calculateEntropy(testData));

        System.out.println("\n--- Enter Patient Information for Stroke Risk Prediction ---");
        System.out.print("Gender (Male/Female/Other): ");
        String genderInput = scanner.nextLine().trim();
        int gender = dataReader.getGenderValue(genderInput);
        System.out.print("Age: ");
        double age = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Hypertension (0 = No, 1 = Yes): ");
        double hypertension = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Heart Disease (0 = No, 1 = Yes): ");
        double heartDisease = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Average Glucose Level: ");
        double avgGlucose = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("BMI: ");
        double bmi = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Smoking Status (never smoked/formerly smoked/smokes/Unknown): ");
        String smokingInput = scanner.nextLine().trim();
        int smokingStatus = dataReader.getSmokingStatusValue(smokingInput);

        double[] userInput = {gender, age, hypertension, heartDisease, avgGlucose, bmi, smokingStatus};

        System.out.println("\n--- Health Report ---");
        if (avgGlucose > 140) {
            System.out.println("Average Glucose Level: Abnormal");
        } else {
            System.out.println("Average Glucose Level: Normal");
        }
        if (bmi < 18.5 || bmi > 24.9) { // Normal BMI is 18.5–24.9
            System.out.println("BMI: Abnormal");
        } else {
            System.out.println("BMI: Normal");
        }

        int prediction = rf.predict(userInput);
        String riskCategory = classifyRisk(prediction);

        System.out.println("\nPredicted Stroke Risk: " + riskCategory);

        if (riskCategory.equals("High Risk")) {
            System.out.println("Seek help from a medical professional!");
        }
    }

    private static String classifyRisk(int prediction) {
        if (prediction == 1) {
            return "High Risk";
        } else {
            return "Low Risk";
        }
    }

    private static double calculateEntropy(List<double[]> dataset) {
        int count0 = 0, count1 = 0;
        for (double[] row : dataset) {
            if (row[row.length - 1] == 1.0) count1++;
            else count0++;
        }
        double p0 = (double) count0 / dataset.size();
        double p1 = (double) count1 / dataset.size();

        double entropy = 0.0;
        if (p0 > 0) entropy -= p0 * (Math.log(p0) / Math.log(2));
        if (p1 > 0) entropy -= p1 * (Math.log(p1) / Math.log(2));

        return entropy;
    }

        private static String selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Dataset CSV File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}
