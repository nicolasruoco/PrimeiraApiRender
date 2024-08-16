package com.example.apidata.controller;

import com.example.apidata.model.Produto;
import com.example.apidata.repository.ProdutoRepository;
import com.example.apidata.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.apidata.service.ProdutoService;
import org.springframework.web.context.annotation.ApplicationScope;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }

    @GetMapping("/selecionar")
    @Operation(summary = "Listar todos os Produtos",
                description = "Retorna uma lista de todos os produtos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O retorno de todos os produtos foi feito com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno na api", content = @Content(mediaType = "application/json"))
    })
    public List<Produto> listarProdutos(){
        return produtoService.buscarTodosProdutos();
    }

    @Operation(summary = "Inserir um novo produto",
                description = "Retorna o código do produto ou uma mensagem de erro"
                )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A inserção do produto foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "O produto não pode ser inserido, verifique-o e tente novamente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping("/inserir")
    public ResponseEntity<?> inserirProduto(@Valid @RequestBody Produto produto, BindingResult result){

        if(result.hasErrors()){

            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : result.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField() + " ", " " + erro.getDefaultMessage());
            }

            return new ResponseEntity<>(erros, HttpStatus.BAD_REQUEST);
        }

        Produto prod = produtoService.salvarProduto(produto);
        return new ResponseEntity<>("Produto Inserido:" + prod.toString(), HttpStatus.OK);
    }

    @Operation(summary = "Excluir um produto",
                description = "Usado para excluir um produto, retorna o código do produto excluído ou um código de erro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A remoção do produto foi realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "O produto não pode ser removido, verifique-o e tente novamente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluirProduto(@Parameter(description = "Id do produto a ser excluído") @PathVariable Long id) {
        Produto produto = produtoService.buscarProdutoPorId(id);

        if (produto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }

        produtoService.excluirProduto(produto.getId());
        return ResponseEntity.ok("Produto Excluído");
    }
    
    @Operation(summary = "Atualiza um produto específico", description = "Atualiza um único produto de acordo com o ID recebido como parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @Schema(description = "Atualiza um produto")
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizarProduto(@Valid @PathVariable Long id,
                                                   @RequestBody Produto produtoAtualizado, BindingResult result){

        if(result.hasErrors()){

            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : result.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField(), erro.getDefaultMessage());
            }

            return ResponseEntity.status(400).body(erros.toString());
        }

        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null && produtoAtualizado != null){
            return produtoService.atualizarProduto(id, produtoAtualizado);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualiza alguns campos de um produto específico", description = "Atualiza um único produto de acordo com o ID e os campos recebidos como parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PatchMapping("/atualizarParcial/{id}")
    public ResponseEntity<String> atualizarProdutoParcial(@PathVariable Long id, @RequestBody Map<String, Object> updates){
        Produto produto = produtoService.buscarProdutoPorId(id);
        if(produto != null){
            return produtoService.atualizarProdutoParcial(produto, updates);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto com ID " + id + " não encontrado");
    }

    @Operation(summary = "Busca produtos baseado no nome", description = "Retorna uma lista de produtos de acordo com o valor recebido como parâmetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @Schema(description = "Busca produto por nome")
    @GetMapping("/buscarPorNome")
    public ResponseEntity<String> buscarPorNome(@RequestParam String txt) {
        if (!txt.equals("")) {

            List<Produto> lp = produtoService.buscarPorNome(txt);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lp.size(); i++) {
                sb.append(lp.get(i));
            }
            return ResponseEntity.ok(sb.toString());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produtos não encontrados");
    }
}
