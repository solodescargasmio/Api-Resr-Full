package com.books.integrate.spring.react.repository;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.books.integrate.spring.react.model.Tutorial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
	List<Tutorial> findByPublished(boolean published);
	List<Tutorial> findByTitleContaining(String title);
@Transactional
	@Modifying
	@Query(value = "UPDATE tutorials SET description=:descripcion, published=:published where title=:title",
nativeQuery = true
)
  void actTutorial(@Param("descripcion") String descripcion,@Param("published") Boolean published,@Param("title") String title);

  @Query(value = "Select precio From tutorials where id=:id", nativeQuery=true)
  Double consultaPrecio(@Param("id") Integer id);

  @Query(value = "Select * From tutorials Where title=:title", nativeQuery = true)
  List<Tutorial> consultarPrecioPorTitulo(@Param("title") String title);

}
