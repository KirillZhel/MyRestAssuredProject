package in.reqres;

import dto.UserData;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String url = "https://reqres.in/";

    @Test
    public void checkAvatarAndIdTest() {
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get("https://reqres.in/api/users?page=2")
                .then().log().all()
                .extract()
                .body()
                .jsonPath()
                .getList("data", UserData.class);
    }
}
