package com.tiger.yunda.ui.home;

public class TrainLocations {
        private String id;
        private String subtaskId;
        private String startTime;
        private String endTime;
        private int locationType;
        private String locationTypeName;
        private String spendTime;
        private int score;
        private int state;
        private String stateText;

    public TrainLocations(String id, String subtaskId, String startTime, String endTime, int locationType, String locationTypeName, String spendTime, int score, int state, String stateText) {
        this.id = id;
        this.subtaskId = subtaskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationType = locationType;
        this.locationTypeName = locationTypeName;
        this.spendTime = spendTime;
        this.score = score;
        this.state = state;
        this.stateText = stateText;
    }

    public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setSubtaskId(String subtaskId) {
            this.subtaskId = subtaskId;
        }
        public String getSubtaskId() {
            return subtaskId;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
        public String getStartTime() {
            return startTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        public String getEndTime() {
            return endTime;
        }

        public void setLocationType(int locationType) {
            this.locationType = locationType;
        }
        public int getLocationType() {
            return locationType;
        }

        public void setLocationTypeName(String locationTypeName) {
            this.locationTypeName = locationTypeName;
        }
        public String getLocationTypeName() {
            return locationTypeName;
        }

        public void setSpendTime(String spendTime) {
            this.spendTime = spendTime;
        }
        public String getSpendTime() {
            return spendTime;
        }

        public void setScore(int score) {
            this.score = score;
        }
        public int getScore() {
            return score;
        }

        public void setState(int state) {
            this.state = state;
        }
        public int getState() {
            return state;
        }

        public void setStateText(String stateText) {
            this.stateText = stateText;
        }
        public String getStateText() {
            return stateText;
        }
}
