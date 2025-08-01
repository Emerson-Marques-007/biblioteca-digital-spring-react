package com.biblioteca.dto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para transferência de dados de Usuario.
 * 
 * Implementa o conceito de abstração, separando a representação
 * de dados da entidade de domínio.
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    @Size(max = 120, message = "Email deve ter no máximo 120 caracteres")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @Size(max = 500, message = "Endereço deve ter no máximo 500 caracteres")
    private String endereco;

    private LocalDateTime dataCadastro;

    private Boolean statusAtivo;

    // Construtor padrão
    public UsuarioDTO() {
    }

    // Construtor com parâmetros principais
    public UsuarioDTO(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.statusAtivo = true;
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

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", statusAtivo=" + statusAtivo +
                '}';
    }
}

