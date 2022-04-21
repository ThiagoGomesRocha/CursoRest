package rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;


import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.internal.path.xml.NodeImpl;

public class UtilizandoVariaveisEstaticas {
	
	@BeforeClass
	public static void init(){
		RestAssured.baseURI = "https://restapi.wcaquino.me";
		RestAssured.port = 443;
//		RestAssured.basePath = "";
	}
	
	@Test
	public void deveTrabalharComXml() {
		
		given()
			.log().all()
		.when()
			.get("/usersXML/3")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("user.name",is("Ana Julia"))
			// para pegar atributo tem que colocar o @
			.body("user.@id", is("3"))
			.body("user.filhos.name.size()", is(2))
			.body("user.filhos.name[0]", is("Zezinho"))
			.body("user.filhos.name[1]", is("Luizinho"))
			.body("user.filhos.name", hasItems("Zezinho","Luizinho"))
			;
	}
	
	@Test
	public void devePassaroNoRaiz() {
		given()
		.log().all()
		.when()
			.get("https://restapi.wcaquino.me/usersXML/3")
		.then()
			.statusCode(200)
			.rootPath("user")
			.body(is(not(nullValue())))
			.body("name",is("Ana Julia"))
			.body("@id", is("3"))
			//passa o no raiz, para não ter que repetir
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			//retira do rootpah
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			//adiciona no rootpath
			.appendRootPath("filhos")
			.body("name", hasItems("Zezinho","Luizinho"))
			;
	}
	
	@Test
	public void deveFazerPesquisaAcanvacaComXml() {
		given()
		.log().all()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("users.user.size()",is(3))
			// it utilizado para interação dentro da arrow function
			.body("user.user.findAll{it.age.toInteger()>=25}.size()", is(2))
			.body("users.user.@id", hasItems("1","2","3"))
			.body("users.user.find{it.age.toInteger()==25}.name", containsString("Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina","Ana Julia"))
			.body("users.user.salary.find{it != null}", is("1234.5678"))
			.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
			.body("users.user.age.collect{it.toInteger()*2}", hasItems(40,50,60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
			.body("users.user.name.findAll{it.toString()}.collect{it.toString().toUpperCase()}", hasItems("JOÃO DA SILVA","MARIA JOAQUINA", "ANA JULIA"))
			;
			
	}
	
	@Test
	public void deveFazerPesquisaAcanvacaComXmlEJava() {
		String name = given().log().all()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("users.user.size()",is(3))
			// it utilizado para interação dentro da arrow function
			.body("user.user.findAll{it.age.toInteger()>=25}.size()", is(2))
			.body("users.user.@id", hasItems("1","2","3"))
			.body("users.user.find{it.age.toInteger()==25}.name", containsString("Joaquina"))
			.extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");
			
			;
			assertEquals("Maria Joaquina".toUpperCase(), name.toUpperCase());
	}
	
	@Test
	public void deveFazerPesquisaAcanvacaComXmlEJava2() {
		ArrayList<String> names = given().log().all()
		.when()
			.get("https://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("users.user.size()",is(3))
			// it utilizado para interação dentro da arrow function
			.body("user.user.findAll{it.age.toInteger()>=25}.size()", is(2))
			.body("users.user.@id", hasItems("1","2","3"))
			.body("users.user.find{it.age.toInteger()==25}.name", containsString("Joaquina"))
			.extract().path("users.user.name.findAll{it.toString()}.collect{it.toString().toUpperCase()}");
			
			;
			assertEquals(3, names.size());
			assertEquals("joão da silva".toUpperCase(), names.get(0));
			assertEquals("maria joaquina".toUpperCase(), names.get(1));
			assertEquals("ana julia".toUpperCase(), names.get(2));
	}
	
	@Test
	public void deveFazerPesquisaAcanvacaComXmlEJava3() {
		ArrayList<NodeImpl> names = given().log().all()
		.when()
			.get("https://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("users.user.size()",is(3))
			// it utilizado para interação dentro da arrow function
			.body("user.user.findAll{it.age.toInteger()>=25}.size()", is(2))
			.body("users.user.@id", hasItems("1","2","3"))
			.body("users.user.find{it.age.toInteger()==25}.name", containsString("Joaquina"))
			.extract().path("users.user.name.findAll{it.toString().contains('n')}");
			
			;
			assertEquals(2, names.size());
			assertEquals("maria joaquina".toUpperCase(), names.get(0).toString().toUpperCase());
			assertEquals("ana julia".toUpperCase(), names.get(1).toString().toUpperCase());
	}
	
	
	@Test
	public void deveFazerPesquisaAcanvacaComXpath() {
		given()
			.log().all()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body(hasXPath("count(/users/user)", is("3")))
			.body(hasXPath("/users/user[@id='1']"))
			.body(hasXPath("//user[@id='1']"))
			.body(hasXPath("//name[text()='Luizinho']/../../name", is("Ana Julia")))
			.body(hasXPath("//name[text()='Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"),containsString("Luizinho"))))
			.body(hasXPath("//name",is("João da Silva")))
			.body(hasXPath("//user[2]/name",is("Maria Joaquina")))
			.body(hasXPath("//user[last()]/name",is("Ana Julia")))
			.body(hasXPath("count(/users/user/name[contains(.,'n')])",is("2")))
			.body(hasXPath("//user[age < 24]/name",is("Ana Julia")))
			.body(hasXPath("//user[age > 20 and age < 30]/name",is("Maria Joaquina")))
			.body(hasXPath("//user[age > 20] [age < 30]/name",is("Maria Joaquina")))
			;
	}
	

}
