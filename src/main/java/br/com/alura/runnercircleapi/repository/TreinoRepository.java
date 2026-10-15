package br.com.alura.runnercircleapi.repository;

import br.com.alura.runnercircleapi.model.Treino;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    @Query(value = "SELECT t FROM Treino t JOIN FETCH t.autor WHERE LOWER(t.descricao) LIKE LOWER(:buscaPattern)",
            countQuery = "SELECT COUNT(t) FROM Treino t JOIN t.autor WHERE LOWER(t.descricao) LIKE LOWER(:buscaPattern)")
    Page<Treino> findAllComAutor(@Param("buscaPattern") String buscaPattern, Pageable pageable);

    List<Treino> findByAutorId(Long autorId);
}
