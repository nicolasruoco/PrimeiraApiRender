package com.example.apidata.service;

import com.example.apidata.model.Produto;
import com.example.apidata.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final Validator validador;

    public ProdutoService(ProdutoRepository produtoRepository, Validator validador) {
        this.produtoRepository = produtoRepository;
        this.validador = validador;
    }

    public List<Produto> buscarTodosProdutos(){
        return produtoRepository.findAll();
    }

    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }

    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Produto Não Encontrado"));
    }

    @Transactional
    public Produto excluirProduto(long id){
        Produto produto = buscarProdutoPorId(id);
        produtoRepository.deleteById(id);
        return produto;
    }

    public ResponseEntity<String> atualizarProduto(Long id, Produto produtoAtualizado) {
        Produto produto = produtoAtualizado;
        salvarProduto(produto);
        return ResponseEntity.ok("Produto atualizado com sucesso");
    }

    public ResponseEntity<String> atualizarProdutoParcial(Produto produto, Map<String, Object> updates){
        try{
            if (updates.containsKey("nome")) {
                produto.setNome((String) updates.get("nome"));
            }
            if (updates.containsKey("descricao")) {
                produto.setDescricao((String) updates.get("descricao"));
            }
            if (updates.containsKey("preco")) {
                produto.setPreco(Double.parseDouble(String.valueOf(updates.get("preco"))));
            }
            if (updates.containsKey("quantidadeEstoque")) {
                produto.setQuantidadeEstoque((Integer) updates.get("quantidadeEstoque"));
            }

            DataBinder binder = new DataBinder(produto);
            binder.setValidator(validador);
            binder.validate();
            BindingResult result = binder.getBindingResult();

            if(result.hasErrors()){

                Map<String, String> erros = new HashMap<>();
                for (FieldError erro : result.getFieldErrors()) {
                    // Coloque o nome do campo e a mensagem de erro no mapa
                    erros.put(erro.getField(), erro.getDefaultMessage());
                }

                return ResponseEntity.status(400).body(erros.toString());
            }

            salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado parcialmente com sucesso");
        }
        catch (RuntimeException re){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao atualizar o Produto");
        }
    }

    public List<Produto> buscarPorNome(String txt){
        return produtoRepository.findByNomeLikeIgnoreCase(txt);
    }
}
