package com.biblioteca.dto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para transferência de dados de Livro.
 * 
 * Implementa o conceito de abstração, separando a representação
 * de dados da entidade de domínio.
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
public class LivroDTO {

    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    private String titulo;

    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 150, message = "Autor deve ter no máximo 150 caracteres")
    private String autor;

    @NotBlank(message = "ISBN é obrigatório")
    @Pattern(regexp = "^[0-9]{10}([0-9]{3})?$", message = "ISBN deve ter 10 ou 13 dígitos")
    private String isbn;

    @NotNull(message = "Ano de publicação é obrigatório")
    @Min(value = 1000, message = "Ano de publicação deve ser maior que 1000")
    @Max(value = 2030, message = "Ano de publicação deve ser menor que 2030")
    private Integer anoPublicacao;

    @Size(max = 100, message = "Gênero deve ter no máximo 100 caracteres")
    private String genero;

    private Boolean statusDisponibilidade;

    private LocalDateTime dataAtualizacao;

    // Construtor padrão
    public LivroDTO() {
    }

    // Construtor com parâmetros principais
    public LivroDTO(String titulo, String autor, String isbn, Integer anoPublicacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.statusDisponibilidade = true;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Boolean getStatusDisponibilidade() {
        return statusDisponibilidade;
    }

    public void setStatusDisponibilidade(Boolean statusDisponibilidade) {
        this.statusDisponibilidade = statusDisponibilidade;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public String toString() {
        return "LivroDTO{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", anoPublicacao=" + anoPublicacao +
                ", statusDisponibilidade=" + statusDisponibilidade +
                '}';
    }
}

