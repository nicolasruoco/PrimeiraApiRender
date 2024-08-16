package com.example.apidata.repository;

import com.example.apidata.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto,Long> {
    Long deleteProdutoById(Long id);

    @Modifying
    @Query ("DELETE FROM Produto obj WHERE :id = obj.id")
    void deleteById(Long id);

    List<Produto> findByNomeLikeIgnoreCase(String nome);
    int countByQuantidadeEstoqueIsLessThanEqual(double qt);
    void deleteProdutoByDescricaoContains(String txt);


}
