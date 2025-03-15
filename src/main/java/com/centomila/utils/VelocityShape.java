package com.centomila.utils;

import java.util.Random;

public class VelocityShape {
    public static String[] velocityShapes = new String[] {
            "---DISABLED---", "Random", "Flat (Value by Min Velocity)", "Linear Inc", "Linear Dec",
            "Ease Out Cubic Inc", "Ease In Cubic Inc", "Ease Out Cubic Dec", "Ease In Cubic Dec",
            "Ease InOut Inc", "Ease InOut Dec", "Arc", "Sine", "Cosine", "Double Cosine",
            "Alternate Min Vel and High Vel", "Alternate High and Min Vel", "Saw 2 Inc", "Saw 2 Dec", "Saw 3 Inc",
            "Saw 3 Dec", "Saw 4 Inc", "Saw 4 Dec",
            "Triangle", "Square Inc", "Square Dec", "Square 2 Inc", "Square 2 Dec", "Square 3 Inc", "Square 3 Dec",
            "Square 4 Inc", "Square 4 Dec"
    };

    /**
     * @param pattern
     * @param velocityType "Random", "Flat (Value by Min Velocity)", "Linear Inc",
     *                     "Linear Dec", "Arc", "Sine", "Cosine", "Double Cosine",
     *                     "Alternate Min Vel and High Vel",
     *                     "Alternate High and Min Vel"
     * @param minVelocity
     * @param maxVelocity
     * @return the modified pattern array
     */
    public static int[] applyVelocityShape(int[] pattern, String velocityType, int minVelocity, int maxVelocity) {
        int[] result = pattern.clone(); // Create a copy of the input array

        // Count non-zero steps first
        int activeStepsCount = 0;
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) {
                activeStepsCount++;
            }
        }

        switch (velocityType) {
            case "Random":
                Random random = new Random();
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = minVelocity + random.nextInt(maxVelocity - minVelocity + 1);
                    }
                }
                break;
            case "Flat (Value by Min Velocity)":
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = minVelocity;
                    }
                }
                break;
            case "Linear Inc":
                int stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * stepCount
                                        / (double) (activeStepsCount - 1));
                        stepCount++;
                    }
                }
                break;
            case "Linear Dec":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * stepCount
                                        / (double) (activeStepsCount - 1));
                        stepCount++;
                    }
                }
                break;
            case "Ease Out Cubic Inc":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double t = stepCount / (double) (activeStepsCount - 1);
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * (1 - Math.pow(1 - t, 3)));
                        stepCount++;
                    }
                }
                break;
            case "Ease In Cubic Inc":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double t = stepCount / (double) (activeStepsCount - 1);
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * Math.pow(t, 3));
                        stepCount++;
                    }
                }
                break;
            case "Ease Out Cubic Dec":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double t = stepCount / (double) (activeStepsCount - 1);
                        result[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * (1 - Math.pow(1 - t, 3)));
                        stepCount++;
                    }
                }
                break;
            case "Ease In Cubic Dec":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double t = stepCount / (double) (activeStepsCount - 1);
                        result[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * Math.pow(t, 3));
                        stepCount++;
                    }
                }
                break;
            case "Ease InOut Inc":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double t = stepCount / (double) (activeStepsCount - 1);
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * (t < 0.5 ? 4 * Math.pow(t, 3)
                                        : 1 - Math.pow(-2 * t + 2, 3) / 2));
                        stepCount++;
                    }
                }
                break;
            case "Ease InOut Dec":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double t = stepCount / (double) (activeStepsCount - 1);
                        result[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * (t < 0.5 ? 4 * Math.pow(t, 3)
                                        : 1 - Math.pow(-2 * t + 2, 3) / 2));
                        stepCount++;
                    }
                }
                break;
            case "Arc":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = minVelocity
                                + (int) Math.round(
                                        (maxVelocity - minVelocity)
                                                * Math.sin(Math.PI * stepCount / (activeStepsCount - 1)));
                        stepCount++;
                    }
                }
                break;
            case "Sine":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double normalizedValue = (Math.sin(2 * Math.PI * stepCount / (activeStepsCount - 1)) + 1) * 0.5;
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * normalizedValue);
                        stepCount++;
                    }
                }
                break;
            case "Cosine":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double normalizedValue = (Math.cos(2 * Math.PI * stepCount / (activeStepsCount - 1))) * 0.5
                                + 0.5;
                        result[i] = Math.max(1,
                                minVelocity + (int) Math.round((maxVelocity - minVelocity) * normalizedValue));
                        stepCount++;
                    }
                }
                break;
            case "Double Cosine":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double normalizedValue = (Math.cos(4 * Math.PI * stepCount / (activeStepsCount - 1))) * 0.5
                                + 0.5;
                        result[i] = Math.max(1,
                                minVelocity + (int) Math.round((maxVelocity - minVelocity) * normalizedValue));
                        stepCount++;
                    }
                }
                break;
            case "Alternate Min Vel and High Vel":
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = i % 2 == 0 ? minVelocity : maxVelocity;
                    }
                }
                break;
            case "Alternate High and Min Vel":
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = i % 2 == 0 ? maxVelocity : minVelocity;
                    }
                }
                break;
            case "Saw 2 Inc":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double normalizedValue = (stepCount % (activeStepsCount / 2)) / (double) (activeStepsCount / 2);
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * normalizedValue);
                        stepCount++;
                    }
                }
                break;
            case "Saw 2 Dec":
                stepCount = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        double normalizedValue = (stepCount % (activeStepsCount / 2)) / (double) (activeStepsCount / 2);
                        result[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * normalizedValue);
                        stepCount++;
                    }
                }
                break;
            case "Saw 3 Inc":
                stepCount = 0;
                int thirdSteps = activeStepsCount / 3;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * (stepCount % thirdSteps)
                                        / (double) thirdSteps);
                        stepCount++;
                    }
                }
                break;
            case "Saw 3 Dec":
                stepCount = 0;
                thirdSteps = activeStepsCount / 3;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * (stepCount % thirdSteps)
                                        / (double) thirdSteps);
                        stepCount++;
                    }
                }
                break;
            case "Saw 4 Inc":
                stepCount = 0;
                int quarterSteps = activeStepsCount / 4;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * (stepCount % quarterSteps)
                                        / (double) quarterSteps);
                        stepCount++;
                    }
                }
                break;
            case "Saw 4 Dec":
                stepCount = 0;
                quarterSteps = activeStepsCount / 4;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * (stepCount % quarterSteps)
                                        / (double) quarterSteps);
                        stepCount++;
                    }
                }
                break;
            case "Triangle":
                stepCount = 0;
                int halfSteps = activeStepsCount / 2;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * (stepCount < halfSteps
                                        ? stepCount / (double) halfSteps
                                        : 1 - (stepCount - halfSteps) / (double) halfSteps));
                        stepCount++;
                    }
                }
                break;
            case "Square Inc":
                stepCount = 0;
                halfSteps = activeStepsCount / 2;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = stepCount < halfSteps ? minVelocity : maxVelocity;
                        stepCount++;
                    }
                }
                break;
            case "Square Dec":
                stepCount = 0;
                halfSteps = activeStepsCount / 2;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = stepCount < halfSteps ? maxVelocity : minVelocity;
                        stepCount++;
                    }
                }
                break;
            case "Square 2 Inc":
                stepCount = 0;
                quarterSteps = activeStepsCount / 4;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = (stepCount < quarterSteps || (stepCount >= 2 * quarterSteps && stepCount < 3 * quarterSteps)) 
                                ? minVelocity : maxVelocity;
                        stepCount++;
                    }
                }
                break;
            case "Square 2 Dec":
                stepCount = 0;
                quarterSteps = activeStepsCount / 4;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = (stepCount < quarterSteps || (stepCount >= 2 * quarterSteps && stepCount < 3 * quarterSteps)) 
                                ? maxVelocity : minVelocity;
                        stepCount++;
                    }
                }
                break;
            case "Square 3 Inc":
                stepCount = 0;
                int sixthSteps = activeStepsCount / 6;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = (stepCount < sixthSteps || 
                                   (stepCount >= 2 * sixthSteps && stepCount < 3 * sixthSteps) ||
                                   (stepCount >= 4 * sixthSteps && stepCount < 5 * sixthSteps)) 
                                ? minVelocity : maxVelocity;
                        stepCount++;
                    }
                }
                break;
            case "Square 3 Dec":
                stepCount = 0;
                sixthSteps = activeStepsCount / 6;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = (stepCount < sixthSteps || 
                                   (stepCount >= 2 * sixthSteps && stepCount < 3 * sixthSteps) ||
                                   (stepCount >= 4 * sixthSteps && stepCount < 5 * sixthSteps)) 
                                ? maxVelocity : minVelocity;
                        stepCount++;
                    }
                }
                break;
            case "Square 4 Inc":
                stepCount = 0;
                int eighthSteps = activeStepsCount / 8;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = (stepCount < eighthSteps || 
                                   (stepCount >= 2 * eighthSteps && stepCount < 3 * eighthSteps) ||
                                   (stepCount >= 4 * eighthSteps && stepCount < 5 * eighthSteps) ||
                                   (stepCount >= 6 * eighthSteps && stepCount < 7 * eighthSteps))
                                ? minVelocity : maxVelocity;
                        stepCount++;
                    }
                }
                break;
            case "Square 4 Dec":
                stepCount = 0;
                eighthSteps = activeStepsCount / 8;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] > 0) {
                        result[i] = (stepCount < eighthSteps || 
                                   (stepCount >= 2 * eighthSteps && stepCount < 3 * eighthSteps) ||
                                   (stepCount >= 4 * eighthSteps && stepCount < 5 * eighthSteps) ||
                                   (stepCount >= 6 * eighthSteps && stepCount < 7 * eighthSteps))
                                ? maxVelocity : minVelocity;
                        stepCount++;
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}