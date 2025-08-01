package com.biblioteca.repository;

import com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Usuario.
 * 
 * Implementa o padrão Repository, fornecendo abstração para
 * acesso aos dados de usuários.
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     * 
     * @param email email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email informado.
     * 
     * @param email email a verificar
     * @return true se existe
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por nome (busca parcial, case-insensitive).
     * 
     * @param nome nome ou parte do nome
     * @return lista de usuários encontrados
     */
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca usuários por status ativo.
     * 
     * @param ativo true para ativos, false para inativos
     * @return lista de usuários com o status especificado
     */
    List<Usuario> findByStatusAtivo(Boolean ativo);

    /**
     * Busca usuários ativos.
     * 
     * @return lista de usuários ativos
     */
    @Query("SELECT u FROM Usuario u WHERE u.statusAtivo = true")
    List<Usuario> findUsuariosAtivos();

    /**
     * Busca usuários inativos.
     * 
     * @return lista de usuários inativos
     */
    @Query("SELECT u FROM Usuario u WHERE u.statusAtivo = false")
    List<Usuario> findUsuariosInativos();

    /**
     * Busca usuários cadastrados em um período.
     * 
     * @param dataInicio data inicial (inclusive)
     * @param dataFim data final (inclusive)
     * @return lista de usuários cadastrados no período
     */
    List<Usuario> findByDataCadastroBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca usuários por múltiplos critérios (nome ou email).
     * 
     * @param termo termo de busca
     * @return lista de usuários que correspondem ao termo
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Usuario> buscarPorTermoGeral(@Param("termo") String termo);

    /**
     * Conta quantos usuários estão ativos.
     * 
     * @return número de usuários ativos
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.statusAtivo = true")
    long contarUsuariosAtivos();

    /**
     * Conta quantos usuários estão inativos.
     * 
     * @return número de usuários inativos
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.statusAtivo = false")
    long contarUsuariosInativos();

    /**
     * Busca usuários com empréstimos ativos.
     * 
     * @return lista de usuários com empréstimos ativos
     */
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "JOIN u.emprestimos e " +
           "WHERE e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    List<Usuario> findUsuariosComEmprestimosAtivos();

    /**
     * Busca usuários com empréstimos em atraso.
     * 
     * @return lista de usuários com empréstimos atrasados
     */
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "JOIN u.emprestimos e " +
           "WHERE e.statusEmprestimo = 'ATRASADO'")
    List<Usuario> findUsuariosComEmprestimosAtrasados();

    /**
     * Busca usuários que nunca fizeram empréstimos.
     * 
     * @return lista de usuários sem empréstimos
     */
    @Query("SELECT u FROM Usuario u WHERE u.emprestimos IS EMPTY")
    List<Usuario> findUsuariosSemEmprestimos();

    /**
     * Conta quantos empréstimos ativos um usuário possui.
     * 
     * @param usuarioId ID do usuário
     * @return número de empréstimos ativos
     */
    @Query("SELECT COUNT(e) FROM Emprestimo e " +
           "WHERE e.usuario.id = :usuarioId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    long contarEmprestimosAtivos(@Param("usuarioId") Long usuarioId);

    /**
     * Verifica se um usuário pode ser deletado (não tem empréstimos ativos).
     * 
     * @param usuarioId ID do usuário
     * @return true se pode ser deletado
     */
    @Query("SELECT CASE WHEN COUNT(e) = 0 THEN true ELSE false END " +
           "FROM Emprestimo e WHERE e.usuario.id = :usuarioId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    boolean podeSerDeletado(@Param("usuarioId") Long usuarioId);

    /**
     * Busca os usuários que mais fizeram empréstimos.
     * 
     * @param limite número máximo de resultados
     * @return lista dos usuários que mais emprestaram
     */
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN u.emprestimos e " +
           "GROUP BY u " +
           "ORDER BY COUNT(e) DESC")
    List<Usuario> findUsuariosQueMaisEmprestaram(@Param("limite") int limite);

    /**
     * Busca usuários por telefone (busca parcial).
     * 
     * @param telefone telefone ou parte do telefone
     * @return lista de usuários encontrados
     */
    List<Usuario> findByTelefoneContaining(String telefone);

    /**
     * Busca usuários cadastrados hoje.
     * 
     * @return lista de usuários cadastrados hoje
     */
    @Query("SELECT u FROM Usuario u WHERE DATE(u.dataCadastro) = CURRENT_DATE")
    List<Usuario> findUsuariosCadastradosHoje();

    /**
     * Busca usuários cadastrados esta semana.
     * 
     * @return lista de usuários cadastrados esta semana
     */
    @Query("SELECT u FROM Usuario u WHERE u.dataCadastro >= :inicioSemana")
    List<Usuario> findUsuariosCadastradosEstaSemana(@Param("inicioSemana") LocalDateTime inicioSemana);
}

