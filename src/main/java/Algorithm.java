import java.awt.Point;
import java.util.List;

public class Algorithm {

    public static double[] approximate(FunctionType type, List<MyPoint> points) {
        switch (type) {
            case LINEAR:
                return linearApproximation(points);
            case POLY2:
                return polynomialApproximation(points, 2);
            case POLY3:
                return polynomialApproximation(points, 3);
            case EXP:
                return exponentialApproximation(points);
            case LOG:
                return logarithmicApproximation(points);
            case POWER:
                return powerApproximation(points);
            case BEST:
                return bestFit(points);
            default:
                throw new IllegalArgumentException("Неизвестный тип функции");
        }
    }

    private static double[] linearApproximation(List<MyPoint> points) {
        int n = points.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (MyPoint point : points) {
            double x = point.getX();
            double y = point.getY();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = n * sumX2 - sumX * sumX;
        double a = (n * sumXY - sumX * sumY) / denominator;
        double b = (sumY * sumX2 - sumX * sumXY) / denominator;

        return new double[]{a, b};
    }

    private static double[] polynomialApproximation(List<MyPoint> points, int degree) {
        int n = points.size();
        int m = degree + 1;
        double[] coeff = new double[m];

        double[][] X = new double[m][m];
        double[] Y = new double[m];

        for (MyPoint point : points) {
            double x = point.getX();
            double y = point.getY();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < m; j++) {
                    X[i][j] += Math.pow(x, i + j);
                }
                Y[i] += y * Math.pow(x, i);
            }
        }

        coeff = gaussianElimination(X, Y);

