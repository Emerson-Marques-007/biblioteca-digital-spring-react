package com.biblioteca.model;

/**
 * Enum que representa os possíveis status de um empréstimo.
 * 
 * Implementa o conceito de abstração ao definir os estados
 * possíveis de um empréstimo no sistema.
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
public enum StatusEmprestimo {
    
    /**
     * Empréstimo ativo, dentro do prazo.
     */
    ATIVO("Ativo"),
    
    /**
     * Empréstimo devolvido no prazo.
     */
    DEVOLVIDO("Devolvido"),
    
    /**
     * Empréstimo em atraso (passou da data de devolução).
     */
    ATRASADO("Atrasado"),
    
    /**
     * Empréstimo renovado.
     */
    RENOVADO("Renovado");

    private final String descricao;

    StatusEmprestimo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Verifica se o status permite renovação.
     * 
     * @return true se pode ser renovado
     */
    public boolean podeRenovar() {
        return this == ATIVO || this == RENOVADO;
    }

    /**
     * Verifica se o status indica que o livro está com o usuário.
     * 
     * @return true se o livro está emprestado
     */
    public boolean isEmprestado() {
        return this == ATIVO || this == ATRASADO || this == RENOVADO;
    }

    /**
     * Verifica se o empréstimo foi finalizado.
     * 
     * @return true se finalizado
     */
    public boolean isFinalizado() {
        return this == DEVOLVIDO;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

