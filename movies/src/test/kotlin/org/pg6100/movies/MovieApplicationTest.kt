package org.pg6100.movies

//import org.junit.Test
//import org.junit.runner.RunWith
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.web.server.LocalServerPort
//import org.springframework.test.context.junit4.SpringRunner
//
//@RunWith(SpringRunner::class)
//@SpringBootTest(classes = [(MovieApplication::class)],
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieApplicationTest
{
//    @LocalServerPort
//    protected var port = 0
//
//    @Test
//    fun test()
//    {
//
//    }


//    @Before
//    @After
//    fun clean()
//    {
//        // RestAssured configs shared by all the tests
//        RestAssured.baseURI = "http://localhost"
//        RestAssured.port = port
//        RestAssured.basePath = "/newsrest/api/news"
//        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
//
//        val list = RestAssured.given().accept(ContentType.JSON).get()
//                .then()
//                .statusCode(200)
//                .extract()
//                .`as`(Array<MovieDto>::class.java)
//                .toList()
//
//
//        list.stream().forEach {
//            RestAssured.given().pathParam("id", it.movieId)
//                    .delete("/{id}")
//                    .then()
//                    .statusCode(204)
//        }
//
//        RestAssured.given().get()
//                .then()
//                .statusCode(200)
//                .body("size()", CoreMatchers.equalTo(0))
//    }
//
//    @Test
//    fun testCleanDB()
//    {
//        RestAssured
//                .given()
//                .get()
//                .then()
//                .statusCode(200)
//                .body("size()", CoreMatchers.equalTo(0))
//    }
}