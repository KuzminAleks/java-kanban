package SaveExceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {

    }

    public ManagerSaveException(String par) {
        System.out.println(par);
    }
}