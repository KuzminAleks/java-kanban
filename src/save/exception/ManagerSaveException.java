package save.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {

    }

    public ManagerSaveException(String par) {
        System.out.println(par);
    }
}