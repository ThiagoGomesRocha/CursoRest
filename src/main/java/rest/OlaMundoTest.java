package rest;
// impor estatico com * importa todos os metodos
import static io.restassured.RestAssured.*;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {
	
	
	@Test
	public void testOlaMundo() {
		Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
		Assert.assertEquals(response.getBody().asString(), "Ola Mundo!");
		Assert.assertTrue("O status code deve ser 200", response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}

	@Test
	public void formaFluenteDeUtilizar() {
		RestAssured.get("https://restapi.wcaquino.me/ola").then().statusCode(200);
	}
	
	@Test
	public void comStaticImport() {
		get("https://restapi.wcaquino.me/ola").then().statusCode(200);
	}
	
	@Test
	public void modoFluente() {
		given() // pré condições
		.when() // ações
			.get("https://restapi.wcaquino.me/ola")
		.then() //assertivas
			.statusCode(200);
		
	}
}