        return coeff;
    }

    private static double[] exponentialApproximation(List<MyPoint> points) {
        int n = points.size();
        double sumX = 0, sumLogY = 0, sumXLogY = 0, sumX2 = 0;

        for (MyPoint point : points) {
            double x = point.getX();
            double y = point.getY();
            if (y <= 0) continue;
            double logY = Math.log(y);
            sumX += x;
            sumLogY += logY;
            sumXLogY += x * logY;
            sumX2 += x * x;
        }

        double denominator = n * sumX2 - sumX * sumX;
        double b = (n * sumXLogY - sumX * sumLogY) / denominator;
        double logA = (sumLogY * sumX2 - sumX * sumXLogY) / denominator;
        double a = Math.exp(logA);

        return new double[]{a, b};
    }

    private static double[] logarithmicApproximation(List<MyPoint> points) {
        int n = points.size();
        double sumLogX = 0, sumY = 0, sumLogXY = 0, sumLogX2 = 0;

        for (MyPoint point : points) {
            double x = point.getX();
            double y = point.getY();
            if (x <= 0) continue;
            double logX = Math.log(x);
            sumLogX += logX;
            sumY += y;
            sumLogXY += logX * y;
            sumLogX2 += logX * logX;
        }

        double denominator = n * sumLogX2 - sumLogX * sumLogX;
        double b = (n * sumLogXY - sumLogX * sumY) / denominator;
        double a = (sumY * sumLogX2 - sumLogX * sumLogXY) / denominator;

        return new double[]{a, b};
    }

    private static double[] powerApproximation(List<MyPoint> points) {
        int n = points.size();
        double sumLogX = 0, sumLogY = 0, sumLogXLogY = 0, sumLogX2 = 0;

        for (MyPoint point : points) {
            double x = point.getX();
            double y = point.getY();
            if (x <= 0 || y <= 0) continue;
            double logX = Math.log(x);
            double logY = Math.log(y);
            sumLogX += logX;
            sumLogY += logY;
            sumLogXLogY += logX * logY;
            sumLogX2 += logX * logX;
        }

        double denominator = n * sumLogX2 - sumLogX * sumLogX;
        double b = (n * sumLogXLogY - sumLogX * sumLogY) / denominator;
        double logA = (sumLogY * sumLogX2 - sumLogX * sumLogXLogY) / denominator;
        double a = Math.exp(logA);

        return new double[]{a, b};
    }

    public static FunctionType findBestFit(List<MyPoint> points) {
        FunctionType bestType = null;
        double minDeviation = Double.MAX_VALUE;

        for (FunctionType type : FunctionType.values()) {
            if (type == FunctionType.BEST) continue;

            double[] coeffs = approximate(type, points);
            double deviation = calculateDeviation(type, points, coeffs);

            if (deviation < minDeviation) {
                minDeviation = deviation;
                bestType = type;
            }
        }

        return bestType;
    }

    public static double[] bestFit(List<MyPoint> points) {
        FunctionType bestType = findBestFit(points);
        return approximate(bestType, points);
    }


    public static double calculateDeviation(FunctionType type, List<MyPoint> points, double[] coeffs) {
        double S = 0;
        for (MyPoint point : points) {
            double x = point.getX();
            double yActual = point.getY();
            double yApprox = 0;

            switch (type) {
                case LINEAR:
                    yApprox = coeffs[0] * x + coeffs[1];
                    break;
                case POLY2:
                    yApprox = coeffs[0] + coeffs[1] * x + coeffs[2] * x * x;
                    break;
                case POLY3:
                    yApprox = coeffs[0] + coeffs[1] * x + coeffs[2] * x * x + coeffs[3] * x * x * x;
                    break;
                case EXP:
                    yApprox = coeffs[0] * Math.exp(coeffs[1] * x);
                    break;
                case LOG:
                    yApprox = coeffs[0] + coeffs[1] * Math.log(x);
                    break;
                case POWER:
                    yApprox = coeffs[0] * Math.pow(x, coeffs[1]);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип функции");
            }

            S += Math.pow(yActual - yApprox, 2);
        }

        return S;
    }

    private static double[] gaussianElimination(double[][] A, double[] B) {
        int n = B.length;
        for (int i = 0; i < n; i++) {
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) {
                    max = j;
                }
            }

            double[] temp = A[i];
            A[i] = A[max];
            A[max] = temp;

            double t = B[i];
            B[i] = B[max];
            B[max] = t;

            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (B[i] - sum) / A[i][i];
        }
        return x;
    }

    public static double calculatePearsonCorrelation(List<MyPoint> points) {
        int n = points.size();
        if (n == 0) return 0;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (MyPoint point : points) {
            double x = point.getX();
            double y = point.getY();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        if (denominator == 0) return 0;

        return numerator / denominator;
    }

    public static double calculateR2(FunctionType type, List<MyPoint> points, double[] coeffs) {
        double meanY = 0;
        double totalSS = 0;
        double residualSS = 0;


        for (MyPoint point : points) {
            meanY += point.getY();
        }
        meanY /= points.size();


        for (MyPoint point : points) {
            double yActual = point.getY();
            double yPredicted = 0;

            switch (type) {
                case LINEAR:
                    yPredicted = coeffs[0] * point.getX() + coeffs[1];
                    break;
                case POLY2:
                    yPredicted = coeffs[0] + coeffs[1] * point.getX() + coeffs[2] * point.getX() * point.getX();
                    break;
                case POLY3:
                    yPredicted = coeffs[0] + coeffs[1] * point.getX() + coeffs[2] * point.getX() * point.getX() + coeffs[3] * point.getX() * point.getX() * point.getX();
                    break;
                case EXP:
                    yPredicted = coeffs[0] * Math.exp(coeffs[1] * point.getX());
                    break;
                case LOG:
                    yPredicted = coeffs[0] + coeffs[1] * Math.log(point.getX());
                    break;
                case POWER:
                    yPredicted = coeffs[0] * Math.pow(point.getX(), coeffs[1]);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип функции");
            }

            totalSS += Math.pow(yActual - meanY, 2);
            residualSS += Math.pow(yActual - yPredicted, 2);
        }

        return 1 - (residualSS / totalSS);
    }
}
