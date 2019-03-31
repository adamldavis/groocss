package micronaut.example

import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.hateos.JsonError
import io.micronaut.http.hateos.Link
import io.micronaut.views.View
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class Control {

    Logger logger = LoggerFactory.getLogger(Control.class)

    @Get("/")
    HttpResponse root() {
        HttpResponse.permanentRedirect new URI("/index")
    }

    @View("index")
    @Produces("text/html; charset=utf-8")
    @Get("/index")
    Publisher<HttpResponse<Map>> index() {
        Flowable.just(HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "adavis")))
    }

    @Error(global = true)
    HttpResponse<JsonError> error(HttpRequest request, Throwable e) {
        logger.error("Rendering error: $e.message")

        JsonError error = new JsonError("Bad Things Happened: " + e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()))

        HttpResponse.<JsonError>serverError().body(error)
    }

}