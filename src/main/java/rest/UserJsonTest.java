package rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class UserJsonTest {
	
	@Test
	public void deveVerificarPrimeiroNivel() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("id", is(1))
			.body("name",containsString("Silva"))
			.body("age",greaterThan(18))
			;
	}
	
	@Test
	public void deveVerificarPrimeiroNivelOutrasFormas() {
		Response response = RestAssured.request(Method.GET,"https://restapi.wcaquino.me/users/1");
		
		//path
		assertEquals(1, response.path("id"));
		assertEquals(1, response.path("%s","id")); //%s para caso seja string como parametro
		
		//jsonPath
		JsonPath jpath = new JsonPath(response.asString());
		assertEquals(1, jpath.getInt("id"));
		
		//from do json path
		int id = JsonPath.from(response.asString()).getInt("id");
		assertEquals(1, id);
	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("name",containsString("Joaquina"))
			.body("endereco.rua",is("Rua dos bobos"))
			;
	}
	
	@Test
	public void deveVerificarLista() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("name",containsString("Ana"))
			.body("filhos",hasSize(2))
			.body("filhos[0].name",is("Zezinho"))
			.body("filhos[1].name",is("Luizinho"))
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho","Luizinho"))
			
		;
	}
	
	@Test
	public void deveRetornarErrorUsuarioInexistente() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body(is(not(nullValue())))
			.body("error",is("Usuário inexistente"))
		;
	}
	
	@Test
	public void deveVerificarListaRaiz() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("$", hasSize(3))
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("age[1]",is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho","Luizinho")))
			.body("salary", contains(1234.5678f,2500,null))
		;
	}
	
	@Test
	public void deveRealizarVerificacoesAvancadas() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()",is(2))
			.body("age.findAll{it <= 25 && it> 20}.size()",is(1))
			.body("findAll{it.age <= 25 && it.age> 20}.name",hasItem("Maria Joaquina"))
			.body("findAll{it.age <= 25}[0].name",is("Maria Joaquina"))
			.body("findAll{it.age <= 25}[-1].name",is("Ana Júlia"))
			.body("findAll{it.name.contains('n')}.name",hasItems("Maria Joaquina","Ana Júlia"))
			.body("findAll{it.name.length()>10}.name",hasItems("João da Silva","Maria Joaquina"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}",hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()",allOf(arrayContaining("MARIA JOAQUINA"),arrayWithSize(1)))
			.body("age.collect{it *2}", hasItems(60,50,40))
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f,0.001)))
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3700d), lessThan(3800d)))
		;
	}
	
	@Test
	public void devoUnirJsonPathJava() {
		ArrayList<String> names = 
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.extract().path("name.findAll{it.startsWith('Maria')}")
		;
		assertEquals(1, names.size());
		assertTrue(names.get(0).equalsIgnoreCase("mAria Joaquiina"));
		assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
	}


}
