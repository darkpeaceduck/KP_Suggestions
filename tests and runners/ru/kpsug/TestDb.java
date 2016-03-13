package ru.kpsug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.DBOperator.FilmNotFoundException;
import ru.kpsug.db.Film;

public class TestDb {
	private DBOperator openConnection() throws SQLException {
		DBOperator dbOperator = new DBOperator(null);
		dbOperator.connect();
		return dbOperator;
	}

	@Ignore
	@Test
	public void testCreateDb() throws SQLException {
		DBOperator dbOperator = openConnection();
		dbOperator.buildDatabase();
	}

	@Test
	public void testSelectDeleteInsertFilm() throws SQLException, FilmNotFoundException {
		DBOperator dbOperator = openConnection();
		String intersterllarId = "258687";

		Film film = dbOperator.selectFilm(intersterllarId);

		dbOperator.deleteFilmFromId(intersterllarId);

		try {
			dbOperator.selectFilm(intersterllarId);
			fail();
		} catch (FilmNotFoundException e) {
		}

		dbOperator.insertFilm(film);

		assertEquals(film, dbOperator.selectFilm(intersterllarId));
	}

	@Test
	public void testSelectUpdatetFilm() throws SQLException, FilmNotFoundException {
		DBOperator dbOperator = openConnection();
		String intersterllarId = "258687";

		Film film = dbOperator.selectFilm(intersterllarId);

		String savedName = film.getName();
		film.setName("Petrovich");

		dbOperator.updateFilm(film);
		assertEquals(film, dbOperator.selectFilm(intersterllarId));

		film.setName(savedName);
		dbOperator.updateFilm(film);
		assertEquals(film, dbOperator.selectFilm(intersterllarId));
	}

	@Test(expected = FilmNotFoundException.class)
	public void testSelectDefunctFilm() throws SQLException, FilmNotFoundException {
		DBOperator dbOperator = openConnection();
		String failId = "25868712312312313123123123";
		dbOperator.selectFilm(failId);
	}
}
