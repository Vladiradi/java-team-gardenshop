package telran.project.gardenshop.exception;

public class InvalidGroupByPeriodException extends RuntimeException {

    public InvalidGroupByPeriodException(String message) {
        super(message);
    }

    public InvalidGroupByPeriodException(String value, String validValues) {
        super(String.format("Invalid group by period: '%s'. Valid values are: %s", value, validValues));
    }
}
