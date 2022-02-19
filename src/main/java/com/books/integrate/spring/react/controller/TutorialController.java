package com.books.integrate.spring.react.controller;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.books.integrate.spring.react.model.Tutorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books.integrate.spring.react.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class TutorialController {

	@Autowired
	TutorialRepository tutorialRepository;

	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
		try {
			List<Tutorial> tutorials = new ArrayList<Tutorial>();

			if (title == null)
				tutorialRepository.findAll().forEach(tutorials::add);
			else
				tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/tutorials/precio/{id}")
	public String getPrecioPorId(@PathVariable("id") Integer id) {
		//Optional<Tutorial> tutorialData = tutorialRepository.findById(id);
		
		try {
			Double precio= tutorialRepository.consultaPrecio(id);
			if(precio>0){
				return "El precio del curso es de "+precio;
			}else{
				return "El tutorial no tiene precio asignado";
			}
		} catch (Exception e) {
			return "TUTORIAL NO EXISTE";
		}
	}

	@GetMapping("/tutorials/precio/")
	public ResponseEntity<List<String>> getPrecioPorTitle(@RequestParam String title) {
		try {
			List<Tutorial> tutoriales = tutorialRepository.consultarPrecioPorTitulo(title);
			List<String> lista=new ArrayList<String>();
			for (Tutorial tutos : tutoriales) {
				lista.add("El tutorial con id "+tutos.getId()+" tiene un precio de "+tutos.getPrecio());
			}
			return new ResponseEntity<>(lista,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/tutorials/{title}")
	public ResponseEntity<List<Tutorial>> getTutorialByTitles(@PathVariable("title") String title) {
		try {
			List<Tutorial> tutorials = tutorialRepository.findByTitleContaining(title);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

 	@PostMapping("")
	public String actualizarTutorials(@RequestParam String description,@RequestParam Boolean published,@RequestParam String title) {	
	try {
			tutorialRepository.actTutorial(description, published,title); 
			return "TUTORIAL ACTUALIZADO CON EXITO"+description+"   "+title;
		} catch (Exception e) {
			return "ERROR AL ACTUALIZAR TUTORIAL "+e.getMessage();
		}
	}


	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {	
	try {
			Tutorial _tutorial = tutorialRepository.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(),  false));
			return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}



	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			Tutorial _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());
			return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

//HttpStatus
	@DeleteMapping("/tutorials/{id}")
	public ResponseEntity<String> deleteTutorial(@PathVariable("id") long id) {
		try {
			tutorialRepository.deleteById(id);
				return new ResponseEntity<>("Tutorials DELETE!! ",HttpStatus.NO_CONTENT);
			} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@DeleteMapping("/{title}")
	public String  deleteTutorialPorTitle(@PathVariable String title) throws Exception{
	
		try {
			List<Tutorial> tutos=tutorialRepository.findByTitleContaining(title);
			for (Tutorial tutorial : tutos) {
				tutorialRepository.deleteById(tutorial.getId());
			}
			
				return "Tutorials DELETE!! ";
			} catch (Exception e) {
			return "ERROR AL BORRAR TUTORIAL";
		}
	}

	@DeleteMapping("/tutorials")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			tutorialRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}

	}

	@GetMapping("/tutorials/published")
	public ResponseEntity<List<Tutorial>> findByPublished() {
		try {
			List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

}
