package exceptions.http;

public class NotFoundHttpException extends HttpException {

    public NotFoundHttpException() {
        super(404, "Not Found");
    }

}
