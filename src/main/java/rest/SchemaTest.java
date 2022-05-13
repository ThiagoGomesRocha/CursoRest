package rest;

import static io.restassured.RestAssured.*;

import org.junit.Test;
import io.restassured.module.jsv.*;
import io.restassured.matcher.RestAssuredMatchers;

public class SchemaTest {

	@Test
	public void deveValidarSchemaXml() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(200)
			.body(RestAssuredMatchers.matchesXsdInClasspath("user.xsd"))
		;
	}
	
	@Test(expected=org.xml.sax.SAXParseException.class)
	public void naoDeveValidarSchemaXmlInvalido() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/invalidusersXML")
		.then()
			.log().all()
			.statusCode(200)
			.body(RestAssuredMatchers.matchesXsdInClasspath("user.xsd"))
		;
	}
	
	@Test
	public void deveValidarSchemaJson() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(200)
			.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("user.json"))
		;
		
	}
}
