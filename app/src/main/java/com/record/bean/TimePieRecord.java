package com.record.bean;

public class TimePieRecord {
    public float SCALE = 12.0f;
    private int color;
    private String endTime;
    private int startAngle;
    private String startTime;
    private int sweepAngle;

    public TimePieRecord(String startTime, String endTime, int color) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        calculate();
    }

    public TimePieRecord(String startTime, String endTime, int color, int scale) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        this.SCALE = (float) scale;
        calculate();
    }

    public TimePieRecord(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = -16711681;
        calculate();
    }

    private void calculate() {
        if (this.startTime != null && !this.startTime.equals(this.endTime)) {
            try {
                int indexSpace = this.startTime.indexOf(" ");
                int indexColon = this.startTime.indexOf(":");
                int indexLastColon = this.startTime.lastIndexOf(":");
                Float startHour = Float.valueOf(Float.parseFloat(this.startTime.substring(indexSpace + 1, indexColon)));
                Float startMinute = Float.valueOf(Float.parseFloat(this.startTime.substring(indexColon + 1, indexLastColon)));
                Float endHour = Float.valueOf(Float.parseFloat(this.endTime.substring(indexSpace + 1, indexColon)));
                Float endMinute = Float.valueOf(Float.parseFloat(this.endTime.substring(indexColon + 1, indexLastColon)));
                if (startHour.floatValue() >= this.SCALE) {
                    startHour = Float.valueOf(startHour.floatValue() - this.SCALE);
                }
                if (endHour.floatValue() >= this.SCALE) {
                    endHour = Float.valueOf(endHour.floatValue() - this.SCALE);
                }
                float startAngle = (startHour.floatValue() * (360.0f / this.SCALE)) + (startMinute.floatValue() * ((360.0f / this.SCALE) / 60.0f));
                float endAngle = (endHour.floatValue() * (360.0f / this.SCALE)) + (endMinute.floatValue() * ((360.0f / this.SCALE) / 60.0f));
                this.startAngle = (int) (startAngle - 90.0f);
                if (this.startAngle < 0) {
                    this.startAngle += 360;
                }
                this.sweepAngle = (int) (endAngle - startAngle);
                if (this.sweepAngle < 0) {
                    this.sweepAngle += 360;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getColor() {
        return this.color;
    }

    public int getStartAngle() {
        return this.startAngle;
    }

    public int getSweepAngle() {
        return this.sweepAngle;
    }
}
