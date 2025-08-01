package com.biblioteca.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa um livro no sistema de biblioteca.
 * 
 * Esta classe implementa os conceitos de POO:
 * - Encapsulamento: atributos privados com getters/setters
 * - Abstração: representa um livro do mundo real
 * - Validações de negócio encapsuladas
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 150, message = "Autor deve ter no máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String autor;

    @NotBlank(message = "ISBN é obrigatório")
    @Pattern(regexp = "^[0-9]{10}([0-9]{3})?$", message = "ISBN deve ter 10 ou 13 dígitos")
    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @NotNull(message = "Ano de publicação é obrigatório")
    @Min(value = 1000, message = "Ano de publicação deve ser maior que 1000")
    @Max(value = 2030, message = "Ano de publicação deve ser menor que 2030")
    @Column(name = "ano_publicacao", nullable = false)
    private Integer anoPublicacao;

    @Size(max = 100, message = "Gênero deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String genero;

    @Column(name = "status_disponibilidade", nullable = false)
    private Boolean statusDisponibilidade = true;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Relacionamento com empréstimos
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos;

    // Construtor padrão
    public Livro() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor com parâmetros principais
    public Livro(String titulo, String autor, String isbn, Integer anoPublicacao) {
        this();
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
    }

    // Métodos de negócio

    /**
     * Valida se o ISBN tem o formato correto.
     * Implementa encapsulamento da lógica de validação.
     * 
     * @return true se o ISBN é válido
     */
    public boolean validarIsbn() {
        if (isbn == null) return false;
        String isbnLimpo = isbn.replaceAll("[^0-9]", "");
        return isbnLimpo.length() == 10 || isbnLimpo.length() == 13;
    }

    /**
     * Retorna uma representação textual dos detalhes do livro.
     * 
     * @return string com detalhes do livro
     */
    public String exibirDetalhes() {
        String disponibilidade = statusDisponibilidade ? "Disponível" : "Indisponível";
        return String.format("%s por %s (%d) - ISBN: %s - %s", 
                           titulo, autor, anoPublicacao, isbn, disponibilidade);
    }

    /**
     * Verifica se o livro está disponível para empréstimo.
     * 
     * @return true se disponível
     */
    public boolean isDisponivel() {
        return statusDisponibilidade;
    }

    /**
     * Marca o livro como indisponível (emprestado).
     */
    public void marcarComoEmprestado() {
        this.statusDisponibilidade = false;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Marca o livro como disponível (devolvido).
     */
    public void marcarComoDisponivel() {
        this.statusDisponibilidade = true;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Callback JPA para atualizar data de modificação
    @PreUpdate
    @PrePersist
    public void atualizarDataModificacao() {
        this.dataAtualizacao = LocalDateTime.now();
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

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    // equals e hashCode baseados no ISBN (identificador único de negócio)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return Objects.equals(isbn, livro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", anoPublicacao=" + anoPublicacao +
                ", statusDisponibilidade=" + statusDisponibilidade +
                '}';
    }
}

