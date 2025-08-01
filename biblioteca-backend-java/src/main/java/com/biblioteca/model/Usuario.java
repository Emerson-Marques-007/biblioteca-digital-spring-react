package com.biblioteca.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Entidade que representa um usuário do sistema de biblioteca.
 * 
 * Implementa conceitos de POO:
 * - Encapsulamento: atributos privados com métodos de acesso
 * - Abstração: representa um usuário real da biblioteca
 * - Validações de negócio encapsuladas
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    @Size(max = 120, message = "Email deve ter no máximo 120 caracteres")
    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String telefone;

    @Size(max = 500, message = "Endereço deve ter no máximo 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String endereco;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "status_ativo", nullable = false)
    private Boolean statusAtivo = true;

    // Relacionamento com empréstimos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos;

    // Construtor padrão
    public Usuario() {
        this.dataCadastro = LocalDateTime.now();
        this.statusAtivo = true;
    }

    // Construtor com parâmetros principais
    public Usuario(String nome, String email) {
        this();
        this.nome = nome;
        this.email = email;
    }

    // Métodos de negócio

    /**
     * Valida se o email tem um formato válido.
     * Implementa encapsulamento da lógica de validação.
     * 
     * @return true se o email é válido
     */
    public boolean validarEmail() {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Ativa o usuário no sistema.
     */
    public void ativar() {
        this.statusAtivo = true;
    }

    /**
     * Desativa o usuário no sistema.
     * Só pode ser desativado se não tiver empréstimos ativos.
     */
    public void desativar() {
        // Verificação de empréstimos ativos seria feita na camada de serviço
        this.statusAtivo = false;
    }

    /**
     * Verifica se o usuário está ativo.
     * 
     * @return true se ativo
     */
    public boolean isAtivo() {
        return statusAtivo;
    }

    /**
     * Verifica se o usuário pode fazer empréstimos.
     * 
     * @return true se pode emprestar
     */
    public boolean podeEmprestar() {
        return statusAtivo;
    }

    /**
     * Conta quantos empréstimos ativos o usuário possui.
     * 
     * @return número de empréstimos ativos
     */
    public long contarEmprestimosAtivos() {
        if (emprestimos == null) return 0;
        
        return emprestimos.stream()
                .filter(emp -> emp.getStatusEmprestimo() == StatusEmprestimo.ATIVO)
                .count();
    }

    /**
     * Verifica se o usuário tem empréstimos em atraso.
     * 
     * @return true se tem empréstimos atrasados
     */
    public boolean temEmprestimosAtrasados() {
        if (emprestimos == null) return false;
        
        return emprestimos.stream()
                .anyMatch(emp -> emp.getStatusEmprestimo() == StatusEmprestimo.ATRASADO);
    }

    // Callback JPA
    @PrePersist
    public void prePersist() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
        if (statusAtivo == null) {
            statusAtivo = true;
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(Boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    // equals e hashCode baseados no email (identificador único de negócio)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", statusAtivo=" + statusAtivo +
                '}';
    }
}

