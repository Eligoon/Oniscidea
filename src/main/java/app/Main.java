package app;



import app.dtos.UserDTO;
import io.javalin.Javalin;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Javalin app =
                Javalin.create(config -> {

                    config.routes.get(
                            "/",
                            ctx -> ctx.result(
                                    "Training API is running"
                            )
                    );

                    config.routes.get(
                            "/test-json",
                            ctx -> ctx.json(
                                    new UserDTO(
                                            1,
                                            "Test User",
                                            "test@test.com",
                                            null
                                    )
                            )
                    );

                }).start(7070);
        }
    }

