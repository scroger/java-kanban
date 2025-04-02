package exceptions.http;

public class BadRequestHttpException extends HttpException {

    public BadRequestHttpException() {
        super(400, "Bad Request");
    }

}
