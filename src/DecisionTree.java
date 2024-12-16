import java.util.ArrayList;
import java.util.List;

class DecisionTree {
    private Node root;
    public void train(List<double[]> data, int maxDepth) {
        root = buildTree(data, maxDepth, 0);
    }
    public int predict(double[] dataPoint) {
        return classify(root, dataPoint);
    }

    private Node buildTree(List<double[]> data, int maxDepth, int currentDepth) {
        if (data.isEmpty() || currentDepth >= maxDepth) {
            return new Node(getMajorityClass(data));
        }

        Split bestSplit = getBestSplit(data);
        if (bestSplit == null || bestSplit.gini == 0) {
            return new Node(getMajorityClass(data));
        }

        List<double[]> leftData = new ArrayList<>();
        List<double[]> rightData = new ArrayList<>();
        for (double[] row : data) {
            if (row[bestSplit.featureIndex] <= bestSplit.threshold) {
                leftData.add(row);
            } else {
                rightData.add(row);
            }
        }

        Node node = new Node(bestSplit.featureIndex, bestSplit.threshold);
        node.left = buildTree(leftData, maxDepth, currentDepth + 1);
        node.right = buildTree(rightData, maxDepth, currentDepth + 1);

        return node;
    }

    private int classify(Node node, double[] dataPoint) {
        if (node.isLeaf()) {
            return node.label;
        }

        if (dataPoint[node.featureIndex] <= node.threshold) {
            return classify(node.left, dataPoint);
        } else {
            return classify(node.right, dataPoint);
        }
    }

    private Split getBestSplit(List<double[]> data) {
        int numFeatures = data.get(0).length - 1;
        double bestGini = Double.MAX_VALUE;
        Split bestSplit = null;

        for (int featureIndex = 0; featureIndex < numFeatures; featureIndex++) {
            for (double[] row : data) {
                double threshold = row[featureIndex];
                double gini = calculateGini(data, featureIndex, threshold);
                if (gini < bestGini) {
                    bestGini = gini;
                    bestSplit = new Split(featureIndex, threshold, gini);
                }
            }
        }
        return bestSplit;
    }

    private double calculateGini(List<double[]> data, int featureIndex, double threshold) {
        List<double[]> left = new ArrayList<>();
        List<double[]> right = new ArrayList<>();

        for (double[] row : data) {
            if (row[featureIndex] <= threshold) {
                left.add(row);
            } else {
                right.add(row);
            }
        }

        double leftGini = calculateGroupGini(left);
        double rightGini = calculateGroupGini(right);

        return (left.size() * leftGini + right.size() * rightGini) / data.size();
    }

    private double calculateGroupGini(List<double[]> group) {
        if (group.isEmpty()) {
            return 0;
        }
        int count0 = 0;
        int count1 = 0;
        for (double[] row : group) {
            if (row[row.length - 1] == 0) {
                count0++;
            } else {
                count1++;
            }
        }

        double size = group.size();
        double p0 = count0 / size;
        double p1 = count1 / size;

        return 1 - (p0 * p0 + p1 * p1);
    }

    private int getMajorityClass(List<double[]> data) {
        int count0 = 0;
        int count1 = 0;

        for (double[] row : data) {
            if (row[row.length - 1] == 0) {
                count0++;
            } else {
                count1++;
            }
        }

        return count1 > count0 ? 1 : 0;
    }

    private static class Node {
        int featureIndex;
        double threshold;
        int label;
        Node left, right;

        Node(int label) {
            this.label = label;
        }
        Node(int featureIndex, double threshold) {
            this.featureIndex = featureIndex;
            this.threshold = threshold;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static class Split {
        int featureIndex;
        double threshold;
        double gini;

        Split(int featureIndex, double threshold, double gini) {
            this.featureIndex = featureIndex;
            this.threshold = threshold;
            this.gini = gini;
        }
    }
}
