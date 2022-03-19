package rest;
// impor estatico com * importa todos os metodos
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
	
	@Test
	public void devoConhecerMatchersHamcrest() {
		assertThat("Maria", Matchers.is("Maria"));
		assertThat(128, Matchers.is(128));
		assertThat(128, Matchers.isA(Integer.class));
		assertThat(128d, Matchers.isA(Double.class));
		assertThat(128d, Matchers.greaterThan(120d));
		assertThat(128d, Matchers.lessThan(130d));
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		assertThat(impares, hasSize(5));
		assertThat(impares, containsInAnyOrder(1,5,3,7,9));
		assertThat(impares, hasItem(1));
		assertThat(impares, hasItems(1,5));
		
		assertThat("Maria", not("João"));
		assertThat("Maria", anyOf(is("Maria"),is("Ana")));
		assertThat("Joaquina", allOf(startsWith("J"),endsWith("a"), containsString("aqui")));
	}
	
	@Test
	public void devoValidarBody() {
		given() 
		.when() 
			.get("https://restapi.wcaquino.me/ola")
		.then() 
			.statusCode(200)
			.body(is(not(nullValue())))
			.body(containsString("Mundo"))
			.body(is("Ola Mundo!"));
	}
	
}
