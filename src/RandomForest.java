import java.util.*;

public class RandomForest {
    private int numTrees;
    private List<DecisionTree> trees;
    public RandomForest(int numTrees) {
        this.numTrees = numTrees;
        this.trees = new ArrayList<>();
    }

    public void train(List<double[]> dataset) {
        int numFeatures = dataset.get(0).length - 1;
        Random rand = new Random();

        for (int i = 0; i < numTrees; i++) {
            List<double[]> bootstrapSample = bootstrapSample(dataset, rand);
            DecisionTree tree = new DecisionTree();
            tree.train(bootstrapSample, numFeatures);
            trees.add(tree);
        }
    }

    private List<double[]> bootstrapSample(List<double[]> dataset, Random rand) {
        List<double[]> sample = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            sample.add(dataset.get(rand.nextInt(dataset.size())));
        }
        return sample;
    }

    public int predict(double[] dataPoint) {
        int[] votes = new int[2];

        for (DecisionTree tree : trees) {
            int prediction = tree.predict(dataPoint);
            votes[prediction]++;
        }

        return votes[1] > votes[0] ? 1 : 0;
    }


    public double evaluate(List<double[]> testDataset) {
        int correctPredictions = 0;

        for (double[] row : testDataset) {
            double[] dataPoint = Arrays.copyOf(row, row.length - 1);
            int actualLabel = (int) row[row.length - 1];
            int predictedLabel = predict(dataPoint);

            if (actualLabel == predictedLabel) {
                correctPredictions++;
            }
        }

        return (double) correctPredictions / testDataset.size();
    }
}
