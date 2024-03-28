public class Epic extends Task {
    private String epicName;
    private int idEpic;
    private SubTask subTask;

    Epic(String newEpicName) {
        epicName = newEpicName;

    }

    @Override
    public int hashCode() {
        int code = 7;

        if (epicName != null) {
            code += epicName.hashCode();
        }

        code *= 37;

        return code;
    }
}
