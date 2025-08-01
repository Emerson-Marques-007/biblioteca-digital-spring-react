package com.biblioteca.repository;

import com.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Livro.
 * 
 * Implementa o padrão Repository, fornecendo abstração para
 * acesso aos dados de livros.
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    /**
     * Busca um livro pelo ISBN.
     * 
     * @param isbn ISBN do livro
     * @return Optional contendo o livro se encontrado
     */
    Optional<Livro> findByIsbn(String isbn);

    /**
     * Verifica se existe um livro com o ISBN informado.
     * 
     * @param isbn ISBN a verificar
     * @return true se existe
     */
    boolean existsByIsbn(String isbn);

    /**
     * Busca livros por título (busca parcial, case-insensitive).
     * 
     * @param titulo título ou parte do título
     * @return lista de livros encontrados
     */
    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Busca livros por autor (busca parcial, case-insensitive).
     * 
     * @param autor autor ou parte do nome do autor
     * @return lista de livros encontrados
     */
    List<Livro> findByAutorContainingIgnoreCase(String autor);

    /**
     * Busca livros por gênero (busca parcial, case-insensitive).
     * 
     * @param genero gênero ou parte do gênero
     * @return lista de livros encontrados
     */
    List<Livro> findByGeneroContainingIgnoreCase(String genero);

    /**
     * Busca livros por status de disponibilidade.
     * 
     * @param disponivel true para disponíveis, false para indisponíveis
     * @return lista de livros com o status especificado
     */
    List<Livro> findByStatusDisponibilidade(Boolean disponivel);

    /**
     * Busca livros disponíveis para empréstimo.
     * 
     * @return lista de livros disponíveis
     */
    @Query("SELECT l FROM Livro l WHERE l.statusDisponibilidade = true")
    List<Livro> findLivrosDisponiveis();

    /**
     * Busca livros por ano de publicação.
     * 
     * @param ano ano de publicação
     * @return lista de livros do ano especificado
     */
    List<Livro> findByAnoPublicacao(Integer ano);

    /**
     * Busca livros publicados em um intervalo de anos.
     * 
     * @param anoInicio ano inicial (inclusive)
     * @param anoFim ano final (inclusive)
     * @return lista de livros no intervalo
     */
    List<Livro> findByAnoPublicacaoBetween(Integer anoInicio, Integer anoFim);

    /**
     * Busca livros por múltiplos critérios (título, autor ou ISBN).
     * 
     * @param termo termo de busca
     * @return lista de livros que correspondem ao termo
     */
    @Query("SELECT l FROM Livro l WHERE " +
           "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(l.autor) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "l.isbn LIKE CONCAT('%', :termo, '%')")
    List<Livro> buscarPorTermoGeral(@Param("termo") String termo);

    /**
     * Conta quantos livros estão disponíveis.
     * 
     * @return número de livros disponíveis
     */
    @Query("SELECT COUNT(l) FROM Livro l WHERE l.statusDisponibilidade = true")
    long contarLivrosDisponiveis();

    /**
     * Conta quantos livros estão emprestados.
     * 
     * @return número de livros emprestados
     */
    @Query("SELECT COUNT(l) FROM Livro l WHERE l.statusDisponibilidade = false")
    long contarLivrosEmprestados();

    /**
     * Busca livros que nunca foram emprestados.
     * 
     * @return lista de livros sem empréstimos
     */
    @Query("SELECT l FROM Livro l WHERE l.emprestimos IS EMPTY")
    List<Livro> findLivrosNuncaEmprestados();

    /**
     * Busca os livros mais emprestados.
     * 
     * @param limite número máximo de resultados
     * @return lista dos livros mais emprestados
     */
    @Query("SELECT l FROM Livro l " +
           "LEFT JOIN l.emprestimos e " +
           "GROUP BY l " +
           "ORDER BY COUNT(e) DESC")
    List<Livro> findLivrosMaisEmprestados(@Param("limite") int limite);

    /**
     * Verifica se um livro pode ser deletado (não tem empréstimos ativos).
     * 
     * @param livroId ID do livro
     * @return true se pode ser deletado
     */
    @Query("SELECT CASE WHEN COUNT(e) = 0 THEN true ELSE false END " +
           "FROM Emprestimo e WHERE e.livro.id = :livroId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    boolean podeSerDeletado(@Param("livroId") Long livroId);
}

