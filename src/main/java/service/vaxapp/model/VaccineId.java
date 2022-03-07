package service.vaxapp.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class VaccineId implements Serializable {
    private Integer doseNumber;
    private Integer userPPS;

    public VaccineId() {
    }

    public VaccineId(Integer doseNumber, Integer userPPS) {
        this.doseNumber = doseNumber;
        this.userPPS = userPPS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VaccineId vaccineId = (VaccineId) o;
        return doseNumber.equals(vaccineId.doseNumber) &&
                userPPS.equals(vaccineId.userPPS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doseNumber, userPPS);
    }

    public Integer getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(Integer doseNumber) {
        this.doseNumber = doseNumber;
    }

    public Integer getUserPPS() {
        return userPPS;
    }

    public void setUserPPS(Integer userPPS) {
        this.userPPS = userPPS;
    }
}
