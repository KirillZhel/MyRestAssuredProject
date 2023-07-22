package in.reqres;

import dto.*;
import io.restassured.http.ContentType;
import io.restassured.internal.common.assertion.Assertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String url = "https://reqres.in/";

    @Test
    public void checkAvatarAndIdTest() {
        Specifications.installSpecifications(Specifications.requestSpec(url), Specifications.responseSpec(200));
        List<UserData> users = given()
                .when()
                //.contentType(ContentType.JSON)
                .get("api/users?page=2")
                .then().log().all()
                .extract()
                .body()
                .jsonPath()
                .getList("data", UserData.class);

        users.forEach(u -> Assertions.assertTrue(u.getAvatar().contains(u.getId().toString())));
        Assertions.assertTrue(users.stream().allMatch(u -> u.getEmail().endsWith("@reqres.in")));
    }

    @Test
    public void successRegistrationTest() {
        Specifications.installSpecifications(Specifications.requestSpec(url), Specifications.responseSpec(200));

        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessRegistration expectedReg = new SuccessRegistration(4, "QpwL5tke4Pnpja7X4");

        SuccessRegistration actualReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract()
                .as(SuccessRegistration.class);

        Assertions.assertEquals(expectedReg.getId(), actualReg.getId());
        Assertions.assertEquals(expectedReg.getToken(), actualReg.getToken());
    }

    @Test
    public void unsuccessRegistrationTest() {
        Specifications.installSpecifications(Specifications.requestSpec(url), Specifications.responseSpec(400));

        Register user = new Register("sydney@fife", "");
        SuccessRegistration expectedReg = new SuccessRegistration(4, "QpwL5tke4Pnpja7X4");

        UnsuccesRegistratin actualReg = given()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract()
                .as(UnsuccesRegistratin.class);

        Assertions.assertEquals("Missing password", actualReg.getError());
    }

    @Test
    public void sortedYearsTest() {
        Specifications.installSpecifications(Specifications.requestSpec(url), Specifications.responseSpec(200));

        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract()
                .body()
                .jsonPath()
                .getList("data", ColorsData.class);

        List<Integer> years = colors.stream().map(ColorsData::getYear).toList();
        List<Integer> sortedYears = years.stream().sorted().toList();

        Assertions.assertEquals(sortedYears, years);
    }

    @Test
    public void deleteUserTest() {
        Specifications.installSpecifications(Specifications.requestSpec(url), Specifications.responseSpec(204));

        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void timeTest() {
        Specifications.installSpecifications(Specifications.requestSpec(url), Specifications.responseSpec(200));

        UserTime user = new UserTime("morpheus", "zion resident");

        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract()
                .as(UserTimeResponse.class);

        String regex = "(.{5})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");

        Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
    }
}
