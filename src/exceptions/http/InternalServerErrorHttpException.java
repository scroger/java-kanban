package exceptions.http;

public class InternalServerErrorHttpException extends HttpException {

    public InternalServerErrorHttpException() {
        super(500, "Internal Server Error");
    }
}
