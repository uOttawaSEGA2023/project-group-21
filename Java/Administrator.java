public class Administrator extends User {
        public Administrator(String email, String password){
            super(email, password);
        }

        public static void main (String [] args){
            Administrator administrator = new Administrator("admin@docappoint.com", "docappoint");
        }

}