import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DataReader {
    private Map<String, Integer> genderMap = new HashMap<>();
    private Map<String, Integer> smokingStatusMap = new HashMap<>();

    public DataReader() {
        genderMap.put("Male", 0);
        genderMap.put("Female", 1);
        genderMap.put("Other", 2);

        smokingStatusMap.put("never smoked", 0);
        smokingStatusMap.put("formerly smoked", 1);
        smokingStatusMap.put("smokes", 2);
        smokingStatusMap.put("Unknown", 3);
    }
    public int getGenderValue(String gender) {
        Integer value = genderMap.get(gender);
        if (value == null) {
            throw new IllegalArgumentException("Invalid gender: " + gender);
        }
        return value;
    }

    public int getSmokingStatusValue(String status) {
        Integer value = smokingStatusMap.get(status);
        if (value == null) {
            throw new IllegalArgumentException("Invalid smoking status: " + status);
        }
        return value;
    }

    public List<double[]> loadDataset(String filePath) {
        List<double[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                if (count >= 1000) break;

                String[] tokens = line.split(",");

                if (tokens.length < 12) {
                    continue;
                }

                try {
                    double gender = tokens[1].equalsIgnoreCase("Male") ? 0.0 :
                            tokens[1].equalsIgnoreCase("Female") ? 1.0 : 2.0;
                    int age = Integer.parseInt(tokens[2].trim());
                    int hypertension = Integer.parseInt(tokens[3].trim());
                    int heartDisease = Integer.parseInt(tokens[4].trim());
                    double avgGlucose = Double.parseDouble(tokens[8].trim());
                    double bmi = Double.parseDouble(tokens[9].trim());
                    double smoking = tokens[10].equalsIgnoreCase("never smoked") ? 0.0 :
                            tokens[10].equalsIgnoreCase("formerly smoked") ? 1.0 :
                                    tokens[10].equalsIgnoreCase("smokes") ? 2.0 : -1.0;
                    int stroke = Integer.parseInt(tokens[11].trim());

                    data.add(new double[]{gender, age, hypertension, heartDisease, avgGlucose, bmi, smoking, stroke});
                    count++;
                } catch (NumberFormatException e) {
                }
            }

            System.out.println("Dataset loaded: " + data.size() + " rows ");
        } catch (Exception e) {
            System.err.println("Error processing the dataset: " + e.getMessage());
        }
        return data;
    }


}
