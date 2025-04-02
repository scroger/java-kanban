package exceptions.http;

public class NotAcceptableHttpException extends HttpException {

    public NotAcceptableHttpException() {
        super(406, "Not Acceptable");
    }

}
