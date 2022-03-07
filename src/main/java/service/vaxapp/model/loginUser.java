package service.vaxapp.model;

public class loginUser {
    private String email;
    private String PPS;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPPS() {
        return PPS;
    }

    public void setPPS(String PPS) {
        this.PPS = PPS;
    }

    public loginUser(String email, String PPS) {
        this.email = email;
        this.PPS = PPS;
    }
}
