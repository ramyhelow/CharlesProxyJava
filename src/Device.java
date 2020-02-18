public class Device {

    private int port;
    private String speed;
    private int pid;

    public int getPort() {
        return this.port;
    }

    public String getSpeed() {
        return this.speed;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(int newPid) {
        this.pid = newPid;
    }

    public void setPort(int newPort) {
        this.port = newPort;
    }

    public void setSpeed(String newSpeed) {
        this.speed = newSpeed;
    }

}
