package com.example.apidata.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="produto")
@Schema(description = "Representa um produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id do produto")
    private long id;
    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, message = "o nome deve ter pelo menos 2 caractéres")
    @Schema(description = "Nome do produto")
    private String nome;
    @Schema(description = "Descrição do Produto")
    private String descricao;
    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "o preço deve ser pelo menos 0")
    @Schema(description = "Preço do Produto")
    private double preco;
    @Column(name = "quantidadeestoque")
    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "a quantidade deve ser pelo menos 0")
    @Schema(description = "Quantidade de estoque do produto")
    private int quantidadeEstoque;

    public Produto(){}

    public Produto(String nome, String descricao, Double preco, Integer quantidadeEstoque) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {this.id = id;}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                '}';
    }
}
