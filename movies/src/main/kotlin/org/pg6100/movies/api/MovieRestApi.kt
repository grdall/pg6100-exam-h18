package org.pg6100.movies.api

import com.google.common.base.Throwables
import io.swagger.annotations.*
import org.pg6100.movies.MoviesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException
import org.pg6100.movies.dto.MovieConverter
import org.pg6100.movies.dto.MovieDto
import java.time.ZonedDateTime

const val ID_PARAM = "The numeric id of the movie"
const val MOVIES_JSON = "application/vnd.pg6100.movies+json;charset=UTF-8;version=1"
const val BASE_JSON = "application/json;charset=UTF-8"

@Api(value = "/movies", description = "Handling of creating and retrieving movies")
@RequestMapping(
        path = ["/movies"],
        produces = [MOVIES_JSON, BASE_JSON]
)
@RestController
class MovieRestApi
{
    @Autowired
    private lateinit var crud: MoviesRepository

    @ApiOperation("Get all the movies")
    @GetMapping
    fun get(@ApiParam("The title of the movie")
            @RequestParam("title")
            title: String?,

            @ApiParam("The name of the director")
            @RequestParam("director")
            director: String?,

            @ApiParam("The category")
            @RequestParam("category")
            category: String?,

            @ApiParam("The start of the screening")
            @RequestParam("screeningFromTime")
            screeningFromTime: ZonedDateTime?,

            @ApiParam("The end of the screening")
            @RequestParam("screeningToTime")
            screeningToTime: ZonedDateTime?

    ): ResponseEntity<List<MovieDto>>
    {

        val list = if(title.isNullOrBlank() && director.isNullOrBlank()
                && category.isNullOrBlank())
        {
            crud.findAll()
        }
        else if(!title.isNullOrBlank())
        {
            crud.findAllByTitle(title!!)
        }
        else if(!director.isNullOrBlank())
        {
            crud.findAllByDirector(director!!)
        }
        else //if(!category.isNullOrBlank())
        {
            crud.findAllByCategory(category!!)
        }

        return ResponseEntity.ok(MovieConverter.transform(list))
    }

    @ApiOperation("Create a movie")
    @PostMapping(consumes = [MOVIES_JSON, BASE_JSON])
    @ApiResponse(code = 201, message = "The id of newly created movie")
    fun createMovie(
            @ApiParam("Title, director, category, screeningFromTime, screeningToTime of movie")
            @RequestBody
            dto: MovieDto)
            : ResponseEntity<Long>
    {

        if (!dto.movieId.isNullOrEmpty())
        {
            //Cannot specify id for a newly generated news
            return ResponseEntity.status(400).build()
        }

        if (dto.title == null || dto.director == null || dto.category == null
                || dto.screeningFromTime == null || dto.screeningToTime == null)
        {
            return ResponseEntity.status(400).build()
        }

        val id: Long?
        try
        {
            id = crud.createMovie(dto.title!!, dto.director!!, dto.category!!
                    , dto.screeningFromTime!!, dto.screeningToTime!!)
        }
        catch (e: Exception)
        {
            if(Throwables.getRootCause(e) is ConstraintViolationException)
            {
                return ResponseEntity.status(400).build()
            }
            throw e
        }

        return ResponseEntity.status(201).body(id)
    }


    @ApiOperation("Get a single movie specified by id")
    @GetMapping(path = ["/{id}"])
    fun getMovie(@ApiParam(ID_PARAM)
                @PathVariable("id")
                pathId: String?)
            : ResponseEntity<MovieDto>
    {
        val id: Long
        try
        {
            id = pathId!!.toLong()
        }
        catch (e: Exception)
        {
            return ResponseEntity.status(404).build()
        }

        val dto = crud.findById(id).orElse(null) ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(MovieConverter.transform(dto))
    }


    @ApiOperation("Update an existing movie entry")
    @PutMapping(path = ["/{id}"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun updateMovie(
            @ApiParam(ID_PARAM)
            @PathVariable("id")
            pathId: String?,
            //
            @ApiParam("The movie that will replace the old one. Cannot change its id though.")
            @RequestBody
            dto: MovieDto
    ): ResponseEntity<Any>
    {
        val dtoId: Long
        try
        {
            dtoId = getMovieId(dto)!!.toLong()
        }
        catch (e: Exception) {
            /*
                invalid id. But here we return 404 instead of 400,
                as in the API we defined the id as string instead of long
             */
            return ResponseEntity.status(404).build()
        }

        if (getMovieId(dto) != pathId)
        {
            // Not allowed to change the id of the resource (because set by the DB).
            // In this case, 409 (Conflict) sounds more appropriate than the generic 400
            return ResponseEntity.status(409).build()
        }

        if (!crud.existsById(dtoId))
        {
            //Here, in this API, made the decision to not allow to create a news with PUT.
            // So, if we cannot find it, should return 404 instead of creating it
            return ResponseEntity.status(404).build()
        }

        if (dto.title == null || dto.director == null || dto.category == null
                || dto.screeningFromTime == null || dto.screeningToTime == null)
        {
            return ResponseEntity.status(400).build()
        }

        try
        {
            crud.update(dtoId, dto.title!!, dto.director!!, dto.category!!,
                    dto.screeningFromTime!!, dto.screeningToTime!!)
        }
        catch (e: Exception)
        {
            if(Throwables.getRootCause(e) is ConstraintViolationException) {
                return ResponseEntity.status(400).build()
            }
            throw e
        }

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Update the title content of an existing movie")
    @PutMapping(path = ["/{id}/title"], consumes = [(MediaType.TEXT_PLAIN_VALUE)])
    fun updateTitle(
            @ApiParam(ID_PARAM)
            @PathVariable("id")
            id: Long?,
            //
            @ApiParam("The new text which will replace the old one")
            @RequestBody
            title: String
    ): ResponseEntity<Any>
    {
        if (id == null)
        {
            return ResponseEntity.status(400).build()
        }

        if (!crud.existsById(id))
        {
            return ResponseEntity.status(404).build()
        }

        try
        {
            crud.updateTitle(id, title)
        }
        catch (e: Exception)
        {
            if(Throwables.getRootCause(e) is ConstraintViolationException)
            {
                return ResponseEntity.status(400).build()
            }
            throw e
        }

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Delete a movie with the given id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteMovie(@ApiParam(ID_PARAM)
               @PathVariable("id")
               pathId: String?): ResponseEntity<Any>
    {

        val id: Long
        try
        {
            id = pathId!!.toLong()
        }
        catch (e: Exception)
        {
            return ResponseEntity.status(400).build()
        }

        if (!crud.existsById(id))
        {
            return ResponseEntity.status(404).build()
        }

        crud.deleteById(id)
        return ResponseEntity.status(204).build()
    }

    private fun getMovieId(dto: MovieDto): String?
    {
        return dto.movieId
    }
}