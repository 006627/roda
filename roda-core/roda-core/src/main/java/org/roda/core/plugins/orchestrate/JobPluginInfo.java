package org.roda.core.plugins.orchestrate;

public class JobPluginInfo {
  private int stepsCompleted = 0;
  private int totalSteps = 0;
  private int completionPercentage = 0;
  private int objectsCount = 0;
  private int objectsWaitingToBeProcessed = 0;
  private int objectsProcessedWithSuccess = 0;
  private int objectsProcessedWithFailure = 0;

  public JobPluginInfo() {

  }

  public JobPluginInfo(int completionPercentage) {
    this.completionPercentage = completionPercentage;
  }

  public int getStepsCompleted() {
    return stepsCompleted;
  }

  public void setStepsCompleted(int stepsCompleted) {
    this.stepsCompleted = stepsCompleted;
  }

  public int getTotalSteps() {
    return totalSteps;
  }

  public void setTotalSteps(int totalSteps) {
    this.totalSteps = totalSteps;
  }

  public int getCompletionPercentage() {
    return completionPercentage;
  }

  public void setCompletionPercentage(int completionPercentage) {
    this.completionPercentage = completionPercentage;
  }

  public int getObjectsCount() {
    return objectsCount;
  }

  public void setObjectsCount(int objectsCount) {
    this.objectsCount = objectsCount;
  }

  public int getObjectsWaitingToBeProcessed() {
    return objectsWaitingToBeProcessed;
  }

  public void setObjectsWaitingToBeProcessed(int objectsWaitingToBeProcessed) {
    this.objectsWaitingToBeProcessed = objectsWaitingToBeProcessed;
  }

  public int getObjectsProcessedWithSuccess() {
    return objectsProcessedWithSuccess;
  }

  public void setObjectsProcessedWithSuccess(int objectsProcessedWithSuccess) {
    this.objectsProcessedWithSuccess = objectsProcessedWithSuccess;
  }

  public int getObjectsProcessedWithFailure() {
    return objectsProcessedWithFailure;
  }

  public void setObjectsProcessedWithFailure(int objectsProcessedWithFailure) {
    this.objectsProcessedWithFailure = objectsProcessedWithFailure;
  }

  @Override
  public String toString() {
    return "JobPluginInfo [stepsCompleted=" + stepsCompleted + ", totalSteps=" + totalSteps + ", completionPercentage="
      + completionPercentage + ", objectsCount=" + objectsCount + ", objectsWaitingToBeProcessed="
      + objectsWaitingToBeProcessed + ", objectsProcessedWithSuccess=" + objectsProcessedWithSuccess
      + ", objectsProcessedWithFailure=" + objectsProcessedWithFailure + "]";
  }
}
