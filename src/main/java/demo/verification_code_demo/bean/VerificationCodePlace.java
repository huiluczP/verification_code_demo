package demo.verification_code_demo.bean;

public class VerificationCodePlace {
    private String backName;
    private String markName;
    private int xLocation;
    private int yLocation;

    public VerificationCodePlace(){}
    public VerificationCodePlace(String backName, String markName, int xLocation, int yLocation){
        this.backName = backName;
        this.markName = markName;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public String getBackName() {
        return backName;
    }

    public void setBackName(String backName) {
        this.backName = backName;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(int yLocation) {
        this.yLocation = yLocation;
    }
}
