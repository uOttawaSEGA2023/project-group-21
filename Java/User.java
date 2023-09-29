public class User {

    private String firstName, lastName, email, password, confirmPassword, address, phoneNumber;

    // Constructor for User
    public Person(String firstName, String lastName, String email, String password, String confirmPassword, String phoneNumber, String
            address){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phoneNumber = phoneNumber;
        this.address = address;

    }

    public boolean checkPassword (String password, String checkPassword){
        return password == checkPassword;
    }

    // Main method
    public static void main (String [] args){

    }

}