package sample;
public class Passenger
{
    private String surname;
    private String firstName;
    private int queueNo;
    private Integer seatNo;

    public Passenger() {
    }

    public Passenger(String surname, String firstName) {
        this.surname = surname;
        this.firstName = firstName;
    }

    public Passenger(String surname, String firstName, int queueNo) {
        this.surname = surname;
        this.firstName = firstName;
        this.queueNo = queueNo;
    }


    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(int queueNo) {
        this.queueNo = queueNo;
    }

    public Integer getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(Integer seatNo) {
        this.seatNo = seatNo;
    }
}
